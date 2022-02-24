package ir.ghasedakservice.app.family.utility;

import android.content.SharedPreferences;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import ir.ghasedakservice.app.family.models.City;
import ir.ghasedakservice.app.family.models.Driver;
import ir.ghasedakservice.app.family.models.Parent;
import ir.ghasedakservice.app.family.models.School;
import ir.ghasedakservice.app.family.models.SchoolService;
import ir.ghasedakservice.app.family.models.SchoolServiceStuent;
import ir.ghasedakservice.app.family.models.State;
import ir.ghasedakservice.app.family.models.Student;
import ir.ghasedakservice.app.family.models.Vehicle;


public class Session
{
    public static Date currentDate = null;
    public static HashMap<Integer, City> allCity = new HashMap<>();
    public static HashMap<String, City> allCityName = new HashMap<>();
    public static HashMap<Integer, State> allState = new HashMap<>();
    public static HashMap<Long, School> allSchool = new HashMap<>();
    public static HashMap<Long, Student> allStudent = new HashMap<>();
    public static HashMap<String, Parent> allParent = new HashMap<>();
    public static HashMap<Integer, Vehicle> allVehicle = new HashMap<>();
    public static HashMap<Integer, SchoolServiceStuent> allServiceStudent = new HashMap<>();
    public static HashMap<Integer, SchoolService> allService = new HashMap<>();
    public static HashMap<Integer, SchoolService> allRunnigService = new HashMap<>();
//    public static ArrayList<SchoolService> allRunnigServiceDuplicate = new ArrayList<>();
    public static HashMap<String, Driver> allDriver = new HashMap<>();
    public static int price=200000;

    private static SharedPreferences prefs;
    private static Session instance;
    public static int timeUpdateDriverLocationNormal;
    public static int timeUpdateDriverLocationInTrip;
    public static String DATA_VERSION = "1";

    public static Session getInstance(SharedPreferences sharedPreferences)
    {
        if (instance == null)
            instance = new Session(sharedPreferences);
        return instance;
    }

    private Session(SharedPreferences sharedPreferences)
    {
        prefs = sharedPreferences;
    }

    public void setSession(String session)
    {
        prefs.edit().putString("session", session).apply();
    }

    public String getSession()
    {
        String session = prefs.getString("session", "");
        return session;

    }

    public void setVersionNumber(int VersionNumber)
    {
        prefs.edit().putInt("VersionNumber", VersionNumber).apply();
    }
    public static boolean getShowHelpDialog()
    {
        return prefs.getBoolean("showHelpDialog",true);

    }

    public static void setShowDialog(boolean show)
    {
        prefs.edit().putBoolean("showHelpDialog", show).apply();
    }


    public int getVersionNumber()
    {
        return prefs.getInt("VersionNumber", -1);
    }

    public static void reBindData()
    {
        Collection<School> values = allSchool.values();
        Vector<School> schools = new Vector<>();
        schools.addAll(values);
        for (int i = 0; i < schools.size(); i++)
        {
            School school = schools.get(i);
        }

//        Collection<Student> values1 = allStudent.values();
//        Vector<Student> students=new Vector<>();
//        students.addAll(values1);
//        for (int i = 0; i < students.size(); i++) {
//            Student student = students.get(i);
//            student.parent=Session.allParent.get(student.parentId);
//            student.parent1=Session.allParent.get(student.parent1Id);
//            student.parent2=Session.allParent.get(student.parent2Id);
//        }

//        Collection<Parent> values2 = allParent.values();
//        Vector<Parent> parents=new Vector<>();
//        parents.addAll(values2);
//        for (int i = 0; i < parents.size(); i++) {
//            Parent school = parents.get(i);
//        }

//        Collection<Vehicle> values3 = allVehicle.values();
//        Vector<Vehicle> vehicles = new Vector<>();
//        vehicles.addAll(values3);
//        for (int i = 0; i < vehicles.size(); i++)
//        {
//            Vehicle vehicle = vehicles.get(i);
//            Driver driver = Session.allDriver.get(vehicle.driverId);
//            if (driver != null && vehicle.status.equals("1"))
//                driver.vehicle = vehicle;
//        }


        Collection<SchoolServiceStuent> values4 = allServiceStudent.values();
        Vector<SchoolServiceStuent> serviceStuents = new Vector<>();
        serviceStuents.addAll(values4);
        for (int i = 0; i < serviceStuents.size(); i++)
        {
            SchoolServiceStuent serviceStuent = serviceStuents.get(i);
            Driver driver = Session.allDriver.get(serviceStuent.driverId);
            serviceStuent.setDriver( driver);
            serviceStuent.setStudent( Session.allStudent.get(serviceStuent.studentId));
            serviceStuent.setSchool( Session.allSchool.get(serviceStuent.schoolId));
        }

//
//        Collection<SchoolService> values5 = allService.values();
//        Vector<SchoolService> schoolServices=new Vector<>();
//        schoolServices.addAll(values5);
//        for (int i = 0; i < schoolServices.size(); i++) {
//            SchoolService school = schoolServices.get(i);
//        }
//
//        Collection<Driver> values6 = allDriver.values();
//        Vector<Driver> drivers=new Vector<>();
//        drivers.addAll(values6);
//        for (int i = 0; i < drivers.size(); i++) {
//            Driver school = drivers.get(i);
//        }

    }
}