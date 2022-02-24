package ir.ghasedakservice.app.family.models;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

/**
 * Created by Iman on 8/29/2018.
 */

public class School {
    public long id;
    public String name, address;
    public float lat, lng;

    public void fillFromJson(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            address = jsonObject.optString("address");
            lat = (float) jsonObject.getDouble("lat");
            lng = (float) jsonObject.getDouble("lng");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDetailes(int id,GeoPoint position,String name){
        this.id=id;
        lat=(float) position.getLatitude();
        lng=(float) position.getLongitude();
        this.name=name;
    }

    @Override
    public boolean equals(Object obj) {
        return id==((School)obj).id;
    }
}
