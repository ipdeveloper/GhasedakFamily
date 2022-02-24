package ir.ghasedakservice.app.family.services;

public interface SendDataToFragmentListener {
    void detailsText(int serviceId,String text);
    void animationHandler(int serviceId,int animationId,boolean start);
    void chevronHandler(int serviceId,float progress,boolean broadcast);
    boolean notificationChange(int serviceId,int studentServiceId);
}
