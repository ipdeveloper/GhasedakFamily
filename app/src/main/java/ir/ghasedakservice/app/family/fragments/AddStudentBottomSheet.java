package ir.ghasedakservice.app.family.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ghasedakservice.app.family.models.Student;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Userconfig;

public class AddStudentBottomSheet extends BottomSheetDialogFragment implements ResponseHandler {

    private EditText studentName, studentFamily, studentPhone;
    TextView driver_name;
    private Button actionDialogButton;
    private AddStudentListener addStudentListener;



    public interface AddStudentListener {
        void addStudent(Student student, String phone);
    }

    public void setAddStudentListener(AddStudentListener addStudentListener) {
        this.addStudentListener = addStudentListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogAddStudent = inflater.inflate(R.layout.dialog_student_input, container, false);
        driver_name = dialogAddStudent.findViewById(R.id.driver_name);
        actionDialogButton = dialogAddStudent.findViewById(R.id.action_button);
        actionDialogButton.setText(R.string.submit_student);
        final ProgressBar progressBar = dialogAddStudent.findViewById(R.id.progress_bar);
        studentName = dialogAddStudent.findViewById(R.id.input_name);
        studentFamily = dialogAddStudent.findViewById(R.id.input_family);
        studentFamily.setText(Userconfig.lastName);
        studentPhone = dialogAddStudent.findViewById(R.id.input_mobile);
        studentPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                driver_name.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String replace = s.toString().replace(" ", "");
                driver_name.setText("");
                if (replace.length() ==11)
                    searchDriverByMobile(replace);

            }
        });
        actionDialogButton.setOnClickListener(view -> {
            if (studentName.getText().length() < 1)
                studentName.setError(getString(R.string.please_input_student_name));
            else if (studentFamily.getText().length() < 1)
                studentFamily.setError(getString(R.string.please_input_student_family));
            else if (studentPhone.getText().length() < 1)
                studentPhone.setError(getString(R.string.please_input_driver_phone));
            else if(!Patterns.PHONE.matcher(studentPhone.getText()).matches())
            {
                studentPhone.setError(getString(R.string.please_valid_driver_phone));
            }else
            {
                actionDialogButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                addNewService();
            }
        });

        return dialogAddStudent;
    }

    String lastRequest;

    private void searchDriverByMobile(String mobile) {

        if (lastRequest != null) {
            VolleyService.getInstance().cancelPendingRequests(lastRequest);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Userconfig.userId);
            jsonObject.put("type", "0");
            jsonObject.put("mobile", mobile);
            jsonObject.put("token", Userconfig.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lastRequest = VolleyService.getInstance().getJsonObjectRequest(VolleyService.SearchWithMobileCode, VolleyService.SearchWithMobileUrl, Request.Method.POST,
                this, jsonObject, null, null);

    }

    private void addNewService() {
        Student student = new Student();
        student.name = studentName.getText().toString();
        student.family = studentFamily.getText().toString();
        addStudentListener.addStudent(student, studentPhone.getText().toString());
    }
    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {
        if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
            if (requestCode == VolleyService.SearchWithMobileCode) {
                try {
                    if (responseJson.getString("status").equals("success")) {
                        JSONObject data = responseJson.getJSONObject("data");
                        String name="";
                        if(data!=null)
                            name=data.getString("name")+" "+data.getString("family");
                            driver_name.setText(name);
//                        driver_name.setText("ایمان پوررضا");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseError, Object requestObject) {

    }
}
