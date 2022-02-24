package ir.ghasedakservice.app.family.utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.models.Driver;
import ir.ghasedakservice.app.family.models.Parent;
import ir.ghasedakservice.app.family.models.School;
import ir.ghasedakservice.app.family.models.SchoolService;
import ir.ghasedakservice.app.family.models.SchoolServiceStuent;
import ir.ghasedakservice.app.family.models.Student;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.models.Vehicle;

public class Config {
    public static Configuration changeLanguage(String language){
        String languageToLoad = language; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        return config;
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public static Dialog confirmDialg2(Context context, String titleText, String text, View.OnClickListener accept, View.OnClickListener cancel) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.custom_dialog);
        TextView title = dialog.findViewById(R.id.title_text);
        title.setText(titleText);
        TextView body = dialog.findViewById(R.id.body_text);
        body.setText(text);
        TextView yes = dialog.findViewById(R.id.yes_button);
        yes.setOnClickListener(accept);
        TextView no = dialog.findViewById(R.id.no_button);
        no.setOnClickListener(cancel);
        dialog.show();
        return dialog;
    }
    public static Bitmap getCorrectRotation(Context context, Bitmap bm, Uri uri) {
        int orientation;
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 4;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;

            Bitmap bitmap = bm;

            InputStream input = context.getContentResolver().openInputStream(uri);
            ExifInterface exif;
            if (Build.VERSION.SDK_INT > 23)
                exif = new ExifInterface(input);
            else
                exif = new ExifInterface(uri.getPath());

            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m=new Matrix();

            if((orientation==3)){
                m.postRotate(180);
                m.postScale((float)bm.getWidth(), (float)bm.getHeight());
                bitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), m, true);
                return  bitmap;
            }
            else if(orientation==6){
                m.postRotate(90);
                bitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), m, true);
                return  bitmap;
            }
            else if(orientation==8){
                m.postRotate(270);
                bitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), m, true);
                return  bitmap;
            }
            return bitmap;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Dialog confirmDialg(Context context, String titleText, String text, View.OnClickListener accept, View.OnClickListener cancel)
    {
        final Dialog dialog=new Dialog(context);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_exit);
        TextView title=dialog.findViewById(R.id.title_text);
        title.setText(titleText);
        TextView body=dialog.findViewById(R.id.body_text);
        body.setText(text);
        TextView yes=dialog.findViewById(R.id.yes_button);
        yes.setOnClickListener(accept);
        TextView no=dialog.findViewById(R.id.no_button);
        if(cancel==null)
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        else
            no.setOnClickListener(cancel);
        dialog.show();
        return dialog;


    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void fillParentDate(JSONObject data1) {
        try {
            JSONArray data = data1.getJSONArray("studentService");
            try {
                String dateNow = data1.getString("dateNow");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Session.currentDate = format.parse(dateNow);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                SchoolServiceStuent a = new SchoolServiceStuent();
                a.fillFromJson(jsonObject);
                Object service = jsonObject.get("service");
                if (service instanceof JSONObject) {
                    JSONObject service1 = (JSONObject) service;
                    int id = service1.getInt("id");
                    SchoolService schoolService = Session.allService.get(id);
                    if(schoolService==null)
                    {
                        schoolService = new SchoolService();
                        schoolService.fillFromJson(service1);
                        Session.allService.put(schoolService.id, schoolService);
                    }
                    if (schoolService.lastStatusTypeId == SchoolService.START_BACK_SERVICE || schoolService.lastStatusTypeId == SchoolService.START_GO_SERVICE){
                        Session.allRunnigService.put(schoolService.id, schoolService);
                    }

                    a.service = schoolService;
//                    a.serviceId = schoolService.id;
                    schoolService.serviceStuents.add(a);

                }
                Object driver = jsonObject.get("driver");
                if (driver instanceof JSONObject) {
                    Driver driver1 = new Driver();
                    driver1.fillFromJson((JSONObject) driver);
                    Session.allDriver.put(driver1.id, driver1);
                    a.setDriver(driver1);
                    if(a.service!=null)
                        a.service.driver=driver1;
                }



                Object vehicle = jsonObject.get("vehicle");
                if (vehicle instanceof JSONObject) {
                    JSONArray vehicle2 = ((JSONObject) vehicle).getJSONArray("vehicle");
                    for (int j = 0; j < vehicle2.length(); j++) {
                        JSONObject jsonObject1 = vehicle2.getJSONObject(j);
                        Vehicle vehicle1 = new Vehicle();
                        vehicle1.fillFromJson(jsonObject1);
                        if(vehicle1.status.equals("1")) {
                            Session.allVehicle.put(vehicle1.id, vehicle1);
                            if (a.service != null && a.service.driver != null)
                                a.service.driver.vehicle = vehicle1;
                            continue;
                        }
                    }
                }
                Object school = jsonObject.get("department");
                if (school instanceof JSONObject) {
                    School school1 = new School();
                    school1.fillFromJson((JSONObject) school);
                    Session.allSchool.put(school1.id, school1);
                }
                Session.allServiceStudent.put(a.id, a);
            }
            JSONArray studentAll = data1.getJSONArray("studentAll");
            for (int i = 0; i < studentAll.length(); i++) {
                JSONObject jsonObject = studentAll.getJSONObject(i);
                Student a = new Student();
                a.fillFromJson(jsonObject);
                Session.allStudent.put(a.id, a);
                JSONObject parent = jsonObject.getJSONObject("parent");
                Parent parent1 = new Parent();
                parent1.fillFromJson( parent);
                a.parentId = parent1.id;
//                if(parent1!=null && parent1.id.equals(Userconfig.parent.id))
//                {
//                    Userconfig.parent=parent1;
//                }
                a.parent=parent1;
                Session.allParent.put(parent1.id, parent1);
                JSONArray otherParent = jsonObject.getJSONArray("otherParent");
                for (int j = 0; j < otherParent.length(); j++) {
                    JSONObject jsonObject1 = otherParent.getJSONObject(j);
                    Parent parent2 = new Parent();
                    parent2.fillFromJson(jsonObject1);
                    if (a.parent1Id == null || a.parent1Id.equals("null")) {
                        a.parent1Id = parent2.id;
                        a.parent1 = parent2;
                        a.parent1Phone = parent2.mobile;
                        a.parent1Relative = parent2.relative;

                    }

                    Session.allParent.put(parent1.id, parent1);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Session.reBindData();
    }

    public static String asciiNumners(String arabicNumber) {
        StringBuilder builder = new StringBuilder();
        String arabicDigits = "";
        String str = arabicNumber;
        char[] arabicChars = {'\u0660', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669'};
        char[] farsiChars = {'\u06f0', '\u06f1', '\u06f2', '\u06f3', '\u06f4', '\u06f5', '\u06f6', '\u06f7', '\u06f8', '\u06f9'};

        for (int i = 0; i < str.length(); i++) {
            boolean isdigit = false;
            for (int j = 0; j < arabicChars.length; j++) {
                if (arabicChars[j] == str.charAt(i)) {
                    isdigit = true;
                    builder.append((char) (j + 48));
                    break;
                }
                if (farsiChars[j] == str.charAt(i)) {
                    isdigit = true;
                    builder.append((char) (j + 48));
                    break;
                }
            }
            if (!isdigit) {
                builder.append(str.charAt(i));
            }


        }

        return builder.toString();


    }

    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) GhasedakApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);

                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else if (connectivityManager != null) {
            //noinspection deprecation
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;

                }


            }

        }
//        Toast.makeText(this, "adasd", Toast.LENGTH_SHORT).show();
        return false;
    }
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
