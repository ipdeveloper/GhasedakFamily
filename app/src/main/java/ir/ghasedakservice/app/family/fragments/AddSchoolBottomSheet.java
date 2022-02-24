package ir.ghasedakservice.app.family.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import ir.ghasedakservice.app.family.R;

public class AddSchoolBottomSheet extends BottomSheetDialogFragment {

    private EditText schoolName;
    private Button actionDialogButton;
    private AddSchoolListener addSchoolListener;
    public  interface AddSchoolListener {
        void addSchoolCreate( String schoolname);
    }

    public void setAddSchoolListener(AddSchoolListener addSchoolListener) {
        this.addSchoolListener = addSchoolListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogAddSchool=inflater.inflate(R.layout.dialog_school_input,container,false);
        actionDialogButton = dialogAddSchool.findViewById(R.id.action_button);
        actionDialogButton.setText(R.string.add_school);
        final ProgressBar progressBar=dialogAddSchool.findViewById(R.id.progress_bar);
        schoolName = dialogAddSchool.findViewById(R.id.input_text);
        actionDialogButton.setOnClickListener(view -> {
            if (schoolName.getText().length() < 3) {
                schoolName.setError(getString(R.string.please_input_school_name));
            } else {
                sendSchoolToServer(schoolName.getText().toString());
                actionDialogButton.setText("");
                actionDialogButton.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        return dialogAddSchool;

    }

    private void sendSchoolToServer(String s) {
        addSchoolListener.addSchoolCreate(s);
    }

}
