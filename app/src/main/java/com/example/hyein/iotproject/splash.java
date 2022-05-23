package com.example.hyein.iotproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.AccessDeniedException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


public class splash extends Activity {
    private String TAG = "food";
    StringBuffer response;
    StringBuffer a;
    CognitoCachingCredentialsProvider s3credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;
    AmazonRekognition client;
    AmazonPollyPresigningClient polly;
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp);
        s3credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:795e992e-8778-4408-9a48-0950b6bde13c", // 자격 증명 풀 ID
                Regions.US_EAST_1 // 리전
        );
        s3 = new AmazonS3Client(s3credentialsProvider);

        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        s3.setEndpoint("s3.us-east-1.amazonaws.com");
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try{

                    int waited = 0;

                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                    initrekognitionClient();
                    new Getfood().execute();
                }catch (InterruptedException e) {

                }finally{
  //                  splash.this.finish();
                }
            }
        };

        splashTread.start();
    }

    private class Getfood extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            DetectLabelsRequest request = new DetectLabelsRequest().withImage(new Image().withS3Object(new S3Object().withName("test.jpg").withBucket("raspgum")));

            try{
                while(true){
                    if(s3.doesObjectExist("raspgum", "test.jpg")){
                        DetectLabelsResult result= client.detectLabels(request);
                        List<Label> labels = result.getLabels();
                        StringBuffer sb= new StringBuffer();
                        for(Label label : labels){
                            if(!label.getName().equals("Food") && !label.getName().equals("Product") && !label.getName().equals("Plant")
                                    &&!label.getName().equals("Vegetable") &&!label.getName().equals("Seafood") &&!label.getName().equals("Fruit")
                                    &&!label.getName().equals("Animal") &&!label.getName().equals("Sea Life") &&!label.getName().equals("Shop")
                                    &&!label.getName().equals("Butcher Shop") &&!label.getName().equals("Produce") &&!label.getName().equals("Citrus Fruit"))
                                if(label.getConfidence().floatValue()>80)
                                    sb.append(label.getName()+",");

                        }

                        a= sb;
                        break;
                    }
                    else{ }
                }

            }catch (AccessDeniedException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new englishtokorea().execute(a.toString());
        }

    }
    public class englishtokorea extends AsyncTask<String, Void, String> {

        String clientId = "F2dTMcYSaj9wwSKObg8m";//클라이언트 아이디값";
        String clientSecret = "UiNzWs_ugN";//클라이언트 시크릿값";
        String sourceLang = "en";
        String targetLang = "ko";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {

            String sourceText = strings[0];

            try {
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else { // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                return response.toString();

            } catch (Exception e) {
                Log.d("error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(response.toString())
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);

            Intent intent = new Intent(splash.this, MainActivity.class);
            intent.putExtra("foodlist",items.getTranslatedText());
            startActivity(intent);
            finish();
        }

    }

    void initrekognitionClient() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:a4327c31-5950-4701-9a3e-b64ab4c9d863", // Identity Pool ID
                Regions.AP_NORTHEAST_2
        );
        client = new AmazonRekognitionClient(credentialsProvider);
    }

    void initpollyClient() {
        CognitoCachingCredentialsProvider pollycredentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:a4327c31-5950-4701-9a3e-b64ab4c9d863", // Identity Pool ID
                Regions.AP_NORTHEAST_2
        );
        polly = new AmazonPollyPresigningClient(pollycredentialsProvider);
    }

    public void onBackPressed(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private class TranslatedItem {
        String translatedText;
        public String getTranslatedText() {
            return translatedText;
        }
    }

}