package ir.ghasedakservice.app.family.models;

import android.support.annotation.NonNull;

import java.util.Vector;

/**
 * Created by Iman on 8/16/2018.
 */

public class State implements Comparable<State>{
    public Integer id;
    public String name;
    public Vector<City> cities=new Vector<>();
    @Override
    public int compareTo(@NonNull State state) {
        return id.compareTo(state.id);
    }

    @Override
    public String toString() {
        return name;
    }
}
