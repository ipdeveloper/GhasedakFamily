package ir.ghasedakservice.app.family.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ghasedakservice.app.family.GhasedakActivity;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;
import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;


public class VerifySMS extends GhasedakActivity implements ResponseHandler, View.OnClickListener {
    private TextView timer;
    private TextView verifyCode,txt_btn_login;
    private ImageView arrowBack;
    private ProgressBar progressBar;
    private String phoneNumber = "";
    private int time = 30;
    Handler handler = new Handler();
    private LottieAnimationView mLottieAnimationView;
    protected Session session;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_sms);
        mLottieAnimationView=findViewById(R.id.sms_animated);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        session = GhasedakApplication.session;
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        TextView verifyTitleText = findViewById(R.id.verify_title_text);
        verifyTitleText.setText(String.format(verifyTitleText.getText().toString(), phoneNumber));
        timer = findViewById(R.id.timer);
        timer.setOnClickListener(this);
        timerForRequest();
        txt_btn_login = findViewById(R.id.btnVerifyLogin);
        txt_btn_login.setOnClickListener(this);
        verifyCode = findViewById(R.id.verify_et);
        verifyCode.setImeOptions(EditorInfo.IME_ACTION_DONE);
        verifyCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (verifyCode.getText().length()==6)
                        sendCheckCode();
                }
                return false;
            }
        });
        progressBar = findViewById(R.id.progressbar_for_check_code);
        arrowBack = findViewById(R.id.back);
        arrowBack.setOnClickListener(this);
        mLottieAnimationView.setSpeed(0.8f);
        mLottieAnimationView.setAnimation("sms_notif.json");
        mLottieAnimationView.setFrame(1);
        new Handler().postDelayed(() -> mLottieAnimationView.playAnimation(),500);
    }

    private void sendRequest() {
        progressBar.setVisibility(View.VISIBLE);
        final JSONObject js = new JSONObject();
        try {
            js.put("mobile", phoneNumber);
            js.put("type", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyService.getInstance().getJsonObjectRequest(VolleyService.RegisterRequestCode, VolleyService.RegisterRequestUrl,
                Request.Method.POST, this, js, null, null);
    }

    private void timerForRequest() {
        handler.post(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (time > 0) {
                    timer.setText(String.format(getString(R.string.time_for_resend), time));
                    time--;
                    handler.postDelayed(this, 1000);
                } else {
                    timer.setText(R.string.resend);
                    timer.setClickable(true);
                    timer.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    private void sendCheckCode() {
        progressBar.setVisibility(View.VISIBLE);
        verifyCode.setEnabled(false);
        txt_btn_login.setEnabled(false);
        JSONObject js = new JSONObject();
        try {
            js.put("mobile", phoneNumber);
            js.put("sendCode", Config.asciiNumners(verifyCode.getText().toString()));
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionApp = pInfo.versionName;
            js.put("imei", VolleyService.CURRENT_RESOURCE+versionApp);
            js.put("modelDevice", Build.MODEL + "");
            js.put("typeOs", Build.VERSION.SDK_INT);
            js.put("versionOs", Build.VERSION.RELEASE);
            js.put("tokenApp", Userconfig.tokenAPP);
            js.put("type", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        VolleyService.getInstance().getJsonObjectRequest(VolleyService.CheckCodeRequestCode, VolleyService.CheckCodeRequestUrl,
                Request.Method.POST, this, js, phoneNumber, null);


    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce)
            super.onBackPressed();
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_text), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.animator.slide_to_left, R.animator.slide_from_right);
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

    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {


        if (requestCode == VolleyService.CheckCodeRequestCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    String statusString = responseJson.getString("status");
                    if (statusString.equals("success")) {//when error message true
                        JSONObject data = responseJson.getJSONObject("data").getJSONObject("userInfo");
                        Userconfig.fillFromJson(data);
//                        Userconfig.saveUserConfig();
                        sendParentData();


                    } else {
                        String error;
                        if (statusString.contains("false2")) {
                            error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);
                        } else {
                            try {
                                error = responseJson.getString("data") + "";
                                if (error.contains("null"))
                                    error = GhasedakApplication.context.getResources().getString(R.string.error_messages_code);
                            } catch (Exception e) {
                                e.printStackTrace();
                                error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);

                            }
                        }
                        showToast(getString(R.string.error_in_data_input));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);
                    showToast(error);
                }


            }
        } else if (requestCode == VolleyService.RegisterRequestCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    if (responseJson.getString("status").equals("success")) {//when error message true
                    } else {
                        JSONObject data = responseJson.getJSONObject("data");
                        String error = data.getString("errorMessage");
                        if (error == null || error.equals(""))
                            error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);
                        showToast(error);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == VolleyService.ParentDataCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    JSONObject result = responseJson;
                    if (result.getString("status").equals("success")) {
                        JSONObject data1 = result.getJSONObject("data");
                        Config.fillParentDate(data1);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (Userconfig.firstName == null || Userconfig.lastName == null || Userconfig.firstName.equals("null") && Userconfig.lastName.equals("null")) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            } else if (Session.allStudent.size() == 0 || Session.allServiceStudent.size() == 0)
            {
                Intent intent_to_main = new Intent(VerifySMS.this, MainActivity.class);
                startActivity(intent_to_main);
                finish();
            }else
            {
                if (Userconfig.userId != null && !Userconfig.userId.equals("null")) {
                    Intent intent_to_main = new Intent(VerifySMS.this, MainActivity.class);
                    startActivity(intent_to_main);
                    finish();
                } else {

                    String error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);
                    showToast(error);
                }
            }
        }
        txt_btn_login.setEnabled(true);
        verifyCode.setEnabled(true);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseJson, Object requestObject) {
        verifyCode.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this,R.string.error_messages_internet,Toast.LENGTH_LONG).show();

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                finish();
                break;
            case R.id.timer:
                timer.setClickable(false);
                if (time < 1) {
                    time = 30;
                    timer.setTextColor(getResources().getColor(R.color.light_gray));
                    timerForRequest();
                    sendRequest();
                }
                break;
            case R.id.btnVerifyLogin:
                if (verifyCode.getText().length()==6)
                    sendCheckCode();
                else
                    Toast.makeText(this, R.string.your_code_incorrect,Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        progressBar.setVisibility(View.GONE);
        super.onDestroy();

    }
    //
}
