package com.cscode.radytocook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cscode.radytocook.R;
import com.cscode.radytocook.model.GenericTextWatcher;
import com.cscode.radytocook.model.User;
import com.cscode.radytocook.retrofit.APIClient;
import com.cscode.radytocook.retrofit.GetResult;
import com.cscode.radytocook.utils.GetService;
import com.cscode.radytocook.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class OtpLoginActivity extends AppCompatActivity implements GetResult.MyListener {

    private EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six;
    private String verificationId;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login);

        sessionManager = new SessionManager(OtpLoginActivity.this);
        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format(
                "+91 - %s", getIntent().getStringExtra("mobile")
        ));

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);

        setupOtpInputs();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button buttonVerify = findViewById(R.id.idBtnVerify);

        verificationId = getIntent().getStringExtra("verificationId");

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp_textbox_one.getText().toString().trim().isEmpty() ||
                        otp_textbox_two.getText().toString().trim().isEmpty() ||
                        otp_textbox_three.getText().toString().trim().isEmpty() ||
                        otp_textbox_four.getText().toString().trim().isEmpty() ||
                        otp_textbox_five.getText().toString().trim().isEmpty() ||
                        otp_textbox_six.getText().toString().trim().isEmpty()) {
                    Toast.makeText(OtpLoginActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = otp_textbox_one.getText().toString() +
                        otp_textbox_two.getText().toString() +
                        otp_textbox_three.getText().toString() +
                        otp_textbox_four.getText().toString() +
                        otp_textbox_five.getText().toString() +
                        otp_textbox_six.getText().toString();
                if (verificationId != null)
                    progressBar.setVisibility(View.VISIBLE);
                buttonVerify.setVisibility(View.INVISIBLE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId, code
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                buttonVerify.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    {
                                        FirebaseUser user = task.getResult().getUser();

                                        long creationTimestamp = user.getMetadata().getCreationTimestamp();
                                        long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                                        String phoneNumber = getIntent().getStringExtra("mobile");
                                        if (creationTimestamp == lastSignInTimestamp) {
                                            //do create new user
                                            signUp(phoneNumber);
                                        }
                                            //user is exists, just do login
//                                            signUp(phoneNumber);
                                            login(phoneNumber);

                                    }

                                } else {
                                    Toast.makeText(OtpLoginActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

            }
        });

    }

    private void signUp(String number) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fname", number);
            jsonObject.put("lname", number);
            jsonObject.put("email", number);
            jsonObject.put("password", number);
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

    private void login(String phoneNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", phoneNumber);
            jsonObject.put("password", phoneNumber);

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
                startActivity(new Intent(OtpLoginActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }
    }

    private void setupOtpInputs() {
        otp_textbox_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp_textbox_two.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp_textbox_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp_textbox_three.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp_textbox_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp_textbox_four.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp_textbox_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp_textbox_five.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp_textbox_five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp_textbox_six.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}


//public class OtpLoginActivity extends AppCompatActivity implements GetResult.MyListener {
//
//
//    // variable for FirebaseAuth class
//    private FirebaseAuth mAuth;
//
//    // variable for our text input
//    // field for phone and OTP.
//    private EditText edtPhone, edtOTP;
//
//    // buttons for generating OTP and verifying OTP
//    private Button verifyOTPBtn, generateOTPBtn;
//
//    // string for storing our verification ID
//    private String verificationId;
//    SessionManager sessionManager;
//
//    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six;
//    Button verify_otp;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_otp_login);
//
//
//        // below line is for getting instance
//        // of our FirebaseAuth.
//        mAuth = FirebaseAuth.getInstance();
//
//        // initializing variables for button and Edittext.
//        edtPhone = findViewById(R.id.idEdtPhoneNumber);
//        edtOTP = findViewById(R.id.idEdtOtp);
//        verifyOTPBtn = findViewById(R.id.idBtnVerify);
//        generateOTPBtn = findViewById(R.id.idBtnGetOtp);
//        sessionManager = new SessionManager(OtpLoginActivity.this);
//
//        // setting onclick listner for generate OTP button.
//        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // below line is for checking weather the user
//                // has entered his mobile number or not.
//                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
//                    // when mobile number text field is empty
//                    // displaying a toast message.
//                    Toast.makeText(OtpLoginActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
//                } else {
//                    // if the text field is not empty we are calling our
//                    // send OTP method for getting OTP from Firebase.
//                    String phone = "+91" + edtPhone.getText().toString();
//                    sendVerificationCode(phone);
//                }
//            }
//        });
//
//        otp_textbox_one = findViewById(R.id.otp_edit_box1);
//        otp_textbox_two = findViewById(R.id.otp_edit_box2);
//        otp_textbox_three = findViewById(R.id.otp_edit_box3);
//        otp_textbox_four = findViewById(R.id.otp_edit_box4);
//        otp_textbox_five = findViewById(R.id.otp_edit_box5);
//        otp_textbox_six = findViewById(R.id.otp_edit_box6);
//
//        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four};
//
//        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
//        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
//        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
//        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
//        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
//        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));
//
//        // initializing on click listener
//        // for verify otp button
//        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // validating if the OTP text field is empty or not.
//                if (TextUtils.isEmpty(edit.toString())) {
//                    // if the OTP text field is empty display
//                    // a message to user to enter OTP
//                    Toast.makeText(OtpLoginActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
//                } else {
//                    // if OTP field is not empty calling
//                    // method to verify the OTP.
//                    verifyCode(edit.toString());
//                }
//            }
//        });
//    }
//
//    private void signInWithCredential(PhoneAuthCredential credential) {
//        // inside this method we are checking if
//        // the code entered is correct or not.
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // if the code is correct and the task is successful
//                            // we are sending our user to new activity.
//
//                            FirebaseUser user = task.getResult().getUser();
//                            long creationTimestamp = user.getMetadata().getCreationTimestamp();
//                            long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
//                            String phoneNumber = edtPhone.getText().toString();
//                            if (creationTimestamp == lastSignInTimestamp) {
//                                //do create new user
//                                signUp(phoneNumber);
//                                login(phoneNumber);
//                            } else {
//                                //user is exists, just do login
//                                login(phoneNumber);
//                            }
//                            finish();
//                        } else {
//                            // if the code is not correct then we are
//                            // displaying an error message to the user.
//                            Toast.makeText(OtpLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }
//
//    private void signUp(String number) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("fname", number);
//            jsonObject.put("lname", number);
//            jsonObject.put("email", number);
//            jsonObject.put("password", number);
//            jsonObject.put("mobile", number);
//            jsonObject.put("city", number);
//
//            JsonParser jsonParser = new JsonParser();
//            Call<JsonObject> call = APIClient.getInterface().getSignUp((JsonObject) jsonParser.parse(jsonObject.toString()));
//            GetResult getResult = new GetResult();
//            getResult.setMyListener(this);
//            getResult.callForLogin(call, "1");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void login(String phoneNumber) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("email", phoneNumber);
//            jsonObject.put("password", phoneNumber);
//
//            JsonParser jsonParser = new JsonParser();
//            Call<JsonObject> call = APIClient.getInterface().getSignIn((JsonObject) jsonParser.parse(jsonObject.toString()));
//            GetResult getResult = new GetResult();
//            getResult.setMyListener(this);
//            getResult.callForLogin(call, "1");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void sendVerificationCode(String number) {
//        // this method is used for getting
//        // OTP on user phone number.
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(number)       // Phone number to verify
//                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // Activity (for callback binding)
//                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }
//
//    // callback method is called on Phone auth provider.
//    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
//
//            // initializing our callbacks for on
//            // verification callback method.
//            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        // below method is used when
//        // OTP is sent from Firebase
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            // when we receive the OTP it
//            // contains a unique id which
//            // we are storing in our string
//            // which we have already created.
//            verificationId = s;
//        }
//
//        // this method is called when user
//        // receive OTP from Firebase.
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            // below line is used for getting OTP code
//            // which is sent in phone auth credentials.
//            final String code = phoneAuthCredential.getSmsCode();
//
//            // checking if the code
//            // is null or not.
//            if (code != null) {
//                // if the code is not null then
//                // we are setting that code to
//                // our OTP edittext field.
//                edtOTP.setText(code);
//
//                // after setting this code
//                // to OTP edittext field we
//                // are calling our verifycode method.
//                verifyCode(code);
//            }
//        }
//
//        // this method is called when firebase doesn't
//        // sends our OTP code due to any error or issue.
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            // displaying error message with firebase exception.
//            Toast.makeText(OtpLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    };
//
//    // below method is use to verify code from Firebase.
//    private void verifyCode(String code) {
//        // below line is used for getting getting
//        // credentials from our verification id and code.
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//
//        // after getting credential we are
//        // calling sign in method.
//        signInWithCredential(credential);
//    }

//}
