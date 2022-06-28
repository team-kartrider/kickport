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

   public AccidentRequest(String user_name, String user_id, String accident_number, String accident_date, String accident_type, String accident_place, Response.Listener<String> listener) {
      super(Method.POST, URL, listener, null);

      map = new HashMap<>();
      map.put("user_name", user_name);
      map.put("user_id", user_id);
      map.put("accident_number", accident_number);
      map.put("accident_date", accident_date);
      map.put("accident_type", accident_type);
      map.put("accident_place", accident_place);

   }

   @Override
   protected Map<String, String>getParams() throws AuthFailureError {
      return map;
   }
}
