/*
package com.example.kickport.PermissionSetting;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {



    private Context context;
    private Activity activity;
    private boolean check = false;



    //요청할 권한 배열로 저장
    private String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,

    };

    private List permissionList;

    // 권한 요청을 할 때 발생하는 창에 대한 결과값을 받기 위해 지정해주는 int형
    private final int MULTIPLE_PERMISSIONS = 1023;

    //생성자에서 Activity와 Context 파라미터로 받기
    public PermissionSupport(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    // 허용 받아야할 권한이 남아있는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        //배열로 선언한 권한 중 허용되지 않은 권한 여부 체크
        for(String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if(result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }

        if(!permissionList.isEmpty()){
            return false;
        }
        return true;
    }

    //권한 허용 요청
    public void requestPermission() {

        if(check) {

        } else {
            ActivityCompat.requestPermissions(activity, (String[]) permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
        }


    }

    //권한 요청에 대한 결과 처리
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        //requestCode 가 아까 final로 선언하였던 숫자와 맞는지, 결과값의 길이가 0보다 큰지 체크
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.length > 0)) {
            for(int i = 0; i < grantResults.length; i++) {
                //grantResults가 0 이면 사용자가 허용 / -1이면 거부
                // -1이 있는지 체크하여 하나라도 -1이면 false 리턴
                if(grantResults[i] == -1) {
                    check = false;
                    return false;
                }
            }
        }

        if(ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_BACKGROUND_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            check = true;
            return false;
        }

        return true;
    }

}
*/