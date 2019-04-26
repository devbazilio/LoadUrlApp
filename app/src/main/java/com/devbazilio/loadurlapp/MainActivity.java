package com.devbazilio.loadurlapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private TextView urlTextBox = null;
    private EditText resultTextBox = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        Button b = findViewById(R.id.buttonGo);
        urlTextBox = findViewById(R.id.urlTextBox);
        resultTextBox = findViewById(R.id.resultTextBox);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resultTextBox.setText("Loading");
                onClickInternal();
            }
        });
        b = findViewById(R.id.btnLoadUrl);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resultTextBox.setText("Loading URL");
                onClickLoadUrl();
            }
        });

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (result!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, 0);

        }

    }

    private void onClickInternal() {
        String s = urlTextBox.getText().toString();
        InetAddress addresses[];
        try {
            resultTextBox.setText("");
            addresses = InetAddress.getAllByName(s);
            for (InetAddress a : addresses) {
                resultTextBox.append(a.toString());
            }
        } catch (UnknownHostException e) {
            resultTextBox.setText(e.getMessage());
        } catch (SecurityException e) {
            resultTextBox.setText(e.getMessage());
        }
    }

    private void onClickLoadUrl() {
        String s = urlTextBox.getText().toString();
        try {
            s = getContents(s);
            resultTextBox.setText(s);
        } catch (Exception e) {
            resultTextBox.setText(e.getMessage());
        }

    }

    public static String getContents(String url) {
        String contents = "";

        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            InputStream in = conn.getInputStream();
            contents = convertStreamToString(in);
        } catch (Exception e) {
            Log.e("app", e.getMessage(), e);
        }

        return contents;
    }

    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}