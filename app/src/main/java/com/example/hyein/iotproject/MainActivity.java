package com.example.hyein.iotproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.AccessDeniedException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    String[] contactArray = {};
    ListView listView;
    TextView textView;
    ArrayAdapter<String> contactAdapter;
    CognitoCachingCredentialsProvider s3credentialsProvider;
    AmazonS3 s3;
    AmazonRekognition client;
    ArrayList<String> detText;
    String foodlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s3credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:795e992e-8778-4408-9a48-0950b6bde13c", // 자격 증명 풀 ID
                Regions.US_EAST_1 // 리전
        );
        s3 = new AmazonS3Client(s3credentialsProvider);

        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        s3.setEndpoint("s3.us-east-1.amazonaws.com");


        Intent intent = getIntent();
        foodlist=intent.getStringExtra("foodlist");

        getSupportActionBar().setTitle("재료목록");

        contactArray = foodlist.split(",");
        int size = contactArray.length;
        for (int i = 0; i < size; i++){
            contactArray[i] = contactArray[i].trim();
        }

        listView = (ListView) findViewById(R.id.listView);

        contactAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, contactArray);
        listView.setAdapter(contactAdapter);

        int a = contactAdapter.getCount();
        textView = (TextView)findViewById(R.id.count1);
        textView.setText("냉장고 속 재료는 " + a +" 개 입니다.");
        initrekognitionClient();
        new GetText().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("key1",listView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
    }


    private class GetText extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            DetectTextRequest request = new DetectTextRequest().withImage(new Image().withS3Object(new S3Object().withName("test2.jpg").withBucket("raspgum")));
            detText = new ArrayList<>();
            try{
                while(true){
                    if(s3.doesObjectExist("raspgum", "test2.jpg")){
                        DetectTextResult result= client.detectText(request);
                        List<TextDetection> textDetections = result.getTextDetections();

                        //final StringBuffer sb= new StringBuffer();
                        for(TextDetection text : textDetections){
                            Log.d("hereisText : ", text.getDetectedText());
                            detText.add(text.getDetectedText());
                        }
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String exp = detText.get(0);
                    //String oldstring = "2011-01-18 00:00:00.0";
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
                    Date expDate = new Date();
                    try {
                        expDate = dateFormat.parse(exp);
                    } catch (ParseException e) {
                        //handle exception
                    }
                    long now = System.currentTimeMillis();
                    Date nowD = new Date(now);
                    try {
                        nowD = dateFormat.parse(dateFormat.format(nowD));
                    } catch (ParseException e) {
                        //handle exception
                    }

                    long time = expDate.getTime()-nowD.getTime();
                    long timecompare = time/3600000;
                    if(timecompare<=12){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("우유 유통기한이 얼마 남지 않았습니다!");
                        builder.setMessage("유통기한이 "+timecompare+"시간 남았어요!");
                        if(timecompare<0){
                            builder.setTitle("우유 유통기한이 지났어요!");
                            builder.setMessage("우유를 버려주세요...\n우유 유통기한은 "+exp+" 까지 였습니다.");
                        }
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.show();
                    }
                }
            });
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
}