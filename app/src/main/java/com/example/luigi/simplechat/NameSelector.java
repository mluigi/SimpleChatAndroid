package com.example.luigi.simplechat;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import static com.example.luigi.simplechat.MainActivity.clientData;
import static com.example.luigi.simplechat.MainActivity.connected;

public class NameSelector extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_selector);

    }

    public void checkClientName(View view) {
        new Thread(() -> {
            Looper.prepare();
            try {
                EditText editText = (EditText) findViewById(R.id.editText3);
                String name = editText.getText().toString();
                clientData.setName(name);
                clientData.out.println("CHECKNAME=" + name);
                String inputLine = clientData.in.readLine();
                if (inputLine.equals("CHECKNAME=ERROR")) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Name already exists.\nChoose a new name.",
                            Toast.LENGTH_SHORT).show();
                } else if (inputLine.equals("CHECKNAME=OK")) {
                    connected = true;
                    clientData.setInitialized();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
