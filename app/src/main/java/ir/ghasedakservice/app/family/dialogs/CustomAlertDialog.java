package ir.ghasedakservice.app.family.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.VolleyService;


public class CustomAlertDialog extends Dialog {

    private final String TAG = "CustomAlertDialog";
    private Context context;
    public CustomAlertDialog(@NonNull Context context, JSONObject jsonObject) {
        super(context);
        setContentView(R.layout.alert_dialog);
        this.context=context;
        setContentAttribute(jsonObject);
    }

    private void setContentAttribute(JSONObject js) {
        TextView title = findViewById(R.id.title);
        TextView body = findViewById(R.id.body);
        NetworkImageView titleImage = findViewById(R.id.title_image);
        NetworkImageView background = findViewById(R.id.background_image);
        Button submit = findViewById(R.id.action_button);
        if (js.has("title")) {
            try {
                setTextViewAttr(title, js.getJSONObject("title"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "title json is not valid!");
            }
        } else
            title.setVisibility(View.GONE);
        if (js.has("body")) {
            try {
                setTextViewAttr(title, js.getJSONObject("body"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "body json is not valid!");
            }
        } else
            body.setVisibility(View.GONE);
        if (js.has("titleImage")) {
            try {
                setTitleImageAttr(titleImage, js.getJSONObject("titleImage"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "title image json is not valid!");
            }
        } else
            body.setVisibility(View.GONE);
        if (js.has("background")) {
            try {
                setTitleImageAttr(titleImage, js.getJSONObject("background"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "background json is not valid!");
            }
        } else
            body.setVisibility(View.GONE);

        if (js.has("actionButton")) {
            try {
                setButtonAttr(submit, js.getJSONObject("actionButton"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "action button json is not valid!");
            }
        } else
            body.setVisibility(View.GONE);

    }

    private void setTextViewAttr(TextView text, JSONObject js) throws JSONException {
        text.setText(js.getString("text"));
        text.setTextColor(js.optInt("textColor", 0x000000));
    }

    private void setTitleImageAttr(NetworkImageView text, JSONObject js) throws JSONException {
        text.setImageUrl(js.getString("url"), VolleyService.getInstance().getImageLoader());
        text.setDefaultImageResId(R.drawable.profile_image);
    }

    private void setButtonAttr(Button button, JSONObject js) throws JSONException{
        button.setText(js.getString("text"));
        String uri=js.optString("uri",null);
        button.setOnClickListener(v -> {
            if(uri!=null){
                Uri url = Uri.parse(uri);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, url);
                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(likeIng,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe)
                    context.startActivity(likeIng);
                else
                    Toast.makeText(context, "شما نرم افزار مربوطه را در گوشی خود نصب ننموده اید.", Toast.LENGTH_SHORT).show();

            }
            else
                this.dismiss();

        });
    }

}
