package ir.ghasedakservice.app.family.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.utils.Utils;
import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import ir.ghasedakservice.app.family.BuildConfig;
import ir.ghasedakservice.app.family.GhasedakActivity;
import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;


public class SplashActivity extends GhasedakActivity implements ResponseHandler {


    private static final String TAG = SplashActivity.class.toString();
    private static final int UI_ANIMATION_DELAY = 300;
    Session session = GhasedakApplication.session; ///...کامنت گذاری جهت تست نرم افزار داکسیژن
    int CurrentVersionNumber;
    boolean isForce;
//    String apkFile = null;
    private boolean newVersionAvalable;
    private boolean animationDone = false;
    LottieAnimationView mLottieAnimationView;
    private boolean checkStartComplete = false;
    private boolean stateOne = false;
    private boolean stateTwo = false;
    private boolean stateThree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView version = findViewById(R.id.version_number);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionApp = pInfo.versionName;
            version.setText(String.format(getString(R.string.app_version), versionApp));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final TextView appName = findViewById(R.id.school_service_text);
        mLottieAnimationView = findViewById(R.id.lotti_animation);
        mLottieAnimationView.setImageAssetsFolder("images/");
        mLottieAnimationView.setSpeed(1f);
        mLottieAnimationView.setAnimation("splash.json");
        mLottieAnimationView.addAnimatorUpdateListener(animation -> {
            if (animation.getAnimatedFraction() > 0.8) {
                appName.setVisibility(View.VISIBLE);
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(500);
                appName.startAnimation(fadeIn);
            }

            if (animation.getAnimatedFraction() == 1) {
                animationDone = true;
                if (checkStartComplete)
                    SplashActivity.this.checkStartApp(stateOne, stateTwo, stateThree);
            }
        });
        mLottieAnimationView.playAnimation();
//        animationDone = true;
        checkStartApp(false, false, false);


    }

    /**
     *
     */

    private void runAnim() {
        new Handler().postDelayed(() -> {
//            String animFileName = "data.json";
//            LottieComposition.Factory.fromAssetFileName(SplashActivity.this, animFileName, composition -> {
//                mLottieAnimationView.setComposition(composition);
//                mLottieAnimationView.playAnimation();
//            });

            mLottieAnimationView.startAnimation(mLottieAnimationView.getAnimation());
        }, 20);
    }

    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {
        if (requestCode == VolleyService.AppVersionRequestCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {

                    if (responseJson.getString("status").equals("success")) {//when error message true

                        if (responseJson.has("data")) {
                            JSONObject data = responseJson.getJSONObject("data");
                            JSONArray appVersions = data.getJSONArray("appVersion");
                            int appVersionsLength = appVersions.length();
                            if (appVersionsLength > 0) {
                                CurrentVersionNumber = session.getVersionNumber();
                                if (CurrentVersionNumber == -1)
                                    CurrentVersionNumber = Integer.parseInt(getVersionCode());

                                newVersionAvalable = false;
                                for (int i = 0; i < appVersionsLength; i++) {
                                    JSONObject version = appVersions.getJSONObject(i);
                                    int versionNumber = version.getInt("versionNumber");
                                    if (CurrentVersionNumber < versionNumber) {
                                        CurrentVersionNumber = versionNumber;
                                        newVersionAvalable = true;
                                        if (version.getInt("forceFlag") > 0)
                                            isForce = true;
//                                        apkFile = version.getString("fileApk");
                                    }
                                }
                                if (newVersionAvalable ){//&& apkFile != null && apkFile.length() > 5) {
                                    CustomDialogClass cdd = new CustomDialogClass(SplashActivity.this, isForce);
                                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    try {
                                        cdd.show();
                                    } catch (Exception esd) {
                                        esd.printStackTrace();
                                    }
                                } else {
                                    checkStartApp(true, false, false);//In App Version
                                }

                            } else {
                                checkStartApp(true, false, false);//In App Version
                            }
                        }
                    } else {
                        if (responseJson.has("data")) {
                            JSONObject data = responseJson.getJSONObject("data");
                            String error = data.getString("errorMessage");
                            if (error == null)
                                error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }


                } catch (JSONException e) {
                    Log.e(TAG, "Exception4: " + e);
                    e.printStackTrace();
                }
            }

        }
        else if (requestCode == VolleyService.CustomerInfoCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    JSONObject result = responseJson;
                    if (result.getString("status").equals("success")) {//when result true
                        JSONObject data = result.getJSONObject("data");
                        Userconfig.fillFromJson(data.getJSONObject("userInfo"));
                        if (animationDone)
                            checkStartApp(true, true, false);
                        return;
                    } else {
                        Userconfig.token = null;
                        Userconfig.userId = null;
                        Userconfig.saveUserConfig();
                        if (animationDone)
                            checkStartApp(true, true, false);

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == VolleyService.ParentDataCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    JSONObject result = responseJson;
                    if (result.getString("status").equals("success")) {//when result true
                        JSONObject data1 = result.getJSONObject("data");
                        Config.fillParentDate(data1);
                        checkStartApp(true, true, true);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //showConnectionError();


    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseJson, Object requestObject) {
//        Toast.makeText(this, R.string.check_out_your_connection, Toast.LENGTH_SHORT).show();
        noInternetConnectionDialog = Config.confirmDialg2(SplashActivity.this, getString(R.string.show_error), getString(R.string.wrong_server_error)
                , v -> {
                    checkStartApp(false, false, false);
                    noInternetConnectionDialog.dismiss();
                }, v -> {
                    noInternetConnectionDialog.dismiss();
                    finish();
                });
        noInternetConnectionDialog.setCancelable(false);
        noInternetConnectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noInternetConnectionDialog.show();
    }

    private class CustomDialogClass extends Dialog {


        public Dialog d;
        public TextView yes, no;
        public CheckBox checkBox;
        public LinearLayout force_layout_hide;
        public TextView version_name;


        boolean forceDownload;
        private JSONObject dataJsonAll;
        private JSONArray description;


        public CustomDialogClass(@NonNull Context context, boolean forceDownload) {
            super(context);
            this.forceDownload = forceDownload;
            this.dataJsonAll = dataJsonAll;
            this.description = description;


        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_appdownload_layout);
            setCancelable(false);

            yes = findViewById(R.id.btn_yes);
            checkBox = findViewById(R.id.pup_checkBox);
            //force_layout_hide = (LinearLayout) findViewById(R.id.force_layout_hide);

            no = findViewById(R.id.btn_no);
            if (forceDownload) {
                no.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            } else {
                no.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
            }
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String url = "bazaar://details?id=ir.ghasedakservice.app.family";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        String url2 = "https://cafebazaar.ir/app/?id=ir.ghasedakservice.app.family";
                        intent.setData(Uri.parse(url2));
                        startActivity(intent);
                    }
//                    CustomDialogClass.this.dismiss();
//                    String url = apkFile;
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//                    finish();
//                    Intent a = new Intent(SplashActivity.this, NewAPKDownloaderActivity.class);
//                    a.putExtra("apkFile", apkFile);
//                    startService(a);

//                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                    } catch (android.content.ActivityNotFoundException anfe) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                    }
//                    if(isStoragePermissionGranted())
//                        downloadded(url);

                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        session.setVersionNumber(CurrentVersionNumber);
                    }
                    dismiss();
                    checkStartApp(true, false, false);//In App Version
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadded(url);
        }
    }

    String url=VolleyService.ServerImageIP+"Home/DownloadApp";
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void downloadded(String linkDownast) {

        String url = linkDownast + "";


        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url)).
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE).setAllowedOverRoaming(true)
                .setDescription("")
                .setTitle("Downloading...")
                .setVisibleInDownloadsUi(true);
        File externalStoragePublicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "GhasedakFamily.apk");
        try {

            if (externalStoragePublicDirectory.exists())
                externalStoragePublicDirectory.delete();
            else
                externalStoragePublicDirectory.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Except ", "" + e);
        }
        final Uri uri = Uri.parse("file://" + externalStoragePublicDirectory);
        request.setDestinationUri(uri);
        //set destination
//        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onReceive(Context ctxt, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor c = manager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Uri apkUri = FileProvider.getUriForFile(SplashActivity.this, BuildConfig.APPLICATION_ID + ".provider", externalStoragePublicDirectory);
                                Intent intent1 = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                intent1.setData(apkUri);
                                intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent1);
                            } else {
                                Uri apkUri = Uri.fromFile(externalStoragePublicDirectory);
                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent2);
                            }
//                            Intent install = new Intent(Intent.ACTION_VIEW);
//
//                            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setDataAndType(Uri.fromFile(externalStoragePublicDirectory), "application/vnd.android.package-archive");
////                            install.setDataAndType(a,
////                                    manager.getMimeTypeForDownloadedFile(downloadId));
//
//                            GhasedakApplication.context.startActivity(install);

                            finish();
                            unregisterReceiver(this);
                            // copy file from external to internal will esaily avalible on net use google.
//                            view.setImageURI(a);
                        }
                    }
                }


            }
        };
        //register receiver for when .apk download is complete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private String getVersionCode() {
        String version = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    private void sendCustomerInfo() {

        final JSONObject js = new JSONObject();
        try {
            js.put("userId", Userconfig.userId);
            js.put("token", Userconfig.token);
//            Log.e(TAG, "userId: " + Integer.parseInt(session.getUserID()) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyService.getInstance().getJsonObjectRequest(VolleyService.CustomerInfoCode, VolleyService.CustomerInfoUrl,
                Request.Method.POST, this, js, null, null);

    }

    private void sendParentData() {

        final JSONObject js = new JSONObject();
        try {
            js.put("userId", Userconfig.userId);
            js.put("token", Userconfig.token);
//            Log.e(TAG, "userId: " + Integer.parseInt(session.getUserID()) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyService.getInstance().getJsonObjectRequest(VolleyService.ParentDataCode, VolleyService.ParentDataUrl,
                Request.Method.POST, this, js, null, null);

    }

    private void sendAppVersion() {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("type", "2");

        VolleyService.getInstance().getJsonObjectRequest(VolleyService.AppVersionRequestCode, VolleyService.AppVersionRequestUrl,
                Request.Method.GET, this, null, null, headers);

    }
    Dialog noInternetConnectionDialog;
    private void checkStartApp(final boolean checkedVersion, final boolean checkedInfo, final boolean checkedData) {
        if (!Config.isNetworkConnected()) {

            noInternetConnectionDialog = Config.confirmDialg2(SplashActivity.this, getString(R.string.show_error), getString(R.string.wrong_server_error)
                    , v -> {
                        checkStartApp(checkedVersion, checkedInfo, checkedData);
                        noInternetConnectionDialog.dismiss();
                    }, v -> {
                        noInternetConnectionDialog.dismiss();
                        finish();
                    });
            noInternetConnectionDialog.setCancelable(false);
            noInternetConnectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            noInternetConnectionDialog.show();
            return;
        }
        if (!checkedVersion) {
            sendAppVersion();
            stateOne = true;
            return;
        }
        checkStartComplete = true;
        if (!checkedInfo && Userconfig.token != null) {
            sendCustomerInfo();
            stateTwo = true;
            return;
        }
        if (!checkedData && Userconfig.token != null) {
            sendParentData();
            stateThree = true;
            return;
        }
        if (animationDone)
            if (Userconfig.token != null) {
                if (Userconfig.firstName == null
                        || Userconfig.lastName == null
                        || Userconfig.firstName.equals("null")
                        || Userconfig.lastName.equals("null")
                        || Userconfig.cityId == 0) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    //intent.putExtra("type", AddLocationActivity.SELECT_SOURCE);//TODO
                    startActivity(intent);
                    finish();
                } else if (Session.allStudent.size() == 0 || Session.allServiceStudent.size() == 0) {
                    Intent intent_to_main = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent_to_main);
                    finish();
                } else {
                    Intent intent_to_main = new Intent(SplashActivity.this, MainActivity.class);
                    //intent_to_main.putExtra("type", AddLocationActivity.SELECT_SCHOOL);//TODO
                    startActivity(intent_to_main);
                    finish();
                }
            } else {
                Intent intentToLogin = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intentToLogin);
                finish();
            }
    }
}
