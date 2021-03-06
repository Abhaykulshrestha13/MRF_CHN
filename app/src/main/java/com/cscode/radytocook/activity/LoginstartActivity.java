package com.cscode.radytocook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cscode.radytocook.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginstartActivity extends AppCompatActivity {

    @BindView(R.id.btn_signin)
    Button btnSignin;
    @BindView(R.id.txt_signup)
    TextView txtSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginstart);
        ButterKnife.bind(this);
        getSupportActionBar().hide();


    }

    @OnClick({R.id.btn_signin, R.id.txt_signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_signin:
                startActivity(new Intent(LoginstartActivity.this, SignUpActivity.class));
                break;
            case R.id.txt_signup:
                startActivity(new Intent(LoginstartActivity.this, SignInActivity.class));
                break;
            default:
                break;
        }
    }
}
