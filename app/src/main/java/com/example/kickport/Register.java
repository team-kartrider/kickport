package com.example.kickport;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kickport.mysql.RegisterRequest;
import com.example.kickport.mysql.ValidateRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText join_name, join_email, join_password, join_pwck, join_phone, join_guardphone;
    private TextView join_pwckmsg;
    private Spinner join_year, join_month, join_day, join_insurance;
    private AlertDialog dialog;
    private boolean validate = false;
    private Button check_email, join_end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        join_name = findViewById(R.id.join_name);
        join_email = findViewById( R.id.join_email );
        join_password = findViewById( R.id.join_password );
        join_pwck = findViewById(R.id.join_pwdcheck);

        join_pwckmsg = findViewById(R.id.join_check_password);

        join_year = findViewById(R.id.join_year);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.test, android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_year.setAdapter(yearAdapter);

        join_month = findViewById(R.id.join_month);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.test, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_month.setAdapter(monthAdapter);

        join_day = findViewById(R.id.join_day);
        ArrayAdapter dayAdapter = ArrayAdapter.createFromResource(this, R.array.test, android.R.layout.simple_spinner_dropdown_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_day.setAdapter(dayAdapter);

        /* 리스너 등록
        join_year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        */

        join_phone = findViewById(R.id.join_phone);
        join_guardphone = findViewById(R.id.join_guardian_phone);

        join_insurance = findViewById(R.id.join_insurance);
        ArrayAdapter insuranceAdapter = ArrayAdapter.createFromResource(this, R.array.test, android.R.layout.simple_spinner_dropdown_item);
        insuranceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        join_insurance.setAdapter(insuranceAdapter);


        //분기점


        //아이디 중복 체크
        check_email = findViewById(R.id.check_email);
        check_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String UserEmail = join_email.getText().toString();
                if (validate) {
                    return; //검증 완료
                }

                if (UserEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                join_email.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(UserEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(validateRequest);
            }
        });

        //회원가입 버튼 클릭 시 수행
        join_end = findViewById( R.id.join_end );
        join_end.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UserName = join_name.getText().toString();
                final String UserEmail = join_email.getText().toString();
                final String UserPwd = join_password.getText().toString();
                final String PassCk = join_pwck.getText().toString();
                final String UserYear = join_year.getSelectedItem().toString();
                final String UserMonth = join_month.getSelectedItem().toString();
                final String UserDay = join_day.getSelectedItem().toString();
                final String UserPhone = join_phone.getText().toString();
                final String UserGuardPhone = join_guardphone.getText().toString();
                final String UserInsurance = join_insurance.getSelectedItem().toString();

                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우

                if (UserEmail.equals("") || UserPwd.equals("") || UserName.equals("") || UserYear.equals("") || UserMonth.equals("") || UserDay.equals("") || UserPhone.equals("") || UserGuardPhone.equals("")|| UserInsurance.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            //회원가입 성공시
                            if(UserPwd.equals(PassCk)) {
                                if (success) {

                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserName), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);

                                    //회원가입 실패시
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //서버로 Volley를 이용해서 요청
                RegisterRequest registerRequest = new RegisterRequest( UserName, UserEmail, UserPwd, UserYear, UserMonth, UserDay, UserPhone, UserGuardPhone, UserInsurance, responseListener);
                RequestQueue queue = Volley.newRequestQueue( Register.this );
                queue.add( registerRequest );
            }
        });
    }
}