package ir.ghasedakservice.app.family.activities;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;

import ir.ghasedakservice.app.family.GhasedakActivity;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.adaptors.SearchAdaptor;
import ir.ghasedakservice.app.family.models.AddressLocation;
import ir.ghasedakservice.app.family.models.School;
import ir.ghasedakservice.app.family.models.SearchAddress;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Userconfig;

public class SearchActivity extends GhasedakActivity implements SearchAdaptor.OnItemClickListener, ResponseHandler {

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            search(s);
        }
    };
    private int source;
    private String nameOfRequest;
    private boolean resultWithPosition = true;
    private String TAG = SearchActivity.class.getSimpleName();

    private void search(Editable s) {
        String url = "";
        if (source == 0 || source == 1) {
            try {
                if (resultWithPosition) {
                    url=Userconfig.lastCity+" "+s.toString();
                }
                else
                    url=s.toString().replaceAll("[ ]+"," ");
                if(url.substring(url.length()-1).equals(" "))
                    url=url.substring(0,url.length()-1);
                url = VolleyService.SearchAddressUrl + URLEncoder.encode(url, "utf-8").replace("+", "%20");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (nameOfRequest != null)
                VolleyService.getInstance().cancelPendingRequests(nameOfRequest);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Vector<SearchAddress> addresses = new Vector<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            SearchAddress a = new SearchAddress();
                            a.fillFromJson(response.getJSONObject(i));
                            addresses.add(a);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    searchAdaptor.setAddress(addresses);
                    searchAdaptor.notifyDataSetChanged();
                    if(addresses.size()==0){
                        textView.setVisibility(View.VISIBLE);
                    }
                    else
                        textView.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            VolleyService.getInstance().addToRequestQueue(jsonArrayRequest);
        } else {
            url=s.toString().replaceAll("[ ]+"," ");
            if(url.substring(url.length()-1).equals(" "))
                url=url.substring(0,url.length()-1);
            if (nameOfRequest != null) {
                VolleyService.getInstance().cancelPendingRequests(nameOfRequest);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userId", Userconfig.userId);
                jsonObject.put("KeySerach", url); //Userconfig.cityId);
                jsonObject.put("lat", lat);
                jsonObject.put("lng", lng);
                jsonObject.put("token", Userconfig.token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameOfRequest = VolleyService.getInstance().getJsonObjectRequest(VolleyService.SearchSchoolsCode, VolleyService.SearchSchoolsUrl, Request.Method.POST,
                    this, jsonObject, null, null);
        }
    }

    private double lat, lng;
    private String locationCity = "";
    private SearchAdaptor searchAdaptor;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.back_image_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
        textView=findViewById(R.id.not_found);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            source = intent.getIntExtra("type", 0);
            lat = intent.getDoubleExtra("lat",0);
            lng = intent.getDoubleExtra("lng",0);
        } else {
            source = 0;
            lat =  Userconfig.lastLocation.getLatitude();
            lng =  Userconfig.lastLocation.getLongitude();
        }

        if(Userconfig.lastCity==null) {
            String url = VolleyService.GeoCoderUrl + "&lat=" + Config.asciiNumners("" +
                    lat) + "&lon=" + Config.asciiNumners("" + lng);
            VolleyService.getInstance().getJsonObjectRequest(VolleyService.GeoCoderCode, url,
                    Request.Method.GET, this, null, null, null);
        }

        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdaptor = new SearchAdaptor(this);
        searchAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(searchAdaptor);
        editText= findViewById(R.id.search_text_activity);
        editText.addTextChangedListener(textWatcher);
    }
    EditText editText;
    @Override
    public void onClickListener(Object o) {
        Intent intent = new Intent();

        if (o instanceof School) {
            School school = (School) o;
            intent.putExtra("lat", school.lat);
            intent.putExtra("lng", school.lng);
            intent.putExtra("name", school.name);
            intent.putExtra("type", "school");
            intent.putExtra("schoolId", school.id);
            setResult(RESULT_OK, intent);
        } else if (o instanceof SearchAddress) {
            SearchAddress address = (SearchAddress) o;
            intent.putExtra("lat", address.lat);
            intent.putExtra("lng", address.lon);
            intent.putExtra("name", address.name);
            intent.putExtra("type", "address");
            setResult(RESULT_OK, intent);
        }else
            setResult(RESULT_CANCELED, intent);
        hideKeyboard();
        finish();
    }
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject) {
        if (requestCode == VolleyService.GeoCoderCode) {
            try {

                Userconfig.lastCity = responseJson.getJSONObject("address").optString("county") + " ";

            } catch (Exception e) {
                Log.e(TAG, getString(R.string.no_proper_data_for_add_school));
            }

        } else if (requestCode == VolleyService.SearchAddressCode) {
            Vector<SearchAddress> addresses = new Vector<>();
            try {
                JSONArray response = new JSONArray(responseJson.toString());
                for (int i = 0; i < response.length(); i++) {
                    SearchAddress a = new SearchAddress();
                    a.fillFromJson(response.getJSONObject(i));
                    addresses.add(a);
                }
                searchAdaptor.setAddress(addresses);
                searchAdaptor.notifyDataSetChanged();
                if(addresses.size()==0){
                    textView.setVisibility(View.VISIBLE);
                }
                else
                    textView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == VolleyService.SearchSchoolsCode) {
            try {
                if (responseJson.has("data")) {
                    JSONArray school = responseJson.getJSONObject("data").getJSONArray("schools");
                    Vector<School> schools=new Vector<>();
                    for (int i = 0; i <school.length() ; i++) {
                        JSONObject m= (JSONObject) school.get(i);
                        School s=new School();
                        s.id=m.getLong("id");
                        s.name=m.getString("name");
                        s.lat= (float) m.getDouble("lat");
                        s.lng= (float) m.getDouble("lng");
                        schools.add(s);
                    }
                    searchAdaptor.setSchools(schools);
                    searchAdaptor.notifyDataSetChanged();
                    if(schools.size()==0){
                        textView.setVisibility(View.VISIBLE);
                    }
                    else
                        textView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e(TAG, getString(R.string.no_proper_data_for_add_school));
            }
        }
    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseError, Object requestObject) {

    }
}
