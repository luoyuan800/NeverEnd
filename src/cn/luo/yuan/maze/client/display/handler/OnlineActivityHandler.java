package cn.luo.yuan.maze.client.display.handler;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;

/**
 * Created by luoyuan on 2017/6/27.
 */
public class OnlineActivityHandler extends Handler {
    private OnlineActivity onlineActivity;
    public OnlineActivityHandler(OnlineActivity activity){
        this.onlineActivity =activity;
    }
    public void handleMessage(Message msg){
        switch (msg.what){
            case 2:
                if(msg.obj instanceof Dialog){
                    ((Dialog) msg.obj).dismiss();
                }
                break;
            case 1:
                onlineActivity.showUploadDialog();
                break;
            case 0:
                onlineActivity.showErrorDialog();
                break;
        }
    }
}
