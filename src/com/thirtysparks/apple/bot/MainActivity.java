package com.thirtysparks.apple.bot;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.GregorianCalendar;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String APPLE_ID = "ENTER_APPLIE_ID";
    private static final String PASSWORD = "ENTER_PASSWORD";

    ReserveWorker reserveWorker;

    TextView tvMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_main);

        tvMsg = (TextView)findViewById(R.id.tv_msg);

        reserveWorker = new ReserveWorker();

        findViewById(R.id.btn_captcha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginCaptcha();
            }
        });

        goFrontPage();
    }

    private void goFrontPage(){
        tvMsg.setText("Visiting front page");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String resultUrl = null;
                try {
                    resultUrl = reserveWorker.visitFirstPage();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                return resultUrl;
            }

            @Override
            protected void onPostExecute(String resultString) {
                //update the UI

                Log.d(TAG, "Result url is " + resultString);
                boolean isLogin = (resultString.startsWith("https://signin.apple.com/IDMSWebAuth/"));
                if(isLogin){
                    tvMsg.setText("Success. Redirected to login page");
                }
                else{
                    tvMsg.setText("Failed");
                }

                goGetCaptcha();

            }
        }.execute();
    }


    private void goGetCaptcha() {
        tvMsg.setText("Getting captcha");
        ImageView captchaImageView = (ImageView)findViewById(R.id.iv_captcha);
        String imageUrl = "https://signin.apple.com/IDMSWebAuth/imageCaptcha/942#" + GregorianCalendar.getInstance().getTime().getTime();
        Glide.with(MainActivity.this).load(imageUrl).into(captchaImageView);
    }



    private void goLoginCaptcha() {
        tvMsg.setText("Submitting captcha");

        final String captchaInput = ((EditText) findViewById(R.id.et_captcha)).getText().toString();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params)  {
                String string = null;
                try {
                    string = reserveWorker.loginWithCaptcha(captchaInput, APPLE_ID, PASSWORD);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                return string;
            }

            @Override
            protected void onPostExecute(String s) {
                if ("https://reserve-hk.apple.com/HK/en_HK/reserve/iPhone?execution=e1s2".equals(s)) {
                    tvMsg.setText("Apple ID Login successfully");
                } else {
                    tvMsg.setText("Error: Apple ID Login failed");
                }
            }
        }.execute();
    }
}
