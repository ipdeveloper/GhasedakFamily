package ir.ghasedakservice.app.family.models;

import android.support.annotation.NonNull;

/**
 * Created by Iman on 8/16/2018.
 */

public class City implements Comparable<City>{
    public Integer id;
    public String name;
    public int stateId;
    public State state;
    public Integer order;
    public float lat,lng;


    @Override
    public int compareTo(@NonNull City city) {
        return order.compareTo(city.order);
    }

    @Override
    public String toString() {
        return name;
    }
}
