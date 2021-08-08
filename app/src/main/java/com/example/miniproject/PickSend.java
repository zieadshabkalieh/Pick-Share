package com.example.miniproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;

public class PickSend extends AppCompatActivity {
    ImageButton btn_Pickfromgallery;
    Button btn_Sharetoperson;
    EditText et_description;
    final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_Pickfromgallery = findViewById(R.id.btn_addphoto);
        btn_Sharetoperson = findViewById(R.id.btn_share);
        et_description = findViewById(R.id.et_photodescription);
        btn_Pickfromgallery.setOnClickListener(v -> {
            if (checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
            else
            {
                CropImage.startPickImageActivity(this);
            }
        });
        btn_Sharetoperson.setOnClickListener(v -> {
            if(btn_Pickfromgallery.getDrawable()!= null) {
                try {
                    String ShareText = et_description.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, ShareText);
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    BitmapDrawable drawable = (BitmapDrawable)btn_Pickfromgallery.getDrawable();
                    Bitmap bitmap =drawable.getBitmap();
                    File f =  new File(getExternalCacheDir()+"/"+getResources().getString(R.string.app_name)+".png");
                    FileOutputStream outputStream = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, "Please Choose an app to share with"));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        else
            {
                Toast.makeText(this, "Please select a photo then click share", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri resulturi = CropImage.getPickImageResultUri(this, data);
            CropImage.activity(resulturi).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).setAspectRatio(1,1).start(this);
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            btn_Pickfromgallery.setImageURI(result.getUri());
        }
    }
}