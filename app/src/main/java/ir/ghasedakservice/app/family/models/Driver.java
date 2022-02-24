package ir.ghasedakservice.app.family.models;

import org.json.JSONObject;

/**
 * Created by Iman on 8/16/2018.
 */

public class Driver {

    public String id;
    public String name;
    public String family;
    public String mobile;
    public String image;
    public Vehicle vehicle;

    public void fillFromJson(JSONObject jsonObject) {
        try {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            family = jsonObject.getString("family");
            mobile = jsonObject.getString("mobile");
            image = jsonObject.getString("image");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
