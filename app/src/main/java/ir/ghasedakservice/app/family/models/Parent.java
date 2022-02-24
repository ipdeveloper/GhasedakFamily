package ir.ghasedakservice.app.family.models;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Iman on 8/16/2018.
 */

public class Parent {
    public String id;
    public String name;
    public String family;
    public String status;
    public String mobile;
    public String relative;
    public String paymentStatus;
    public Date registerDate;


    public void fillFromJson(JSONObject parent) {
        try {
            id = parent.getString("id");
            name = parent.getString("name");
            this.name = (name != null && name.length() != 0 && !name.equals("null")) ? name : null;
            family = parent.getString("family");
            family = (family != null && family.length() != 0 && !family.equals("null")) ? family : null;
            if (parent.has("status"))
                status = parent.getString("status");
            mobile = parent.getString("mobile");
            paymentStatus=parent.optString("paymentStatus");
            mobile = (mobile != null && mobile.length() != 0 && !mobile.equals("null")) ? mobile : null;
            if (parent.has("relationType"))
                relative = parent.getString("relationType");
            relative = (relative != null && relative.length() != 0 && !relative.equals("null")) ? relative : null;

            try {
                String registerDate = parent.getString("registerDate");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                this.registerDate= format.parse(registerDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
