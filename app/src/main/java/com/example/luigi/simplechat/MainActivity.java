package com.example.luigi.simplechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    public static ClientData clientData = null;
    public static boolean connected = false;
    public static int PORT = 8189;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollo);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMessage(this.getCurrentFocus());
                return true;
            }
            return false;
        });

        if (clientData != null) {
            if (!clientData.getSocket().isConnected() && connected && clientData.isInitialized()) {
                try {
                    clientData.setSocket(new Socket(clientData.getAddress(), PORT));
                } catch (IOException e) {
                    connected = false;
                    Toast.makeText(
                            getApplicationContext(),
                            "Server down.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connected) {
            new Thread(this::listenToMessages).start();
        }
    }

    public void sendMessage(View view) {
        new Thread(() -> {
            if (connected) {
                EditText editText = (EditText) findViewById(R.id.editText);
                String text = editText.getText().toString().trim();
                if (!text.isEmpty()) {
                    clientData.out.println("MESSAGE=" + text);
                    runOnUiThread(() -> editText.setText(""));
                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "You need to connect to a server.",
                        Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.connectItem) {
            if (!connected) {
                Intent intent = new Intent(this, ServerSelector.class);
                startActivity(intent);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Already connected.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.exit) {
            if (connected) {
                new Thread(() -> {
                    clientData.out.println("EXIT");
                }).start();
            }
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void listenToMessages() {
        try {
            //noinspection InfiniteLoopStatement
            while (connected) {
                String inputLine = clientData.in.readLine();
                if (inputLine.startsWith("MESSAGE=")) {
                    runOnUiThread(() -> {
                        TextView textView = (TextView) findViewById(R.id.textView);
                        textView.append(inputLine.split("=", 2)[1] + "\n");
                    });
                } else if (inputLine.equals("KEEPALIVE")) {
                    clientData.out.println("KEEPALIVE");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
