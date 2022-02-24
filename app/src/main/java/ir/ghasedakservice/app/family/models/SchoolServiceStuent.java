package ir.ghasedakservice.app.family.models;

import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import org.json.JSONObject;
import java.util.Calendar;
import ir.ghasedakservice.app.family.utility.Session;


/**
 * Created by Iman on 8/16/2018.
 */

public class SchoolServiceStuent implements Comparable {
    public SchoolService service;
    private School school;
    private Student student;
    private Driver driver;
    public int lastTime;
    public int id;
    public int maxDistance=500;

    public long schoolId,  studentId;//serviceId,
    public int lastStatusTypeId;
//    public String paymentStatus;


    private AddressLocation srcAddress;
    private AddressLocation desAddress;
    public String driverId;
    public int status=0;

    //    public String driverPhoneNumber;
//    public Vector<Student> students = new Vector<>();

    public void fillFromJson(JSONObject jsonObject) {
        try {
            if (srcAddress == null || desAddress == null) {
                srcAddress = new AddressLocation();
                desAddress = new AddressLocation();
            }
            status=jsonObject.getInt("status");
            student = new Student();
            id = jsonObject.getInt("id");
            if (jsonObject.has("lastStatusId")) {
                try {
                    lastStatusTypeId = jsonObject.getInt("lastStatusId");
                }catch (Exception e)
                {
//                    e.printStackTrace();
                }
            }
            String studentId = jsonObject.getString("studentId");
            if (studentId != null && !studentId.equals("null"))
                this.studentId = Integer.parseInt(studentId);

            String schoolId = jsonObject.getString("departmentId");
            if (schoolId != null && !schoolId.equals("null"))
                this.schoolId = Integer.parseInt(schoolId);


//            paymentStatus = jsonObject.getString("paymentStatus");
            String serviceId = jsonObject.getString("serviceId");
//            if (serviceId != null && !serviceId.equals("null"))
//                this.serviceId = Integer.parseInt(serviceId);
            srcAddress.address = jsonObject.getString("srcAddress");
            desAddress.address = jsonObject.getString("desAddress");
            driverId = jsonObject.getString("driverId");
            srcAddress.lat = (float) jsonObject.getDouble("srcLat");
            srcAddress.lng = (float) jsonObject.getDouble("srcLng");
            desAddress.lat = (float) jsonObject.getDouble("desLat");
            desAddress.lng = (float) jsonObject.getDouble("desLng");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Driver getDriver() {
        return driver;
    }

    public School getSchool() {
        return school;
    }

    public Student getStudent() {
        return student;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
        if (driver != null)
            this.driverId = driver.id;
    }

    public boolean allowTrack()
    {
        Parent parent = getStudent().parent;
        Calendar cal = Calendar.getInstance();
        cal.setTime(parent.registerDate);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        return Session.currentDate.before(cal.getTime()) || (parent.paymentStatus != null && !parent.paymentStatus.equals("0"));
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void setSrcAddress(AddressLocation srcAddress) {
        this.srcAddress = srcAddress;
    }

    public void setDesAddress(AddressLocation desAddress) {
        this.desAddress = desAddress;
    }

    public AddressLocation getDesAddress() {
        return desAddress;
    }

    public AddressLocation getSrcAddress() {
        return srcAddress;
    }

    public float getLatitude(){
        return (float)srcAddress.lat;
    }
    public float getLongitude(){
        return (float)srcAddress.lng;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof SchoolServiceStuent) {
            if (((SchoolServiceStuent) o).id >= this.id)
                return 1;
        }
        return -1;
    }

    public boolean equalsDriver(String driverMobile) {
        return driver != null && driver.mobile != null && driver.mobile.equals(driverMobile);
    }

    public boolean equals(Student mStudent, String s, School school, AddressLocation src, AddressLocation des) {
        if (student == null || !student.equals(mStudent))
            return false;
        if (driver == null || driver.mobile == null || !driver.mobile.equals(s))
            return false;
        if (this.school == null || !this.school.equals(school))
            return false;


        if (this.srcAddress == null)
            return false;
        Location a = new Location(LocationManager.GPS_PROVIDER);
        Location b = new Location(LocationManager.GPS_PROVIDER);
        a.setLatitude(src.lat);
        a.setLatitude(src.lng);
        b.setLatitude(srcAddress.lat);
        b.setLatitude(srcAddress.lng);
        if (a.distanceTo(b) > 100)
            return false;
        if (this.desAddress == null)
            return false;
        a.setLatitude(des.lat);
        a.setLatitude(des.lng);
        b.setLatitude(desAddress.lat);
        b.setLatitude(desAddress.lng);
        return !(a.distanceTo(b) > 100);
    }
}
