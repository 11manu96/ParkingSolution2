package com.android.parking.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.parking.R;
import com.android.parking.Utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by manumaheshwari on 8/2/16.
 */
public class AddParkingLotActivity extends AppCompatActivity{

    Context context;
    ImageView mapshot;
    ImageView lotPictureImageView;
    String selectedImagePath;
    String[] permsCamera = {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    int permsRequestCodeCamera = 200;
    String[] permsGallery = {"android.permission.READ_EXTERNAL_STORAGE"};
    int permsRequestCodeGallery = 201;


    Button choosePictureButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.add_lot_layout);

        Bundle bundle = getIntent().getExtras();
        String image = bundle.getString("mapScreenshot");
        mapshot = (ImageView) findViewById(R.id.map_screenshot_imageview);
        lotPictureImageView = (ImageView) findViewById(R.id.parkinglot_photo);
        choosePictureButton = (Button) findViewById(R.id.choose_picture_button);
        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();

            }
        });




        new LoadImage(mapshot).execute(image);


        context = this;

        //setToolBar();
    }



    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Bitmap bitmap;
        ImageView img;

        public LoadImage(ImageView imageView){
            this.img = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);

            }else{

                Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void choosePicture() {
        final CharSequence[] items = {"From Gallery", "Take Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Picture");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                if (canMakeSmores()) {
                                    if (hasPermission(permsGallery[0])) {
                                        fireGalleryIntent();
                                    } else {
                                        requestPermissions(permsGallery, permsRequestCodeGallery);
                                    }
                                } else {
                                    fireGalleryIntent();
                                }
                                break;
                            case 1:
                                if (canMakeSmores()) {
                                    if (hasPermission(permsCamera[0]) && hasPermission(permsCamera[1])) {
                                        fireCameraIntent();
                                    } else {
                                        requestPermissions(permsCamera, permsRequestCodeCamera);
                                    }
                                } else {
                                    fireCameraIntent();
                                }
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("fdfdfdfd3232fdfdf: " + requestCode + " ; " + resultCode);
        Bitmap bitmapUploadId;
        if (requestCode == 100) {
            try {
                if (data != null && data.hasExtra("data")) {

                    Uri selectedImageUri = getImageUri(this, (Bitmap) data.getExtras().get("data"));

                    String[] projection = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImageUri, projection, null,
                            null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    selectedImagePath = cursor.getString(column_index);

                    if (selectedImagePath != null & selectedImagePath.length() > 0) {

                        Bitmap bm;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(selectedImagePath, options);

                        final int REQUIRED_SIZE;
                        REQUIRED_SIZE = 300;
                        int scale = 1;
                        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                            scale *= 2;
                        options.inSampleSize = scale;
                        options.inJustDecodeBounds = false;
                        bm = BitmapFactory.decodeFile(selectedImagePath, options);
                        lotPictureImageView.setImageBitmap(bm);
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101) {
            try {
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    selectedImagePath = imgDecodableString;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imgDecodableString, options);
                    final int REQUIRED_SIZE;
                    REQUIRED_SIZE = 300;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory
                            .decodeFile(imgDecodableString, options);
                    lotPictureImageView.setImageBitmap(bitmap);
                    cursor.close();
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private  Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void fireGalleryIntent() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    private void fireCameraIntent() {
        Intent intent1 = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(intent1, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        System.out.println("sdsdsdsds 1 " + permissions.length + ", " + permissions[0] + ", " + grantResults[0]);
        if (permissions.length == 1) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fireGalleryIntent();
            }
        } else {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.CAMERA) && permissions[1].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fireCameraIntent();
            }
        }
    }
    public boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private boolean hasPermission(String permission) {
        return (this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }



}
