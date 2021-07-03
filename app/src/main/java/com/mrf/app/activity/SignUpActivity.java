package com.mrf.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mrf.app.R;
import com.mrf.app.model.User;
import com.mrf.app.retrofit.APIClient;
import com.mrf.app.retrofit.GetResult;
import com.mrf.app.utils.GetService;
import com.mrf.app.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class SignUpActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.btn_singup)
    Button btnSingup;
    SessionManager sessionManager;
    @BindView(R.id.txt_forgot)
    TextView txtForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        sessionManager = new SessionManager(SignUpActivity.this);
    }

    public boolean isValidation() {

        if (!GetService.EmailValidator(edEmail.getText().toString())) {
            edEmail.setError("Required email");
            return false;
        } else if (TextUtils.isEmpty(edPassword.getText().toString())) {
            edPassword.setError("Required password");
            return false;
        }

        return true;
    }

    private void login() {
        GetService.showPrograss(SignUpActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", edEmail.getText().toString());
            jsonObject.put("password", edPassword.getText().toString());

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSignIn((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void login(String number) {
//        GetService.showPrograss(SignUpActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", number);
            jsonObject.put("password", number);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSignIn((JsonObject) jsonParser.parse(jsonObject.toString()));
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
        if (callNo.equalsIgnoreCase("1") || result.toString().length() != 0) {
            Gson gson = new Gson();
            User response = gson.fromJson(result.toString(), User.class);
//            GetService.ToastMessege(SignUpActivity.this, response.getResponseMsg());
            if (response.getResult().equalsIgnoreCase("true")) {
                sessionManager.setUserDetails("", response.getResultData());
                sessionManager.setBooleanData(SessionManager.USERLOGIN, true);
                startActivity(new Intent(SignUpActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }
    }

    @OnClick({R.id.txt_forgot, R.id.btn_singup, R.id.btn_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_forgot:
                startActivity(new Intent(SignUpActivity.this, ForgotActivity.class));
                break;
            case R.id.btn_singup:
                if (isValidation()) {
                    login();
                }
                break;
            case R.id.btn_sign:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                break;
            default:
                break;
        }
    }
}