package cyut.threadarea;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCommand;

import cyut.handlerare.UIHandler;

/**
 * Created by DennyOcean on 2018/4/4.
 */

public class OpenServerSocket extends Thread {
    private static final int LISTEN_PORT = 5987;
    private Handler handler;
    private TextView statusTextView, dataTextView;
    private String line = "";
    private ServerSocket serverSocket;
    private RobotAPI robotAPI;

    /**
     * 建構子:傳入主執行緒的物件,以利各式資料改變時能夠使裝置動作或畫面更新.
     * @param pStatusTextView 用於顯示狀態更新.
     * @param pDataTextView 用於顯示接收到的資料.
     * @param pHandler 副執行緒透過此物件與主執行緒溝通.
     * @param pRobot 用於操作Zenbo各式功能.
     */
    public OpenServerSocket(TextView pStatusTextView, TextView pDataTextView, Handler pHandler, RobotAPI pRobot) {

        this.statusTextView = pStatusTextView;
        this.dataTextView = pDataTextView;
        this.handler = pHandler;
        this.robotAPI = pRobot;
        try {
            serverSocket = new ServerSocket(LISTEN_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                try {
                    new UIHandler(statusTextView, handler).updateUI("Listening...");
                    Socket client = serverSocket.accept();
                    new UIHandler(statusTextView, handler).updateUI("Connected");
                    BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    while (true) {
                        if (!isClientClose(client)) {
                            line = bf.readLine();
                            new UIHandler(dataTextView, handler).updateUI("data is " + line);
                            new UIHandler(this.handler).letZenboRespond(this.robotAPI, Integer.parseInt(line));
                            /*
                            switch (line) {
                                case "dance":
                                    //new UIHandler(handler).letZenboRespond(this.robotAPI);
                                    break;
                                case "break":
                                    client.close();
                                    new UIHandler(statusTextView, handler).updateUI("client is disconnection");
                                    break;
                            }
                            */
                        }else{
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new UIHandler(statusTextView, handler).updateUI("client is disconnection");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static boolean isClientClose(Socket socket) {
        try {
            // 如果斷線,sendUtgentData 會因為送不出來導致IOException
            socket.sendUrgentData(0);
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
