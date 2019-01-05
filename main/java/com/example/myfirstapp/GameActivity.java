package com.example.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.example.myfirstapp.client.view.NonBlockingInterpreter;


public class GameActivity extends AppCompatActivity {
    NonBlockingInterpreter nb;
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String username = intent.getStringExtra(MainActivity.EXTRA_USER);
        TextView textView = findViewById(R.id.gameHistory);
        joinButton = (Button) findViewById(R.id.button_join);
        Button rockButton = (Button) findViewById(R.id.button_rock);
        Button paperButton = (Button) findViewById(R.id.button_paper);
        Button scissorButton = (Button) findViewById(R.id.button_scissor);


        nb = new NonBlockingInterpreter(textView, joinButton /* , rockButton, paperButton, scissorButton*/);

        final String connectMessage = "CONNECT " + message + " 8081";

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nb.nextCall(connectMessage);
            }
        });
    }
    public void sendUsername(View view) {
        EditText editUsername = (EditText) findViewById(R.id.editUsername);
        final String cmdUsername = "USER " + editUsername.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                nb.nextCall(cmdUsername);
            }
        }).start();
    }

    public void sendJoin(View view) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nb.nextCall("JOIN");
                //if(nb.join){
                //    joinButton.setVisibility(View.INVISIBLE);
                //}
            }
        });
    }

    public void sendPlay(View view){
        final String choice = view.getTag().toString();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nb.nextCall("PLAY " + choice);
            }
        });
    }

    public void sendQuit(View view) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() { nb.nextCall("QUIT"); }
        });
    }

}
