package com.cscode.radytocook.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cscode.radytocook.R;
import com.cscode.radytocook.database.DatabaseHelper;
import com.cscode.radytocook.model.UserData;
import com.cscode.radytocook.retrofit.APIClient;
import com.cscode.radytocook.retrofit.GetResult;
import com.cscode.radytocook.utils.GetService;
import com.cscode.radytocook.utils.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import retrofit2.Call;

public class CodActivity extends AppCompatActivity implements GetResult.MyListener {


    String aid;
    String pid;
    String quantity;
    String total;

    UserData userData;
    SessionManager sessionManager;
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod);
        ButterKnife.bind(this);
        helper = new DatabaseHelper(CodActivity.this);
        sessionManager = new SessionManager(CodActivity.this);
        userData = new UserData();
        userData = sessionManager.getUserDetails("");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            aid = extras.getString("aid");
            pid = extras.getString("pid");
            quantity = extras.getString("quantity");
            total = extras.getString("total");
            sendOrder();
        }

    }


    private void sendOrder() {
        GetService.showPrograss(CodActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", userData.getId());
            jsonObject.put("aid", aid);
            jsonObject.put("pid", pid);
            jsonObject.put("qty", quantity);
            jsonObject.put("total", total);
            jsonObject.put("type", "cod");

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().sendOrder((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void callback(JsonObject result, String callNo) {
        GetService.close();
        helper.DeleteCard();
        startActivity(new Intent(CodActivity.this,CongratulactionActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();

    }
}
