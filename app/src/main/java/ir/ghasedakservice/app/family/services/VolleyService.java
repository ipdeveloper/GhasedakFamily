package ir.ghasedakservice.app.family.services;


import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.Consts;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ir.ghasedakservice.app.family.utility.Userconfig;
import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.models.Student;


public class VolleyService {
    public static final String TAG = VolleyService.class.getSimpleName();
    public static final String BAZZAR_APK="0-";
    public static final String WEB_SITE="1-";
    public static final String BADE_SABA="2-";
    public static final String OTHER_1="3-";
    public static final String OTHER_2="4-";
    public static final String CURRENT_RESOURCE=BAZZAR_APK;



    public static final String FAG_LINK = "http://ghasedakservice.ir/Home/TroubleshootAppParent";

    public static int ERROR_STATUS_CODE = -1;


    /**
     * {@OK_CODE} vaghti az service javab bargash in code be darkhast konnande pas dade mishe
     */
    public static int OK_STATUS_CODE = 1;


    static VolleyService instance;
    private RequestQueue mRequestQueue;
    public static final String SERVERKEY = "AIzaSyDy5Zpm8KxzdMbKcF7mCrFnbYPWEPf9hN0";
    public static final String ServerIP = "http://185.252.28.132:8075/api/";
    public static final String ServerImageIP = "http://185.252.28.132:8075/";
//
//    public static final String GoogleMapmageAddress = "https://maps.googleapis.com/maps/api/staticmap?size=800x400" +
//            "&zoom=11&color:0xEE0000";
//
//    //    public static final String ServerIP = "http://192.168.1.34:11679/api/";
    public static int RegisterRequestCode = 0;
    public static String RegisterRequestUrl = ServerIP + "Login";

    public static int CheckCodeRequestCode = 1;
    public static String CheckCodeRequestUrl = ServerIP + "CheckCode";

    public static int AppVersionRequestCode = 2;
    public static String AppVersionRequestUrl = ServerIP + "AppVersion";

    public static int changeUserTokenCode = 9;
    public static String changeUserTokenUrl = ServerIP + "ChangeApplicationToken";

    public static int SearchSchoolsCode = 10;
    public static String SearchSchoolsUrl = ServerIP + "SearchSchools";

    public static int CustomerEditCode = 11;
    public static String CustomerEditeUrl = ServerIP + "DriverParentEditeProfile";

    public static int AddStudentCode = 13;
    public static String AddStudentUrl = ServerIP + "StudentAddWithParent";

    public static int SchoolListCode = 15;
    public static String SchoolListUrl = ServerIP + "SchoolList";

    public static int ServiceSchoolStudentCode = 16;
    public static String ServiceSchoolStudentUrl = ServerIP + "StudentServiceAddByParent";

    public static int SchoolAddCode = 17;
    public static String SchoolAddUrl = ServerIP + "SchoolAdd";


    public static int RC_REQUEST = 10001;
    public static String PAYMENT_BASE64 = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCnAd43HeY0P4S5RmtuqSNSYXZZZ+DpqX1fpgqVxYslfvNpLAVkPaNTJYGi23huNFg72ZQd/8gKEfHV+r5/GDw0zfxc82ZjscVjgt63BlKC0e5Pof3ItfSL223koqS9B8Oetruv9qsybdr2rawHoC02B6IoFKr0bS7c5lG9ODI8BXePQ19Hfs/DNRhgNtH/LAC9pI+DwymS3uCN4W9SGLaLFzR8cXVub8f7iagdVT0CAwEAAQ==";
    public static String SKU_PREMIUM = "ghasedakservicefamily30000";


    public static int CustomerInfoCode = 14;
    public static String CustomerInfoUrl = ServerIP + "UserInfo";

    public static int ServiceSchoolEditStudent = 21;
    public static String ServiceSchoolStudentEditUrl = ServerIP + "StudentServiceEditeByParent";

    public static int SearchWithMobileCode = 40;
    public static String SearchWithMobileUrl = ServerIP + "GetParentAndDriverInfoWhithMobile";

    public static int ParentDataCode = 20;
    public static String ParentDataUrl = ServerIP + "ParentData";

    public static int GeoCoderCode = 18;
    public static String GeoCoderUrl = "http://185.252.28.133/reverse.php?format=json&accept-language=fa";//&lat=%.4f&lon=%.4f

    public static int RouteCode = 19;
    public static String RouteUrl = "http://185.252.28.133:5000/route/v1/driving/";

    public static int SearchAddressCode = 60;
    public static String SearchAddressUrl = "http://185.252.28.133/search.php?format=jsonv2&accept-language=fa&q=";

    public static int GetDriversLocationCode = 41;
    public static String GetDriversLocationUrl = ServerIP + "GetLocationServices";

    public static int ServiceStudentAddCode = 42;
    public static String ServiceStudentAddUrl = ServerIP + "ServiceStudentAdd";

    public static int CreditIncreaseCode = 50;
    public static String CreditIncreaseUrl = ServerIP + "UserCredite";
    public static int PRICE = 30000;

    public static int ServiceInfoCode = 45;
    public static String ServiceInfoUrl = ServerIP + "GetServiceStudentById";

    public static int AnnuallyOrderCode = 10001;
    public static String AnnuallyOrderUrl = ServerIP + "AnnuallyOrder";


    public static int LogOutCode = 22;
    public static String LogOutUrl = ServerIP + "logout";

    public static int StudentServiceDeleteCode = 51;
    public static String StudentServiceDeleteUrl = ServerIP + "StudentServiceDelete";


    public static int ServiceInformationCode = 52;
    public static String ServiceInformationUrl = ServerIP + "ServiceInformation";

    public static String DownloadAppLink = "http://185.252.28.132:8033/Home/DownloadApp";




    private VolleyService() {
    }


    public static VolleyService getInstance() {
        if (instance == null) {
            instance = new VolleyService();
        }
        return instance;
    }

    public String getJsonObjectRequest(final int requestCode, String url, int method, final ResponseHandler handler, JSONObject data,
                                       final Object requestDate, final HashMap header) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(method, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (handler == null)
                            return;

                        if (response == null) {
                            handler.onJsonObjectResult(requestCode, ERROR_STATUS_CODE, response, requestDate);
                        }
                        try {
                            handler.onJsonObjectResult(requestCode, OK_STATUS_CODE, response, requestDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.onJsonObjectResult(requestCode, ERROR_STATUS_CODE, response, requestDate);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    byte[] data1 = networkResponse.data;
                    String a = new String(data1);
                    if (handler != null) {//com.android.volley.TimeoutError
                        try {
                            handler.onJsonObjectResult(requestCode, ERROR_STATUS_CODE, new JSONObject(a), requestDate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.onJsonObjectResult(requestCode, ERROR_STATUS_CODE, null, requestDate);
                        }
                    }
                } else {
                    if (handler != null)
                        handler.onJsonErrorResponse(requestCode, ERROR_STATUS_CODE, error + "", requestDate);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (header == null)
                    return super.getHeaders();
                else return header;
            }
        };


//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy());
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        String code = "" + System.currentTimeMillis();
        addToRequestQueue(jsonObjReq, code);
        return code;
    }






    private com.android.volley.toolbox.ImageLoader mImageLoader;

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(GhasedakApplication.context);
        }

        return mRequestQueue;
    }



    public com.android.volley.toolbox.ImageLoader getImageLoader() {
//        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new com.android.volley.toolbox.ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}





