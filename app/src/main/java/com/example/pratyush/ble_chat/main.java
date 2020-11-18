package com.example.pratyush.ble_chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class main extends Activity {

    EditText IP_TEXT;
    Button IP_SEND;
    TextView IP_TEXTVIEW;
    TextView message;
    String shared = "file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstmain);
        IP_TEXT = (EditText) findViewById(R.id.ipadress);
        IP_SEND = (Button) findViewById(R.id.ipadressSend);
        IP_TEXTVIEW = (TextView) findViewById(R.id.iptextview);
        message = (TextView)findViewById(R.id.messageeeeeee111) ;
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        IP_TEXT.startAnimation(animation);

        SharedPreferences sharedPreferences = getSharedPreferences(shared, 0);
        String value = sharedPreferences.getString("key", "");
        IP_TEXT.setText(value);
        IP_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    SharedPreferences sharedPreferences = getSharedPreferences(shared, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String value = IP_TEXT.getText().toString();
                    editor.putString("key", value);
                    editor.commit();

                    Intent intentmain = new Intent(main.this, ClientActivity.class);
                    IP_TEXTVIEW.setText("연결 성공");


                    intentmain.putExtra("ipname", IP_TEXT.getText().toString()); /*송신*/


                    startActivity(intentmain);


                    overridePendingTransition(R.anim.out_left,R.anim.out_right);

                }




        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = getSharedPreferences(shared, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = IP_TEXT.getText().toString();
        editor.putString("key", value);
        editor.commit();


    }
}