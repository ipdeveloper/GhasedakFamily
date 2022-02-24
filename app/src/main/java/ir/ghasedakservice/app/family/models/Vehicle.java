package ir.ghasedakservice.app.family.models;

import org.json.JSONObject;

/**
 * Created by Iman on 8/31/2018.
 */

public class Vehicle {
    public int id;
    public String pelak;
    public String status;
    public String vehicleName;
    public String color;
    public int vehicleCount;
    public String driverId;

    public void fillFromJson(JSONObject jsonObject1) {
        try {
            id = jsonObject1.getInt("id");
            pelak = jsonObject1.getString("pelak");
            status = jsonObject1.getString("status");
            vehicleName = jsonObject1.getString("vehicleName");
            color = jsonObject1.getString("color");
            vehicleCount = jsonObject1.getInt("vehicleCount");
            driverId = jsonObject1.getString("driverId");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
