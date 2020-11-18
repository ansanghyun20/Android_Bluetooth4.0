package com.example.pratyush.ble_chat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class PHPSEARCH extends Activity {
    String ip;
    private static String TAG = "phpquerytest";
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_locate = "locate";
    private static final String TAG_date ="date";
    private static final String TAG_temp = "temp";
    private static final String TAG_time ="time";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListViewList;
    EditText mEditTextSearchKeyword1, mEditTextSearchKeyword2;
    String mJsonString;
    String keyword2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent1 = getIntent(); /*데이터 수신*/
        final String ipname = intent1.getExtras().getString("ipname");
        ip=ipname;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.InitializeListener();


        setContentView(R.layout.activity_phpsearch);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mListViewList = (ListView) findViewById(R.id.listView_main_list);
        mEditTextSearchKeyword1 = (EditText) findViewById(R.id.editText_main_searchKeyword1);
        mEditTextSearchKeyword2 = (EditText) findViewById(R.id.editText_main_searchKeyword2);
        Button back = (Button) findViewById(R.id.back2);

        Button button_search = (Button) findViewById(R.id.button_main_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyword2= mEditTextSearchKeyword2.getText().toString();
                mArrayList.clear();


                GetData task = new GetData();
                task.execute( mEditTextSearchKeyword1.getText().toString(), keyword2);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        mArrayList = new ArrayList<>();


    }
    public void InitializeListener()
    {
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                int month= monthOfYear+1;
                if((int)(Math.log10(monthOfYear)+1)==2 && (int)(Math.log10(dayOfMonth)+1)==2) {
                    mEditTextSearchKeyword2.setText(year + "-" + month + "-" + dayOfMonth);
                }
                else if((int)(Math.log10(monthOfYear)+1)==1 && (int)(Math.log10(dayOfMonth)+1)==2){
                    mEditTextSearchKeyword2.setText(year + "-0" + month + "-" + dayOfMonth);
                }
                else if((int)(Math.log10(monthOfYear)+1)==1 && (int)(Math.log10(dayOfMonth)+1)==1){
                    mEditTextSearchKeyword2.setText(year + "-0" + month + "-0" + dayOfMonth);
                }
                else if((int)(Math.log10(monthOfYear)+1)==2 && (int)(Math.log10(dayOfMonth)+1)==1){
                    mEditTextSearchKeyword2.setText(year + "-" + month + "-0" + dayOfMonth);
                }


            }
        };
    }

    public void OnClickHandler(View view)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2020, 7, 22);

        dialog.show();
    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PHPSEARCH.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];

            String serverURL = "http://"+ip+"/query.php";
            String postParameters = "locate=" + searchKeyword1 + "&date=" + searchKeyword2;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String locate = item.getString(TAG_locate);
                String temp = item.getString(TAG_temp);
                String time = item.getString(TAG_time);
                String date = item.getString(TAG_date);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_locate, locate);
                hashMap.put(TAG_temp, temp);
                hashMap.put(TAG_time, time);
                hashMap.put(TAG_date, date);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    PHPSEARCH.this, mArrayList, R.layout.php_list_view,
                    new String[]{TAG_locate,TAG_time,TAG_date, TAG_temp},
                    new int[]{R.id.textView_list_locate, R.id.textView_list_name,R.id.textView_list_date, R.id.textView_list_address}
            );

            mListViewList.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}