package ir.ghasedakservice.app.family.models;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import ir.ghasedakservice.app.family.utility.Userconfig;

/**
 * Created by Iman on 8/16/2018.
 */

public class Student implements Comparable {
    public Student()
    {
        Parent a=new Parent();
        parentId= Userconfig.userId;
        a.mobile=Userconfig.mobile;
        a.id=Userconfig.userId;
        a.name=Userconfig.firstName;
        a.family=Userconfig.lastName;
        parent=a;

    }
    public Long id;
    public String name;
    public String family;
    public String sex;
    public String status;
    public String avator;
    public String birthDate;
    public String parent1Phone;
    public String parent1Relative;
//    public String parent2Phone;
//    public String parent2Relative;
    public String parentId, parent1Id;//, parent2Id;


    public Parent parent, parent1;//, parent2;


    public void fillFromJson(JSONObject jsonObject) {
        try {
            String bithDate = jsonObject.getString("bithDate");
            if(bithDate!=null && bithDate.length()>10)
                bithDate= bithDate.substring(0,10);
            set(jsonObject.getString("name"),jsonObject.getString("family"),jsonObject.getString("sex"),jsonObject.getString("image"), bithDate
                    ,"","");
            id = jsonObject.getLong("id");
//            parentId = jsonObject.getString("parentId");
//            name = jsonObject.getString("name");
//            sex = jsonObject.getString("sex");
//            family = jsonObject.getString("family");
//            birthDate = jsonObject.getString("bithDate");
//            if (birthDate != null && !birthDate.equals("null") && birthDate.length() > 0)
//                birthDate = birthDate.substring(0, 10);
//            status = jsonObject.getString("status");
//            avator = jsonObject.getString("image");
//            if (jsonObject.has("parentId"))

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof Student) {
            if (((Student) o).id >= this.id)
                return 1;
        }
        return -1;
    }



    public void set(String name, String family, String sex,  String avator, String birthDate, String parent1Phone, String parent1Relative) {
        this.name = (name!=null && name.length()!=0 && !name.equals("null"))?name:null;
        this.family = (family !=null && family .length()!=0 && !family .equals("null"))?family :null;
        this.sex =  (sex !=null && sex .length()!=0 && !sex .equals("null"))?sex :null;
        this.avator =  (avator !=null && avator .length()!=0 && !avator .equals("null"))?avator :null;
        this.birthDate =  (birthDate !=null && birthDate .length()!=0 && !birthDate .equals("null"))?birthDate :null;
        this.parent1Phone =  (parent1Phone !=null && parent1Phone .length()!=0 && !parent1Phone .equals("null"))?parent1Phone :null;
        this.parent1Relative =  (parent1Relative !=null && parent1Relative .length()!=0 && !parent1Relative .equals("null"))?parent1Relative :null;
//        this.parent2Phone =  (parent2Phone !=null && parent2Phone .length()!=0 && !parent2Phone .equals("null"))?parent2Phone :null;
//        this.parent2Relative =  (parent2Relative !=null && parent2Relative .length()!=0 && !parent2Relative .equals("null"))?parent2Relative :null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Student) {
            return  (((Student) o).id == this.id);

        }
        return false;
    }

    public boolean equalsStudent(Object obj) {
        Student student = (Student) obj;



        if (name != null && student.name != null) {
            if(! name.equals(student.name))
                return false;
        } else if (name != null || student.name != null) {
            return false;
        }
        if (sex != null && student.sex != null) {
            if(! sex.equals(student.sex))
                return false;
        } else if (sex != null || student.sex != null) {
            return false;
        }

        if (family != null && student.family != null) {
            if(! family.equals(student.family))
                return false;
        } else if (family != null || student.family != null) {
            return false;
        }
//        if (parent1 != null && student.parent1 != null) {
//            check = parent1.equals(student.parent1);
//        } else if (parent1 != null || student.parent1 != null) {
//            check = false;
//        }

//        if (parent2 != null && student.parent2 != null) {
//            check = parent2.equals(student.parent2);
//        } else if (parent2 != null || student.parent2 != null) {
//            check = false;
//        }

        if (birthDate != null && student.birthDate != null) {
            if(! birthDate.equals(student.birthDate))
                return false;
        } else if (birthDate != null || student.birthDate != null) {
            return false;
        }
//        if (parent2Phone != null && student.parent2Phone != null) {
//            if(! parent2Phone.equals(student.parent2Phone))
//                check = false;
//        } else if (parent2Phone != null || student.parent2Phone != null) {
//            check = false;
//        }
        if (parent1Phone != null && student.parent1Phone != null) {
            if(! parent1Phone.equals(student.parent1Phone))
                return false;
        } else if (parent1Phone != null || student.parent1Phone != null) {
            return false;
        }
        if (avator != null && student.avator != null) {
            if(! avator.equals(student.avator))
                return false;
        } else if (avator != null || student.avator != null) {
            return false;
        }


        if (parent1Relative != null && student.parent1Relative != null) {
            return parent1Relative.equals(student.parent1Relative);
        } else return parent1Relative == null && student.parent1Relative == null;

//        if (parent2Relative != null && student.parent2Relative != null) {
//            if(! parent2Relative.equals(student.parent2Relative))
//                check = false;
//        } else if (parent2Relative != null || student.parent2Relative != null) {
//            check = false;
//        }


    }




}
