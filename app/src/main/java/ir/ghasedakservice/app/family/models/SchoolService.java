package ir.ghasedakservice.app.family.models;

import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by Iman on 8/16/2018.
 */

public class SchoolService  {
    public static final int START_GO_SERVICE = 1;
    public static final int START_BACK_SERVICE = 2;
    public static final int FINISH_SERVICE = 3;
    public int id;
    public String driverId;
    public Driver driver;
    public String name;
    public int schoolId;
    public String startDate, goStartTime, backStartTime;
    public int lastStatusTypeId;
    public float lat,lng;

    public Vector<SchoolServiceStuent> serviceStuents = new Vector<>();

    public void fillFromJson(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            try {
                lastStatusTypeId = jsonObject.getInt("lastStatusTypeId");
                lat= (float) jsonObject.getDouble("lat");
                lng= (float) jsonObject.getDouble("lng");

            } catch (Exception e) {
                e.printStackTrace();
            }
            schoolId = jsonObject.getInt("departmentId");
            this.driverId = jsonObject.getString("driverId");
            startDate = jsonObject.getString("startDate");
            goStartTime = jsonObject.getString("startServiceTime");
            backStartTime = jsonObject.getString("startServiceTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SchoolServiceStuent getAllowTrack() {
        for (int i = 0; i < serviceStuents.size(); i++) {
            SchoolServiceStuent schoolServiceStuent = serviceStuents.get(i);
            if (schoolServiceStuent != null && schoolServiceStuent.allowTrack())
                return schoolServiceStuent;
        }
        if (serviceStuents.size() > 0)
            return serviceStuents.get(0);
        else
           return null;
    }

    @Override
    public String toString() {
        SchoolServiceStuent allowTrack = getAllowTrack();
        if (allowTrack != null)
            return allowTrack.getStudent().name + " " + allowTrack.getStudent().family;
        else
            return name;
    }
}
