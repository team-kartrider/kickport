/*package com.example.kickport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kickport.PermissionSetting.PermissionSupport;

public class Permission extends AppCompatActivity {

    Button ok;
    String test = "test";

    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        ok = (Button) findViewById(R.id.permission_bt);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                permissionCheck();

            }
        });

    }
    private void permissionCheck(){

        //SDK 23버전 이하 버전은 Permission 필요 X
        if(Build.VERSION.SDK_INT >= 23){

            permission = new PermissionSupport(this, this);

            //권한 체크 후에 리턴이 false로 들어오면
            if(!permission.checkPermission()) {
                //권한 요청
                permission.requestPermission();

                //위치 항상 허용을 위한것, 포그라운드 이용 안하면 사용 X
                permissionDialog();

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!permission.permissionResult(requestCode, permissions, grantResults)){
            permission.requestPermission();
        }

    }

    //위치 항상 허용 다이얼로그
    public void permissionDialog(){

        String message, title, button;


        message = "백그라운드 위치 권한을 위해 '항상 허용' 으로 설정해주세요.";
        title = "권한 설정";
        button = "예";

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

            oDialog.setMessage(message).setTitle(title).setPositiveButton(button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    ActivityCompat.requestPermissions( Permission.this, new String[] { Manifest.permission.ACCESS_BACKGROUND_LOCATION }, 0 );

                }
            })
                    //백버튼으로 팝업창이 닫히지 않도록 함
                    .setCancelable(false)
                    .show();

        }
    }
}

 */