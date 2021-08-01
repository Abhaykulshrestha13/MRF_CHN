package com.mrf.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mrf.app.R;
import com.mrf.app.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mrf.app.utils.SessionManager.CONTACT_US;


public class ContectusActivity extends AppCompatActivity {

    TextView textEmail,whatsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contectus);

        textEmail = findViewById(R.id.txteml);
        whatsApp = findViewById(R.id.txtwa);
    }

    public void email (View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, textEmail.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "MRF FOODS");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void whatsapp (View view){
        String number = whatsApp.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+number));
        startActivity(intent);
//        String url = "https://api.whatsapp.com/send?phone="+number;
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setType("text/plain");
//        sendIntent.setPackage("com.whatsapp");
//        sendIntent.setData(Uri.parse(url));
//        startActivity(sendIntent);
    }
}