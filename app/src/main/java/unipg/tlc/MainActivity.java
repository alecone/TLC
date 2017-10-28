package unipg.tlc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("kiss_fft");
        System.loadLibrary("provaMain");
    }

    /* Public Variables*/
    LocationManager locationManager;
    public double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS;
    final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public int count = 0; // This variable exists because I'm dumb and can't find any better solution to switch the GPS action
    Location locus;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates;
    final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public UsbDevice tlcDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final TextView tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setText(peppeName());

        /* USB_Device staff*/
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        tlcDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (tlcDevice != null) {
                                Toast.makeText(MainActivity.this, "RTL-USB device NOT connected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Permission denied for RTL-USB device", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        /****************   This Part could stay out of the onCreate() method********************/

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Do Nothing right now, we don't need to give the user explanation
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

        locus = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        final Button btnGPS = (Button) this.findViewById(R.id.button1);
        btnGPS.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                if ((count++) % 2 == 0)
                    tv1.setText("GPS Location Updating ...");
                else
                    tv1.setText("GPS Location Paused.");
                toggleGPSUpdates(view);
            }
        });
        final Button btnSend = (Button) this.findViewById(R.id.button2);
        btnSend.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Start Processing Data in new Thread...", Toast.LENGTH_SHORT).show();
                processData();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permission,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Ajee PERMISSION GRANTED!", Toast.LENGTH_LONG).show();
                } else {
                    // Permission denied, NOT our case tho
                }
                return;
            }
            default:
                return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFusedLocationClient.removeLocationUpdates(locationListenerGPS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                /* should never access here*/
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationListenerGPS,
                    null /* Looper */);
        }
    }

    public void toggleGPSUpdates(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals(getResources().getString(R.string.pause))) {
            mRequestingLocationUpdates = false;
            mFusedLocationClient.removeLocationUpdates(locationListenerGPS);
            button.setText(R.string.resume);
        } else {
            mRequestingLocationUpdates = true;
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationListenerGPS,
                    null /* Looper */);
            Toast.makeText(MainActivity.this, "GPS Provider updating...", Toast.LENGTH_SHORT).show();
            button.setText(R.string.pause);
            }

    }

    private final LocationCallback locationListenerGPS = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                /* should never access here*/
                    return;
                }
                longitudeGPS = location.getLongitude();
                latitudeGPS = location.getLatitude();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        longitudeValueGPS.setText(longitudeGPS + "");
                        latitudeValueGPS.setText(latitudeGPS + "");
                    }
                });
            }
        }

        ;
    };

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    /**
     * A native method that is implemented by a native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String peppeName();
    public native int dateOfBirth(int age);
    public native String peppeAge();
    public native String printStruct(String name, int age);
    //public native int getMain();

    public void processData(){
        new Thread(new Runnable() {
            public void run() {
                Log.d("TAG", "Hello I'm a new thread. My number is: "+this.toString());
                int idDevice = tlcDevice.getDeviceId();
                String nameDevice = tlcDevice.getDeviceName();
                int vendorTLC = tlcDevice.getVendorId();
                int productTLC = tlcDevice.getProductId();
                Log.d("TAG", "Id Device: "+idDevice+", name device: "+nameDevice+
                ", vendor ID: "+vendorTLC+", product ID: "+productTLC);

            }
        }).start();
    }
}