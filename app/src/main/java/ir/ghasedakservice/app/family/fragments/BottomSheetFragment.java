package ir.ghasedakservice.app.family.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ghasedakservice.app.family.activities.MainActivity;
import ir.ghasedakservice.app.family.models.Driver;
import ir.ghasedakservice.app.family.models.SchoolService;
import ir.ghasedakservice.app.family.models.SchoolServiceStuent;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.SendDataToFragmentListener;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;

public class BottomSheetFragment extends Fragment implements View.OnClickListener, SendDataToFragmentListener, ResponseHandler {

    public static final int INIT_SERVICE = 0;
    public static final int START_SERVICE = 1;
    public static final int RETURN_SERVICE = 2;
    public static final int FINISHED_SERVICE = 3;
    public static final int STUDENT_RIDE = 4;
    public static final int STUDENT_ABSENT = 5;

    public static final int NO_PAYMENT = 10;
    public static final int SMS_TO_OTHER_PARENT = 11;
    public static final int NEW_SERVICE = 12;
    public static final int CALL_DRIVER = 13;
    public static final int ABSENT_ALERT = 14;
    public static final int DELETE_SERVICE = 15;
    public static final int NO_SERVICE = 15;
    public static final int OPEN_BOTTOM_SHEET = 20;
    private int serviceId;


    /**
     * this method create fragment layout for all student
     *
     * @param serviceId is student id in hash map that get in parent data service
     * @return new fragment
     */
    public static BottomSheetFragment newInstance(int serviceId) {
        BottomSheetFragment myFragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("studentId", serviceId);
        myFragment.setArguments(args);
        return myFragment;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.absent_button) {
            if (clickState == DELETE_SERVICE)
                listener.onFragmentButtonClick(DELETE_SERVICE);
            else
                listener.onFragmentButtonClick(CALL_DRIVER);
        } else if (view.getId() == R.id.create_new_service || view.getId() == R.id.create_new_service_peek) {
            listener.onFragmentButtonClick(INIT_SERVICE);
        } else if (view.getId() == R.id.pay_new_service) {
            listener.onFragmentButtonClick(NO_PAYMENT);
        } else if (view.getId() == R.id.call_button) {
            listener.onFragmentButtonClick(CALL_DRIVER);
        } else if (view.getId() == R.id.chevron || view.getId() == R.id.student_details_layout) {
            listener.onFragmentButtonClick(OPEN_BOTTOM_SHEET);
        }
    }

    @Override
    public void detailsText(int serviceId, String text) {
        if (serviceId == this.serviceId) {
            if (services != null && (services.lastStatusTypeId == 1 || services.lastStatusTypeId == 2))
                setStatusServiceText(text);

        }
    }

    @Override
    public void animationHandler(int serviceId, int animationId, boolean start) {
        if (services != null && !start)
            serviceState.pauseAnimation();
        else if (serviceId == this.serviceId) {
            serviceState.pauseAnimation();
        }

    }

    @Override
    public void chevronHandler(int serviceId, float progress, boolean broadcast) {
        if (serviceId == this.serviceId || broadcast) {
            chevronSetFrame(progress);
        }
    }

    @Override
    public boolean notificationChange(int studentServiceId, int serviceId) {
        if (serviceId == -1) {
            if (this.serviceId == studentServiceId) {
                getServiceInformation(studentServiceId);
                return true;
            }
            return false;
        }
        if (studentServiceId != 0 && studentServiceId == this.serviceId) {
            changeStateIfNecessary();
            serviceState.playAnimation();
            return true;
        } else if (serviceId != 0 && this.serviceId != 0 && services.service != null && services.service.id == serviceId) {
            changeStateIfNecessary();
            serviceState.playAnimation();
            return true;
        }
        return false;
    }

    private void getServiceInformation(long id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Userconfig.userId);
            jsonObject.put("token", Userconfig.token);
            jsonObject.put("serviceStudentId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyService.getInstance().getJsonObjectRequest(VolleyService.ServiceInformationCode, VolleyService.ServiceInformationUrl, Request.Method.POST,
                this, jsonObject, null, null);
    }

    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {
        if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
            if (requestCode == VolleyService.ServiceInformationCode) {
                if (responseJson.has("service")) {
                    int id;
                    try {
                        JSONObject service1 = responseJson.getJSONObject("service");
                        id = service1.getInt("id");
                        SchoolService schoolService = Session.allService.get(id);
                        if (schoolService == null) {
                            schoolService = new SchoolService();
                            schoolService.fillFromJson(service1);
                            Session.allService.put(schoolService.id, schoolService);
                            JSONObject driver=responseJson.getJSONObject("driver");
                            Driver d=new Driver();
                            d.fillFromJson(driver);
                            Session.allDriver.put(d.id,d);
                            schoolService.driver=d;
                            services.setDriver(d);
                        }
                        schoolService.serviceStuents.add(services);
                        services.service=schoolService;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                services.service = new SchoolService();
                changeStateIfNecessary();
                Toast.makeText(getContext(), R.string.driver_accept, Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseError, Object requestObject) {

    }

    public interface ServiceStudentChangeMapListener {
        void onFragmentButtonClick(int state);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            setListener((ServiceStudentChangeMapListener) context);
            if (getContext() != null)
                ((MainActivity) getContext()).fragmentsHandler.add(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getContext() instanceof MainActivity)
            ((MainActivity) getContext()).fragmentsHandler.remove(this);
    }

    private ServiceStudentChangeMapListener listener;

    private SchoolServiceStuent services;
    private TextView studentName, schoolName, detailsText, driverName, driverPlate1, DriverPlate2;
    private ImageView studentAvatar;
    private LottieAnimationView chevron, backgroundAnimation, serviceState;
    private RelativeLayout studentDetailsLayout, mainLayout;
    private LinearLayout bottomCreateService, bottomCallDriver, bottomAbsentNotif, payment, createNewServicePeek, driverDetails;
    private int clickState = NEW_SERVICE;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_view_pager_layout, container, false);
        initView(fragmentView);
        int serviceId = getArguments() != null ? getArguments().getInt("studentId") : 0;

        if (serviceId == 0) {
            lastPosition();
        } else {
            detailsText.setVisibility(View.VISIBLE);
            setServices(serviceId);
            initDefaultProperty();
            changeStateIfNecessary();
        }
        return fragmentView;
    }

    /**
     * initial all object in fragment view for dynamically change those
     *
     * @param fragmentView root view
     */
    private void initView(View fragmentView) {
        studentName = fragmentView.findViewById(R.id.name_of_student);
        schoolName = fragmentView.findViewById(R.id.name_of_school);
        mainLayout = fragmentView.findViewById(R.id.fragment_main_layout);
        studentAvatar = fragmentView.findViewById(R.id.student_default_image);
        detailsText = fragmentView.findViewById(R.id.all_service_status);
        bottomCreateService = fragmentView.findViewById(R.id.create_new_service);
        serviceState = fragmentView.findViewById(R.id.service_status_peek);
        bottomCallDriver = fragmentView.findViewById(R.id.call_button);
        bottomAbsentNotif = fragmentView.findViewById(R.id.absent_button);
        driverName = fragmentView.findViewById(R.id.driver_name);
        driverPlate1 = fragmentView.findViewById(R.id.plate_country);
        DriverPlate2 = fragmentView.findViewById(R.id.plate_number);
        driverDetails = fragmentView.findViewById(R.id.driver_details);
        createNewServicePeek = fragmentView.findViewById(R.id.create_new_service_peek);
        payment = fragmentView.findViewById(R.id.pay_new_service);
        chevron = fragmentView.findViewById(R.id.chevron);
        chevron.setOnClickListener(this);
        chevron.setAnimation("chevron.json");
        backgroundAnimation = fragmentView.findViewById(R.id.background_image);
        studentDetailsLayout = fragmentView.findViewById(R.id.student_details_layout);
        studentDetailsLayout.setOnClickListener(this);
        bottomCallDriver.setOnClickListener(this);
        bottomCreateService.setOnClickListener(this);
        bottomAbsentNotif.setOnClickListener(this);
        createNewServicePeek.setOnClickListener(this);
        payment.setOnClickListener(this);
    }

    /**
     * if this student not allow tracking if other parent don't pay annually payment
     */
    @Deprecated
    private void notAllowOtherParentTrack() {
        bottomCreateService.setVisibility(View.GONE);
        bottomAbsentNotif.setVisibility(View.VISIBLE);
        TextView buttonText = bottomAbsentNotif.findViewById(R.id.button_text);
        buttonText.setText(R.string.send_to);
        bottomCallDriver.setVisibility(View.GONE);
        createNewServicePeek.setVisibility(View.GONE);
        driverDetails.setVisibility(View.GONE);
        payment.setVisibility(View.GONE);
        driverDetails.setVisibility(View.GONE);
        studentAvatar.setBackgroundResource(R.drawable.bg_oval_red);
        studentDetailsLayout.setVisibility(View.VISIBLE);
        serviceState.setAnimation("waitingforconfig.json");
        detailsText.setText(R.string.not_allow_resons);
        backgroundAnimation.setAnimation("waitingforconfig.json");
        clickState = SMS_TO_OTHER_PARENT;
    }

    /**
     * if this student not allow tracking if student's parent don't pay annually payment
     */
    private void notAllowTrackState() {
        bottomCreateService.setVisibility(View.GONE);
        bottomAbsentNotif.setVisibility(View.GONE);
        bottomCallDriver.setVisibility(View.GONE);
        createNewServicePeek.setVisibility(View.GONE);
        driverDetails.setVisibility(View.GONE);
        payment.setVisibility(View.VISIBLE);
        studentAvatar.setBackgroundResource(R.drawable.bg_oval_red);
        studentDetailsLayout.setVisibility(View.VISIBLE);
        serviceState.setAnimation("waitingforconfig.json");
        detailsText.setText(R.string.pay_text);
        backgroundAnimation.setAnimation("waitingforconfig.json");
        clickState = NO_PAYMENT;
    }


    /**
     * this method initialize default value for views in fragment view that use for all of state
     */
    private void initDefaultProperty() {
        if (services != null) {
            String name = services.getStudent().name + " " + services.getStudent().family;
            studentName.setText(name);
            schoolName.setText(services.getSchool().name);
            if (services.getDriver() != null)
                if (services.getDriver().family != null && services.getDriver().name != null &&
                        services.getDriver().vehicle != null && services.getDriver().vehicle.pelak != null) {
                    try {
                        String temp = services.getDriver().name + " " + services.getDriver().family;
                        driverName.setText(temp);
                        JSONObject pelak = new JSONObject(services.getDriver().vehicle.pelak);
                        String number = pelak.getString("4") + " " + pelak.getString("3") + " " + pelak.getString("2");
                        this.DriverPlate2.setText(number);
                        this.driverPlate1.setText(pelak.getString("1"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    /**
     * check all of state on service that have no problem in payment
     */
    private void allowTrackStates() {
        initDefaultProperty();
        if (services.service == null)
            noServiceExist();
        else {
            switch (services.lastStatusTypeId) {
                case INIT_SERVICE:
                    finishService();
                    break;
                case START_SERVICE:
                    startService();
                    break;
                case RETURN_SERVICE:
                    returnService();
                    break;
                case FINISHED_SERVICE:
                    finishService();
                    break;
                case STUDENT_RIDE:
                    if (services.service.lastStatusTypeId == 1)
                        rideStudentService();
                    else if (services.service.lastStatusTypeId == 2)
                        gotOffStudentService();
                    else if (services.service.lastStatusTypeId == 3)
                        finishService();
                    break;
                case STUDENT_ABSENT:
                    if (services.service.lastStatusTypeId == 1)
                        absentStudentService();
                    else if (services.service.lastStatusTypeId == 2)
                        absentStudentService();
                    else if (services.service.lastStatusTypeId == 3)
                        finishService();
                    break;
            }
        }
    }

    /**
     * init view for starting service from home to school
     */
    private void startService() {
        bottomCreateService.setVisibility(View.GONE);
        bottomAbsentNotif.setVisibility(View.GONE);
        bottomCallDriver.setVisibility(View.VISIBLE);
        createNewServicePeek.setVisibility(View.GONE);
        driverDetails.setVisibility(View.VISIBLE);
        payment.setVisibility(View.GONE);
        studentAvatar.setBackgroundResource(R.drawable.bg_oval_green);
        studentDetailsLayout.setVisibility(View.VISIBLE);
        serviceState.setAnimation("service_running.json");
        backgroundAnimation.setAnimation("service_running.json");
        detailsText.setText(R.string.in_calculation);

    }

    private void noServiceExist() {
        finishService();
        serviceState.setAnimation("waitingforconfirm.json");
        backgroundAnimation.setAnimation("waitingforconfirm.json");
        detailsText.setText("راننده هنوز برای دانش آموز شما سرویسی انتخاب نکرده است");
        studentAvatar.setBackgroundResource(R.drawable.bg_oval_yellow);
        driverDetails.setVisibility(View.GONE);
        TextView buttonText = bottomAbsentNotif.findViewById(R.id.absent_text);
        buttonText.setText(R.string.delete_service);
        clickState = NO_SERVICE;
    }

    /**
     * init view for starting service from school to home
     */
    private void returnService() {
        startService();
        clickState = RETURN_SERVICE;
    }

    /**
     * init view for finished service
     */
    private void finishService() {
        bottomCreateService.setVisibility(View.GONE);
        bottomAbsentNotif.setVisibility(View.VISIBLE);
        TextView buttonText = bottomAbsentNotif.findViewById(R.id.absent_text);
        buttonText.setText(R.string.call_to_driver);
        bottomCallDriver.setVisibility(View.GONE);
        createNewServicePeek.setVisibility(View.GONE);
        payment.setVisibility(View.GONE);
        driverDetails.setVisibility(View.VISIBLE);
        studentDetailsLayout.setVisibility(View.VISIBLE);
        serviceState.setAnimation("sleepmotion.json");
        studentAvatar.setBackgroundResource(R.drawable.bg_oval_blue);
        backgroundAnimation.setAnimation("sleepmotion.json");
        detailsText.setText(R.string.no_run_service_exist);
        clickState = CALL_DRIVER;
    }

    /**
     * init view for ride change status in service
     */
    private void rideStudentService() {
        startService();
        detailsText.setText(R.string.riding);
        clickState = STUDENT_RIDE;
    }

    /**
     * init view for got of change status in service
     */
    private void gotOffStudentService() {
        startService();
        detailsText.setText(R.string.your_student_get_off);
        clickState = STUDENT_RIDE;
    }

    /**
     * init view for student absent state on service
     */
    private void absentStudentService() {
        finishService();
        detailsText.setText(R.string.student_is_absent);
        bottomCallDriver.setVisibility(View.VISIBLE);
        bottomAbsentNotif.setVisibility(View.GONE);
    }

    /**
     * this method use for last page in fragment pager that have navigation to add new service
     */
    private void lastPosition() {
        bottomCreateService.setVisibility(View.VISIBLE);
        bottomAbsentNotif.setVisibility(View.GONE);
        bottomCallDriver.setVisibility(View.GONE);
        driverDetails.setVisibility(View.GONE);
        createNewServicePeek.setVisibility(View.VISIBLE);
        payment.setVisibility(View.GONE);
        studentDetailsLayout.setVisibility(View.GONE);
//        detailsText.setText(R.string.add_service_student);
        detailsText.setVisibility(View.GONE);
        backgroundAnimation.setAnimation("sleepmotion.json");
        clickState = NEW_SERVICE;
    }


    /**
     * bind service to this fragment
     *
     * @param service service id in hash map
     */
    public void setServices(int service) {

        services = Session.allServiceStudent.get(service);
        if (services != null)
            serviceId = services.id;
        else
            serviceId = 0;
    }

    public void setListener(ServiceStudentChangeMapListener listener) {
        this.listener = listener;
    }

    public void changeStateIfNecessary() {
        if (services != null)

            if (services.allowTrack()) {
                allowTrackStates();
            } else if (services.getStudent().parent1Id.equals(Userconfig.parent.id)) {
                notAllowTrackState();
            } else {
                notAllowOtherParentTrack();
            }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void pauseAnimation() {
        if (serviceState != null)
            serviceState.pauseAnimation();
    }

    public void startAnimation() {
//        if (serviceState != null)
//            serviceState.playAnimation();
    }

    public boolean isPlaying() {
        if (serviceState != null && serviceState.isAnimating())
            return true;
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setStatusServiceText(String s) {
        detailsText.setText(s);
    }

    public void chevronSetFrame(float v) {
        if (chevron != null)
            chevron.setProgress(v);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
