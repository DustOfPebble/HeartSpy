package dummy.core.heartspy;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class StartupSettings extends Activity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_REQUEST_IGNORE_BATTERY_SAVING = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
            if (this.checkSelfPermission(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, PERMISSION_REQUEST_IGNORE_BATTERY_SAVING);
            }
        }
        setContentView(R.layout.startup_settings);
        findViewById(R.id.heart_rate_sensor_view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults == null) { Log.d("Startup:", "Granted Permissions is undefined"); return;}
        if (grantResults.length == 0) { Log.d("Startup:", "Granted Permissions empty"); return;}

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "<REQUEST_IGNORE_BATTERY_OPTIMIZATIONS> permission granted");
                }
            }
            case PERMISSION_REQUEST_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "<WRITE_EXTERNAL_STORAGE> permission granted");
                }
            }
            case PERMISSION_REQUEST_IGNORE_BATTERY_SAVING: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "<WRITE_EXTERNAL_STORAGE> permission granted");
                }
            }
        }
    }
}

