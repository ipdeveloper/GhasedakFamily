package ir.ghasedakservice.app.family.fragments;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import ir.ghasedakservice.app.family.activities.LoginActivity;
import ir.ghasedakservice.app.family.models.NavDrawerItem;
import ir.ghasedakservice.app.family.adaptors.NavigationDrawerAdapter;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.services.ResponseHandler;
import ir.ghasedakservice.app.family.services.VolleyService;
import ir.ghasedakservice.app.family.utility.Config;
import ir.ghasedakservice.app.family.utility.Userconfig;

public class FragmentDrawer extends Fragment  implements ResponseHandler {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    private static ArrayList<String> titles = new ArrayList<>();
    private static ArrayList<Integer> drawables = new ArrayList<Integer>();
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private TextView title, exitProfile;
    private FragmentDrawerListener drawerListener;
    private Dialog dialog;

    public FragmentDrawer() {

    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.size(); i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles.get(i));
            navItem.setImageId(drawables.get(i));
            data.add(navItem);
        }
        return data;
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (titles.size() == 0 || drawables.size() == 0) {
            titles = new ArrayList<>();
            drawables = new ArrayList<>();
            titles.add(getString(R.string.my_profile));
            drawables.add(R.drawable.ic_baseline_account_circle);
            titles.add(getString(R.string.add_service_student));
            drawables.add(R.drawable.ic_drawer_service);
            titles.add(getString(R.string.back_up));
            drawables.add(R.drawable.ic_support_sidemenu);
            titles.add(getString(R.string.share));
            drawables.add(R.drawable.ic_share);
            titles.add(getString(R.string.pay));
            drawables.add(R.drawable.ic_paytosubscribe);

        }
    }
    private void logOut()
    {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        Userconfig.logout();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("draft", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        startActivity(intent);
        getActivity().finish();
    }
    @Override
    public void onJsonObjectResult(int requestCode, int responseStatusCode, JSONObject responseJson, Object requestObject)
    {
        if(requestCode==VolleyService.LogOutCode){
            if(responseStatusCode==VolleyService.OK_STATUS_CODE)
                logOut();
        }
        else{
            Log.e(TAG,"can not log out");
            Toast.makeText(getActivity(), "لطفا برای خروج مجدد تلاش کنید", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onJsonErrorResponse(int requestCode, int responseStatusCode, String responseError, Object requestObject)
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = layout.findViewById(R.id.drawerList);
        title = layout.findViewById(R.id.title_of_drawer);
        String fullName = Userconfig.firstName + " " + Userconfig.lastName;
        title.setText(fullName);
        TextView version = layout.findViewById(R.id.version);
        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionApp = pInfo.versionName;
            version.setText(String.format(getString(R.string.app_version), versionApp));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        FragmentActivity activity = getActivity();
        exitProfile = layout.findViewById(R.id.exit_profile);
        exitProfile.setOnClickListener(v -> {
            dialog = Config.confirmDialg(getActivity(), getString(R.string.exit_account), getString(R.string.do_you_want_exit_your_account), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    final JSONObject js = new JSONObject();
                    try
                    {
                        js.put("userId", Userconfig.userId);
                        js.put("token", Userconfig.token);
//            Log.e(TAG, "userId: " + Integer.parseInt(session.getUserID()) + "");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    VolleyService.getInstance().getJsonObjectRequest(VolleyService.LogOutCode, VolleyService.LogOutUrl,
                            Request.Method.POST, FragmentDrawer.this, js, null, null);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

        });


        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                /*toolbar.setAlpha(1 - slideOffset / 2);*/
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }
}