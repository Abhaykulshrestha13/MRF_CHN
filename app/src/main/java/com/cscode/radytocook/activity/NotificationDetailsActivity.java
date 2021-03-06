package com.cscode.radytocook.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cscode.radytocook.R;
import com.cscode.radytocook.model.Noti;
import com.cscode.radytocook.model.ReadNoti;
import com.cscode.radytocook.model.UserData;
import com.cscode.radytocook.retrofit.APIClient;
import com.cscode.radytocook.retrofit.GetResult;
import com.cscode.radytocook.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class NotificationDetailsActivity extends AppCompatActivity implements GetResult.MyListener {
    Noti noti;
    @BindView(R.id.img_noti)
    ImageView imgNoti;
    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_titel)
    TextView txtTitel;
    @BindView(R.id.txt_desc)
    TextView txtDesc;
    UserData user;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification Details");
        ButterKnife.bind(this);
        sessionManager = new SessionManager(NotificationDetailsActivity.this);
        user = sessionManager.getUserDetails("");
        noti = getIntent().getParcelableExtra("myclass");
        txtTitel.setText("" + noti.getTitle());
        txtDate.setText("" + noti.getDate());
        txtDesc.setText("" + noti.getMsg());
        Glide.with(this).asBitmap().load(APIClient.Base_URL + noti.getImg()).placeholder(R.drawable.slider).into(imgNoti);
        readNotification(noti.getId());
    }

    private void readNotification(String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("nid", id);
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().readNoti((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                ReadNoti readNoti = gson.fromJson(result.toString(), ReadNoti.class);
                HomeActivity.notificationCount(readNoti.getRemainNotification());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
