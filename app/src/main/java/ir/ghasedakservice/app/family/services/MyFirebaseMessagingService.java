package ir.ghasedakservice.app.family.services;


import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ir.ghasedakservice.app.family.activities.SplashActivity;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;
import io.fabric.sdk.android.Fabric;

import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.models.SchoolService;
import ir.ghasedakservice.app.family.models.SchoolServiceStuent;


public class MyFirebaseMessagingService extends FirebaseMessagingService implements ResponseHandler {

    private static final String TAG = "MyFirebaseMsgService";

    Session session;
    public static HashMap<String, NotificationObserver> observers = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        session = GhasedakApplication.session;

    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Userconfig.setNewTokenApp(Userconfig.tokenAPP, s);
//
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
        GhasedakApplication.sendRegistrationToServer();
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Log.d(TAG, "From: onSendError");

    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d(TAG, "From: onMessageSent");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (Userconfig.userId == null || Userconfig.token == null || Userconfig.token.length() < 5)
            return;

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> jsonObject = remoteMessage.getData();
//            boolean oo = jsonObject.containsKey("oo");
            JSONObject a = new JSONObject(jsonObject);


            try {
                if (a.has("StatusTypeId")) {
                    int serviceId = 0;
                    int statusTypeId = a.getInt("StatusTypeId");
                    int studentServiceId = 0;

                    SchoolServiceStuent schoolServiceStuent = null;
                    if (a.has("StudentServiceId")) {
                        serviceId = a.getInt("ServiceId");
                        studentServiceId = a.getInt("StudentServiceId");
                        if (studentServiceId == 0) {
                            Vector<SchoolServiceStuent> schoolServiceStuents = new Vector<>(Session.allServiceStudent.values());
                            SchoolService mService = Session.allService.get(serviceId);
                            if (mService != null)
                                mService.lastStatusTypeId = statusTypeId;
                            for (int i = 0; i < schoolServiceStuents.size(); i++) {
                                if (schoolServiceStuents.get(i).service != null && schoolServiceStuents.get(i).service.id == serviceId) {
                                    schoolServiceStuent = schoolServiceStuents.get(i);
                                    schoolServiceStuent.lastStatusTypeId = statusTypeId;
                                }
                            }
                        } else {
                            schoolServiceStuent = Session.allServiceStudent.get(studentServiceId);
                            schoolServiceStuent.lastStatusTypeId = statusTypeId;
                            String text = schoolServiceStuent.service.lastStatusTypeId == 1 ? getString(R.string.your_student_get_on) : getString(R.string.your_student_get_off);
                            if (schoolServiceStuent.lastStatusTypeId == 5)
                                text = getString(R.string.student_is_absent);
                            sendNotification(text, getString(R.string.ghasedak), null);
                        }
                        if (statusTypeId < 4) {
                            if (a.has("ServiceId")) {
                                try {
                                    double lat = a.getDouble("Lat");
                                    double lng = a.getDouble("Lng");
                                    SchoolService schoolService = Session.allService.get(serviceId);
                                    if (schoolService != null) {
                                        schoolService.lat = (float) lat;
                                        schoolService.lng = (float) lng;
                                        schoolService.lastStatusTypeId = statusTypeId;
                                        if (statusTypeId == 1 || statusTypeId == 2) {
                                            Session.allRunnigService.put(serviceId, schoolService);
                                        } else if (statusTypeId == 3) {
                                            Session.allRunnigService.remove(serviceId);

                                        }
                                    }
                                    String text = (statusTypeId == 1) ? getString(R.string.begin_went_service) : (statusTypeId == 2) ? getString(R.string.begin_back_service) : (statusTypeId == 4) ? getString(R.string.your_student_get_on) : getString(R.string.service_done);
                                    sendNotification(text, getString(R.string.ghasedak), null);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Vector<NotificationObserver> observ = new Vector<>();
                        observ.addAll(observers.values());
                        for (int o = 0; o < observ.size(); o++) {
                            try {
                                NotificationObserver notificationObserver = observ.get(o);
                                if (notificationObserver != null)
                                    notificationObserver.refresh(studentServiceId, serviceId, statusTypeId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }


//                    Vector<NotificationObserver> observ = new Vector<>();
//                    observ.addAll(observers.values());
//                    for (int o = 0; o < observ.size(); o++) {
//                        try {
//                            NotificationObserver notificationObserver = observ.get(o);
//                            if (notificationObserver != null)
//                                notificationObserver.refresh(0,);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }

                }
                else if (a.has("StudetnServiceId")) {
                    //Todo ------
                    sendParentData(a.getString("StudetnServiceId"));
                    int studentServiceId = a.getInt("StudetnServiceId");
                    sendNotification(getString(R.string.driver_confirm_your_service), getString(R.string.ghasedak), null);
                    Vector<NotificationObserver> observ = new Vector<>(observers.values());
                    for (int o = 0; o < observ.size(); o++) {
                        try {
                            NotificationObserver notificationObserver = observ.get(o);
                            if (notificationObserver != null)
                                notificationObserver.driverAccepted(studentServiceId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }

                if (a.has("message")) {
                    JSONObject b = a.getJSONObject("message");
                    String message = b.optString("message", null);
                    if (a.has("fileApk")) {
                        String fileApk = a.getString("fileApk");
                        sendNotification(message, getString(R.string.ghasedak), fileApk);
                    } else {
                        if (message != null)
                            sendNotification(message, getString(R.string.ghasedak), null);
                        if (b.has("dialog")) {
                            showDialog(b.getJSONObject("dialog"));
                        }
                    }

                }
                if (a.has("adminpanel")) {
                    sendNotification(a.getString("adminpanel"), getString(R.string.ghasedak), null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


    private void sendParentData(String studentServiceId) {

        final JSONObject js = new JSONObject();
        try {
            js.put("userId", Userconfig.userId);
            js.put("token", Userconfig.token);
//            Log.e(TAG, "userId: " + Integer.parseInt(session.getUserID()) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyService.getInstance().getJsonObjectRequest(VolleyService.ParentDataCode, VolleyService.ParentDataUrl,
                Request.Method.POST, this, js, studentServiceId, null);

    }

    private void sendNotification(String messageBody, String messageTitle, String apkFile) {

        Intent intent;
        PendingIntent pendingIntent;
        if (apkFile == null) {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else {
            intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("apkFile", apkFile);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

        }

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_scort_notification)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true).setSmallIcon(R.mipmap.ic_parent_launcher)
                        .setSound(defaultSoundUri).setVibrate(new long[]{500, 1000});
        if (!Config.isAppRunning(this, getPackageName())) {
            notificationBuilder.setContentIntent(pendingIntent);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && apkFile != null) {
            notificationBuilder = notificationBuilder.addAction(R.mipmap.ic_stat_name, getString(R.string.download), pendingIntent);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {
        if (requestCode == VolleyService.ParentDataCode) {
            if (responseStatusCode == VolleyService.OK_STATUS_CODE) {
                try {
                    JSONObject result = responseJson;
                    if (result.getString("status").equals("success")) {//when result true
                        JSONObject data1 = result.getJSONObject("data");
                        Config.fillParentDate(data1);
                        Vector<NotificationObserver> observe = new Vector<>();
                        observe.addAll(observers.values());
                        for (int o = 0; o < observe.size(); o++) {
                            try {
                                NotificationObserver notificationObserver = observe.get(o);
                                if (notificationObserver != null) {
                                    String a = (String) requestObject;
                                    SchoolServiceStuent schoolServiceStuent = Session.allServiceStudent.get(a);

                                    notificationObserver.refresh(0, 0, 0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showDialog(JSONObject js) {
        Vector<NotificationObserver> observe = new Vector<>(observers.values());
        for (int o = 0; o < observe.size(); o++) {
            try {
                NotificationObserver notificationObserver = observe.get(o);
                if (notificationObserver != null) {
                    notificationObserver.showDialog(js);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseError, Object requestObject) {

    }
}