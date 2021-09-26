package com.reddit.pinfo;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.Manifest;
import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeStream;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditInfoActivity extends AppCompatActivity {
    TextView phoneText;
    TextView emailText;
    TextView webText;
    TextView nameText;
    TextView addressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<Contacts> list = new ArrayList<Contacts>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_main);
        phoneText = findViewById(R.id.phoneText);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        webText = findViewById(R.id.webText);
        addressText = findViewById(R.id.addressText);
        Intent intent = getIntent();
        String[] intentArray = intent.getStringArrayExtra("key");
        nameText.setText(intentArray[0]);
        phoneText.setText(intentArray[1]);
        emailText.setText(intentArray[2]);
        webText.setText(intentArray[3]);
        addressText.setText(intentArray[4]);

        list.add(new Contacts("" + nameText.getText(), "" + phoneText.getText(), "" + emailText.getText(), "" + webText.getText(), "" + addressText.getText()));
        Button btn = findViewById(R.id.addBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, "" + nameText.getText())
                        .putExtra(ContactsContract.Intents.Insert.PHONE, phoneText.getText())
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, emailText.getText())
                        .putExtra(ContactsContract.Intents.Insert.POSTAL, addressText.getText());
                startActivity(intent);
            }
        });
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityCamera();
            }
        });
    }

    public void startActivityCamera() {
        Intent backInt = new Intent(this, CameraActivity.class);
        startActivity(backInt);
    }
}
class Contacts {
    private String name, phone, email, web, address;

    public Contacts() {
        this.name = "";
        this.phone = "";
        this.address = "";
        this.email = "";
        this.web = "";
    }

    public Contacts(String name, String phone, String email, String address, String web) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.web = web;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setWeb(String web) {
        this.web = web;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneNum() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }
    public String getWeb() {
        return this.web;
    }
    public String getEmail() {
        return this.email;
    }
    public String toString(){
        return name + " " + phone + " "  + email + " "+ address + " " + web;
    }
}
//    private void runTextRecognition() {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imgBmp);
//        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
//                .getOnDeviceTextRecognizer();
//        detector.processImage(image)
//                .addOnSuccessListener(
//                        new OnSuccessListener<FirebaseVisionText>() {
//                            @Override
//                            public void onSuccess(FirebaseVisionText texts) {
//                                processTextRecognitionResult(texts);
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                e.printStackTrace();
//                            }
//                        });
//    }
//
//    private void processTextRecognitionResult(FirebaseVisionText texts) {
//        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
//        if (blocks.size() == 0) {
//            txtview.setText("No text found");
//            return;
//        }
//        txtview.setText("");
//
//        for (int i = 0; i < blocks.size(); i++) {
//            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
//            for (int j = 0; j < lines.size(); j++) {
//                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
//                for (int k = 0; k < elements.size(); k++) {
//
//                    txtview.append(elements.get(k).getText());
//                }
//            }
//        }
//    }
//    private void runTextRecog() {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imgBmp);
//
////Create an instance of FirebaseVisionCloudTextDetector//
//
//        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
//
////Register an OnSuccessListener//
//
//        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//
////Implement the onSuccess callback//
//
//            public void onSuccess(FirebaseVisionText texts) {
//
////Call processExtractedText with the response//
//
//                processExtractedText(texts);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//
////Implement the onFailure calback//
//
//            public void onFailure
//                    (@NonNull Exception exception) {
//                Toast.makeText(EditInfoActivity.this,
//                        "Exception", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//    private void processExtractedText(FirebaseVisionText firebaseVisionText) {
//        txtview.setText(null);
//        if (firebaseVisionText.getTextBlocks().size() == 0) {
//            txtview.setText("no_text");
//            return;
//        }
//        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
//            for(FirebaseVisionText.Line eachLine : block.getLines()){
//                for(FirebaseVisionText.Element element : eachLine.getElements()){
//
//                        txtview.append(element.getText());
//
//                }
//            }
//
//
//        }
//    }

