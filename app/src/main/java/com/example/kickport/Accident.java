package com.example.kickport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kickport.mysql.AccidentRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Accident extends AppCompatActivity {

    // 사고 감지 - 아니오 버튼
    private Button btn_no;

    // 사고 감지 - 신고 버튼(누르면 전화 및 문자 기능으로 활성화 해야 함)
    private Button btn_report;

    // 카운트
    private TextView count;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accident_detection);

        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String UserName = sharedPreferences.getString("name", "");
        String UserEmail = sharedPreferences.getString("email", "");

        GpsTracker gpsTracker = new GpsTracker(Accident.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        // 사고 유형 구하기 + 다른 값(아이디/이름 등) 넣어 주는 것 진행하기
        String AccidentNumber = getNumber(); // 사고 번호 - 아이디 + 사고 날짜 및 시간
        String AccidentDate = getDate(); // 사고 날짜 및 시간
        String AccidentType = sharedPreferences.getString("accident_type", ""); // 사고 유형
        String AccidentPlace = getCurrentAddress(latitude, longitude); // 사고 장소


        // 카운트 다운 객체
        CountDownTimer countDownTimer;
        
        // 카운트 값
        count = findViewById(R.id.count);

        // 카운트 다운 시작
        countDownTimer = new CountDownTimer(30000, 1000) { // 30초 동안 1초의 간격으로 onTick 메소드를 호출
            @Override
            public void onTick(long millisUntilFinished) {
                count.setText("자동 신고: " + millisUntilFinished / 1000 + " 초 남았습니다.");
            } // 1초마다 호출되면서 남은 시간을 초 단위로 보여 줌. 30, 29, 28..
            @Override
            public void onFinish() {
                count.setText("자동 신고가 진행됩니다.");

                // DB로 값 보내기
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                count.setText("자동 신고가 완료되었습니다.");
                                Intent intent = new Intent(Accident.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                AccidentRequest accidentRequest = new AccidentRequest(UserName, UserEmail, AccidentNumber, AccidentDate, AccidentType, AccidentPlace, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Accident.this);
                queue.add(accidentRequest);

            } //종료 되었을 때 진행
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

                // DB로 값 보내기
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                count.setText("신고가 완료되었습니다.");
                                Intent intent = new Intent(Accident.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                AccidentRequest accidentRequest = new AccidentRequest(UserName, UserEmail, AccidentNumber, AccidentDate, AccidentType, AccidentPlace, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Accident.this);
                queue.add(accidentRequest);

            }
        });
    }

    private String getNumber() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Accident.this, MainActivity.class);
        startActivity(intent);
    }
}