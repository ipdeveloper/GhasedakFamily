package ir.ghasedakservice.app.family;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ir.ghasedakservice.app.family.models.City;
import ir.ghasedakservice.app.family.models.State;
import ir.ghasedakservice.app.family.utility.Session;



public class GhasedakSqliteOpenHelper extends SQLiteOpenHelper {
    private static GhasedakSqliteOpenHelper instance;
    public static String pathpack = GhasedakApplication.context.getPackageName();
    public final String path = "/data/data/" + pathpack + "/databases/";
    public static final String DATABASE_NAME = "ghasedak";
    private SQLiteDatabase mydb;
    public static final String CITIES_TABLE = "city";
    //    public static final String STUDENT_TABLE = "student";
//    public static final String SERVICE_TABLE = "service";
//    public static final String SERVICE_STUDENT_TABLE = "servicestudent";
//    public static final String PARENT_TABLE = "parent";
//    public static final String STUDENT_PARENT_TABLE = "studentparent";
    public static final int Version = 3;

    public static GhasedakSqliteOpenHelper getInstance() {
        if (instance == null)
            instance = new GhasedakSqliteOpenHelper();
        return instance;
    }

    private GhasedakSqliteOpenHelper() {
        super(GhasedakApplication.context, DATABASE_NAME, null, Version);
        checkDb();
        loadCity();
    }


    private void checkDb() {
        File f = new File(path + DATABASE_NAME);
        SharedPreferences preferences = GhasedakApplication.ghasedakPreferences;
        if (preferences.getInt("db_version", -1) < Version && f.exists()) {
            f.delete();
        }
        if (!f.exists()) {
            try {
                copydatabase();
                SharedPreferences.Editor edit = preferences.edit();
                edit.putInt("db_version", Version);
                edit.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        open();
    }

    public void open() {
        try {
            mydb = SQLiteDatabase.openDatabase(path + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void copydatabase() throws IOException {
        File f = new File(path + DATABASE_NAME);
        f.getParentFile().mkdirs();
        OutputStream myOutput = new FileOutputStream(path + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        InputStream myInput = GhasedakApplication.context.getAssets().open(DATABASE_NAME);
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    private void loadCity() {
        try {
            Session.allCity.clear();
            String query = "select * from " + CITIES_TABLE;
            Cursor res = mydb.rawQuery(query, null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                City a = new City();
                a.id = res.getInt(res.getColumnIndex("id"));
                a.name = res.getString(res.getColumnIndex("name"));
                a.order = res.getInt(res.getColumnIndex("orderr"));
                a.stateId = res.getInt(res.getColumnIndex("p_id"));
                a.lat = res.getFloat(res.getColumnIndex("lat"));
                a.lng = res.getFloat(res.getColumnIndex("lng"));

                State state = Session.allState.get(a.stateId);
                if (state == null) {
                    state = new State();
                    state.name = res.getString(res.getColumnIndex("p_name"));
                    state.id = a.stateId;
                    Session.allState.put(state.id, state);
                }
                a.state = state;
                state.cities.add(a);
                Session.allCity.put(a.id, a);
                a.name=a.name.replaceAll("\u064a","\u06cc");
                a.name=a.name.replaceAll("\u06a9","\u0643");
                Session.allCityName.put(a.name.trim(), a);
                res.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

//    private void loadService() {
//        try {
//            Session.allService.clear();
//            Session.allServiceStudent.clear();
//            String query = "select * from " + SERVICE_TABLE;
//
//            Cursor res = mydb.rawQuery(query, null);
//            res.moveToFirst();
//
//            while (res.isAfterLast() == false) {
//                SchoolService schoolService = new SchoolService();
//                schoolService.id = res.getInt(res.getColumnIndex("id"));
//                SchoolService schoolService2 = Session.allService.get(schoolService.id);
//                if (schoolService2 == null) {
//                    schoolService.name = res.getString(res.getColumnIndex("name"));
//                    schoolService.schoolId = res.getInt(res.getColumnIndex("schoolid"));
//                    Session.allService.put(schoolService.id, schoolService);
//                }
//
//                res.moveToNext();
//            }
//            query = "select * from " + SERVICE_STUDENT_TABLE;
//            res = mydb.rawQuery(query, null);
//            res.moveToFirst();
//
//            while (res.isAfterLast() == false) {
//                SchoolServiceStuent schoolServiceStuent = new SchoolServiceStuent();
//                schoolServiceStuent.id = res.getInt(res.getColumnIndex("id"));
//                SchoolServiceStuent state = Session.allServiceStudent.get(schoolServiceStuent.id);
//                if (state == null) {
//                    schoolServiceStuent.schoolName = res.getString(res.getColumnIndex("schoolName"));
//                    schoolServiceStuent.srcAddress = res.getString(res.getColumnIndex("srcAddress"));
//                    schoolServiceStuent.desAddress = res.getString(res.getColumnIndex("desAddress"));
//                    schoolServiceStuent.srcLat = res.getFloat(res.getColumnIndex("srcLat"));
//                    schoolServiceStuent.srcLng = res.getFloat(res.getColumnIndex("srcLng"));
//                    schoolServiceStuent.desLat = res.getFloat(res.getColumnIndex("desLat"));
//                    schoolServiceStuent.desLng = res.getFloat(res.getColumnIndex("desLng"));
//                    Session.allServiceStudent.put(state.id, state);
//                }
//                int studentid = res.getInt(res.getColumnIndex("id"));
//                Student student = Session.allStudent.get(studentid);
//                if (student != null) {
//                    schoolServiceStuent.students.add(student);
//                }
//                res.moveToNext();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void loadParent() {
//        try {
//            Session.allParent.clear();
//            String query = "select * from " + PARENT_TABLE;
//            Cursor res = mydb.rawQuery(query, null);
//            res.moveToFirst();
//
//            while (res.isAfterLast() == false) {
//                Parent a = new Parent();
//                a.id = res.getInt(res.getColumnIndex("id"));
//                Parent p2 = Session.allParent.get(a.id);
//                if (p2 == null) {
//                    a.name = res.getString(res.getColumnIndex("name"));
//                    a.family = res.getString(res.getColumnIndex("family"));
//                    a.status = res.getString(res.getColumnIndex("status"));
//                    a.mobile = res.getString(res.getColumnIndex("mobile"));
//                    Session.allParent.put(a.id, a);
//                }
//                res.moveToNext();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void loadStudent() {
//        try {
//            Session.allStudent.clear();
//            String query = "select * from " + STUDENT_TABLE;
//            Cursor res = mydb.rawQuery(query, null);
//            res.moveToFirst();
//            while (res.isAfterLast() == false) {
//                Student student = new Student();
//                student.id = res.getInt(res.getColumnIndex("id"));
//                Student student2 = Session.allStudent.get(student.id);
//                if (student2 == null) {
//                    student.name = res.getString(res.getColumnIndex("name"));
//                    student.family = res.getString(res.getColumnIndex("family"));
//                    student.parentId = res.getInt(res.getColumnIndex("parentid"));
//                    student.family = res.getString(res.getColumnIndex("family"));
//                    student.status = res.getString(res.getColumnIndex("status"));
//                    student.avator = res.getString(res.getColumnIndex("avator"));
//                    Parent parent = Session.allParent.get(student.parentId);
//                    if (parent != null) {
//                        student.parent = parent;
//                    }
//                    Session.allStudent.put(student.id, student);
//                }
//
//                res.moveToNext();
//            }
//            query = "select * from " + STUDENT_PARENT_TABLE;
//            res = mydb.rawQuery(query, null);
//            res.moveToFirst();
//
//            while (res.isAfterLast() == false) {
//
//                int studentid = res.getInt(res.getColumnIndex("studentid"));
//                int parentid = res.getInt(res.getColumnIndex("parentid"));
//                Parent parent = Session.allParent.get(parentid);
//                Student student = Session.allStudent.get(studentid);
//                if (parent != null && student != null) {
//                    student.parent = parent;
//                }
//                res.moveToNext();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



//   public void addStudent(Student student) {
//        open();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id", student.id);
//        contentValues.put("name", student.name);
//        contentValues.put("family", student.family);
//        contentValues.put("status", student.status);
//        contentValues.put("avator", student.avator);
//        contentValues.put("parentId", student.parentId);
//        try {
//            mydb.insert(STUDENT_TABLE, null, contentValues);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//
//    }

//    public void addServiceStudent(SchoolServiceStuent serviceStuent) {
//        open();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id", serviceStuent.id);
//        contentValues.put("schoolId", serviceStuent.schoolId);
//        contentValues.put("schoolName", serviceStuent.schoolName);
//        contentValues.put("srcAddress", serviceStuent.srcAddress);
//        contentValues.put("desAddress", serviceStuent.desAddress);
//        contentValues.put("srcLat", serviceStuent.srcLat);
//        contentValues.put("srcLng", serviceStuent.srcLng);
//        contentValues.put("desLat", serviceStuent.desLat);
//        contentValues.put("desLng", serviceStuent.desLng);
//        try {
//            mydb.insert(STUDENT_TABLE, null, contentValues);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//    public   void addService(SchoolService service) {
//        open();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id", service.id);
//        contentValues.put("name", service.name);
//        contentValues.put("schoolId", service.schoolId);
//        contentValues.put("goStartDate", service.goStartDate);
//        contentValues.put("backStartDate", service.backStartDate);
//        try {
//            mydb.insert(STUDENT_TABLE, null, contentValues);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void addParent(Parent parent) {
//        open();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id",parent.id);
//        contentValues.put("name",parent.name);
//        contentValues.put("family",parent.family);
//        contentValues.put("status",parent.status);
//        contentValues.put("mobile",parent.mobile);
//        try {
//            mydb.insert(STUDENT_TABLE, null, contentValues);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//    public void addStudentParent(int studentIdm,int parentId)
//    {
//        open();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("studentid",studentIdm);
//        contentValues.put("parentid",parentId);
//        try {
//            mydb.insert(STUDENT_TABLE, null, contentValues);
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }



