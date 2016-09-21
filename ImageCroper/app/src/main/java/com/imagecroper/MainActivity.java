package com.imagecroper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.imagecroper.cropper.ChooserActivity;
import com.imagecroper.utils.AppConfig;

public class MainActivity extends AppCompatActivity {

    ImageView imgDemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgDemo= (ImageView) findViewById(R.id.imgDemo);
        imgDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ChooserActivity.class);
                intent.putExtra("isFullScreen",false);
//                intent.putExtra("className",getClass());
                startActivityForResult(intent, AppConfig.RequestCode.REQUEST_CODE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.RequestCode.REQUEST_CODE) {
            if (resultCode == AppConfig.ResultCode.RESULT_OK) {
                if (null != data) {

                    // fetch the message String

                    Bitmap bitmap = (Bitmap) data.getParcelableExtra("ImageBitmap");

                    imgDemo.setImageBitmap(bitmap);

                }
            } else if (resultCode == AppConfig.ResultCode.RESULT_CANCEL) {

            }
        }
    }
}
