package cyut.handlerare;

import android.os.Handler;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotFace;

/**
 * Created by DennyOcean on 2018/4/4.
 */

public class UIHandler {
    private Handler handler;
    private TextView textView;
    private RobotAPI robotAPI;

    /**
     * 建構子:傳入主執行緒的物件,以利各式資料改變時能夠使裝置動作或畫面更新.
     * @param pTextView 用於顯示狀態更新.
     * @param pHandler 副執行緒透過此物件與主執行緒溝通.
     */
    public UIHandler(TextView pTextView, Handler pHandler) {
        this.handler = pHandler;
        this.textView = pTextView;
    }

    public UIHandler(Handler pHandler) {
        this.handler = pHandler;
    }

    public void updateUI(final String updateMessage) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(String.format("%s", updateMessage));
            }
        });
    }

    /**
     * 讓Zenbo動
     * @param parentRobAPI 傳入MainActivity的Zenbo操作類
     * @param score 遊戲端傳來的動作百分比
     */
    public void letZenboRespond(RobotAPI parentRobAPI, int score) {
        this.robotAPI = parentRobAPI;
        if(score >= 70){
            this.robotAPI.robot.setExpression(RobotFace.SINGING, "你做得很好");
            this.robotAPI.utility.playAction(score - 73);
        }else if(score >= 50){
            this.robotAPI.robot.setExpression(RobotFace.HAPPY, "繼續加油");
            this.robotAPI.utility.playAction(score - 75);
        }else if(score >= 20){
            this.robotAPI.robot.setExpression(RobotFace.QUESTIONING, "要再努力哦!");
            this.robotAPI.utility.playAction(score - 69);
        }else{
            this.robotAPI.robot.setExpression(RobotFace.SHOCKED, "分數也太低了吧!");
            this.robotAPI.utility.playAction(score - 66);
        }
        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                robotAPI.cancelCommand(RobotCommand.MOTION_PLAY_ACTION.getValue());
            }
        },3000);
    }
}
