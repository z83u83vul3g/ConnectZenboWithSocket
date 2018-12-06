package cyut.sockettest;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cyut.handlerare.UIHandler;
import cyut.threadarea.OpenServerSocket;
import com.asus.robotframework.API.RobotAPI;

public class MainActivity extends Activity {
    private Handler handler;
    private OpenServerSocket serverSocketThread;
    private TextView showData, showStatus, displayIP;
    private Button startButton, showIP, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showStatus = findViewById(R.id.showStatus);
        showData = findViewById(R.id.showData);
        displayIP = findViewById(R.id.displayIP);
        showIP = findViewById(R.id.showIP);
        startButton = findViewById(R.id.startButton);
        exit = findViewById(R.id.exit);
        handler = new Handler();
        RobotAPI robotAPI = new RobotAPI(MainActivity.this);
        serverSocketThread = new OpenServerSocket(showStatus,showData,handler, robotAPI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverSocketThread.start();
            }
        });
        showIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayIP.setText(String.format("%s", getIP()));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverSocketThread.interrupt();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showStatus.setText(String.format("%s","Server is close"));
                    }
                });
            }
        });
    }

    private String getIP() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddr = wifiInfo.getIpAddress();
        return String.format("%s.%s.%s.%s", (ipAddr & 0xff), (ipAddr >> 8 & 0xff), (ipAddr >> 16 & 0xff), (ipAddr >> 24 & 0xff));
    }
}
