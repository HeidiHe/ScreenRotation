package en.heidi.android.screenrotation;


        import android.hardware.Sensor;
        import android.hardware.SensorManager;
        import android.support.v7.app.AppCompatActivity;
        import android.view.animation.Animation;
        import android.view.animation.RotateAnimation;
        import android.hardware.SensorEvent;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.hardware.SensorEventListener;
        import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ImageView compassimage;
    // record the angle turned of the compass picture
    private float DegreeStart = 0f;

    TextView DegreeTV;
    TextView tvSensorChangded;

    private String store;

    // Gravity rotational data
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];

    // yaw, pitch and roll
    private float yaw;
    private float pitch;
    private float roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassimage = (ImageView) findViewById(R.id.compass_image);

        // TextView that will display the degree
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);
        tvSensorChangded = (TextView)findViewById(R.id.tvSensorChanged);
        store = getString(R.string.none);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null) {
            // success! we have an accelerometer
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(
                    this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(
                    this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tvSensorChangded.setText("no sensor");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // code for system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensor,
        SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(
                this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accels = event.values.clone();
                break;
        }

        if (mags != null && accels != null) {
            gravity = new float[9];
            magnetic = new float[9];
            SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
            float[] outGravity = new float[9];
            SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,SensorManager.AXIS_Z, outGravity);
            SensorManager.getOrientation(outGravity, values);

            yaw = values[0] * 57.2957795f;
            pitch =values[1] * 57.2957795f;
            roll = values[2] * 57.2957795f;
            mags = null;
            accels = null;
        }
        float degree = Math.round(yaw) + 180;

        degreeToStore(degree);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }



    public void degreeToStore(float degree){

        DegreeTV.setText(getString(R.string.Degree_title) + Float.toString(degree) + getString(R.string.degrees));

        if (isBetween(degree, 1, 20)) {
            store = "mcDonald";
        }else if(isBetween(degree,20,60)){
            store = "KFC";
        }else if(isBetween(degree,60,90)){
            store = "IKEA";
        }else if(isBetween(degree,90,120)){
            store = "starbucks";
        }else if(isBetween(degree,120,160)){
            store = "吉野家";
        }else if(isBetween(degree,160,200)){
            store = "Jo Malone";
        }else if(isBetween(degree,200,280)){
            store = "Exit";
        }else if(isBetween(degree,280,360)){
            store = "地铁站";
        }

        tvSensorChangded.setText(store);



    }

    //rotate the map
    public void rotateImage(float degree){
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(210);

        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
    }



    public static boolean isBetween( float x, int lower, int upper) {
            return lower <= x && x <= upper;
    }
}