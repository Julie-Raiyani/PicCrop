package com.imagecroper.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by SAGAR on 6/5/2016.
 */
public class PermissionUtility {

    public static boolean checkPermission(final Context context, final String permissionType, final int permissionCode, String permissionMsg) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        //check device os is marshmallow or up
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permissionType) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionType)) {
                    showAlertDialog(context, "Permission necessary", permissionMsg, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{permissionType}, permissionCode);
                        }
                    });

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permissionType}, permissionCode);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    static void showAlertDialog(Context context, String title, String msg, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(msg);
        alertBuilder.setPositiveButton(android.R.string.yes, clickListener);
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
