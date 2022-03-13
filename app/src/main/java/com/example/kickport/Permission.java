package com.example.kickport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kickport.PermissionSupport;

public class Permission extends AppCompatActivity {

    Button ok;

    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        permissionCheck();

        ok = (Button) findViewById(R.id.permission_bt);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Permission.this, Login.class);
                startActivity(intent);
                finish();

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

        /*
        String message, title, button;


        message = "백그라운드 위치 권한을 위해 '항상 허용' 으로 설정해주세요.";
        title = "권한 설정";
        button = "예";

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            AlertDialog.Builder oDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

            oDialog.setMessage(message).setTitle(title).setPositiveButton(button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    ActivityCompat.requestPermissions( Permission.this, new String[] { Manifest.permission.ACCESS_BACKGROUND_LOCATION }, 0 );

                }
            })
                    //백버튼으로 팝업창이 닫히지 않도록 함
                    .setCancelable(false)
                    .show();

        } */

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Permission.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions( Permission.this, new String[] { Manifest.permission.ACCESS_BACKGROUND_LOCATION }, 0 );
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}