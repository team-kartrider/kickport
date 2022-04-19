package com.example.kickport;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private MapView mapView;
    private NaverMap naverMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    private Button btn_move;
    private boolean isMove = false;

    private static final String TAG = "Main_Activity";

    private ImageView ivMenu;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav;
    private Location mLastlocation = null;
    private double speed, calSpeed, getSpeed;

    // 센서이용-중력 제외
    private SensorManager sensorManager1;
    private android.hardware.Sensor senAccelerometer1;

    // 센서이용-중력 포함
    private SensorManager sensorManager2;
    private android.hardware.Sensor senAccelerometer2;

    // 센서이용-자이로 센서
    private SensorManager sensorManager3;
    private android.hardware.Sensor senGyroscope;
    
    private long lastUpdate = 0;
    private float LAimpulse, Aimpulse, Gimpulse;
    private float Gx, Gy, Gz, lastGx, lastGy, lastGz;
    private float LAx, LAy, LAz, lastLAx, lastLAy, lastLAz;
    private float Ax, Ay, Az, lastAx, lastAy, lastAz;


    private double IMPULSE_THRESHOLD = 50;
    private double FALLDOWN_THRESHOLD = 30;
    private int impulseCounter = 0;
    private int falldownCounter = 0;

    private Button resetTrigger;

//    // 충격 횟수 세기
//    TextView tImpulseCounter;
//    // 넘어짐 횟수 세기
//    TextView tfalldownCounter;

    private long backBtnTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
     
//         resetTrigger.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 // 모든 카운터 초기화
//                 impulseCounter = 0;
//                 falldownCounter = 0;
//                 tImpulseCounter.setText(String.valueOf(impulseCounter));
//                 tfalldownCounter.setText(String.valueOf(falldownCounter));
//             }
//         });


        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        // 위치를 반환하는 구현체인 FusedLocationSource 생성
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


        ivMenu=findViewById(R.id.iv_menu);
        drawerLayout=findViewById(R.id.main);
        toolbar=findViewById(R.id.toolbar);

        nav = findViewById(R.id.navigation_view);


        //액션바 변경하기(들어갈 수 있는 타입 : Toolbar type
        setSupportActionBar(toolbar);




        // 사고 신고 버튼 누른 경우 - 나중에 변수명 바꿀 것
        btn_move = (Button) findViewById(R.id.start);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Accident.class);
                startActivity(intent);
                finish();

                // naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


                /* 버튼 누를시 텍스트 변경
                if (isMove == false){
                    btn_move.setText("주행 종료");
                    isMove = true;
                }else{
                    btn_move.setText("주행 시작");
                    isMove = false;
                }
                */

            }
        });

        // 메뉴 버튼 누른 경우
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 클릭됨");
                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {

                    case R.id.menu_accident_report:
                        Log.d(TAG, "onNavigationItemSelected: 확인2");
                        break;

                    case R.id.menu_logout:
                        Intent intent = new Intent(MainActivity.this, Login.class);

                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.clear();
                        editor.commit();


                        // intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(intent);
                        finish();
                        break;

                }
                return false;
            }
        });

        // 센서 종류 설정 - linear acceleration sensor 이용(중력 제외)
        sensorManager1 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        senAccelerometer1 = sensorManager1.getDefaultSensor(android.hardware.Sensor.TYPE_LINEAR_ACCELERATION);

        // 센서 종류 설정 - accelerometer sensor 이용(중력 포함)
        sensorManager2 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        senAccelerometer2 = sensorManager2.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);

        // 센서 종류 설정 - gyroscope sensor 이용
        sensorManager3 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        senGyroscope = sensorManager3.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);

        // 센서리스너
        sensorManager1.registerListener( MainActivity.this, senAccelerometer1, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager2.registerListener( MainActivity.this, senAccelerometer2, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager3.registerListener( MainActivity.this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);

    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
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



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);

                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, id);
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        final TextView textview_address = (TextView)findViewById(R.id.address);
        /*
        final TextView textView_lat = findViewById(R.id.lat);
        final TextView textView_lon = findViewById(R.id.lon);
        */
        final TextView tvGetSpeed = findViewById(R.id.tvGetspeed);
        final TextView tvCalSpeed = findViewById(R.id.tvCalspeed);


        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {

                gpsTracker = new GpsTracker(MainActivity.this);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                double deltaTime = 0;

                // getSpeed() 함수를 이용하여 속도 계산(m/s -> km/h)
                getSpeed = Double.parseDouble(String.format("%.3f", location.getSpeed() * 3.6));

                // 위치 변경이 두번째로 변경된 경우 계산에 의해 속도 계산
                if(mLastlocation != null){
                    deltaTime = (location.getTime() - mLastlocation.getTime());
                    // 속도 계산(시간=ms, 거리=m -> km/h)
                    speed = (mLastlocation.distanceTo(location) / deltaTime) * 3600;
                    calSpeed = Double.parseDouble(String.format("%.3f", speed));
                }
                // 현재위치를 지난 위치로 변경
                mLastlocation = location;

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);


                textview_address.setText(address);

                String lat_str = Double.toString(latitude);
                String lon_str = Double.toString(longitude);

                /*
                textView_lat.setText(lat_str);
                textView_lon.setText(lon_str);
                 */

                String gs_str = Double.toString(getSpeed);
                String cs_str = Double.toString(calSpeed);

                tvGetSpeed.setText("구한 속도: " + gs_str);
                tvCalSpeed.setText("함수 속도: " + cs_str);


            }
        });

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleBarEnabled(true);
        uiSettings.setZoomControlEnabled(true);
        uiSettings.setLocationButtonEnabled(true);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
    
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.hardware.Sensor mySensor = sensorEvent.sensor;
        // 중력 제외인 경우
        if(mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            LAx = sensorEvent.values[0];
            LAy = sensorEvent.values[1];
            LAz = sensorEvent.values[2];
            LAimpulse = (float) Math.sqrt(Math.pow(LAx - lastLAx, 2)
                                        + Math.pow(LAy - lastLAy, 2)
                                        + Math.pow(LAz - lastLAz, 2));

            if (LAimpulse > IMPULSE_THRESHOLD){
                impulseCounter++;

            }

            long curTime = System.currentTimeMillis(); // 현재시간, ms
            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {

                lastUpdate = curTime;

                //갱신
                lastLAx = LAx;
                lastLAy = LAy;
                lastLAz = LAz;
            }
        }
        // 중력 포함인 경우
        else if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Ax = sensorEvent.values[0];
            Ay = sensorEvent.values[1];
            Az = sensorEvent.values[2];
            Aimpulse = (float) Math.sqrt(Math.pow(Ax - lastAx, 2)
                                        + Math.pow(Ay - lastAy, 2)
                                        + Math.pow(Az - lastAz, 2));

            if(Aimpulse > IMPULSE_THRESHOLD){
                impulseCounter++;
                if(impulseCounter == 2){
                    impulseCounter = 0;
                    Intent intent = new Intent(MainActivity.this, Accident.class);
                    startActivity(intent);
                }
            }

            long curTime = System.currentTimeMillis(); // 현재시간, ms
            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {

                lastUpdate = curTime;

                //갱신
                lastAx = Ax;
                lastAy = Ay;
                lastAz = Az;
            }

        }
        // 각속도 구하기
        else if(mySensor.getType() == Sensor.TYPE_GYROSCOPE){
            Gx = sensorEvent.values[0];
            Gy = sensorEvent.values[1];
            Gz = sensorEvent.values[2];
            Gimpulse = (float) Math.sqrt(Math.pow(Gx - lastGx, 2)
                                        + Math.pow(Gy - lastGy, 2)
                                        + Math.pow(Gz - lastGz, 2));

            if (Gimpulse > FALLDOWN_THRESHOLD){
                falldownCounter++;
            }

            long curTime = System.currentTimeMillis(); // 현재시간, ms
            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {

                lastUpdate = curTime;

                //갱신
                lastGx = Gx;
                lastGy = Gy;
                lastGz = Gz;
            }
        }

        if(impulseCounter > 0 || falldownCounter > 0){
            Intent intent = new Intent(MainActivity.this, Accident.class);
            startActivity(intent);
            // 초기화
            impulseCounter = 0;
            falldownCounter = 0;
        }
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다",Toast.LENGTH_SHORT).show();
        }
    }

}