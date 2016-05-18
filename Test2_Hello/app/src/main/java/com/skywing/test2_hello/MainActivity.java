package com.skywing.test2_hello;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button)findViewById(R.id.b1);
        final TextView tv = (TextView)findViewById(R.id.tv);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tv.append("\n버튼이 눌렸습니다");
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

            }
        });

/*
        LinearLayout layout = new LinearLayout(this);
        Button b = new Button(this);
        b.setText("전송");
        layout.addView(b);
        setContentView(layout);
*/
    }

    public void move11(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.11st.co.kr"));
        startActivity(intent);
    }

    public void call(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-4664-5371"));
        startActivity(intent);
    }

}
