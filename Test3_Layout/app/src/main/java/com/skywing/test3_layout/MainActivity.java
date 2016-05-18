package com.skywing.test3_layout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    int viewImgNo = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        iv1 = (ImageView)findViewById(R.id.iv1);
//        iv2 = (ImageView)findViewById(R.id.iv2);

    }

    public void changeImg(View view) {
        if(viewImgNo == 1) {
//            iv1.set
        }
    }
}
