package com.example.navermaptest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 간단한 텍스트를 표시
        TextView textView = findViewById(R.id.text_detail);
        textView.setText("Detail Activity 실행됨");
    }
}