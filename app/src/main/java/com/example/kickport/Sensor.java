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

    // 센서이용-자이로 센서
    private SensorManager sensorManager3;
    private android.hardware.Sensor senGyroscope;

    private long lastUpdate = 0;
    private float last_tlx, last_tly, last_tlz;
    private float last_lx, last_ly, last_lz;
    private float last_gx, last_gy, last_gz;
//    private double IMPULSE_THRESHOLD;
    private int impulseCounter = 0;

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

        sensorManager3 = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        senGyroscope = sensorManager3.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);


        sensorManager1.registerListener( Sensor.this, senAccelerometer1, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager2.registerListener( Sensor.this, senAccelerometer2, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager3.registerListener( Sensor.this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.hardware.Sensor mySensor = sensorEvent.sensor;

        // 중력 제외
        final TextView tlx = findViewById(R.id.lx);
        final TextView tly = findViewById(R.id.ly);
        final TextView tlz = findViewById(R.id.lz);
        final TextView tlImpulse = findViewById(R.id.limpulse);

        // 충격 횟수 세기
        final TextView tImpulseCounter = findViewById(R.id.impulseCnt);

        // 중력 포함
        final TextView tx = findViewById(R.id.x);
        final TextView ty = findViewById(R.id.y);
        final TextView tz = findViewById(R.id.z);
        final TextView tImpulse = findViewById(R.id.impulse);

        // 각속도
        final TextView gx = findViewById(R.id.gx);
        final TextView gy = findViewById(R.id.gy);
        final TextView gz = findViewById(R.id.gz);
        final TextView gImpulse = findViewById(R.id.gimpulse);

        // 중력 제외인 경우
        if(mySensor.getType() == android.hardware.Sensor.TYPE_LINEAR_ACCELERATION){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float impulse = (float)Math.sqrt( Math.pow(z-last_tlz, 2)
                                            + Math.pow(x-last_tlx, 2)
                                            + Math.pow(y-last_tly, 2));

            if (impulse > 30){
                impulseCounter++;
            }

            String x_str = Float.toString(x);
            String y_str = Float.toString(y);
            String z_str = Float.toString(z);
            String impulse_str = Float.toString(impulse);
            String counter_str = Integer.toString(impulseCounter);
            tlx.setText("x: " + x_str);
            tly.setText("y: " + y_str);
            tlz.setText("z: " + z_str);
            tlImpulse.setText("impulse : " + impulse_str);
            tImpulseCounter.setText("impulse counter : " + counter_str);

            Log.v("linear acceleration sensor x", x_str);
            Log.v("linear acceleration sensor y", y_str);
            Log.v("linear acceleration sensor z", z_str);
            Log.v("linear acceleration impulse", impulse_str);
            Log.v("linear acceleration impulse counter", counter_str);

            long curTime = System.currentTimeMillis(); // 현재시간, ms

            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                //갱신
                last_tlx = x;
                last_tly = y;
                last_tlz = z;
            }
        }

        // 중력 포함인 경우
        else if(mySensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER){

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float impulse = (float)Math.sqrt( Math.pow(z-last_lz, 2)
                                            + Math.pow(x-last_lx, 2)
                                            + Math.pow(y-last_ly, 2));

            String x_str = Float.toString(x);
            String y_str = Float.toString(y);
            String z_str = Float.toString(z);
            String impulse_str = Double.toString(impulse);
            tx.setText("x: " + x_str);
            ty.setText("y: " + y_str);
            tz.setText("z: " + z_str);
            tImpulse.setText("impulse : " + impulse_str);

            Log.v("accelerometer sensor x", x_str);
            Log.v("accelerometer sensor y", y_str);
            Log.v("accelerometer sensor z", z_str);
            Log.v("accelerometer impulse", impulse_str);


            long curTime = System.currentTimeMillis(); // 현재시간

            // 0.1초 간격으로 가속도값을 업데이트
            if((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                //갱신
                last_lx = x;
                last_ly = y;
                last_lz = z;
            }
        }
        // 각속도 구하기
        else if(mySensor.getType() == android.hardware.Sensor.TYPE_GYROSCOPE){

            float axisx = sensorEvent.values[0];
            float axisy = sensorEvent.values[1];
            float axisz = sensorEvent.values[2];
            float impulse = (float)Math.sqrt( Math.pow(axisz-last_gz, 2)
                                            + Math.pow(axisx-last_gx, 2)
                                            + Math.pow(axisz-last_gy, 2));

            String x_str = Float.toString(axisx);
            String y_str = Float.toString(axisy);
            String z_str = Float.toString(axisz);
            String impulse_str = Float.toString(impulse);
            gx.setText("x: " + x_str);
            gy.setText("y: " + y_str);
            gz.setText("z: " + z_str);
            gImpulse.setText("impulse : "+ impulse_str);

            Log.v("gyroscope sensor x", x_str);
            Log.v("gyroscope sensor y", y_str);
            Log.v("gyroscope sensor z", z_str);
            Log.v("gyroscope impulse", impulse_str);

            long curTime = System.currentTimeMillis(); // 현재시간

            if((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                //갱신
                last_gx = axisx;
                last_gy = axisy;
                last_gz = axisz;
            }
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}