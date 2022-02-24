package ir.ghasedakservice.app.family.models;


import org.json.JSONObject;

import java.io.Serializable;

public class AddressLocation implements Serializable {

    public String id, name, address;
    public double lat, lng;
    public void fillFromJson(JSONObject jsonObject)
    {
        try{
            lat=jsonObject.getDouble("lat");
            lng=jsonObject.getDouble("lng");
            address=jsonObject.getString("address");

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    @Override
    public boolean equals(Object obj) {
        return (obj != null && id != null && ((AddressLocation) obj).id != null && ((AddressLocation) obj).id.equals(id));
    }
}
