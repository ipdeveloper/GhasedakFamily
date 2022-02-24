package ir.ghasedakservice.app.family.services;

import org.json.JSONObject;

/**
 * Created by Iman on 6/29/2018.
 */

public interface NotificationObserver {
    void refresh(int serviceStudentId,int serviceId,int status);
    void driverAccepted(int serviceId);
    void showDialog(JSONObject js);
}
