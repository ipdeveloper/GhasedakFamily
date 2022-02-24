package ir.ghasedakservice.app.family.models;

import org.json.JSONObject;

/**
 * Created by Iman on 9/29/2018.
 */

public class SearchAddress {
    public float lat;
    public float lon;
    public String name;
    public void fillFromJson(JSONObject jsonObject)
    {
        try{
            lat= (float) jsonObject.getDouble("lat");
            lon= (float) jsonObject.getDouble("lon");
            name=jsonObject.getString("display_name");

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
