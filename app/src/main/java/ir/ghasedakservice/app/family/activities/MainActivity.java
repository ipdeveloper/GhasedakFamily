package ir.ghasedakservice.app.family.activities;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import ir.ghasedakservice.app.family.GhasedakActivity;
import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.dialogs.CustomAlertDialog;
import ir.ghasedakservice.app.family.fragments.AddSchoolBottomSheet;
import ir.ghasedakservice.app.family.fragments.AddStudentBottomSheet;
import ir.ghasedakservice.app.family.fragments.BottomSheetFragment;
import ir.ghasedakservice.app.family.fragments.FragmentDrawer;
import ir.ghasedakservice.app.family.jobs.UIJob;
import ir.ghasedakservice.app.family.models.AddressLocation;
import ir.ghasedakservice.app.family.models.City;
import ir.ghasedakservice.app.family.models.Driver;
import ir.ghasedakservice.app.family.models.School;
import ir.ghasedakservice.app.family.models.SchoolService;
import ir.ghasedakservice.app.family.models.SchoolServiceStuent;
import ir.ghasedakservice.app.family.models.Student;
import ir.ghasedakservice.app.family.services.MyFirebaseMessagingService;
import ir.ghasedakservice.app.family.services.NotificationObserver;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.SendDataToFragmentListener;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.IabHelper;
import ir.ghasedakservice.app.family.utility.IabResult;
import ir.ghasedakservice.app.family.utility.Inventory;
import ir.ghasedakservice.app.family.utility.Purchase;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;

public class MainActivity extends GhasedakActivity implements
        ResponseHandler, Marker.OnMarkerClickListener, BottomSheetFragment.ServiceStudentChangeMapListener, NotificationObserver, FragmentDrawer.FragmentDrawerListener
        , AddStudentBottomSheet.AddStudentListener, AddSchoolBottomSheet.AddSchoolListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private BottomSheetBehavior studentDetails;
    private UIJob uiHandler;
    private final int DEFAULT = 0;
    private final int ADD_LOCATION = 1;
    private final int SELECT_SCHOOL = 2;
//    private final int ADD_SERVICE = 3;


    //    private final MapJob mapJob=new MapJob(this);
    Marker driverMarker, homeMarker, schoolMarker, currentMarker;
    int thisLocationCityId;
    //    Polyline line;
    private MapView mMapView;
    private IMapController mMapController;
    private DrawerLayout mDrawerLayout;
    private LottieAnimationView centreLocation;
    private int mainShowStatus = DEFAULT;//, previousStatuse = mainShowStatus;
    private boolean toggle = false;

    private SchoolServiceStuent selectService = null, lastServiceLocationRequest;

    private String lastDriverLocationRequest;
    private Dialog dialog;

    private boolean goToDriverLocation = true;

    private RelativeLayout mainLayout, searchLayout;
    private ImageView searchImage, menuButton, helpButton, locationFinder;
    private TextView helpText, searchText;
    private Button mainButton, alternativeButton;
    private School school;
    private AddressLocation addressLocation;
    private boolean clickMarker;
    private Marker marker;
    private int animationNumber;

    private boolean weHaveRunningService = false;

    private float density, bottomSheetPeekSize;
    private StudentPager adaptor;
    private LocationManager lm;
    private Dialog gpsDialog;
    private MyLocationNewOverlay myLocationOverlay;
    private boolean sendSchoolAddress;
    ViewPager viewPager;
    public Vector<SendDataToFragmentListener> fragmentsHandler = new Vector<>();
    private int deletedId;
    boolean mIsPremium = false;
    IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
//                textView.setText(result+"");
                return;
            } else {
                Log.d(TAG, "Query inventory was successful.");
                // does the user have the premium upgrade?
                mIsPremium = inventory.hasPurchase(VolleyService.SKU_PREMIUM);

                // update UI accordingly

                Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//                textView.setText(result+"");
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                //textView.setText(result+"");
                return;
            } else if (purchase.getSku().equals(VolleyService.SKU_PREMIUM)) {
                //
            }
        }
    };

    private void initPayment() {
        mHelper = new IabHelper(this, VolleyService.PAYMENT_BASE64);
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(result -> {
            Log.d(TAG, "Setup finished.");

            if (!result.isSuccess()) {
                // Oh noes, there was a problem.
                Log.d(TAG, "Problem setting up In-app Billing: " + result);
            }
            // Hooray, IAB is fully set up!
            mHelper.queryInventoryAsync(mGotInventoryListener);
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyFirebaseMessagingService.observers.put(TAG, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (uiHandler == null) {
            uiHandler = new UIJob(this);
        }
        getLocation();
        initView();
        try {
            initPayment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        changeStateOfMain(DEFAULT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        mHelper = null;
    }

    void setServiceHomeSchoolMarker() {
        if (driverMarker != null) {
            driverMarker.remove(mMapView);
            driverMarker = null;
        }
        if (schoolMarker != null) {
            schoolMarker.closeInfoWindow();
            schoolMarker.remove(mMapView);
            schoolMarker = null;
        }
        if (homeMarker != null) {
            homeMarker.closeInfoWindow();
            homeMarker.remove(mMapView);
            homeMarker = null;
        }
        if (selectService != null) {
            setSchoolMarker();
            setHomeMarker();
            if (selectService.lastStatusTypeId == 1 || selectService.lastStatusTypeId == 2) {
                if (selectService.service != null)
                    setDriverMarker(selectService.service.lat, selectService.service.lng, true);
            }
            ArrayList<GeoPoint> points = new ArrayList<>();
            points.add(new GeoPoint(selectService.getSchool().lat, selectService.getSchool().lng));
            points.add(new GeoPoint(selectService.getLatitude(), selectService.getLongitude()));


//            if (selectService.service != null)
//                points.add(new GeoPoint(selectService.service.lat, selectService.service.lng));
            mMapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), true, 100 * (int) density);

        } else {
            if (school != null) {
                schoolMarker = new Marker(mMapView);
                schoolMarker.setPosition(new GeoPoint(school.lat, school.lng));
                schoolMarker.setTag(school);
                schoolMarker.setIcon(getResources().getDrawable(R.drawable.school_pin));
                mMapView.getOverlays().add(schoolMarker);
            }
            if (addressLocation != null) {
                homeMarker = new Marker(mMapView);
                homeMarker.setPosition(new GeoPoint(addressLocation.lat, addressLocation.lng));
                homeMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker_home));
                homeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                homeMarker.setTitle("خانه");
                mMapView.getOverlays().add(homeMarker);
            }
            if (addressLocation != null && school != null) {
                ArrayList<GeoPoint> points = new ArrayList<>();
                points.add(homeMarker.getPosition());
                points.add(new GeoPoint(school.lat, school.lng));
                // mMapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 200);
            }


        }
        if (myLocationOverlay != null)
            mMapView.getOverlays().add(myLocationOverlay);
    }

    private void setHomeMarker() {
        homeMarker = new Marker(mMapView);
        homeMarker.setPosition(new GeoPoint(selectService.getLatitude(), selectService.getLongitude()));
        homeMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker_home));
        homeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        homeMarker.setTitle("خانه");
        mMapView.getOverlays().add(homeMarker);
    }

    private void setSchoolMarker() {
        schoolMarker = new Marker(mMapView);
        schoolMarker.setPosition(new GeoPoint(selectService.getSchool().lat, selectService.getSchool().lng));
        schoolMarker.setTag(selectService.getSchool());
        schoolMarker.setOnMarkerClickListener(this);
        schoolMarker.setIcon(getResources().getDrawable(R.drawable.school_pin));
        mMapView.getOverlays().add(schoolMarker);
    }

    private void checkCurrentPageState() {
        clearSchoolMarkers();
        if (selectService != null) {
            setServiceHomeSchoolMarker();
        } else if (mainLocation != null) {
            mMapController.setZoom(17.99);
            mMapView.getController().animateTo(mainLocation);
        }
    }

    @Override
    public void onFragmentButtonClick(int state) {


        if (state == BottomSheetFragment.INIT_SERVICE) {

            if (studentDetails.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                studentDetails.setPeekHeight(0);
                studentDetails.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            changeStateOfMain(ADD_LOCATION);
        } else if (state == BottomSheetFragment.CALL_DRIVER) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            try {
                intent.setData(Uri.parse("tel:" + selectService.service.driver.mobile));
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe)
                    MainActivity.this.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "راننده هنوز اپلیکیشن را نصب نکرده است", Toast.LENGTH_SHORT).show();
            }
        } else if (state == BottomSheetFragment.DELETE_SERVICE) {
            deleteService();
        } else if (state == BottomSheetFragment.ABSENT_ALERT) {

        } else if (state == BottomSheetFragment.NO_PAYMENT) {

        } else if (state == BottomSheetFragment.OPEN_BOTTOM_SHEET) {
            if (studentDetails.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                studentDetails.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (studentDetails.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                studentDetails.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        if (studentDetails.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            studentDetails.setState(BottomSheetBehavior.STATE_COLLAPSED);
            setServiceHomeSchoolMarker();
        }
    }

    private void deleteService() {
        View.OnClickListener accept = v -> {
            if (selectService != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userId", Userconfig.userId);
                    jsonObject.put("studentServiceId", selectService.id);
                    jsonObject.put("token", Userconfig.token);
                    jsonObject.put("status", 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                deletedId = selectService.id;
                VolleyService.getInstance().getJsonObjectRequest(VolleyService.StudentServiceDeleteCode, VolleyService.StudentServiceDeleteUrl, Request.Method.POST,
                        MainActivity.this, jsonObject, null, null);
            } else {
                Toast.makeText(MainActivity.this, "این دانش آموز قبلا حذف شده لطف اپ را مجددا راه اندازی کنید.", Toast.LENGTH_SHORT).show();
                deleteDialog.dismiss();
                adaptorNotify(0);
            }
        };
        View.OnClickListener cancel = v -> deleteDialog.dismiss();
        deleteDialog = Config.confirmDialg(this, "حذف سرویس", "آیا از حذف سرویس مورد نظر اطمینان دارید؟", accept, cancel);

    }

    Dialog deleteDialog;


    @Override
    public void refresh(int serviceStudentId, int serviceId, int status) {
        runOnUiThread(() -> {
            if ((serviceStudentId != 0 || serviceId != 0) && status != 0) {
                for (int i = 0; i < adaptor.services.size(); i++) {
                    if (serviceId != 0) {
                        SchoolServiceStuent schoolServiceStuent = Session.allServiceStudent.get(adaptor.services.get(i));
                        if (schoolServiceStuent != null && schoolServiceStuent.service != null && schoolServiceStuent.service.id == serviceId) {
                            viewPager.setCurrentItem(i, true);
                            changeFragmentViewIfNecessary(serviceStudentId, serviceId);
                            break;
                        }
                    } else {
                        if (adaptor.services.get(i) == serviceId) {
                            viewPager.setCurrentItem(i, true);
                            changeFragmentViewIfNecessary(serviceStudentId, serviceId);
                            break;
                        }
                    }
                }
                if (status == 3) {
                    showFinishDialog(serviceId);
                }

            }
        });

    }

    @Override
    public void driverAccepted(int serviceId) {
        runOnUiThread(() -> changeFragmentViewIfNecessary(serviceId, -1));
    }

    @Override
    public void showDialog(JSONObject js) {
        runOnUiThread(() -> {
            CustomAlertDialog dialog = new CustomAlertDialog(MainActivity.this, js);
            locationFinder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                    if (js.has("height")) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dialog.findViewById(R.id.main_layout).getLayoutParams();
                        try {
                            params.height = js.getInt("height") * (int) density;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.findViewById(R.id.main_layout).setLayoutParams(params);
                    }
                }
            }, 2000);
        });


    }

    private void showFinishDialog(int serviceId) {
        ArrayList<SchoolServiceStuent> schoolServiceStuent = new ArrayList<>();
        for (int j = 0; j < adaptor.services.size(); j++) {
            SchoolServiceStuent schoolServiceStuent1 = Session.allServiceStudent.get(adaptor.services.get(j));
            if (schoolServiceStuent1.service != null && schoolServiceStuent1.service.id == serviceId) {
                schoolServiceStuent.add(schoolServiceStuent1);
                break;
            }
        }

        if (schoolServiceStuent.size() > 0 && schoolServiceStuent.get(0).allowTrack()) {
            LottieAnimationView animation;
            Dialog finish = new Dialog(MainActivity.this);
            finish.setContentView(R.layout.dialog_finish);
            animation = finish.findViewById(R.id.animation_view);
            TextView detailsText = finish.findViewById(R.id.finish_text);
            String name = "";

            for (int i = 0; i < schoolServiceStuent.size(); i++) {
                name += schoolServiceStuent.get(i).getStudent().name + " " + schoolServiceStuent.get(i).getStudent().family + " ";
                if (schoolServiceStuent.size() > i + 1) {
                    name += "و ";
                }
            }
            String text = String.format("سرویس دانش آموز(ها) %s تمام شد", name);
            detailsText.setText(text);
            Button textView = finish.findViewById(R.id.close_finish_dialog);
            animation.setAnimation("finish.json");
            animation.playAnimation();
            animation.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            textView.setOnClickListener(view -> {
                animation.pauseAnimation();
                finish.dismiss();
            });
            try {
                finish.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewPager.setCurrentItem(adaptor.services.indexOf(schoolServiceStuent.get(0).id), true);
        }
    }

    @Override
    public void addStudent(Student student, String phone) {
        setServiceHomeSchoolMarker();
        this.student = student;
        this.studentPhone = phone;
        addNewService();
    }

    @Override
    public void addSchoolCreate(String schoolname) {
        sendSchoolAddress = true;
        schoolName = schoolname;
        searchAddress(mMapView.getMapCenter());
    }

    private class StudentPager extends FragmentStatePagerAdapter {

        public ArrayList<Integer> services = new ArrayList<>();

        public StudentPager(FragmentManager fm) {
            super(fm);
            selectService = null;
            ArrayList<SchoolServiceStuent> deletedService = new ArrayList<>();
            deletedService.addAll(Session.allServiceStudent.values());
            for (int i = 0; i < deletedService.size(); i++) {
                if (deletedService.get(i).status != 10)
                    services.add(deletedService.get(i).id);
            }
            Collections.sort(services, Collections.reverseOrder());
            fragmentsHandler.clear();
        }

        @Override
        public Fragment getItem(int i) {
            BottomSheetFragment fragment = BottomSheetFragment.newInstance(i < services.size() ? services.get(i) : 0);
            if (viewPager.getCurrentItem() == i) {
                if (i > services.size()) {
                    selectService = Session.allServiceStudent.get(services.get(i));
                    mMapView.getOverlays().clear();
                    setServiceHomeSchoolMarker();
                    mMapView.postInvalidate();
                }
                locationFinder.postDelayed(() -> fragment.startAnimation(), 25);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return services.size() + 1;
        }
    }

    /**
     * check map permission
     */
    public void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, R.string.permission_not_allow_to_show_map, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.v(TAG, "Permission is granted");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, R.string.permission_not_allow_to_show_map, Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                Log.v(TAG, "Permission is revoked");
                return;
            }
        }
        onMapReady();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectService != null && mMapView != null && mainShowStatus == DEFAULT) {
            mMapView.getOverlays().clear();
            setServiceHomeSchoolMarker();
            mMapView.postInvalidate();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
        if (studentDetails.getState() == BottomSheetBehavior.STATE_EXPANDED)
            studentDetails.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (mainShowStatus == ADD_LOCATION) {
            changeStateOfMain(DEFAULT);
        } else if (mainShowStatus == SELECT_SCHOOL) {
            changeStateOfMain(ADD_LOCATION);
        } else {
            dialog = Config.confirmDialg(this, getString(R.string.exit), getString(R.string.do_you_want_exit),
                    v -> {
                        dialog.dismiss();
                        MainActivity.super.onBackPressed();

                    }, v -> dialog.dismiss());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null || grantResults.length == 0) {
            return;
        }
        if (requestCode == 1)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                isPermissionGranted();
            }
        if (requestCode == 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted();
            }
        if (requestCode == 2)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted();
            }
        if (requestCode == 3 || requestCode == 4)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
    }

    public void onMapReady() {
        if (mMapView != null)
            return;
        mMapView = new MapView(this);
        ((RelativeLayout) findViewById(R.id.main_layout_for_map)).addView(mMapView, 0);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(0XFFE0DFDF);
        mMapView.getOverlayManager().getTilesOverlay().setLoadingLineColor(0XFFE0DFDF);
        mMapView.setMaxZoomLevel(17.99);
        mMapView.setMinZoomLevel(5.0);
        mMapController = mMapView.getController();
        mMapController.setZoom(17.99);
        City city = Session.allCity.get(Userconfig.cityId);
        GeoPoint startPoint;
        if (Userconfig.lastLocation != null && Userconfig.lastLocation.getLatitude() != 0.0) {
            startPoint = Userconfig.lastLocation;
        } else if (city != null) {
            startPoint = new GeoPoint(city.lat, city.lng);
        } else {
            startPoint = new GeoPoint(35.744754, 51.375218);
        }
        mMapController.setCenter(startPoint);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.addMapListener(mapListener);
        mMapView.addMapListener(delayedMapListener);
        mMapView.setMultiTouchControls(true);

    }

    private FragmentDrawer drawerFragment;
    private View.OnClickListener searchListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);

            if (mMapView != null) {
                Double lat = mMapView.getMapCenter().getLatitude(), lng = mMapView.getMapCenter().getLongitude();
                if (mainShowStatus == SELECT_SCHOOL && addressLocation != null) {
                    lat = addressLocation.lat;
                    lng = addressLocation.lng;
                }
                i.putExtra("type", mainShowStatus);
                i.putExtra("lat", lat);
                i.putExtra("lng", lng);
                startActivityForResult(i, 100);
            } else {
                isPermissionGranted();
            }
        }
    };

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, null);
        drawerFragment.setDrawerListener(this);
        findViewById(R.id.nav_header_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        density = getResources().getDisplayMetrics().density;
        bottomSheetPeekSize = density * 136;
        mainLayout = findViewById(R.id.main_layout_for_map);
        searchLayout = findViewById(R.id.search_layout);
        searchImage = findViewById(R.id.search_image);
        menuButton = findViewById(R.id.menu_button);
        searchText = findViewById(R.id.search_text);
        searchText.setOnClickListener(searchListner);
        searchImage.setOnClickListener(searchListner);
        searchLayout.setOnClickListener(searchListner);
        menuButton.setOnClickListener(menuButtonListener);
        helpButton = findViewById(R.id.help_icon);
        mainButton = findViewById(R.id.main_button);
        mainButton.setOnClickListener(mainButtonListener);
        centreLocation = findViewById(R.id.centre_location);
        alternativeButton = findViewById(R.id.alternative_button);
        alternativeButton.setOnClickListener(alternativeButtonListener);
        studentDetails = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        studentDetails.setBottomSheetCallback(bottomSheetCallback);
        locationFinder = findViewById(R.id.location_finder);
        locationFinder.setOnClickListener(findLocationListener);
        viewPager = findViewById(R.id.student_pager);
        adaptor = new StudentPager(getSupportFragmentManager());
        viewPager.setAdapter(adaptor);
        viewPager.addOnPageChangeListener(studentDetailsPageListener);
        helpText = findViewById(R.id.help_text);
        invisibleBottomSheet = ValueAnimator.ofInt((int) bottomSheetPeekSize, 0);
        invisibleBottomSheet.setDuration(250);
        invisibleBottomSheet.addUpdateListener(animatorUpdateListener);
        invisibleBottomSheet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                centreLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    private ValueAnimator invisibleBottomSheet;


    private void changeStateOfMain(int state) {
        mainShowStatus = state;
        switch (mainShowStatus) {
            case DEFAULT:
                defaultState();
                break;
            case ADD_LOCATION:
                addLocation();
                break;
            case SELECT_SCHOOL:
                addSchool();
                break;

        }
    }

    private void defaultState() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        clearSchoolMarkers();
        if (mMapView != null)
            searchGeoCoder(mMapView.getMapCenter());
        //Todo
        mainButton.setVisibility(View.INVISIBLE);
        centreLocation.setVisibility(View.GONE);
        alternativeButton.setVisibility(View.GONE);
        helpText.setVisibility(View.GONE);
        helpText.setText(R.string.default_help);
        studentDetails.setState(BottomSheetBehavior.STATE_COLLAPSED);
        menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_black_24dp));
        if (adaptor.services != null && Session.allServiceStudent.size() != 0) {
            if (viewPager.getCurrentItem() < adaptor.services.size())
                selectService = Session.allServiceStudent.get(adaptor.services.get(viewPager.getCurrentItem()));
            else
                selectService = null;
        }

        ValueAnimator visibleBottomSheet = ValueAnimator.ofInt(0, (int) bottomSheetPeekSize);
        visibleBottomSheet.setDuration(250);
        visibleBottomSheet.addUpdateListener(valueAnimator -> {
            int position = (int) valueAnimator.getAnimatedValue();
            studentDetails.setPeekHeight(position);
            //locationFinder.setTranslationY(mainLayout.getHeight() + position);
        });
        visibleBottomSheet.start();
        getDriversLocation();
    }

    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int position = (int) valueAnimator.getAnimatedValue();
            studentDetails.setPeekHeight(position);
            //locationFinder.setTranslationY(position);
        }
    };

    private void addLocation() {

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        clearSchoolMarkers();
        searchText.setText(R.string.search);
        school = null;
        if (addressLocation != null && getLocation()) {
            mMapController.animateTo(new GeoPoint(addressLocation.lat, addressLocation.lng));
            addressLocation = null;
        }
        alternativeButton.setVisibility(View.GONE);
        centreLocation.setAnimation("Pin_animation.json");
        centreLocation.setFrame(1);
        centreLocation.setVisibility(View.VISIBLE);
        helpText.setVisibility(View.GONE);
        helpText.setText(R.string.add_location_help);
        if (mainButton.getVisibility() == View.VISIBLE)
            mainButton.setVisibility(View.INVISIBLE);
        if (studentDetails.getPeekHeight() != 0)
            invisibleBottomSheet.start();
        menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));

    }

    private void addSchool() {
        clearSchoolMarkers();
        if (school != null) {
            mMapController.animateTo(new GeoPoint(school.lat, school.lng));
            school = null;
            schoolSelected = false;
        }

        setServiceHomeSchoolMarker();
        mainButton.setVisibility(View.INVISIBLE);
        mainButton.postDelayed(() -> {
            mainButton.setVisibility(View.VISIBLE);
            mainButton.setText(R.string.choice_school);
        }, 300);
        alternativeButton.setText(R.string.add_school);
        centreLocation.setVisibility(View.VISIBLE);
        centreLocation.setAnimation("Pin_animation.json");
        centreLocation.setFrame(1);
        helpText.setVisibility(View.GONE);
        helpText.setText(R.string.add_location_help);
        menuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
    }

//    private void addHomeMarker() {
//        if (addressLocation == null) {
//            return;
//        }
//        if (homeMarker != null)
//            homeMarker.remove(mMapView);
//        homeMarker = new Marker(mMapView);
//        homeMarker.setPosition(new GeoPoint(addressLocation.lat, addressLocation.lng));
//        homeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        homeMarker.setTag(school);
//        homeMarker.setIcon(getResources().getDrawable(R.drawable.ic_student));
//        homeMarker.setTitle("خانه");
//        mMapView.getOverlays().add(homeMarker);
//
//
//
//
//    }

    void mapChangeLocation(IGeoPoint target) {

        int round = Math.round(100 * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
        Projection proj = mMapView.getProjection();
        Point startPoint = proj.toPixels(target, null);
        IGeoPoint startGeoPoint = proj.fromPixels(startPoint.x, startPoint.y - round);

        mMapController.animateTo(startGeoPoint);

    }

    private void addAddress(IGeoPoint target) {
        String s = searchText.getText().toString();
        if (!s.equals(getResources().getString(R.string.searching))) {
            addressLocation = new AddressLocation();
            addressLocation.address = s;
            addressLocation.lat = target.getLatitude();
            addressLocation.lng = target.getLongitude();
            setServiceHomeSchoolMarker();
            changeStateOfMain(SELECT_SCHOOL);
            mapChangeLocation(target);
            mMapView.postDelayed(() -> {
                searchText.setText(R.string.searching);
                searchGeoCoder(mMapView.getMapCenter());
            }, 500l);

        } else if (s.length() == 0) {
            Toast.makeText(this, R.string.please_define_address, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.please_stay_until_end, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * search school or address for special location
     *
     * @param target map view center coordinate or any custom coordinate tha we want it address
     */
    private void searchGeoCoder(IGeoPoint target) {
        if (mainShowStatus == SELECT_SCHOOL && school == null) {
            searchSchool(target);
        } else if (mainShowStatus == DEFAULT) {
            searchAddress(target);
            searchText.setText(R.string.search);
        } else if (mainShowStatus == ADD_LOCATION)
            searchAddress(target);
    }


    private Vector<School> schools = new Vector<>();

    /**
     * show search school on map
     */
    private void showSchools() {
        clearSchoolMarkers();
        for (int i = 0; i < schools.size(); i++) {
            School school = schools.get(i);
            addMarker(school);
        }
        if (mainShowStatus != SELECT_SCHOOL)
            setServiceHomeSchoolMarker();
        mMapView.invalidate();
        searchText.setText(R.string.please_click_on_school);

    }

    private void clearSchoolMarkers() {
        if (mMapView == null)
            return;
        if (preMarker != null) {
            for (Marker marker : preMarker) {
                marker.remove(mMapView);
            }
            preMarker.clear();

        }
        if (marker != null) {
            marker.remove(mMapView);
            marker = null;
        }
        if (driverMarker != null) {
            driverMarker.remove(mMapView);
            driverMarker = null;
        }

        if (schoolMarker != null) {
            schoolMarker.closeInfoWindow();
            schoolMarker.remove(mMapView);
            schoolMarker = null;
        }
//        if (homeMarker != null && mainShowStatus!=SELECT_SCHOOL) {
//            homeMarker.remove(mMapView);
//            homeMarker = null;
//        }
        mMapView.getOverlays().clear();
        if (homeMarker != null && mainShowStatus == SELECT_SCHOOL) {
            mMapView.getOverlays().add(homeMarker);
        }
        if (myLocationOverlay != null)
            mMapView.getOverlays().add(myLocationOverlay);

        mMapView.postInvalidate();
    }

    GeoPoint lastDriverLocation;

    private void setDriverMarker(double lat, double lng, boolean mapMove) {
        if (mMapView == null) {
            return;
        }
        clearSchoolMarkers();
        GeoPoint startPoint = new GeoPoint(lat, lng);
        if (selectService.lastStatusTypeId != 4 && selectService.maxDistance > 0) {
            if (selectService.service.lastStatusTypeId == 1) {
                AddressLocation srcAddress = selectService.getSrcAddress();
                GeoPoint endPoint = new GeoPoint(srcAddress.lat, srcAddress.lng);
                alarmDistance(startPoint, endPoint, selectService.maxDistance);
                selectService.maxDistance = selectService.maxDistance - 250;

            } else if (selectService.service.lastStatusTypeId == 2) {
                AddressLocation desAddress = selectService.getDesAddress();
                GeoPoint endPoint = new GeoPoint(desAddress.lat, desAddress.lng);
                alarmDistance(startPoint, endPoint, selectService.maxDistance);
                selectService.maxDistance = selectService.maxDistance - 250;
            }
        }
        if (goToDriverLocation) {
            goToDriverLocation = false;
            mMapController.setZoom(17.99);
            mMapController.animateTo(startPoint);
        }
        if (toggle) {
            mMapController.setZoom(17.99);
            mMapController.animateTo(startPoint);
        }
        if (driverMarker != null) {
            driverMarker.remove(mMapView);
        }
        driverMarker = new Marker(mMapView);
        if (lastDriverLocation != null)
            driverMarker.setPosition(lastDriverLocation);
        else {
            driverMarker.setPosition(startPoint);
        }

        Log.v(TAG, "" + lat + " " + lng + "................................");
        driverMarker.setIcon(getResources().getDrawable(R.drawable.ic_driver_marker));
        driverMarker.setOnMarkerClickListener((marker, mapView) -> {
            marker.showInfoWindow();
            marker.setTitle("آقا/خانم" + " " + selectService.getDriver().family);
            mapView.getController().animateTo(marker.getPosition());
            return true;
        });
        mMapView.getOverlays().add(driverMarker);
        if (selectService != null) {
            setSchoolMarker();
            setHomeMarker();
        }
        mMapView.invalidate();
        lastDriverLocation = new GeoPoint(lat, lng);
        if (mapMove) {
            animateMarker(driverMarker, startPoint);
        }
    }

    void alarmDistance(GeoPoint startPoint, GeoPoint endPoint, int distance) {
        if (startPoint.distanceToAsDouble(endPoint) < distance && distance > 1) {
            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(getString(R.string.ghasedak))
                            .setContentText(getString(R.string.your_student_service_arrived))
                            .setAutoCancel(true).setSmallIcon(R.mipmap.ic_parent_launcher)
                            .setSound(defaultSoundUri);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }
            assert notificationManager != null;
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    private Vector<Marker> preMarker;

    /**
     * in this method we add schools as markers to map
     *
     * @param school location and school name
     */
    private Marker addMarker(School school) {
        if (preMarker == null) {
            preMarker = new Vector<>();
        }
        Marker marker = new Marker(mMapView);
        marker.setPosition(new GeoPoint(school.lat, school.lng));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTag(school);
        marker.setIcon(getResources().getDrawable(R.drawable.school_pin));
        marker.setOnMarkerClickListener(this);
        marker.setTitle(school.name);
        mMapView.getOverlays().add(marker);
        preMarker.add(marker);
        return marker;
    }

    /**
     * this method handel marker click listener
     *
     * @param marker  markers
     * @param mapView map view
     * @return boolean
     */
    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        selectSchoolMarker(marker);
        return true;

    }

    private void selectSchoolMarker(Marker marker) {
        if (mainButton.getVisibility() != View.VISIBLE) {
            mainButton.setText(R.string.choice_school);
            mainButton.setVisibility(View.VISIBLE);
        }
        alternativeButton.setVisibility(View.GONE);
        clickMarker = true;
        this.marker = marker;
        centreLocation.postDelayed(new Runnable() {
            @Override
            public void run() {
                centreLocation.setAnimation("Pin_animation.json");
                centreLocation.playAnimation();
                animationNumber = 1;
            }
        }, 190);

        school = (School) marker.getTag();
        searchText.setText(school != null ? school.name : getString(R.string.this_school_not_available));
        String schoolName = getString(R.string.select_school_) + " " + school.name;
        mainButton.setText(schoolName);
        mMapView.getOverlays().add(marker);
        mMapController.animateTo(marker.getPosition());
        marker.setTitle(school.name);
        marker.showInfoWindow();
    }

    /**
     * animate driver marker to destination position
     *
     * @param marker     marker that we want animate
     * @param toPosition target position
     */
    public void animateMarker(final Marker marker, final GeoPoint toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMapView.getProjection();
        Point startPoint = proj.toPixels(marker.getPosition(), null);
        final IGeoPoint startGeoPoint = proj.fromPixels(startPoint.x, startPoint.y);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.getLongitude() + (1 - t) * startGeoPoint.getLongitude();
                double lat = t * toPosition.getLatitude() + (1 - t) * startGeoPoint.getLatitude();
                marker.setPosition(new GeoPoint(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 15);
                }
                mMapView.postInvalidate();
            }
        });
    }


    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject
            responseJson, Object requestObject) {
        if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
            if (requestCode == VolleyService.GetDriversLocationCode) {
                try {
                    if (responseJson.getString("status").equals("success")) {
                        JSONArray jsonArray = responseJson.getJSONObject("data").getJSONArray("path");
                        double lat = 0;
                        double lng = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject m = (JSONObject) jsonArray.get(i);
                            Session.allService.get(m.getInt("id")).lat = (float) m.getDouble("lat");
                            Session.allService.get(m.getInt("id")).lng = (float) m.getDouble("lng");
//                            System.out.println("-------------------------------------- " + i);
                            if (selectService.service != null && selectService.service.id == m.getInt("id")) {
                                selectService.service.lat = (float) m.getDouble("lat");
                                selectService.service.lng = (float) m.getDouble("lng");
                            }
                        }//TODO
                        try {

                            String url = VolleyService.RouteUrl + Config.asciiNumners("" + lastServiceLocationRequest.service.lng) + "," +
                                    Config.asciiNumners("" + lastServiceLocationRequest.service.lat) + ";" +
                                    Config.asciiNumners("" + lastServiceLocationRequest.getLongitude()) + "," +
                                    Config.asciiNumners("" + lastServiceLocationRequest.getLatitude()) + "?geometries=geojson";
                            VolleyService.getInstance().getJsonObjectRequest(VolleyService.RouteCode, url,
                                    Request.Method.GET, this, null, null, null);
                        } catch (Exception e) {

                        }
                        if (selectService == lastServiceLocationRequest && selectService.lastStatusTypeId != 0 && selectService.lastStatusTypeId != 3) {
                            setDriverMarker(selectService.service.lat, selectService.service.lng, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(VolleyService.GetDriversLocationCode + " ", "Json Exception");
                } finally {
                    mMapView.postDelayed(this::getDriversLocation, GhasedakApplication.timeRequest);
                }

            } else if (requestCode == VolleyService.RouteCode) {
                try {
                    JSONObject result = responseJson;
                    double duration = responseJson.optJSONArray("routes").optJSONObject(0).optDouble("duration");
                    makArrivalText(duration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == VolleyService.SchoolListCode) {
                schools.clear();
                try {
                    if (responseJson.getString("status").equals("success")) {
                        JSONArray jsonArray = responseJson.getJSONObject("data").getJSONArray("schools");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            School school = new School();
                            school.fillFromJson(jsonArray.getJSONObject(i));
                            schools.add(school);
                        }
                        showSchools();
                    }
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.no_proper_data_for_show));
                }
            } else if (requestCode == VolleyService.SchoolAddCode) {
                try {
                    if (responseJson.getString("status").equals("success")) {
                        dialogAddSchool.dismiss();
                        Long schoolId = responseJson.getJSONObject("data").getLong("departmentId");
                        school = new School();
                        school.id = schoolId;
                        school.name = schoolName;
                        school.lat = (float) mMapView.getMapCenter().getLatitude();
                        school.lng = (float) mMapView.getMapCenter().getLongitude();
                        addMarker(school);
                        centreLocation.setAnimation("Pin_animation_close.json");
                        centreLocation.setFrame(1);
                        animationNumber = 1;
                        clickMarker = false;
                        searchText.setText(school.name);
                        mainButton.setText(getString(R.string.select_school));
                        Session.allSchool.put(schoolId, school);
                        addStudentDialog();
                    }
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.no_proper_data_for_add_school));
                }
            } else if (requestCode == VolleyService.ServiceStudentAddCode) {

                SchoolServiceStuent mService = new SchoolServiceStuent();
                try {
                    dialogAddStudent.dismiss();
                    JSONObject data = responseJson.getJSONObject("data");
                    mService.id = data.getInt("studentServiceId");
                    mService.setSchool(school);
                    student.id = data.getLong("studentId");
                    student.parent = Userconfig.parent;
                    mService.setStudent(student);
                    mService.setDesAddress(addressLocation);
                    mService.setSrcAddress(addressLocation);
                    String phoneNumber = studentPhone;
                    Driver driver = new Driver();
                    driver.id = data.getString("driverId");
                    driver.name = data.getString("driverName");
                    driver.family = data.getString("driverFamily");
                    driver.mobile = phoneNumber;
                    mService.setDriver(driver);
                    Session.allServiceStudent.put(mService.id, mService);
                    adaptorNotify(mService.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // showToast(getString(R.string.error_in_data_input));
                }
                changeStateOfMain(DEFAULT);

            } else if (requestCode == VolleyService.SchoolListCode) {
                schools.clear();
                try {
                    if (responseJson.getString("status").equals("success")) {
                        JSONArray jsonArray = responseJson.getJSONObject("data").getJSONArray("schools");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            School school = new School();
                            school.fillFromJson(jsonArray.getJSONObject(i));
                            schools.add(school);
                        }
                        showSchools();
                    }
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.no_proper_data_for_show));
                }

            } else if (requestCode == VolleyService.GeoCoderCode) {
                try {
                    if (responseJson.has("place_id")) {
                        String address = responseJson.getJSONObject("address").optString("county");
                        String[] addreses = address.split(" ");
                        address = addreses[addreses.length - 1].replaceAll("\u064a", "\u06cc");
                        address = address.replaceAll("\u06a9", "\u0643");
                        City city = Session.allCityName.get(address.trim());
                        if (city != null) {
                            thisLocationCityId = city.id;
                        } else {
                            thisLocationCityId = 0;
                        }
                        address += " ";
                        if (responseJson.getJSONObject("address").has("road"))
                            address = responseJson.getJSONObject("address").getString("road") + " ";
                        if (responseJson.getJSONObject("address").has("neighbourhood"))
                            address += responseJson.getJSONObject("address").getString("neighbourhood");
                        else if (responseJson.getJSONObject("address").has("suburb"))
                            address += responseJson.getJSONObject("address").getString("suburb");
                        Userconfig.lastCity = responseJson.getJSONObject("address").optString("county");
                        if (sendSchoolAddress) {
                            sendSchoolAddress = false;
                            sendSchoolToServer(schoolName, address);
                            return;
                        }
                        if (mainShowStatus != DEFAULT)
                            searchText.setText(address);
                    } else {
                        if (mainShowStatus != DEFAULT)
                            searchText.setText(R.string.unknown);
                    }
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.no_proper_data_for_add_school));
                    if (sendSchoolAddress) {
                        sendSchoolAddress = false;
                        sendSchoolToServer(schoolName, getString(R.string.unknown));
                        return;
                    }
                    if (mainShowStatus != DEFAULT)
                        searchText.setText(R.string.unknown);
                }
            } else if (requestCode == VolleyService.StudentServiceDeleteCode) {
                try {
                    if (responseJson.getString("status").equals("success")) {
                        Session.allServiceStudent.get(selectService.id).status = 10;
                        if (deleteDialog != null)
                            deleteDialog.dismiss();

                        adaptorNotify(0);
                        viewPager.setCurrentItem(0);
                    }
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.no_proper_data_for_show));
                }
            } else if (mainShowStatus != DEFAULT)
                searchText.setText(R.string.unknown);

            else if (requestCode == VolleyService.CreditIncreaseCode) {

                try {
                    if (responseJson.getString("status").equals("success")) {//when error message true
                        if (responseJson.has("data")) {
                            JSONObject data = responseJson.getJSONObject("data");
                            String bankLink = data.getString("bankLink");
                            startBankPaymentBrowser(bankLink);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void startBankPaymentBrowser(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }

    private void adaptorNotify(final int id) {

        adaptor = new StudentPager(getSupportFragmentManager());
        viewPager.setAdapter(adaptor);
        locationFinder.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id != 0) {
                    viewPager.setCurrentItem(adaptor.services.indexOf(id), true);
                }
            }
        }, 1000);

    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String
            responseError, Object requestObject) {

        if (responseStatusCode == VolleyService.ERROR_STATUS_CODE)
            if (requestCode == VolleyService.GetDriversLocationCode)
                mMapView.postDelayed(this::getDriversLocation, GhasedakApplication.timeRequest);
    }


    private final static int ALL_PERMISSIONS_RESULT = 101;
    private final static int ALL_PERMISSIONS_RESULT2 = 102;

    /**
     * get driver location #check permission #gdl
     *
     * @return
     */
    private boolean getLocation() {
        isPermissionGranted();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, R.string.permission_do_not_access_to_gps, Toast.LENGTH_SHORT).show();
                    return false;
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
                return false;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, R.string.permission_do_not_access_to_gps, Toast.LENGTH_SHORT).show();
                    return false;
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        4);
                return false;
            }
        }
        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            gpsDialog = Config.confirmDialg(this, getString(R.string.enable_gps), getString(R.string.do_you_want_turn_gps_on), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gpsDialog.dismiss();
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MainActivity.this.startActivityForResult(myIntent, 888);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gpsDialog.dismiss();
                }
            });
            return false;

        } else {
            if (mMapView != null) {
                if (myLocationOverlay == null) {
                    myLocationOverlay = new MyLocationNewOverlay(mMapView);
                    myLocationOverlay.setDirectionArrow(getBitmap(R.drawable.ic_current_location), getBitmap(R.drawable.ic_direction_arrow));
                    mMapView.getOverlays().add(myLocationOverlay);
                    myLocationOverlay.enableMyLocation();
                }


            }
        }
        return true;
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }




    private EditText studentName, studentFamily;
    private String schoolName;
    String studentPhone;
    private ProgressBar progressBar;
    private Button actionDialogButton;
    AddSchoolBottomSheet dialogAddSchool;

    /**
     * add school dialog #school dialog #sd
     */
    private void addSchoolDialog() {
        dialogAddSchool = new AddSchoolBottomSheet();
        dialogAddSchool.setAddSchoolListener(this);
        dialogAddSchool.show(getSupportFragmentManager(), TAG);
    }

    Student student;

    AddStudentBottomSheet dialogAddStudent;

    /**
     * add student dialog #school dialog #sd
     */
    private void addStudentDialog() {
        dialogAddStudent = new AddStudentBottomSheet();
        dialogAddStudent.setAddStudentListener(this);
        dialogAddStudent.show(getSupportFragmentManager(), TAG);
    }

    /**
     * this is server request part------------------------------------------------------------------------------------------------------
     * <p>
     * search address with this coordinate #geo search #gs
     *
     * @param target map view center coordinate or any custom coordinate that we want it's address
     */
    private void searchAddress(IGeoPoint target) {
        if (!sendSchoolAddress && mainShowStatus != DEFAULT)
            searchText.setText(R.string.searching);
        String url = VolleyService.GeoCoderUrl + "&lat=" + Config.asciiNumners("" +
                target.getLatitude()) + "&lon=" + Config.asciiNumners("" + target.getLongitude());
        VolleyService.getInstance().getJsonObjectRequest(VolleyService.GeoCoderCode, url,
                Request.Method.GET, this, null, null, null);
    }

    private String lastSchoolListRequest;

    /**
     * request school from server #get school from server #gsfs
     *
     * @param target location that we want get school around it
     */
    private void searchSchool(IGeoPoint target) {
        searchText.setText(R.string.searching);
        if (lastSchoolListRequest != null) {
            VolleyService.getInstance().cancelPendingRequests(lastSchoolListRequest);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Userconfig.userId);
            jsonObject.put("cityId", thisLocationCityId); //Userconfig.cityId);
            jsonObject.put("lat", target.getLatitude());
            jsonObject.put("lng", target.getLongitude());
            jsonObject.put("token", Userconfig.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lastSchoolListRequest = VolleyService.getInstance().getJsonObjectRequest(VolleyService.SchoolListCode, VolleyService.SchoolListUrl, Request.Method.POST,
                this, jsonObject, null, null);

    }

    /**
     * get driver location from server #gdlfs
     */
    public void getDriversLocation() {
        lastServiceLocationRequest = selectService;
        if (lastDriverLocationRequest != null)
            VolleyService.getInstance().cancelPendingRequests(lastDriverLocationRequest);
        if (selectService != null && selectService.service != null)
            Log.v(TAG, "service id=" + selectService.service.id);
        JSONArray serviceId = new JSONArray();
        Vector<SchoolService> services = new Vector<>(Session.allService.values());

        for (int i = 0; i < services.size(); i++) {
            serviceId.put(services.get(i).id);
            if (services.get(i).lastStatusTypeId == 1 || services.get(i).lastStatusTypeId == 2)
                weHaveRunningService = true;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", Userconfig.userId);
            jsonObject.put("serviceIds", serviceId);
            jsonObject.put("token", Userconfig.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!weHaveRunningService)
            GhasedakApplication.timeRequest = 9000;
        else
            GhasedakApplication.timeRequest = 9000;
        lastDriverLocationRequest = VolleyService.getInstance().getJsonObjectRequest(VolleyService.GetDriversLocationCode, VolleyService.GetDriversLocationUrl, Request.Method.POST,
                this, jsonObject, null, null);

    }

    /**
     * send school that created by parent to server # send school #ssts
     *
     * @param name
     */
    private void sendSchoolToServer(String name, String address) {
        JSONObject js = new JSONObject();
        try {
            js.put("userId", Userconfig.userId);
            js.put("departmentTypeId", 1);
            js.put("cityId", Userconfig.cityId);
            js.put("lat", mMapView.getMapCenter().getLatitude());
            js.put("lng", mMapView.getMapCenter().getLongitude());
            js.put("name", name);
            js.put("address", address);
            js.put("token", Userconfig.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyService.getInstance().getJsonObjectRequest(VolleyService.SchoolAddCode, VolleyService.SchoolAddUrl,
                Request.Method.POST, this, js, null, null);
    }


    /**
     * add new service and sending it to server #ssts
     */
    private void addNewService() {
        if (addressLocation != null && school != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", Userconfig.userId);
                jsonObject.put("token", Userconfig.token);
                jsonObject.put("departmentId", school.id);
                jsonObject.put("srcLat", addressLocation.lat);
                jsonObject.put("srcLng", addressLocation.lng);
                jsonObject.put("srcAddress", addressLocation.address);
                jsonObject.put("desLat", addressLocation.lat);
                jsonObject.put("desLng", addressLocation.lng);
                jsonObject.put("desAddress", addressLocation.address);
                jsonObject.put("driverMobile", studentPhone);
                jsonObject.put("studentName", student.name);
                jsonObject.put("studentId", 0);
                jsonObject.put("studentFamily", student.family);
            } catch (Exception e) {
                e.printStackTrace();
            }
            VolleyService.getInstance().getJsonObjectRequest(VolleyService.ServiceStudentAddCode, VolleyService.ServiceStudentAddUrl,
                    Request.Method.POST, this, jsonObject, null, null);
        }
    }

    /**
     * inner classes -----------------------------------------------------------------------------------------------------------------
     */
    private boolean setLocation = true;
    private static GeoPoint mainLocation;

    @Override
    public void onDrawerItemSelected(View view, int position) {
        final Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case 1:
                viewPager.setCurrentItem(adaptor.services.size() + 1);
                break;
            case 2:
                supportDialog();
                break;
            case 3:
                share();
                break;
            case 4:
                payAnnually();
                break;


        }
    }

    private void payAnnually() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment);
        TextView pay = dialog.findViewById(R.id.payment_button);
        TextView desc = dialog.findViewById(R.id.description);
        desc.setText(getString(R.string.payment_details) );
        pay.setText(R.string.pay);
        pay.setOnClickListener(v -> {
            payment();
            dialog.dismiss();
        });
//        + "\n" + getString(R.string.payment_details2)
//        Calendar c = Calendar.getInstance();
//        Date current=new Date();
//        c.setTime(current);
//
//        System.out.println("---------------------------------" + c.getTime());
//        c.set(2019, 9, 8);
//        c.set(Calendar.HOUR,0);
//        c.set(Calendar.MINUTE,0);
//        c.set(Calendar.SECOND,0);
//        Date pointDate=c.getTime();
//        System.out.println("---------------------------------" + c.getTime());
//        if( current.before(pointDate)) {
//            pay.setText(R.string.close);
//            pay.setOnClickListener(v -> {
////                payment();
//                dialog.dismiss();
//            });
//
//        }else
//        {
//
//        }

        dialog.show();
    }

    private void payment() {
        if (VolleyService.CURRENT_RESOURCE.equals(VolleyService.BAZZAR_APK)) {
            try {
                mHelper.launchPurchaseFlow(this, VolleyService.SKU_PREMIUM, VolleyService.RC_REQUEST, mPurchaseFinishedListener, Userconfig.userId);
            } catch (Exception e) {
                Toast.makeText(this, R.string.bazaar_payment_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", Userconfig.userId);
                jsonObject.put("price", VolleyService.PRICE);
                jsonObject.put("token", Userconfig.token);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            VolleyService.getInstance().getJsonObjectRequest(VolleyService.CreditIncreaseCode, VolleyService.CreditIncreaseUrl,
                    Request.Method.POST, this, jsonObject, null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getString("type").equals("school")) {
                    GeoPoint mapCenter = new GeoPoint((double) bundle.getFloat("lat", (float) Userconfig.lastLocation.getLatitude()),
                            (double) bundle.getFloat("lng", (float) Userconfig.lastLocation.getLongitude()));
                    School school = new School();
                    school.setDetailes(bundle.getInt("schoolId", 0), mapCenter, bundle.getString("name", ""));
                    Marker marker = addMarker(school);
                    selectSchoolMarker(marker);
                } else {
                    GeoPoint mapCenter = new GeoPoint((double) bundle.getFloat("lat", (float) Userconfig.lastLocation.getLatitude()),
                            (double) bundle.getFloat("lng", (float) Userconfig.lastLocation.getLongitude()));
                    mMapView.getController().animateTo(mapCenter);
                }

            }
        }
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (data != null) {
            try {
                Bundle bundle = data.getExtras();
                if (resultCode == RESULT_OK && requestCode == VolleyService.AnnuallyOrderCode) {
                    String temp = (bundle != null ? bundle.getString("INAPP_PURCHASE_DATA") : null);
                    JSONObject payment = null;
                    if (temp != null)
                        payment = new JSONObject(temp);
                    String rescode = null;
                    if (payment != null) {
                        try {
                            rescode = payment.getString("orderId");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "no order id found");
                        }
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userId", Userconfig.userId);
                        jsonObject.put("price", 300000);
                        jsonObject.put("token", Userconfig.token);
                        jsonObject.put("ResCode", rescode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (rescode != null) {
                        VolleyService.getInstance().getJsonObjectRequest(VolleyService.AnnuallyOrderCode, VolleyService.AnnuallyOrderUrl,
                                Request.Method.POST, this, jsonObject, null, null);
                        Userconfig.parent.paymentStatus = "2";
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "شما قبلا با این نام کاربری در بازار پرداخت انجام داده اید", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "no data found");
            }
        }
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ghasedak_link));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }


    class MyLocationOverlay extends MyLocationNewOverlay {

        MyLocationOverlay(MapView mapView) {
            super(mapView);
        }

        @Override
        public void onLocationChanged(Location location, IMyLocationProvider source) {
            if (mainLocation != null)
                if (myLocationOverlay != null) {
                    myLocationOverlay.disableMyLocation();
                }
            if (location != null) {
                mainLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                if (currentMarker != null) {
                    currentMarker.remove(mMapView);
                }
                currentMarker = new Marker(mMapView);
                currentMarker.setPosition(mainLocation);
                currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                currentMarker.setIcon(getResources().getDrawable(R.drawable.ic_current_location));
                mMapView.getOverlays().add(currentMarker);

                Userconfig.setLastLocation(mainLocation);
                if (setLocation) {
                    mMapController.setZoom(18.);
                    mMapController.animateTo(mainLocation);
                }
                setLocation = false;
            }
        }
    }

    /**
     * bottom sheet listener
     */
    double defaultZoom;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int i) {
            if (i == BottomSheetBehavior.STATE_COLLAPSED && mainShowStatus == ADD_LOCATION) {
                studentDetails.setPeekHeight(0);
            }
            if (studentDetails.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                setChevronProgress(0, true);
                if (mMapView != null)
                    defaultZoom = mMapView.getZoomLevelDouble();
            }
            //defaultZoom =mMapView!=null? mMapView.getZoomLevelDouble():0;
            if (studentDetails.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                setChevronProgress(1, true);
                defaultZoom -= 1;
            }

        }

        @Override
        public void onSlide(@NonNull View view, float v) {
            setChevronProgress(v, false);
            if (studentDetails.getState() == BottomSheetBehavior.STATE_COLLAPSED && mMapView != null) {

            }
            if (studentDetails.getState() == BottomSheetBehavior.STATE_EXPANDED && mMapView != null) {

            }
//            if(defaultZoom==0)
////                defaultZoom=mMapView.getZoomLevelDouble();
////            mMapView.getController().setZoom(defaultZoom+v);
        }
    };

    /**
     * delay map listener. this uses for after scroll event on map (special case this listener good for touch finish on map) #dml
     */
    private DelayedMapListener delayedMapListener = new DelayedMapListener(new MapListener() {
        @Override
        public boolean onScroll(ScrollEvent event) {

            if (!clickMarker) {
                if (marker != null)
                    marker.closeInfoWindow();
                if (!schoolSelected)
                    school = null;
                if (schoolMarker != null) {
                    if (schoolMarker.isInfoWindowShown())
                        schoolMarker.closeInfoWindow();
                }
                searchGeoCoder(mMapView.getMapCenter());
                if (mainShowStatus == SELECT_SCHOOL)
                    mainButton.setText(R.string.choice_school);
            }
            clickMarker = false;
            return false;
        }

        @Override
        public boolean onZoom(ZoomEvent event) {
            if (!clickMarker) {
                if (marker != null)
                    marker.closeInfoWindow();
                searchText.setText(R.string.searching);
                if (!schoolSelected)
                    school = null;
                searchGeoCoder(mMapView.getMapCenter());
                if (mainShowStatus == SELECT_SCHOOL)
                    mainButton.setText(R.string.choice_school);
            }
            clickMarker = false;

            return false;
        }
    }, 20);

    /**
     * regular map listener. this listener uses for sense touch and motion on map  #ml
     */
    private MapListener mapListener = new MapListener() {
        @Override
        public boolean onScroll(ScrollEvent event) {

            if (!clickMarker) {
                if (animationNumber == 1) {
                    centreLocation.setAnimation("Pin_animation_close.json");
                    centreLocation.playAnimation();
                    animationNumber = 2;

                }
                schoolSelected = false;
                if (mainShowStatus == SELECT_SCHOOL && alternativeButton.getVisibility() != View.VISIBLE) {
                    alternativeButton.setVisibility(View.VISIBLE);
                }
                if (mainShowStatus == ADD_LOCATION && mainButton.getVisibility() == View.INVISIBLE)
                    mainButton.postDelayed(() -> {
                        mainButton.setVisibility(View.VISIBLE);
                        mainButton.setText(R.string.choice_source_location);
                    }, 250);
            }
            return false;
        }

        @Override
        public boolean onZoom(ZoomEvent event) {
            if (driverMarker != null && driverMarker.isInfoWindowOpen())
                driverMarker.closeInfoWindow();
            if (schoolMarker != null && schoolMarker.isInfoWindowOpen())
                schoolMarker.closeInfoWindow();
            return true;
        }
    };

    /**
     * view pager listener. #vpl
     */
    private ViewPager.OnPageChangeListener studentDetailsPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            changeFragmentAnimationState(false);

            if (i < adaptor.services.size()) {
                selectService = Session.allServiceStudent.get(adaptor.services.get(i));
            } else {
                selectService = null;
            }
            if (studentDetails.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                setChevronProgress(0, true);
            }
            if (studentDetails.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                setChevronProgress(1, true);
            }
            if (selectService != null && (selectService.lastStatusTypeId == 1 || selectService.lastStatusTypeId == 2) && selectService.lastTime != 0) {
                makArrivalText(selectService.lastTime);
            }
            locationFinder.postDelayed(() -> {
                changeFragmentAnimationState(true);
                mMapView.getOverlays().clear();
                setServiceHomeSchoolMarker();
                mMapView.postInvalidate();
            }, 250);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };


    /**
     * menu button listener. #mbl
     */
    private View.OnClickListener menuButtonListener = view -> {
        switch (mainShowStatus) {
            case DEFAULT:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);

                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            case ADD_LOCATION:
            case SELECT_SCHOOL:
//            case ADD_SERVICE:
                onBackPressed();
        }

    };
    private boolean schoolSelected;

    /**
     * main button listener. #mbli
     */
    private View.OnClickListener mainButtonListener = view -> {
        switch (mainShowStatus) {
            case DEFAULT:
                break;
            case ADD_LOCATION:
                if (mMapView != null)
                    addAddress(mMapView.getMapCenter());
                schoolSelected = false;
                break;
            case SELECT_SCHOOL:
                if (school != null) {
                    addStudentDialog();
                    schoolSelected = true;
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "لطفا مدرسه خود انتخاب نمایید", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
        }
    };

    /**
     * alternative button listener. #abl
     */
    private View.OnClickListener alternativeButtonListener = view -> {
        switch (mainShowStatus) {
            case DEFAULT:
                break;
            case ADD_LOCATION:
                break;
            case SELECT_SCHOOL:
                addSchoolDialog();
                break;
        }
    };

    /**
     * find location listener. #fll
     */
    private View.OnClickListener findLocationListener = view -> {
        isPermissionGranted();
        if (myLocationOverlay == null) {
            getLocation();
            return;
        }
        switch (mainShowStatus) {
            case DEFAULT:
                setLocation = true;
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .repeat(1)
                        .playOn(locationFinder);
                if (getLocation()) {
                    if (selectService != null && (selectService.status == 1 || selectService.status == 1)) {
                        mainLocation.setLatitude(selectService.getLatitude());
                        mainLocation.setLongitude(selectService.getLongitude());
                    } else
                        mainLocation = myLocationOverlay.getMyLocation();
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat((float) mMapView.getZoomLevelDouble(), 17.99f);
                    valueAnimator.setDuration(250);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float position = (float) animation.getAnimatedValue();
                            Log.v(TAG, "position=" + position);

//                            mMapView.getController().zoomTo(position);
                            if (mainLocation != null) {
                                mMapView.getController().setZoom(position);
                                mMapView.getController().animateTo(mainLocation);
                            }
                        }
                    });
                    valueAnimator.start();

                }
                if (mainLocation == null) {
                    Toast.makeText(MainActivity.this, R.string.find_your_location, Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case ADD_LOCATION:
                setLocation = true;
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .repeat(1)
                        .playOn(locationFinder);
                if (getLocation()) {
                    mainLocation = myLocationOverlay.getMyLocation();
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat((float) mMapView.getZoomLevelDouble(), 17.99f);
                    valueAnimator.setDuration(450);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float position = (float) animation.getAnimatedValue();
                            Log.v(TAG, "position=" + position);

//                            mMapView.getController().zoomTo(position);
                            if (mainLocation != null) {
                                mMapView.getController().setZoom(position);
                                mMapView.getController().animateTo(mainLocation);
                            }
                        }
                    });
                    if (mainLocation != null) {
                        mMapView.getController().animateTo(mainLocation);
                    }
                    valueAnimator.start();

                }
                if (mainLocation == null) {
                    Toast.makeText(MainActivity.this, R.string.find_your_location, Toast.LENGTH_SHORT).show();
                    return;
                }


                break;
            case SELECT_SCHOOL:
                setLocation = true;
                YoYo.with(Techniques.Pulse)
                        .duration(400)
                        .repeat(1)
                        .playOn(locationFinder);
                if (getLocation()) {
                    mainLocation = myLocationOverlay.getMyLocation();
                    mMapView.getController().setZoom(17.99);
                    if (mainLocation != null)
                        mMapView.getController().animateTo(mainLocation);
                }
                if (mainLocation == null) {
                    Toast.makeText(MainActivity.this, R.string.find_your_location, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mMapView != null) {
                    mMapController.setZoom(17.99);
                    mMapController.animateTo(mainLocation);
                }
                break;

        }
    };

    private void makArrivalText(double duration) {
        if (duration > 60) {
            String time = " زمان تقریبی رسیدن سرویس:  " + (int) duration / 60 + " " + " دقیقه";
            changeFragmentText(time);
        } else {
            changeFragmentText("رسیدن سرویس تا لحظاتی دیگر");
        }
        if (selectService != null)
            selectService.lastTime = (int) duration;
    }

    private void changeFragmentText(String s) {
        for (int i = 0; i < fragmentsHandler.size(); i++) {
            int id = 0;
            if (selectService != null)
                id = selectService.id;
            fragmentsHandler.get(i).detailsText(id, s);
        }
    }

    private void changeFragmentAnimationState(boolean state) {
        for (int i = 0; i < fragmentsHandler.size(); i++) {
            int id = 0;
            if (selectService != null)
                id = selectService.id;
            fragmentsHandler.get(i).animationHandler(id, 0, state);
        }
    }

    private void setChevronProgress(float progress, boolean broadcast) {
        for (int i = 0; i < fragmentsHandler.size(); i++) {
            int id = 0;
            if (selectService != null)
                id = selectService.id;
            fragmentsHandler.get(i).chevronHandler(id, progress, broadcast);
        }
    }

    private void changeFragmentViewIfNecessary(int studentServiceId, int serviceId) {
        boolean flag = false;
        for (int i = 0; i < fragmentsHandler.size(); i++) {
            flag = fragmentsHandler.get(i).notificationChange(studentServiceId, serviceId);
//            if (flag)
//                break;
        }
//        if (!flag)
//            locationFinder.postDelayed(() -> changeFragmentViewIfNecessary(studentServiceId, serviceId), 50);
    }

    Dialog dialogSupport;

    private void supportDialog() {
        dialogSupport = new Dialog(this);
        dialogSupport.setContentView(R.layout.back_up_layout);
        View call, telegram, instagram, linkedin, aparat, close;
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.call_to_support:
                        callToSupport();
                        break;
                    case R.id.telegram_layout:
                        openTelegram();
                        break;
                    case R.id.instagram_layout:
                        openInstagram();
                        break;
                    case R.id.linkedin_layout:
                        openLinkedIn();
                        break;
                    case R.id.aparat_layout:
                        openAparat();
                        break;
                    case R.id.exit_dialog:
                        dialogSupport.dismiss();
                        break;
                }
            }
        };
        call = dialogSupport.findViewById(R.id.call_to_support);
        call.setOnClickListener(clickListener);
        telegram = dialogSupport.findViewById(R.id.telegram_layout);
        telegram.setOnClickListener(clickListener);
        instagram = dialogSupport.findViewById(R.id.instagram_layout);
        instagram.setOnClickListener(clickListener);
        linkedin = dialogSupport.findViewById(R.id.linkedin_layout);
        linkedin.setOnClickListener(clickListener);
        aparat = dialogSupport.findViewById(R.id.aparat_layout);
        aparat.setOnClickListener(clickListener);
        close = dialogSupport.findViewById(R.id.exit_dialog);
        close.setOnClickListener(clickListener);
        dialogSupport.show();
    }

    private void openAparat() {
        openApps("https://www.aparat.com/ghasedakservice");
    }

    private void openLinkedIn() {
        openApps("https://www.linkedin.com/in/ghasedakservice");
    }

    private void openInstagram() {
        openApps("https://www.instagram.com/ghasedakservice");
    }

    private void openTelegram() {
        openApps("https://t.me/ghasedakservice");
    }

    private void callToSupport() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "05136674620"));
        MainActivity.this.startActivity(intent);
    }

    private void openApps(String url) {
        Uri uri = Uri.parse(url);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe)
            startActivity(likeIng);
        else
            Toast.makeText(this, "شما نرم افزار مربوطه را در گوشی خود نصب ننموده اید.", Toast.LENGTH_SHORT).show();
    }


}
