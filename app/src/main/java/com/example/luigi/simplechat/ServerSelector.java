package com.example.luigi.simplechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static com.example.luigi.simplechat.MainActivity.PORT;
import static com.example.luigi.simplechat.MainActivity.clientData;
import static com.example.luigi.simplechat.MainActivity.connected;

public class ServerSelector extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_selector);
    }

    public void connect(View view) {
        new Thread(() -> {
            EditText editText = (EditText) findViewById(R.id.editText2);
            String address = editText.getText().toString();
            try {
                Socket socket = new Socket(InetAddress.getByName(address), PORT);
                clientData = new ClientData("", socket);
                if (socket.isConnected()) {
                    Intent intent = new Intent(this, NameSelector.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(
                        getApplicationContext(),
                        "Server unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        }).start();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connected) finish();
    }
}
