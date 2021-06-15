package com.mrf.app.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.mrf.app.R;
import com.mrf.app.retrofit.APIClient;
import com.mrf.app.retrofit.GetResult;
import com.mrf.app.utils.GetService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class SignInActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.ed_email)
    EditText edEmail;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.ed_mobile)
    EditText edMobile;
    @BindView(R.id.ed_city)
    EditText edCity;
    @BindView(R.id.btn_singup)
    Button btnSingup;
    @BindView(R.id.ed_fname)
    EditText edFname;
    @BindView(R.id.ed_lname)
    EditText edLname;
    static String globalNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        getSupportActionBar().hide();

    }

    @OnClick(R.id.btn_singup)
    public void onViewClicked() {
        if (isValidation()) {
            signUp();
        }
    }

    private void signUp() {
        GetService.showPrograss(SignInActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fname", edFname.getText().toString());
            jsonObject.put("lname", edLname.getText().toString());
            jsonObject.put("email", edEmail.getText().toString());
            jsonObject.put("password", edPassword.getText().toString());
            jsonObject.put("mobile", edMobile.getText().toString());
            jsonObject.put("city", edCity.getText().toString());

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSignUp((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void signUp(String number) {
        globalNumber = number;
//        GetService.showPrograss(SignInActivity.this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fname", number);
            jsonObject.put("lname", number);
            jsonObject.put("email", number);
            jsonObject.put("password",number);
            jsonObject.put("mobile", number);
            jsonObject.put("city", number);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getSignUp((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidation() {
        if (TextUtils.isEmpty(edFname.getText().toString())) {
            edFname.setError("Required First Name");
            return false;
        } else if (TextUtils.isEmpty(edLname.getText().toString())) {
            edLname.setError("Required Last Name");
            return false;
        } else if (TextUtils.isEmpty(edEmail.getText().toString())) {
            edEmail.setError("Required Email");
            return false;
        } else if (TextUtils.isEmpty(edPassword.getText().toString())) {
            edPassword.setError("Required Password");
            return false;
        } else if (TextUtils.isEmpty(edMobile.getText().toString())) {
            edMobile.setError("Required Phone Number");
            return false;
        } else if (TextUtils.isEmpty(edCity.getText().toString())) {
            edCity.setError("Required Password");
            return false;
        }

        return true;
    }

    @Override
    public void callback(JsonObject result, String callNo) {

//        if (callNo.equalsIgnoreCase("1") || result.toString().length() != 0) {
//            Gson gson = new Gson();
//            gson.fromJson(result.toString(), Response.class);
////            GetService.ToastMessege(SignInActivity.this, response.getResponseMsg());
//
////                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
//
////                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
//
//
//                }
//
//                finish();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
