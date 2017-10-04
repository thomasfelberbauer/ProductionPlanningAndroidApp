package com.production_planning_game.eps_game;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    EditText machine;
    EditText passwordm;
    Button loginButton;
    public static String machinenumber;
    public static String nbrM;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        machine     =  (EditText) findViewById(R.id.textMachine);
        passwordm   =  (EditText) findViewById(R.id.textPassword);
        loginButton =  (Button) findViewById(R.id.buttonLogin);


        loginButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                machinenumber = machine.getText().toString(); //get the text from the username you fill
                nbrM = machinenumber.substring(7); //save the number of the machine you are
                String password = passwordm.getText().toString(); //get the text from the password you fill

                HttpRequest req = new HttpRequest(getApplicationContext());
                req.requestName = "appsignin"; //send which request you do in the HttpRequest activity
                req.var1 = machinenumber; //send the username you fill
                req.var2 = password; //send the password you fill
                req.execute(); //Do the request to the server
            }
        });
    }
}
