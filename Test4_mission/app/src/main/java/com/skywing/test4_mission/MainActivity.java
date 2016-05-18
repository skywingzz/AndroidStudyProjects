package com.skywing.test4_mission;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = (ImageView)findViewById(R.id.view1);
        view2 = (ImageView)findViewById(R.id.view2);



    }

    public void up(View view) {
        view1.setImageResource(R.drawable.image1);
        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.INVISIBLE);
    }

    public void down(View view) {
        view2.setImageResource(R.drawable.image1);
        view2.setVisibility(View.VISIBLE);
        view1.setVisibility(View.INVISIBLE);
    }

}
