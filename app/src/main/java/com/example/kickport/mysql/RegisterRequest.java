package com.example.kickport.mysql;

        import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    //서버 URL 설정(php 파일 연동)
    final static private String URL = "http://183.101.85.213:8645/Register.php";
    private Map<String, String> map;
    //private Map<String, String>parameters;

    public RegisterRequest(String user_name, String user_id, String user_password, String user_year, String user_month, String user_day, String user_phone, String user_guardphone, String user_insurance, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_name", user_name);
        map.put("user_id", user_id);
        map.put("user_password", user_password);
        map.put("user_year", user_year);
        map.put("user_month", user_month);
        map.put("user_day", user_day);
        map.put("user_phone", user_phone);
        map.put("user_guardphone", user_guardphone);
        map.put("user_insurance", user_insurance);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}