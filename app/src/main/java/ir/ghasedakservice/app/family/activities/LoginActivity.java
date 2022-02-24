package ir.ghasedakservice.app.family.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ghasedakservice.app.family.GhasedakActivity;
import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;


public class LoginActivity extends GhasedakActivity implements ResponseHandler {

    private EditText phoneInputText;
    private TextView btn_login;
    private ProgressBar progressbar_login;
    private boolean validPhoneNumber;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = GhasedakApplication.session;

        if (Userconfig.token != null) {
            Intent intent_to_main = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent_to_main);
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        init();

        listener();
    }

    private void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressbar_login = findViewById(R.id.progressbar_for_login);
        phoneInputText = findViewById(R.id.input_login_username);
        phoneInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        btn_login = findViewById(R.id.btn_login);
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        if (phoneNumber != null && !phoneNumber.equals("")) {
            phoneInputText.setText(phoneNumber);
            validPhoneNumber = true;
        }

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_text), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    private void listener() {
        phoneInputText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, final int count) {
                if (s.length() > 10) {
                    String temp = s + "";
                    if (temp.substring(0, 2).equals("09")) {
                        validPhoneNumber = true;
                    }
                } else {
                    validPhoneNumber = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        phoneInputText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        phoneInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (validPhoneNumber)
                        sendPhoneToRegister(Config.asciiNumners(phoneInputText.getText().toString()));
                }
                return false;
            }
        });

        btn_login.setOnClickListener(view -> {
            if (validPhoneNumber) {
                btn_login.setEnabled(false);
                sendPhoneToRegister(Config.asciiNumners(phoneInputText.getText().toString()));

            } else {
                Toast.makeText(LoginActivity.this, R.string.phone_is_not_correct, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void sendPhoneToRegister(final String phoneNumber) {

        showProgressDialog();
        /*btn_login.setEnabled(false);*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

    private void hideProgressDialog() {
        progressbar_login.setVisibility(View.GONE);
        btn_login.setEnabled(true);
    }

    private void showProgressDialog() {
        progressbar_login.setVisibility(View.VISIBLE);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
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
        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right);
    }

    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {


        btn_login.setEnabled(true);
        if (requestCode == VolleyService.RegisterRequestCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    if (responseJson.getString("status").equals("success")) {//when error message true
                        Intent intent_to_verify = new Intent(LoginActivity.this, VerifySMS.class);
                        String s = phoneInputText.getText().toString();
                        intent_to_verify.putExtra("phoneNumber", s);
                        startActivity(intent_to_verify);
                        finish();
                    } else {
                        JSONObject data = responseJson.getJSONObject("data");
                        String error = data.getString("errorMessage");
                        if (error == null)
                            error = GhasedakApplication.context.getResources().getString(R.string.error_messages_internet);
                        showToast(getString(R.string.error_in_data_input));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                hideProgressDialog();
            }
        }
        hideProgressDialog();

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseJson, Object requestObject) {
        hideProgressDialog();
        Toast.makeText(this, R.string.error_messages_internet, Toast.LENGTH_LONG).show();

    }
}
