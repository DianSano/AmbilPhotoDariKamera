package com.jodemy.ambilphotodarikamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 100;

    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "lokasi photo: ";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000;
    ImageView imageView;
    ImageButton imageButton;
    String currentPhotoPath;
    String imageName;
    String picturePath;

    Bitmap bitmap;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView= findViewById(R.id.imageView);
        imageButton = findViewById(R.id.imageButton);

            File fileFromCamera = new File(getExternalStorageDirectory() +
                    "/Android/data/com.jodemy.ambilphotodarikamera/files/Pictures" + "/profile.jpg");
            if(fileFromCamera.exists()){
            //Do something
                currentPhotoPath = fileFromCamera.getAbsolutePath();
                setPicCamera();
            } else {
                File fileFromGallery = new File(getExternalStorageDirectory() + "/DCIM/Camera/fromGallery.jpg");
                if (fileFromGallery.exists()) {
                    //Do something
                    Toast.makeText(this, "File ada", Toast.LENGTH_SHORT).show();
                    picturePath = fileFromGallery.getAbsolutePath();
                    setInitGallery();
                } else {
                    Toast.makeText(this, "File tidak ada " + picturePath, Toast.LENGTH_LONG).show();
                }
            }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent();
                showPictureDialog();
            }
        });

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                pickFromGallery();
                                break;
                            case 1:
                                dispatchTakePictureIntent();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    Log.i("URI", "URInya apa:" + selectedImage);
                    picturePath = getRealPathFromURI(selectedImage, this);
                    //imageView.setImageURI(selectedImage);
                    Log.i(TAG, "setPic00: " + picturePath + "\n");
                    setPicGallery();
                    break;

                case REQUEST_TAKE_PHOTO:
                    setPicCamera();
                    break;
            }
    }

    private void setInitGallery() {

        bitmap = BitmapFactory.decodeFile(picturePath);
        //Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        Log.i(TAG, "setPic: " + picturePath + "\n");


        imageView.setImageBitmap(bitmap);
    }

    private void setPicCamera() {

            bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            //Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            Log.i(TAG, "setPic: " + currentPhotoPath + "\n");
            Log.i(TAG, "setPic: " + getExternalStorageDirectory() +
                    "/Android/data/com.jodemy.ambilphotodarikamera/files/Pictures" + "/profile.jpg");

            imageView.setImageBitmap(bitmap);
            setFileNameFromGalleryNonActive("nonActive");

    }

    private void setFileNameFromGalleryNonActive(String nonActive) {
        File fileFG = new File(getExternalStorageDirectory() + "/DCIM/Camera/" + imageName);
        if(fileFG.exists()){
            //Do something
            //currentPhotoPath = fileFC.getAbsolutePath();
            //setPic();

            File to        = new File(getExternalStorageDirectory() + "/DCIM/Camera/" + nonActive + ".jpg");
            fileFG.renameTo(to);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }*/

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "profile";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            ex.getMessage();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.jodemy.ambilphotodarikamera.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

    private void setPicGallery() {
        if (!TextUtils.isEmpty(picturePath)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                //Toast.makeText(this, "Tidak ada ijin", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "setPic0: " + picturePath + "\n");
            }

            File f = new File(picturePath);
            imageName = f.getName();
            Log.i(TAG, "setPic15: " + picturePath + "\n");
            if (f.exists()) {
                bitmap = BitmapFactory.decodeFile(picturePath);
            } else {
                Log.i(TAG, "setPic22: File tidak ada");
            }

           // setFileName("fromGallery");
            Log.i(TAG, "setPic1: " + picturePath + "\n");
           // Log.i(TAG, "setPic2: " + getExternalStorageDirectory() + "/DCIM/Camera/" + imageName);
            Log.i(TAG, "setPic3: " + imageName);


        }

        imageView.setImageBitmap(bitmap);
        Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();
        setFileNameFromCamera("fromCamera");
        setFileNameFromGallery(picturePath, "fromGallery");
    }


    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

    private void setFileNameFromCamera(String fileBaru) {
        File fileFC = new File(getExternalStorageDirectory() +
                "/Android/data/com.jodemy.ambilphotodarikamera/files/Pictures" + "/profile.jpg");
        if (fileFC.exists()) {
            //Do something
            currentPhotoPath = fileFC.getAbsolutePath();
            //setPic();

            File to = new File(getExternalStorageDirectory() +
                    "/Android/data/com.jodemy.ambilphotodarikamera/files/Pictures/" + fileBaru + ".png");
            fileFC.renameTo(to);
        }
    }
    private void setFileNameFromGallery(String namaLama, String namaBaru) {
        File fileFG = new File(namaLama);
        if(fileFG.exists()){
            //Do something
            //currentPhotoPath = fileFC.getAbsolutePath();
            //setPic();

            File to        = new File(getExternalStorageDirectory() + "/DCIM/Camera/" + namaBaru + ".jpg");
            fileFG.renameTo(to);
        }

    }

}
