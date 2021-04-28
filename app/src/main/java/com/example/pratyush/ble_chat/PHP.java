package com.example.pratyush.ble_chat;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PHP extends Activity {




    
    private static String TAG = "phptest";

    String value;
    String locate_text;
    private TextView mTextViewResult;
    private TextView Temptext;
    private TextView Datetext;
    private TextView Timetext;
    private TextView temp_inspec;
    private TextView test;
    private TextView temp_sensor;
    private TextView locatetext;
    Button modify;
    // 사이드바 배열을 위한 어레이 리스트
    ArrayAdapter<String> adapter;
    ArrayList<String> array = new ArrayList<>();
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_php);

        //Drawer에 List에 JSon 내용을 가져옴 ( 꺼도 저장되는 기능 함수는 아래에 정의 되어 있다. )
        array = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);
        
        //DrawerLayout 연결
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        final View drawerView = (View) findViewById(R.id.drawer);


        Intent intent1 = getIntent(); /*데이터 수신*/
        final String temp = intent1.getExtras().getString("temp");
        final String date = intent1.getExtras().getString("date");
        final String ipname = intent1.getExtras().getString("ipname");
        final String time = intent1.getExtras().getString("time");
        final String sensor = intent1.getExtras().getString("sensor");
        final String result = intent1.getExtras().getString("result");


        mTextViewResult = (TextView) findViewById(R.id.textView_main_result);
        Temptext = (TextView) findViewById(R.id.temptext);
        Datetext = (TextView) findViewById(R.id.datetext);
        Timetext = (TextView) findViewById(R.id.timetext);
        test = (TextView) findViewById(R.id.test);
        locatetext = (TextView) findViewById(R.id.locatetext);
        temp_inspec = (TextView) findViewById(R.id.temp_inspection);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());
        temp_sensor = (TextView)findViewById(R.id.Temperature_Sensor);
        Button btnOpenDrawer = (Button) findViewById(R.id.btn_OpenLocate);
        Button btnCloseDrawer = (Button) findViewById(R.id.btn_CloseLocate);
        Button addButton = (Button) findViewById(R.id.add);
        Button modifyButton = (Button) findViewById(R.id.modify);
        Button deleteButton = (Button) findViewById(R.id.delete);
        Button back = (Button) findViewById(R.id.back1);
        modify = (Button) findViewById(R.id.btn_modify_confirm);
        Temptext.setText(temp);
        Datetext.setText(date);
        Timetext.setText(time);
        temp_sensor.setText(sensor);
        temp_inspec.setText(result);
        array = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);
        
        //array와 레이아웃의 어댑터
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, array);

        /*final ArrayList<String> items = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items);*/
        
        
        // 리스트뷰를 생성하고 adapter와 연결한다. array를 보여주기위한 과정인듯
        final ListView listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);


        modify.setVisibility(View.GONE);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String vo = (String) adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                String str = array.get(i);
                locate_text=vo;
                locatetext.setText(vo);    //드로어 밖의 장소를 지칭하는 부분에 값을 채워 넣는다.
            }
        });


        Button buttonInsert = (Button) findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locate = locatetext.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + ipname + "/insert.php", date, temp, time, locate, sensor,result);

                locatetext.setText("");


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });


        // 드로어 여는 버튼 리스너
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        // 입력 완료후 닫는 리스터
        btnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.closeDrawer(drawerView);

            }
        });


        //리스트뷰에 아이템 추가
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count;
                count = adapter.getCount();

                // 아이템 추가.
                array.add("LIST" + Integer.toString(count + 1));

                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        });

        //리스트뷰에 아이템 수정

        modify.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        // 아이템 수정
                        String abc = test.getText().toString();
                        array.set(checked, abc);
                        locatetext.setText(locate_text);
                        modify.setVisibility(View.GONE);
                        // listview 갱신
                        adapter.notifyDataSetChanged();
                    }
                }
             }
         });


        modifyButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                show();

            }
        });


        //리스트뷰에 아이템 삭제
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        // 아이템 삭제
                        array.remove(checked);

                        // listview 선택 초기화.
                        listview.clearChoices();

                        // listview 갱신.
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }


        public void show() {

            AlertDialog.Builder ad = new AlertDialog.Builder(PHP.this);
            final EditText et = new EditText(PHP.this);
            ad.setView(et);
            ad.setMessage("원하는 장소명을 입력하세요.");   // 내용 설정
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Log.v(TAG, "Yes Btn Click");

                    modify.setVisibility(View.VISIBLE);
                    value = et.getText().toString();
                    Log.v(TAG, value);
                    test.setText(value);
                    dialog.dismiss();

                }
            });


            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.v(TAG, "No Btn Click");
                    dialog.dismiss();     //닫기

                }
            });
            ad.show();
        }



    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }

    private ArrayList getStringArrayPref(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
    
    @Override
    protected void onPause() {  // 앱을 일시 정지 했을 때 set String Array로 Sharedpre 를 통해 JSon으로 저장되고 자료를 지워지지 않게 둠
        super.onPause();

        setStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON, array);
        Log.d(TAG, "Put json");
    }








    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PHP.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }
        @Override
        protected String doInBackground(String... params) {

            String date = (String)params[1];
            String temp = (String)params[2];
            String time = (String)params[3];
            String locate = (String)params[4];
            String sensor = (String)params[5];
            String result = (String)params[6];
            String serverURL = (String)params[0];
            String postParameters = "date=" + date + "&temp=" + temp + "&time=" + time + "&locate=" + locate + "&sensor=" + sensor + "&result=" + result;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}
