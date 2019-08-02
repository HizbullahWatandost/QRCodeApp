package com.aps.qrcode.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.aps.qrcode.R;


public class TestActivity extends AppCompatActivity {

    TextView txtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        txtView = (TextView)findViewById(R.id.qr_content);

        String qrTxt = getIntent().getStringExtra("qrImgDetails");

        txtView.setText(qrTxt);

    }
}
