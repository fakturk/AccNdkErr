package netlab.fakturk.accndkerr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static android.hardware.SensorManager.GRAVITY_EARTH;

public class MainActivity extends AppCompatActivity
{
    TextView tv, tv_ndkAccXValue,tv_ndkAccYValue,tv_ndkAccZValue;
    TextView tv_ndkGyrXValue,tv_ndkGyrYValue,tv_ndkGyrZValue;
    TextView tv_ndkMagXValue,tv_ndkMagYValue,tv_ndkMagZValue;
    TextView tv_AngleXAxis,tv_AngleYAxis,tv_AngleZAxis;
    TextView tv_correctedX,tv_correctedY,tv_correctedZ;
    TextView tv_velXValue,tv_velYValue,tv_velZValue;
    TextView tv_distanceXValue,tv_distanceYValue,tv_distanceZValue;
    AccSensorErrorData errorData;
    Gravity g;
    Orientation orientation;
    float[] gravity, startingEuler, mag, initialMag, euler, correctedAcc, error, velocity, distance;
    float[][] rotation;
    boolean isGravityInitilazed=false, isMagInitilazed=false;
    float angle = 0;

    // Used to load the 'native-lib' library on application startup.
    static
    {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_ndkAccXValue = (TextView) findViewById(R.id.tv_ndkAccXValue);
        tv_ndkAccYValue = (TextView) findViewById(R.id.tv_ndkAccYValue);
        tv_ndkAccZValue = (TextView) findViewById(R.id.tv_ndkAccZValue);

        tv_ndkGyrXValue = (TextView) findViewById(R.id.tv_ndkGyrXValue);
        tv_ndkGyrYValue = (TextView) findViewById(R.id.tv_ndkGyrXValue);
        tv_ndkGyrZValue = (TextView) findViewById(R.id.tv_ndkGyrZValue);

        tv_ndkMagXValue = (TextView) findViewById(R.id.tv_ndkMagXValue);
        tv_ndkMagYValue = (TextView) findViewById(R.id.tv_ndkMagYValue);
        tv_ndkMagZValue = (TextView) findViewById(R.id.tv_ndkMagZValue);

        tv_AngleXAxis = (TextView) findViewById(R.id.tv_AngleXValue);
        tv_AngleYAxis = (TextView) findViewById(R.id.tv_AngleYValue);
        tv_AngleZAxis = (TextView) findViewById(R.id.tv_AngleZValue);

        tv_correctedX = (TextView) findViewById(R.id.tv_correctedXValue);
        tv_correctedY = (TextView) findViewById(R.id.tv_correctedYValue);
        tv_correctedZ = (TextView) findViewById(R.id.tv_correctedZValue);

        tv_velXValue = (TextView) findViewById(R.id.tv_velXValue);
        tv_velYValue = (TextView) findViewById(R.id.tv_velYValue);
        tv_velZValue = (TextView) findViewById(R.id.tv_velZValue);

        tv_distanceXValue = (TextView) findViewById(R.id.tv_distanceXValue);
        tv_distanceYValue = (TextView) findViewById(R.id.tv_distanceYValue);
        tv_distanceZValue = (TextView) findViewById(R.id.tv_distanceZValue);


        g = new Gravity();
        orientation = new Orientation();
        mag = new float[3];
        initialMag = new float[]{0, 0, 0};
        gravity = new float[3];
        startingEuler= new float[]{0,0,0};
        rotation=null;
        euler = new float[]{0,0,0};
        correctedAcc=new float[3];
        error=new  float[]{0,0,0};
        velocity=new  float[]{0,0,0};
        distance=new  float[]{0,0,0};


        errorData = new AccSensorErrorData();
//        errorData.print();
        errorData.getError("htc", 12);


        // Example of a call to a native method
        sensorValue();
         tv= (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    public void writeData(long time, float x, float y, float z)
    {


        String acc =  x + " " + y + " " + z+"\n";
//        tv.setText(acc);

//        System.out.println(acc);
        float accNorm = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

        gravity[0] = x * (GRAVITY_EARTH / accNorm);
        gravity[1] = y * (GRAVITY_EARTH / accNorm);
        gravity[2] = z * (GRAVITY_EARTH / accNorm);
        rotation = orientation.rotationFromGravity(gravity);
        if (!isGravityInitilazed)
        {
            if (isMagInitilazed)
            {
                isGravityInitilazed = true;
                startingEuler = orientation.eulerFromRotation(rotation);
                initialMag = orientation.rotationVectorMultiplication(orientation.rotationTranspose(rotation), mag);
            }

        }
//        euler = orientation.eulerFromRotation(rotation);



        tv_ndkAccXValue.setText(Float.toString(x));
        tv_ndkAccYValue.setText(Float.toString(y));
        tv_ndkAccZValue.setText(Float.toString(z));
//        tv_AngleXAxis.setText(Float.toString(gravity[0]));
        angle = (float) Math.toDegrees( g.angleBetweenGravity(gravity));
        if(angle<0)
        {
            angle=360+angle;
        }
        tv_AngleYAxis.setText(Float.toString(angle));
        error=errorData.getError("htc",angle);
        correctedAcc[0]=x-error[0]-gravity[0];
        correctedAcc[1]=y-error[1]-gravity[1];
        correctedAcc[2]=z-error[2]-gravity[2];

        tv_correctedX.setText(Float.toString(correctedAcc[0]));
        tv_correctedY.setText(Float.toString(correctedAcc[1]));
        tv_correctedZ.setText(Float.toString(correctedAcc[2]));

//        tv_AngleZAxis.setText(Float.toString(gravity[2]));
        for (int i = 0; i < 3; i++)
        {
            velocity[i]+=correctedAcc[i]*0.01;
        }
        for (int i = 0; i < 3; i++)
        {
            distance[i]+=velocity[i]*0.01;
        }

        tv_velXValue.setText(Float.toString(velocity[0]));
        tv_velYValue.setText(Float.toString(velocity[1]));
        tv_velZValue.setText(Float.toString(velocity[2]));

        tv_distanceXValue.setText(Float.toString(distance[0]));
        tv_distanceYValue.setText(Float.toString(distance[1]));
        tv_distanceZValue.setText(Float.toString(distance[2]));












    }
    public void writeGyrData(long time, float x, float y, float z)
    {


        String gyr =  x + " " + y + " " + z+"\n";
//        tv.setText(acc);

//        System.out.println(gyr);

        tv_ndkGyrXValue.setText(Float.toString(x));
        tv_ndkGyrYValue.setText(Float.toString(y));
        tv_ndkGyrZValue.setText(Float.toString(z));





    }
    public void writeMagData(long time, float x, float y, float z)
    {
        if (!isMagInitilazed)
        {
            isMagInitilazed=true;
        }


//        String mag =  x + " " + y + " " + z+"\n";
        mag[0]=x;
        mag[1]=y;
        mag[2]=z;
//        tv.setText(acc);

//        System.out.println(mag);

        tv_ndkMagXValue.setText(Float.toString(x));
        tv_ndkMagYValue.setText(Float.toString(y));
        tv_ndkMagZValue.setText(Float.toString(z));





    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void sensorValue();
}

