package ir.ghasedakservice.app.family;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;
import ir.ghasedakservice.app.family.services.VolleyService;

public class GhasedakApplication extends android.support.multidex.MultiDexApplication {

    public static long timeRequest=9000;
    public static Session session;
    public static SharedPreferences ghasedakPreferences;
    public static GhasedakSqliteOpenHelper database;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        ghasedakPreferences = this.getSharedPreferences("ghasedak", MODE_PRIVATE);
        session = Session.getInstance(ghasedakPreferences);
        database=GhasedakSqliteOpenHelper.getInstance();
        Userconfig.loadConfig();

    }
    public static  void sendRegistrationToServer() {
        if(!Userconfig.userTokenServices || Userconfig.token==null || Userconfig.oldTokenAPP==null || Userconfig.tokenAPP!=null ||  Userconfig.tokenAPP.equals(Userconfig.oldTokenAPP))
            return;

        JSONObject js = new JSONObject();
        try {
            js.put("userId", Userconfig.userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, VolleyService.changeUserTokenUrl, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = new JSONObject(response.toString());

                            if (result.getString("status").equals("success")) {
                                Userconfig.setUserTokenServices(false);
                            } else {
                                Userconfig.setUserTokenServices(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Userconfig.setUserTokenServices(true);
//                if (error.toString().contains("Network is unreachable")) {
//                    Userconfig.setUserTokenServices(true);
//                } else {
//                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Userconfig.token);
                headers.put("tokenAppOld", Userconfig.oldTokenAPP );
                headers.put("tokenAppNew", Userconfig.tokenAPP);
                return headers;
            }


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjReq);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
