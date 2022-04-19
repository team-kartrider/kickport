package com.example.kickport.mysql;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AccidentRequest extends StringRequest {

   //서버 URL 설정(php 파일 연동)
   final static private String URL = "http://183.101.85.213:8645/Accident.php";
   private Map<String, String> map;
   //private Map<String, String>parameters;

   public AccidentRequest(String AccidentType, Response.Listener<String> listener) {
      super(Method.POST, URL, listener, null);

      map = new HashMap<>();
      map.put("accident_type", AccidentType);

   }

   @Override
   protected Map<String, String>getParams() throws AuthFailureError {
      return map;
   }
}
