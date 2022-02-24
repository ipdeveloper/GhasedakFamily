package ir.ghasedakservice.app.family;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ir.ghasedakservice.app.family.utility.Config;


public class GhasedakActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getBaseContext().getResources().updateConfiguration(Config.changeLanguage("fa"),
                    getBaseContext().getResources().getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Fabric.with(this, new Crashlytics());
//        Crashlytics.getInstance().crash();
    }
}
