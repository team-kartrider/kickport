package com.example.kickport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Accident extends AppCompatActivity {

    // 사고 감지 - 아니오 버튼
    private Button btn_no;

    // 사고 감지 - 신고 버튼(누르면 전화 및 문자 기능으로 활성화 해야 함)
    private Button btn_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accident_detection);

        // 아니오 버튼 누른 경우
        btn_no = (Button) findViewById(R.id.accident_check);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Accident.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        // 사고 신고 버튼 누른 경우
        btn_report = (Button) findViewById(R.id.accident_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Intent intent = new Intent(Accident.this, MainActivity.class);
                startActivity(intent);
                finish();
                */

            }
        });
    }
}