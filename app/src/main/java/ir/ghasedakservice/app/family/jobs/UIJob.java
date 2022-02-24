package ir.ghasedakservice.app.family.jobs;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.airbnb.lottie.LottieAnimationView;

import com.airbnb.lottie.LottieAnimationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;

public class UIJob {
    private static final String TAG =UIJob.class.getSimpleName() ;
    private HashMap<String, TextView> textViewHashMap;
    private HashMap<String, EditText> editTextHashMap;
    private HashMap<String, ImageView> imageViewHashMap;
    private HashMap<String, LinearLayout> linearLayoutHashMap;
    private HashMap<String, RelativeLayout> relativeLayoutHashMap;
    private HashMap<String,LottieAnimationView> lottieAnimationViewHashMap;
    private MapView mMapView;
    private IMapController mMapController;
    private Context context;
    private BottomSheetBehavior studentDetailed;
    private Marker studentMarker,driverMarker;


    public UIJob(Context context) {
        textViewHashMap = new HashMap<>();
        imageViewHashMap = new HashMap<>();
        linearLayoutHashMap = new HashMap<>();
        relativeLayoutHashMap = new HashMap<>();
        //lottieAnimationViewHashMap=new HashMap<>();
        editTextHashMap = new HashMap<>();
        this.context = context;
    }

    public void setViewGroupLayout(String name, View mainLayout) {
        if (mainLayout instanceof RelativeLayout)
            relativeLayoutHashMap.put(name, (RelativeLayout) mainLayout);
        if (mainLayout instanceof LinearLayout)
            linearLayoutHashMap.put(name, (LinearLayout) mainLayout);
    }

    public void assignBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        if (studentDetailed == null) {
            studentDetailed = bottomSheetBehavior;
        }
    }

    public void initMap(String mainLayoutName) {
        mMapView = new MapView(context);
        if(relativeLayoutHashMap.get(mainLayoutName)!=null)
            relativeLayoutHashMap.get(mainLayoutName).addView(mMapView, 0);
        else{
            Log.e(TAG,"input name for map main layout not assign or main layout is not relative layout");
            return;
        }
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(0XFFE0DFDF);
        mMapView.getOverlayManager().getTilesOverlay().setLoadingLineColor(0XFFE0DFDF);
        mMapView.setMaxZoomLevel(18.0);
        mMapView.setMinZoomLevel(5.0);
        mMapController = mMapView.getController();
        mMapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (studentMarker != null && studentMarker.isInfoWindowOpen())
                    studentMarker.closeInfoWindow();
                if (driverMarker != null && driverMarker.isInfoWindowOpen())
                    driverMarker.closeInfoWindow();
                return true;
            }
        });
        mMapController.setZoom(18.0);
        GeoPoint startPoint = new GeoPoint(35.744754, 51.375218);
        mMapController.setCenter(startPoint);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
    }


}
