package ir.ghasedakservice.app.family.utility;

/**
 * Created by MA on 3/4/2018.
 */


import android.content.SharedPreferences;



import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;

import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.models.Parent;


public class Userconfig
{

    public static String userId;
    public static Date registerDate;

    public static int cityId;
    public static Parent parent;


    //tokens
    public static boolean userTokenServices;
    public static String token;
    public static String tokenAPP;
    public static String oldTokenAPP;


    public static String firstName;
    public static String paymentStatus;
    public static String lastName;
    public static String mobile;
    public static GeoPoint lastLocation;
    public static String lastCity;


    public static void saveUserConfig()
    {

        SharedPreferences Scort = GhasedakApplication.ghasedakPreferences;
        SharedPreferences.Editor edit = Scort.edit();

        edit.putString("userId", userId);
        edit.putInt("cityId", cityId);
        edit.putString("name", firstName);
        edit.putString("family", lastName);
        edit.putString("token", token);
        edit.putString("tokenApp", tokenAPP);
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String registerDate = format.format(Userconfig.registerDate);
            edit.putString("registerDate", registerDate);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        edit.putString("oldTokenApp", oldTokenAPP);
        loadParent();

        edit.commit();
    }

    static void loadParent()
    {
        Parent a = new Parent();
        a.id = Userconfig.userId;
        a.name = Userconfig.firstName;
        a.family = Userconfig.lastName;
        a.mobile = Userconfig.mobile;
        a.paymentStatus=Userconfig.paymentStatus;
        a.registerDate=Userconfig.registerDate;
        Session.allParent.put(a.id, a);
        Userconfig.parent = a;
    }

    public static void loadConfig()
    {
        SharedPreferences Scort = GhasedakApplication.ghasedakPreferences;
        userId = Scort.getString("userId", null);
        token = Scort.getString("token", null);
        cityId = Scort.getInt("cityId", 0);
        try
        {
            String registerDate = Scort.getString("registerDate", "");
            if(registerDate!=null && registerDate.trim().length()>3) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                Userconfig.registerDate = format.parse(registerDate);
            }
        } catch (Exception e)
        {
            e.printStackTrace();

        }
        Userconfig.firstName = Scort.getString("name", null);
        Userconfig.lastName = Scort.getString("family", null);
        oldTokenAPP = Scort.getString("oldtokenApp", null);//getOldTokenapp
        tokenAPP = Scort.getString("tokenApp", "2222222222");
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>()
//        {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult)
//            {
//                String deviceToken = instanceIdResult.getToken();
//                if (tokenAPP == null)
//                {
//                    setTokenApp(deviceToken);
//                } else if (!tokenAPP.equals(deviceToken))
//                {
//                    setNewTokenApp(oldTokenAPP, deviceToken);
//                }
//                GhasedakApplication.sendRegistrationToServer();
//            }
//        });
        float lat = Scort.getFloat("lat", 0);
        float lng = Scort.getFloat("lng", 0);
        lastLocation = new GeoPoint(0., 0.);
        lastLocation.setLatitude(lat);
        lastLocation.setLongitude(lng);
        loadParent();


    }


    public static void setLastLocation(IGeoPoint target)
    {
        lastLocation.setLatitude(target.getLatitude());
        lastLocation.setLongitude(target.getLongitude());

        SharedPreferences.Editor edit = GhasedakApplication.ghasedakPreferences.edit();
        edit.putFloat("lat", (float) lastLocation.getLatitude());
        edit.putFloat("lng", (float) lastLocation.getLongitude());
        edit.apply();


    }

    public static void fillFromJson(JSONObject data)
    {
        try
        {
            Userconfig.firstName = data.getString("name");
            Userconfig.lastName = data.getString("family");
            Userconfig.mobile = data.getString("mobile");
            String o = data.getString("cityId");
            if (o != null && !o.equals("null"))
            {
                Userconfig.cityId = Integer.parseInt(o);
            }
            try
            {
                String registerDate = data.getString("registerDate").substring(0, 11);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Userconfig.registerDate = format.parse(registerDate);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            if (Userconfig.registerDate == null)
            {
                try
                {
                    String registerDate = data.getString("registerDate");
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    Userconfig.registerDate = format.parse(registerDate);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            paymentStatus=data.optString("paymentStatus");


            JSONArray userToken = data.getJSONArray("userToken");
            for (int i = 0; i < userToken.length(); i++)
            {
                JSONObject jsonObject = userToken.getJSONObject(i);
//                if(jsonObject.getString("tokenApp").equals(tokenAPP))
                {
                    Userconfig.userId = jsonObject.getString("userId");
                    setToken(jsonObject.getString("token"));
                    break;
                }
            }


//            Userconfig.rate = data.getInt("rate") + "";

//            Userconfig.totalAsset = data.getInt("totalAsset") ;


        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Userconfig.saveUserConfig();
    }


    public static void logout()
    {


        Userconfig.firstName = null;
        Userconfig.userId = null;
        Userconfig.cityId = 0;
        Userconfig.token = null;
        Userconfig.lastName = null;
        //  Session.allCity.clear();
        //Session.allState.clear();
        Session.allSchool.clear();
        Session.allStudent.clear();
        Session.allParent.clear();
//        Session.allVehicle.clear();
        Session.allServiceStudent.clear();
        Session.allService.clear();
        Session.allRunnigService.clear();
        Session.allDriver.clear();
        Userconfig.saveUserConfig();

    }


    public static void setToken(String token)
    {
        GhasedakApplication.ghasedakPreferences.edit().putString("token", token).apply();
        Userconfig.token = token;
    }

//    public static void setTokenApp(String token)
//    {
//        GhasedakApplication.ghasedakPreferences.edit().putString("tokenApp", token).apply();
//        Userconfig.tokenAPP = token;
//    }


    public static void setUserTokenServices(boolean userTokenServices)
    {
        GhasedakApplication.ghasedakPreferences.edit().putBoolean("userTokenServices", userTokenServices).apply();
        Userconfig.userTokenServices = userTokenServices;
    }

    public static void setNewTokenApp(String oldTokenApp, String newApptoken)
    {
        Userconfig.setUserTokenServices(true);
        SharedPreferences.Editor edit = GhasedakApplication.ghasedakPreferences.edit();
        edit.putString("oldtokenAp", oldTokenApp);
        Userconfig.oldTokenAPP = Userconfig.tokenAPP;
        edit.putString("tokenApp", newApptoken);
        Userconfig.tokenAPP = newApptoken;
        edit.apply();
    }


//

}
