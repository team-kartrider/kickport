package com.example.kickport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Accident extends AppCompatActivity {

    // 사고 감지 - 아니오 버튼
    private Button btn_no;

    // 사고 감지 - 신고 버튼(누르면 전화 및 문자 기능으로 활성화 해야 함)
    private Button btn_report;

    // 카운트
    private TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accident_detection);

        // 카운트 다운 객체
        CountDownTimer countDownTimer;
        
        // 카운트 값
        count = findViewById(R.id.count);

        // 카운트 다운 시작
        countDownTimer = new CountDownTimer(30000, 1000) { // 30초 동안 1초의 간격으로 onTick 메소드를 호출
            @Override
            public void onTick(long millisUntilFinished) {
                count.setText("자동 신고: " + millisUntilFinished / 1000 + " 초 남았습니다.");
            } //1초마다 호출되면서 남은 시간을 초 단위로 보여 줍니다. 30, 29, 28.. 이런식으로 나타나게 됩니다
            @Override
            public void onFinish() {
                count.setText("자동 신고가 완료되었습니다.");
            } //종료 되었을 때 문자열을 보여줍니다
        }.start(); //카운트 시작

        // 카운트 다운 종료
        // countDownTimer.onFinish();
        // countDownTimer.cancel();

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