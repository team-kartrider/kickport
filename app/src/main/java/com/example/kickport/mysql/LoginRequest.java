package com.example.kickport.mysql;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

   //서버 URL 설정(php 파일 연동)
   final static private String URL = "http://183.101.85.213:8645/Login.php";
   private Map<String, String> map;

   public LoginRequest(String UserEmail, String UserPwd, Response.Listener<String> listener) {
      super(Method.POST, URL, listener, null);

      map = new HashMap<>();
      map.put("user_id", UserEmail);
      map.put("user_password", UserPwd);
   }

   @Override
   protected Map<String, String>getParams() throws AuthFailureError {
      return map;
   }
}
