package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String EXTRA_USER = "com.example.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the connect button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        final String ipaddress = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, ipaddress);

        startActivity(intent);

    }
}
