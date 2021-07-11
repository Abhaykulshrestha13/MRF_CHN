package com.mrf.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mrf.app.R;
import com.mrf.app.database.DatabaseHelper;
import com.mrf.app.fragment.HomeFragment;
import com.mrf.app.fragment.ItemItemFragment;
import com.mrf.app.fragment.MyCartFragment;
import com.mrf.app.model.UserData;
import com.mrf.app.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {
//
//    @BindView(R.id.myprofile)
//    LinearLayout myprofile;
    @BindView(R.id.myoder)
    LinearLayout myoder;
    @BindView(R.id.address)
    LinearLayout address;
    @BindView(R.id.contect)
    LinearLayout contect;
    @BindView(R.id.logout)
    LinearLayout logout;
    @BindView(R.id.about)
    LinearLayout about;
    @BindView(R.id.tramscondition)
    LinearLayout tramscondition;
    @BindView(R.id.privecy)
    LinearLayout privecy;
    @BindView(R.id.faq)
    LinearLayout faq;
    @BindView(R.id.drawer)
    LinearLayout drawer;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar_top)
    Toolbar toolbarTop;
    @BindView(R.id.home_frame)
    FrameLayout homeFrame;

    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.img_cart)
    ImageView imgCart;

    SessionManager sessionManager;
    public static TextView txtCount;
    DatabaseHelper helper;

    public static TextView txtNoti;
    @BindView(R.id.txtfirstl)
    TextView txtfirstl;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_mob)
    TextView txtMob;
    @BindView(R.id.txt_email)
    TextView txtEmail;
    public static LinearLayout lvlMycart;
    public static HomeActivity homeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeActivity = this;
        ButterKnife.bind(this);
        setSupportActionBar(toolbarTop);
        txtNoti = findViewById(R.id.txt_noti);
        txtCount = findViewById(R.id.txt_Count);
        lvlMycart = findViewById(R.id.lvl_mycart);
        helper = new DatabaseHelper(HomeActivity.this);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // hide the current title from the Toolbar
        toolbarTop.setNavigationIcon(getResources().getDrawable(R.drawable.menudrawer));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        sessionManager = new SessionManager(HomeActivity.this);
        data();

        Cursor resw = helper.getAllData();
        txtCount.setText("" + resw.getCount());
        HomeFragment fragment = new HomeFragment();
        fragment(fragment);
    }

    public void data() {
        UserData user = sessionManager.getUserDetails("");
        if (user == null) {
            txtfirstl.setText("");
            txtName.setText("");
            txtMob.setText("");
            txtEmail.setText("");
            finish();
        } else {

            char first = user.getFname().charAt(0);
            Log.e("first", "-->" + first);
            if(user.getMobile().equals(user.getFname())){
                txtfirstl.setText("");
                txtName.setText("");
            } else {
                txtfirstl.setText("" + first);
                txtName.setText("" + user.getFname());
            }
            txtMob.setText("" + user.getMobile());
//            txtEmail.setText("" + user.getCity());
        }
    }
    public static void notificationCount(int i) {

        if (i <= 0) {
            txtNoti.setVisibility(View.GONE);
        } else {
            txtNoti.setVisibility(View.VISIBLE);
            txtNoti.setText("" + i);
        }
    }

//    @OnClick({R.id.home, R.id.myprofile, R.id.myoder, R.id.address, R.id.contect, R.id.logout, R.id.about, R.id.tramscondition, R.id.privecy, R.id.faq, R.id.img_search, R.id.img_cart, R.id.img_close, R.id.btn_gotocart, R.id.img_notification})
@OnClick({R.id.home, R.id.myoder, R.id.address, R.id.contect, R.id.about,R.id.logout, R.id.tramscondition, R.id.privecy, R.id.faq, R.id.img_search, R.id.img_cart, R.id.img_close, R.id.btn_gotocart, R.id.img_notification})
public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home:
                HomeFragment fragment = new HomeFragment();
                fragment(fragment);
                break;
//            case R.id.myprofile:
//                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//                break;
            case R.id.myoder:
                startActivity(new Intent(HomeActivity.this, MyordersActivity.class));
                break;
            case R.id.address:
                startActivity(new Intent(HomeActivity.this, AddressActivity.class));
                break;
            case R.id.contect:
                startActivity(new Intent(HomeActivity.this, ContectusActivity.class));
                break;
            case R.id.logout:
                sessionManager.setBooleanData(SessionManager.USERLOGIN, false);
//                sessionManager.setUserDetails(null,null);
                sessionManager.logoutUser();
                startActivity(new Intent(HomeActivity.this, OtpVerifyActivity.class));
                finish();
                break;
            case R.id.about:
                startActivity(new Intent(HomeActivity.this, AboutsActivity.class));
                break;
            case R.id.tramscondition:
                break;
            case R.id.privecy:
                startActivity(new Intent(HomeActivity.this, PrivecyPolicyActivity.class));
                break;
            case R.id.faq:
                startActivity(new Intent(HomeActivity.this, TramsAndConditionActivity.class));
                break;
            case R.id.img_search:
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                break;
            case R.id.img_close:

                break;
            case R.id.btn_gotocart:
                MyCartFragment fragment1 = new MyCartFragment();
                fragment(fragment1);
                break;
            case R.id.img_cart:
                MyCartFragment fragment2 = new MyCartFragment();
                fragment(fragment2);
                break;
            case R.id.img_notification:
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));

                break;
            default:
                break;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void fragment(Fragment fragmentClass) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_frame, fragmentClass).addToBackStack(null).commit();


        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_frame);
            if (fragment instanceof HomeFragment && fragment.isVisible()) {
                finish();
            } else {
                super.onBackPressed();
            }
        }

    }

    public boolean isView() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_frame);
        return fragment instanceof ItemItemFragment && fragment.isVisible();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sessionManager!=null){
            data();
        }
    }
}
