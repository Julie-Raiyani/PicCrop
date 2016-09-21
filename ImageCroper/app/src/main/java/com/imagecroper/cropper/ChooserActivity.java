package com.imagecroper.cropper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imagecroper.MainActivity;
import com.imagecroper.R;
import com.imagecroper.utils.AppConfig;
import com.imagecroper.utils.InternalStorageContentProvider;
import com.imagecroper.utils.PermissionUtility;
import com.imagecroper.utils.ScalingUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChooserActivity extends AppCompatActivity {

    private boolean isSetPhoto;
    Context context;
    File mFileTemp;
    private Uri outputFileUri;
    int DEFAULT_HEIGHT_WIDTH = 200;
    ImageView img;
    boolean isFullScreenCrop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        context = ChooserActivity.this;
        imageSelectionFrom(getIntent().getBooleanExtra("isFullScreen", false));
    }


    public void imageSelectionFrom(boolean isFullScreenCrop) {

        this.isFullScreenCrop = isFullScreenCrop;
        isSetPhoto = false;
        final Dialog dialogView = new Dialog(context);
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView.setContentView(R.layout.custom_dialog);
        TextView _txtDialogTitle = (TextView) dialogView.findViewById(R.id.txtDialogTitle);
        LinearLayout camre = (LinearLayout) dialogView.findViewById(R.id.lnrCamera);
        LinearLayout Gallery = (LinearLayout) dialogView.findViewById(R.id.lnrGallery);
        Button cancle = (Button) dialogView.findViewById(R.id.btnCancle);


        camre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();

                if (isCameraAvailable(context)) {
                    final boolean result = PermissionUtility.checkPermission(context, AppConfig.PermissionType.CAMERAE,
                            AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_CAMERA, "Camera permission necessary");
                    if (result) {
                        takePicture();
                    }

                } else {
                    Toast.makeText(ChooserActivity.this, "Camera error", Toast.LENGTH_LONG).show();

                }
            }
        });


        Gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialogView.dismiss();
                final boolean result = PermissionUtility.checkPermission(context, AppConfig.PermissionType.READ_EXTERNAL_STORAGE,
                        AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, "External storage  access permission necessary");
                if (result) {

                    openGallery();
                }
            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView.dismiss();
                finish();
            }
        });
        dialogView.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    //code for deny
                }
                break;

            case AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    //code for deny
                }
                break;
        }
    }


    public void takePicture() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), AppConfig.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), AppConfig.TEMP_PHOTO_FILE_NAME);
        }

        try {

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                outputFileUri = Uri.fromFile(mFileTemp);
            } else {
/*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
                outputFileUri = InternalStorageContentProvider.CONTENT_URI;
            }


        } catch (Exception e) {

        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_CAMERA);


    }

    public void openGallery() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), AppConfig.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), AppConfig.TEMP_PHOTO_FILE_NAME);
        }
        outputFileUri = Uri.fromFile(mFileTemp);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        // photoPickerIntent.setType("image*/");
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    private void startCropImage() {
        startCropImageActivity(outputFileUri);
    }


    public int getdevicewidth() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    private void startCropImageActivity(Uri imageUri) {


        if (isFullScreenCrop) {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropWindowSize(getdevicewidth(), (int) getResources().getDimension(R.dimen.placeholder_height))
                    .setAspectRatio(getdevicewidth(), (int) getResources().getDimension(R.dimen.placeholder_height))
                    .setFixAspectRatio(true)
                    .start((Activity) context);
        } else {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropWindowSize(400, 400)
                    .setAspectRatio(400, 400)
                    .setFixAspectRatio(true)
                    .start((Activity) context);
        }


    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {

            return;
        }

        try {

            InputStream inputStream = getContentResolver().openInputStream(data.getData());
            OutputStream fileOutputStream = new FileOutputStream(mFileTemp);
            copyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();

        } catch (Exception e) {
            Log.e("", "Error while creating temp file", e);
        }
        Bitmap bitmap = null;

        switch (requestCode) {

            case AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                try {
                    startCropImage();
                } catch (Exception e) {
                    Log.e("", "Error while creating  file", e);
                }

                break;
            case AppConfig.PermissionCode.MY_PERMISSIONS_REQUEST_CAMERA:
                startCropImage();
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                String path = mFileTemp.getPath();
                if (path == null) {

                    return;
                }
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {


                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                        if (!(bitmap.getWidth() <= DEFAULT_HEIGHT_WIDTH && bitmap.getHeight() <= DEFAULT_HEIGHT_WIDTH)) {
                            bitmap = ScalingUtilities.createScaledBitmap(bitmap, DEFAULT_HEIGHT_WIDTH, DEFAULT_HEIGHT_WIDTH, ScalingUtilities.ScalingLogic.FIT);
                        }
                        //((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());

                        // this.img.setImageBitmap(bitmap);

                        Intent myIntent = new Intent(context, MainActivity.class);
                        myIntent.putExtra("ImageBitmap", bitmap);
                        setResult(AppConfig.ResultCode.RESULT_OK, myIntent);
                        finish();
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }


    public boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}
