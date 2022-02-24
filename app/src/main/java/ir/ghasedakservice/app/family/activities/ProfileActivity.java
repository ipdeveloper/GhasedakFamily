package ir.ghasedakservice.app.family.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import ir.ghasedakservice.app.family.GhasedakActivity;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Session;
import ir.ghasedakservice.app.family.utility.Userconfig;
import ir.ghasedakservice.app.family.models.City;
import ir.ghasedakservice.app.family.models.State;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;


public class ProfileActivity extends GhasedakActivity implements View.OnClickListener,ResponseHandler
{
    //    private RelativeLayout btn_verify;//, progressbar_layout;
    EditText input_name;
    EditText input_family;
    private ImageView back;
    //    private ProgressBar progressbar_verify;
    private TextView city, state;
    private Button txt_btn_login;
    int stateId, cityId;
    private ProgressBar progressBar;
    private LayoutInflater inflater ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_profile);
        TextView phoneNumber = findViewById(R.id.phone_number);
        phoneNumber.setText(Userconfig.mobile);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        city = findViewById(R.id.city);
        View cityLayout,stateLayout;
        cityLayout=findViewById(R.id.city_click);
        cityLayout.setOnClickListener(this);
        stateLayout=findViewById(R.id.state_click);
        stateLayout.setOnClickListener(this);
        state = findViewById(R.id.state);
//        city.setOnClickListener(this);
//        state.setOnClickListener(this);
        progressBar = findViewById(R.id.profile_progress_bar);

        if (Userconfig.firstName == null || Userconfig.lastName == null || Userconfig.firstName.equals("null") && Userconfig.lastName.equals("null"))
        {
            back.setVisibility(View.GONE);
        }
        txt_btn_login = findViewById(R.id.save_profile_details);
        input_family = findViewById(R.id.input_last_name);
        input_name = findViewById(R.id.input_name);
        String famil = Userconfig.lastName;
        String name = Userconfig.firstName;
        if (Userconfig.lastName != null && !Userconfig.lastName.equals("null"))
            input_family.setText(famil);
        if (Userconfig.firstName != null && !Userconfig.firstName.equals("null"))
            input_name.setText(name);
        txt_btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveProfile();


            }
        });

        if (Userconfig.cityId != 0)
        {
            City city1 = Session.allCity.get(Userconfig.cityId);
            cityId = Userconfig.cityId;
            if (city1 != null)
            {
                city.setText(city1.toString());
                state.setText(city1.state.toString());
                stateId = city1.stateId;
            }

        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    private boolean sendOk = true;

    private void saveProfile()
    {

        if (sendOk)
        {
//            showProgressDialog();
            String family = input_family.getText().toString();
            String name = input_name.getText().toString();
            if (family.length() == 0 || name.length() == 0 || cityId == 0)
            {
                Toast.makeText(this, R.string.please_insert_all_of_information, Toast.LENGTH_SHORT).show();
                return;
            }
            if (family.equals(Userconfig.lastName) && name.equals(Userconfig.firstName) && cityId == Userconfig.cityId)
            {
                Toast.makeText(this, R.string.no_change, Toast.LENGTH_SHORT).show();
                return;
            }


            JSONObject js = new JSONObject();
            try
            {

                js.put("userId", Userconfig.userId);
                js.put("name", name);
                js.put("family", family);
                js.put("cityId", cityId);
                js.put("token", Userconfig.token);

            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            HashMap<String, String> headers = new HashMap<>();//09373343885
            headers.put("token", Userconfig.token);
            progressBar.setVisibility(View.VISIBLE);
            txt_btn_login.setEnabled(false);
            sendOk = false;
            VolleyService.getInstance().getJsonObjectRequest(VolleyService.CustomerEditCode, VolleyService.CustomerEditeUrl,
                    Request.Method.POST, this, js, null, headers);
        }
//

    }

    Dialog dialog;

    @Override
    public void onBackPressed()
    {
        String family = input_family.getText().toString();
        String name = input_name.getText().toString();
        if (family.equals(Userconfig.lastName) && name.equals(Userconfig.firstName) && cityId == Userconfig.cityId)
        {
            super.onBackPressed();
        } else
        {
            Config.confirmDialg(this, getString(R.string.save), getString(R.string.do_you_want_save_setting), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                    saveProfile();

                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ProfileActivity.super.onBackPressed();
                }
            });

        }
    }

    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject)
    {
        progressBar.setVisibility(View.GONE);
        txt_btn_login.setEnabled(true);
        sendOk = true;
        if (responseStatusCode == VolleyService.OK_STATUS_CODE)
        {
            try
            {
                JSONObject result = responseJson;
                String statusString = result.getString("status");
                if (statusString.equals("success"))
                {
                    String famil = input_family.getText().toString();
                    String name = input_name.getText().toString();
                    Userconfig.cityId = cityId;
                    Userconfig.firstName = name;
                    Userconfig.lastName = famil;
                    Userconfig.saveUserConfig();
                    Toast.makeText(this, R.string.successful_done, Toast.LENGTH_LONG).show();
                    if (back.getVisibility() == View.GONE)
                    {
                        Intent intent_to_main = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent_to_main);
                    }
                    finish();

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseJson, Object requestObject)
    {
        progressBar.setVisibility(View.GONE);
        txt_btn_login.setEnabled(true);
        sendOk = true;
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.city_click)
        {
            if (stateId == 0)
            {
                Toast.makeText(ProfileActivity.this, R.string.please_select_your_state, Toast.LENGTH_SHORT).show();
                return;
            }
            cityDialog = new CustomDialogClass(this, 1);
            cityDialog.show();
        } else if (id == R.id.state_click)
        {
            if (stateDialog == null)
                stateDialog = new CustomDialogClass(this, 2);
            stateDialog.show();

        }
        if (id == R.id.back)
        {
            finish();
        }
    }

    View.OnClickListener stateSelect = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            State state = (State) view.getTag();
            stateId = state.id;
            cityId = 0;
            ProfileActivity.this.city.setText(R.string.city);
            ProfileActivity.this.state.setText(state.toString());
            stateDialog.dismiss();

        }
    };
    View.OnClickListener citySelect = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            City city = (City) view.getTag();
            cityId = city.id;
            ProfileActivity.this.city.setText(city.toString());
            cityDialog.dismiss();

        }
    };
    CustomDialogClass stateDialog, cityDialog;

    private class CustomDialogClass extends Dialog
    {
        public TextView no;
        public TextView title;
        ListView listView;
        int type;

        public CustomDialogClass(@NonNull Context context, int type)
        {
            super(context);
            this.type = type;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            inflater=this.getLayoutInflater();
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_select_citystate);
            setCancelable(true);
            listView = findViewById(R.id.list);
            title = findViewById(R.id.title);
            if (type == 1)
            {
                State state = Session.allState.get(stateId);
                final Vector<City> a = state.cities;
                Collections.sort(a);
                StateCityAdapter list = new StateCityAdapter(1);
                list.setCityData(a);
                listView.setAdapter(list);


            } else
            {
                Collection<State> values = Session.allState.values();
                final Vector<State> a = new Vector<>();
                a.addAll(values);
                Collections.sort(a);
                StateCityAdapter list = new StateCityAdapter(2);
                list.setStateData(a);
                listView.setAdapter(list);


            }
            title.setText(((type == 1) ? getString(R.string.select_city) : getString(R.string.select_state)));
            no = findViewById(R.id.btn_no);
            no.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dismiss();
                }
            });

        }
    }

    class StateCityAdapter extends BaseAdapter
    {
        Vector<Object> data = new Vector<>();
        int type;

        StateCityAdapter(int type)
        {
            this.type = type;
        }

        void setStateData(Vector<State> data)
        {
            this.data.clear();
            this.data.addAll(data);

        }

        void setCityData(Vector<City> data)
        {
            this.data.clear();
            this.data.addAll(data);


        }

        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int i)
        {
            return data.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            TextView row = (TextView) view;
            if (row == null)
            {
                row = (TextView) inflater.inflate(R.layout.list_city_item, viewGroup, false);
            }
            row.setTag(data.get(i));
            row.setText(data.get(i).toString());
            row.setOnClickListener(((type == 1) ? citySelect : stateSelect));


            return row;
        }
    }
}

