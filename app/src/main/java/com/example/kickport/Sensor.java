package com.example.kickport;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Sensor extends AppCompatActivity implements SensorEventListener {

    // 센서이용-중력 제외
    private SensorManager sensorManager1;
    private android.hardware.Sensor senAccelerometer1;

    // 센서이용-중력 포함
    private SensorManager sensorManager2;
    private android.hardware.Sensor senAccelerometer2;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
//    private double IMPULSE_THRESHOLD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);


        sensorManager1 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        senAccelerometer1 = sensorManager1.getDefaultSensor(android.hardware.Sensor.TYPE_LINEAR_ACCELERATION);
        // 센서 종류 설정 - linear acceleration sensor 이용(중력 제외)

        sensorManager2 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        senAccelerometer2 = sensorManager2.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        // 센서 종류 설정 - accelerometer sensor 이용(중력 포함)




        sensorManager1.registerListener( Sensor.this, senAccelerometer1, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager2.registerListener( Sensor.this, senAccelerometer2, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.hardware.Sensor mySensor = sensorEvent.sensor;

        // 중력 제외
        final TextView tlx = findViewById(R.id.lx);
        final TextView tly = findViewById(R.id.ly);
        final TextView tlz = findViewById(R.id.lz);

        // 중력 포함
        final TextView tx = findViewById(R.id.x);
        final TextView ty = findViewById(R.id.y);
        final TextView tz = findViewById(R.id.z);

        // 중력 제외인 경우
        if(mySensor.getType() == android.hardware.Sensor.TYPE_LINEAR_ACCELERATION){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            String x_str = Float.toString(x);
            String y_str = Float.toString(y);
            String z_str = Float.toString(z);
            tlx.setText("x: " + x_str);
            tly.setText("y: " + y_str);
            tlz.setText("z: " + z_str);

            Log.v("linear acceleration sensor x", x_str);
            Log.v("linear acceleration sensor y", y_str);
            Log.v("linear acceleration sensor z", z_str);

            long curTime = System.currentTimeMillis(); // 현재시간, ms


            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {
//                System.currentTimeMillis()의 단위가 ms 이므로 m/(s^2) 단위에 맞게 second로 변경
                long diffTime = (curTime - lastUpdate)/1000;
                lastUpdate = curTime;

//                가속도 값 업데이트와 동시에 시간당 충격량을 계산. 충격량 = F*t = (a*m)*t = (m/(s^2))*kg*(ms/1000), 질량 m은 임의로 1로 설정
                double impulse = (double)z*1*diffTime;

                Log.v("linear acceleration impulse", Double.toString(impulse));

                //갱신
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

        // 중력 포함인 경우
        else if(mySensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            String x_str = Float.toString(x);
            String y_str = Float.toString(y);
            String z_str = Float.toString(z);
            tx.setText("x: " + x_str);
            ty.setText("y: " + y_str);
            tz.setText("z: " + z_str);

            Log.v("accelerometer sensor x", x_str);
            Log.v("accelerometer sensor y", y_str);
            Log.v("accelerometer sensor z", z_str);


            long curTime = System.currentTimeMillis(); // 현재시간


            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {
//                System.currentTimeMillis()의 단위가 ms 이므로 m/(s^2) 단위에 맞게 second로 변경
                long diffTime = (curTime - lastUpdate)/1000;
                lastUpdate = curTime;

//                가속도 값 업데이트와 동시에 시간당 충격량을 계산. 충격량 = F*t = (a*m)*t = (m/(s^2))*kg*(ms/1000), 질량 m은 임의로 1로 설정
                double impulse = (double)z*1*diffTime;

                Log.v("accelerometer impulse", Double.toString(impulse));

                //갱신
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}