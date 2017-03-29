package netlab.fakturk.accndkerr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView tv, tv_ndkAccXValue,tv_ndkAccYValue,tv_ndkAccZValue;
    AccSensorErrorData errorData;

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

        errorData = new AccSensorErrorData();
        errorData.print();


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

        tv_ndkAccXValue.setText(Float.toString(x));
        tv_ndkAccYValue.setText(Float.toString(y));
        tv_ndkAccZValue.setText(Float.toString(z));





    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void sensorValue();
}

