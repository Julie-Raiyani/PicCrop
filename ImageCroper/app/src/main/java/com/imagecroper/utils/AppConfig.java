package com.imagecroper.utils;

import android.Manifest;

public class AppConfig {

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

    public interface PermissionCode {
        int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
        int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    }

    public interface PermissionType {
        String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
        String CAMERAE = Manifest.permission.CAMERA;
    }

    public  interface RequestCode{
        public static int REQUEST_CODE=10;

    }

    public  interface ResultCode{
        public static int RESULT_OK=11;
        public static int RESULT_CANCEL=12;
    }
}



