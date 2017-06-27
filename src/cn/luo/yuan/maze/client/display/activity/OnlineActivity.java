package cn.luo.yuan.maze.client.display.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.display.handler.OnlineActivityHandler;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ServerService;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class OnlineActivity extends Activity {
    private ServerService service = new ServerService(getVersion());
    private NeverEnd gameContext;
    private ScheduledExecutorService executor;
    private OnlineActivityHandler handler = new OnlineActivityHandler(this);
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameContext = (NeverEnd)getApplication();
        Resource.init(this);
        LogHelper.initLogSystem(this);
        setContentView(R.layout.online_view);
        executor = gameContext.getExecutor();
        gameContext.setContext(this);
        initView();
    }

    public NeverEnd getContext() {
        return gameContext;
    }

    public void setContext(NeverEnd context) {
        this.gameContext = context;
    }

    private void initView(){
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("正在同步服务器数据，请稍候……").setCancelable(false).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).show();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                initDialog(dialog);
            }
        });
    }

    private void initDialog(Dialog showing) {
        try {
            ServerData data = service.queryOnlineHeroData(gameContext);
            if (data == null || data.hero == null) {
                handler.sendEmptyMessage(1);//showuploaddialog
            } else {
                startPost();
            }
        }catch (Exception e){
            handler.sendEmptyMessage(0);//showErrorDialog();
        }
        dismissDialog(showing);
    }

    private void dismissDialog(Dialog showing) {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = showing;
        handler.sendMessage(msg);
    }

    public void showErrorDialog() {
        SimplerDialogBuilder.build("网络错误，稍后再试", Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        },this);
    }

    public void showUploadDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(OnlineActivity.this).setMessage("上传你的人物数据到战斗塔，他/她会自动寻找其他玩家进行战斗，获取奖励。上传之后，你可以关闭本地的游戏，实现在线挂机。").setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        upload();
                    }
                }).show();
            }
        });
    }

    private void upload() {
        ServerData uploaddData = new ServerData();
        uploaddData.hero = gameContext.getHero();
        uploaddData.accessories = new ArrayList<>(gameContext.getHero().getAccessories());
        uploaddData.pets = new ArrayList<>(gameContext.getHero().getPets());
        uploaddData.skills = Arrays.asList(gameContext.getHero().getSkills());
        uploaddData.maze = gameContext.getMaze();
        AlertDialog uploadDialog = new AlertDialog.Builder(OnlineActivity.this).setMessage("上传中……").setCancelable(true).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
            }
        }).show();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if(service.uploadHero(uploaddData)){
                    initDialog(uploadDialog);
                }else{
                   handler.sendEmptyMessage(0);
                }
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.action_button:
                SimplerDialogBuilder.build("你确定退出战斗塔吗？", Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimplerDialogBuilder.build("正在同步服务器数据……", OnlineActivity.this, false);
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                getBackHeroData();
                            }
                        });
                    }
                }, this);
                break;
        }
    }

    private void getBackHeroData() {
        String award = service.queryAwardString(gameContext);
        ServerData data = service.getBackHero(gameContext);
        if(data!=null && StringUtils.isNotEmpty(award)) {
            gameContext.getHero().setMaterial(gameContext.getHero().getMaterial() + data.material);
            if (data.accessories != null) {
                for (Accessory accessory : data.accessories) {
                    gameContext.getDataManager().saveAccessory(accessory);
                }
            }
            if (data.pets != null) {
                for (Pet pet : data.pets) {
                    gameContext.getDataManager().savePet(pet);
                }
            }
            SimplerDialogBuilder.build("你获得了：<br>" + award, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            },OnlineActivity.this);
        }else{
            finish();
        }
    }

    private void startPost(){
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postBattleMsg();
            }
        },10, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postOnlineDataMsg();
            }
        },10, Data.REFRESH_SPEED, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postAward();
            }
        }, 15, Data.REFRESH_SPEED * 5, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postGroup();
            }
        }, 20, Data.REFRESH_SPEED * 10, TimeUnit.MILLISECONDS);
    }

    private void postBattleMsg(){
        String msg = service.postSingleBattleMsg(gameContext);
        if(StringUtils.isNotEmpty(msg)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((RollTextView)findViewById(R.id.online_battle_msg)).addMessage(msg);
                }
            });
        }
    }
    private void postOnlineDataMsg(){
        String msg = service.postOnlineData(gameContext);
        if(StringUtils.isNotEmpty(msg)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((TextView)findViewById(R.id.online_data)).setText(Html.fromHtml(msg));
                }
            });
        }
    }

    private void postGroup(){
        //TODO GROUP
    }

    private void postAward(){
        String award = service.queryAwardString(gameContext);
        if(StringUtils.isNotEmpty(award)){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((TextView)findViewById(R.id.online_award)).setText(Html.fromHtml(award));
                }
            });
        }
    }

    public String getVersion() {
        try {
            String pkName = getPackageName();
            int versionCode = getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return versionCode + "";
        } catch (Exception e) {
            return "0";
        }
    }

}
