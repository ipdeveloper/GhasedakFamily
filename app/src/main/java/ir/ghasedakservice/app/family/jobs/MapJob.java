package ir.ghasedakservice.app.family.jobs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;


import java.util.ArrayList;
import java.util.Vector;

import ir.ghasedakservice.app.family.models.AddressLocation;
import ir.ghasedakservice.app.family.models.School;
import ir.ghasedakservice.app.family.models.SchoolServiceStuent;
import ir.ghasedakservice.app.family.R;

public class MapJob implements Marker.OnMarkerClickListener {

    private Vector<Marker> preMarker;
    private Marker driverMarker, homeMarker, schoolMarker, currentMarker;
    private MapView mMapView;
    private Context context;
    private boolean goToDriverLocation, toggle, enableAlarmDistance;

    public MapJob(Context context) {
        this.preMarker = new Vector<>();
        this.mMapView = new MapView(context);
        this.driverMarker = new Marker(mMapView);
        this.homeMarker = new Marker(mMapView);
        this.schoolMarker = new Marker(mMapView);
        this.currentMarker = new Marker(mMapView);
        this.context = context;
    }

    public void addMarker(Object tag) {

    }

    public void clearAllMarkers() {

    }

    /**
     * in this method we add schools as markers to map
     *
     * @param school location and school name
     */
    private void addMarker(School school) {
        Marker marker = new Marker(mMapView);
        marker.setPosition(new GeoPoint(school.lat, school.lng));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTag(school);
        marker.setIcon(context.getResources().getDrawable(R.drawable.school_pin));
        marker.setOnMarkerClickListener(this);
        marker.setTitle(school.name);
        mMapView.getOverlays().add(marker);
        preMarker.add(marker);
    }

    void alarmDistance(GeoPoint startPoint, GeoPoint endPoint, int distance) {
        if (startPoint.distanceToAsDouble(endPoint) < distance && distance > 1) {
            String channelId = context.getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setContentTitle(context.getString(R.string.ghasedak))
                            .setContentText(context.getString(R.string.your_student_service_arrived))
                            .setAutoCancel(true).setSmallIcon(R.mipmap.ic_parent_launcher)
                            .setSound(defaultSoundUri);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

    public void setDriverMarker(double lat, double lng, boolean mapMove, SchoolServiceStuent selectService) {
        if (mMapView == null) {
            return;
        }
        clearSchoolMarkers();
        GeoPoint startPoint = new GeoPoint(lat, lng);
        if (selectService.lastStatusTypeId != 4 && selectService.maxDistance > 0) {
            if (selectService.service.lastStatusTypeId == 1) {
                AddressLocation srcAddress = selectService.getSrcAddress();
                GeoPoint endPoint = new GeoPoint(srcAddress.lat, srcAddress.lng);
                if (enableAlarmDistance)
                    alarmDistance(startPoint, endPoint, selectService.maxDistance);
                selectService.maxDistance = selectService.maxDistance - 250;

            } else if (selectService.service.lastStatusTypeId == 2) {
                AddressLocation desAddress = selectService.getDesAddress();
                GeoPoint endPoint = new GeoPoint(desAddress.lat, desAddress.lng);
                if (enableAlarmDistance)
                    alarmDistance(startPoint, endPoint, selectService.maxDistance);
                selectService.maxDistance = selectService.maxDistance - 250;
            }
        }
        if (goToDriverLocation) {
            this.goToDriverLocation = false;
            mMapView.getController().setZoom(17.99);
            mMapView.getController().animateTo(startPoint);
        }
        if (toggle) {
            mMapView.getController().setZoom(17.99);
            mMapView.getController().animateTo(startPoint);
        }
        if (driverMarker != null) {
            driverMarker.remove(mMapView);
        }
        driverMarker = new Marker(mMapView);
        driverMarker.setPosition(startPoint);
        Log.v("Map Job", "" + lat + " " + lng + "................................");
        driverMarker.setIcon(context.getResources().getDrawable(R.drawable.ic_driver_marker));
        driverMarker.setOnMarkerClickListener((marker, mapView) -> {
            marker.showInfoWindow();
            marker.setTitle("آقا/خانم" + " " + selectService.getDriver().family);
            mapView.getController().animateTo(marker.getPosition());
            return true;
        });
        mMapView.getOverlays().add(driverMarker);
        mMapView.invalidate();
        if (mapMove) {
            animateMarker(driverMarker, startPoint);
        }
    }

    /**
     * show search school on map
     */
    public void showSchools(Vector<School> schools, SchoolServiceStuent selectService, School mSchool, AddressLocation addressLocation) {
        clearSchoolMarkers();
        for (int i = 0; i < schools.size(); i++) {
            School school = schools.get(i);
            addMarker(school);
        }
        setServiceHomeSchoolMarker(selectService, mSchool, addressLocation);
        mMapView.invalidate();

    }

    public void setServiceHomeSchoolMarker(SchoolServiceStuent selectService, School school, AddressLocation addressLocation) {
        if (schoolMarker != null) {
            schoolMarker.remove(mMapView);
            schoolMarker = null;
        }
        if (homeMarker != null) {
            homeMarker.remove(mMapView);
            homeMarker = null;
        }

        if (selectService != null) {
            schoolMarker = new Marker(mMapView);
            schoolMarker.setPosition(new GeoPoint(selectService.getSchool().lat, selectService.getSchool().lng));
            schoolMarker.setIcon(context.getResources().getDrawable(R.drawable.school_pin));
            mMapView.getOverlays().add(schoolMarker);


            homeMarker = new Marker(mMapView);
            homeMarker.setPosition(new GeoPoint(selectService.getLatitude(), selectService.getLongitude()));
            homeMarker.setIcon(context.getResources().getDrawable(R.drawable.ic_student));
            homeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            homeMarker.setTitle("خانه");
            mMapView.getOverlays().add(homeMarker);


            if (selectService.lastStatusTypeId == 1 || selectService.lastStatusTypeId == 2) {
                setDriverMarker(selectService.service.lat, selectService.service.lng, true, selectService);
            }
            ArrayList<GeoPoint> points = new ArrayList<>();
            points.add(new GeoPoint(selectService.getSchool().lat, selectService.getSchool().lng));
            points.add(new GeoPoint(selectService.getLatitude(), selectService.getLongitude()));
            if (selectService.service != null)
                points.add(new GeoPoint(selectService.service.lat, selectService.service.lng));

            mMapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 100);


        } else {
            if (school != null) {
                schoolMarker = new Marker(mMapView);
                schoolMarker.setPosition(new GeoPoint(school.lat, school.lng));
                schoolMarker.setTag(school);
                schoolMarker.setIcon(context.getResources().getDrawable(R.drawable.school_pin));
                mMapView.getOverlays().add(schoolMarker);
            }
            if (addressLocation != null) {
                homeMarker = new Marker(mMapView);
                homeMarker.setPosition(new GeoPoint(addressLocation.lat, addressLocation.lng));
                homeMarker.setIcon(context.getResources().getDrawable(R.drawable.ic_student));
                homeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                homeMarker.setTitle("خانه");
                mMapView.getOverlays().add(homeMarker);
            }
            if (addressLocation != null && school != null) {
                ArrayList<GeoPoint> points = new ArrayList<>();
                points.add(homeMarker.getPosition());
                points.add(new GeoPoint(school.lat, school.lng));
                mMapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 200);
            }


        }
    }

    public void clearSchoolMarkers() {

        for (Marker marker : preMarker) {
            marker.remove(mMapView);
        }
        preMarker.clear();
        mMapView.getOverlays().clear();

//        if (marker != null) {
//            marker.remove(mMapView);
//            marker = null;
//        }
        if (driverMarker != null) {
            driverMarker.remove(mMapView);
            driverMarker = null;
        }
//        if (line != null) {
//            mMapView.getOverlayManager().remove(line);
//            line = null;
//
//        }
        if (schoolMarker != null) {
            schoolMarker.remove(mMapView);
            schoolMarker = null;
        }
        if (homeMarker != null) {
            homeMarker.remove(mMapView);
            homeMarker = null;
        }
        if (mMapView != null)
            mMapView.postInvalidate();


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
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        return false;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public void setGoToDriverLocation(boolean goToDriverLocation) {
        this.goToDriverLocation = goToDriverLocation;
    }

    public void setEnableAlarmDistance(boolean enableAlarmDistance) {
        this.enableAlarmDistance = enableAlarmDistance;
    }
}
