package com.reddit.pinfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedReader;
import java.io.IOException;
/*import java.util.regex.Matcher;
import java.util.regex.Pattern;*/
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CameraActivity extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    TextView phoneText;
    TextView emailText;
    TextView webText;
    TextView nameText;
    TextView addressText;

    CameraSource mCameraSource;
    Button finBtn;
    String phone = "";
    String name = "";
    String email = "";
    String address = "";
    String web = "";
    String[] intentArray = new String[5];
    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);

        mCameraView = findViewById(R.id.surfaceView);
        phoneText = findViewById(R.id.phoneText);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        webText = findViewById(R.id.webText);
        mTextView = findViewById(R.id.mTextView);
        addressText = findViewById(R.id.addressText);
        phoneText.setText("Phone:");
        nameText.setText("Name:");
        emailText.setText("Email:");
        webText.setText("Website:");
        addressText.setText("Address:");
        startCameraSource();
        Button btn = findViewById(R.id.finBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditActivity();
            }
        });
        Button rescanbtn = findViewById(R.id.RetryBtn);
        rescanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = "";
                name = "";
                email = "";
                address = "";
                web = "";
            }
        });


    }
    public void openEditActivity(){
        Intent editInt = new Intent(this, EditInfoActivity.class);
        editInt.putExtra("key", intentArray);
        startActivity(editInt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(CameraActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){
                        mTextView.post(new Runnable() {
                            @Override

                            public void run() {
                                if(name.equals("")) {
                                    name = nameAnalyze(items);
                                    nameText.setText("Name: " + name);
                                    intentArray[0] = name;
                                }
                                if(phone.equals("")){
                                    phone = phoneAnalyze(items);
                                    phoneText.setText("Primary Phone: "+ phone);
                                    intentArray[1] = phone;
                                }
                                if(email.equals("")){
                                    email = emailAnalyze(items);
                                    emailText.setText("Email: " + email);
                                    intentArray[2] = email;
                                }
                                if(web.equals("")){
                                    web = webAnalyze(items);
                                    webText.setText("Website: " + web);
                                    intentArray[3] = web;
                                }
                                if(address.equals("")) {
                                    address = addressAnalyze(items);
                                    addressText.setText("Address: " + address);
                                    intentArray[4] = address;
                                }
                            }
                        });

                    }
                }
                public String phoneAnalyze(SparseArray<TextBlock> array){
                    String ret = "";
                    for(int i=0; i<array.size(); i++){
                        if(array.get(i) == null)
                            continue;
                        TextBlock item = array.get(i);
                        String s = item.getValue();

                        s = s.replaceAll(" ", "");
                        String punct = ".(-)";
                        s = s.replaceAll("\\p{Punct}", "");
                        s = s.replaceAll("\\p{Alpha}", "");
                        s = s.replaceAll("-", "");
                        int numlength = s.length()-10;
                        if(numlength<0)
                            numlength = 0;
                        s = s.substring(numlength);

                        if(s.length()>6){
                            ret = s;
                            break;}
                    }
                    return ret;

                }
                public String emailAnalyze(SparseArray<TextBlock> array) {
                    String ret = "";
                    for (int i = 0; i < array.size(); i++) {
                        if (array.get(i) == null)
                            continue;
                        TextBlock item = array.get(i);
                        String s = item.getValue();

                        s = s.replaceAll(" ", "");
                        if (s.contains("@")) {
                            int x = s.indexOf(':');
                            if (x != -1)
                                s = s.substring(x);
                            String[] sa = s.split("\n");
                            boolean found = false;
                            for(String t: sa){
                                if(t.contains("@")) {
                                    ret = t;
                                    found = true;
                                    break;
                                }
                            }
                            if(found==true)
                                break;
                        }
                    }
                    return ret;
                }
                public String webAnalyze(SparseArray<TextBlock> array) {
                    String ret = "";
                    for (int i = 0; i < array.size(); i++) {
                        if (array.get(i) == null)
                            continue;
                        TextBlock item = array.get(i);
                        String s = item.getValue();

                        s = s.replaceAll(" ", "");
                        if (s.contains("www.") || s.contains("WWW.")) {
                            int x = s.indexOf(':');
                            if (x != -1)
                                s = s.substring(x);
                            String[] sa = s.split("\n");
                            boolean found = false;
                            for(String t: sa){
                                if(t.contains("www.") || t.contains("WWW.") ){
                                    ret = t;
                                    found = true;
                                    break;
                                }
                            }
                            if(found==true)
                                break;
                        }
                    }
                    return ret;
                }

                public String addressAnalyze(SparseArray<TextBlock> array) {
                    String ret = "";
                    for (int i = 0; i < array.size(); i++) {
                        if (array.get(i) == null)
                            continue;
                        TextBlock item = array.get(i);
                        String s = item.getValue();

                        boolean found = true;
                        boolean found2 = false;
                        boolean found3 = false;
                        int count = 0;

                        if( s.length() > 10)
                            found2 = true;
                        else
                            return "";
                        // Starts w number, longer than 10 chars, ends w a number, is not phone number or website.
                        for(int x=0; x<2; x++)
                            if(!("1234567890").contains("" + s.charAt(x)) && found2)
                                found = false;

                        String[] stringA = s.split("\n");
                        s = stringA[0];

                        int upCount = 0;
                        for(int x=0; x<s.length(); x++)
                            if( Character.isUpperCase(s.charAt(x)))
                                upCount++;

                        if( upCount >= 2)
                            found3 = true;

                        if( found && found2 && found3)
                            ret = s;

                    }
                    return ret;
                }
                public String nameAnalyze(SparseArray<TextBlock> array) {

                    AssetManager assetManager = getAssets();
                    ArrayList<String> wordList = new ArrayList<String>();
// To load text file
                    InputStream input;
                    try {
                        input = assetManager.open("dict.txt");
                        InputStreamReader in = new InputStreamReader(input);
                        BufferedReader bf = new BufferedReader(in);
                        String line;
                        do{
                            line = bf.readLine();
                            wordList.add(line);
                        }while(line != null);



//
//                        // byte buffer into a string
//                        String text = new String(buffer);
//
//                        txtContent.setText(text);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



                    String ret = "";
                    for (int i = 0; i < array.size(); i++) {
                        if (array.get(i) == null)
                            continue;

                        TextBlock item = array.get(i);
                        String s = item.getValue();

                         for(int x=0; x<s.length(); x++)
                            if(("1234567890{}|!@#$%%^&*()-~`/?><,.").contains("" + s.charAt(x)))
                                return "";

                        String[] nameArray = s.split(" ");
                        for(String str: nameArray) {
                            System.out.println(str);
                            str = str.toUpperCase();
                            if( wordList.contains(str)) {
                                 str = str.charAt(0) + str.substring(1).toLowerCase();
                                 return str;
                            }
                        }
                    }

                    return ret;
                }

            });
        }
    }

}



