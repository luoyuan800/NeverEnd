package cn.luo.yuan.maze.client.display.handler;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/6/27.
 */
public class OnlineActivityHandler extends Handler {
    private OnlineActivity onlineActivity;
    private ProgressDialog progress;
    public OnlineActivityHandler(OnlineActivity activity){
        this.onlineActivity =activity;
    }
    public void handleMessage(Message msg){
        switch (msg.what){
            case 4:
                if(progress!=null && progress.isShowing()){
                    progress.dismiss();
                }
                break;
            case 3:
                if(progress == null){
                    progress = new ProgressDialog(onlineActivity);
                }
                if(!progress.isShowing()){
                    if(msg.obj!=null){
                        progress.setMessage(msg.obj.toString());
                    }else{
                        progress.setMessage(StringUtils.EMPTY_STRING);
                    }
                    progress.show();
                }
                break;
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
