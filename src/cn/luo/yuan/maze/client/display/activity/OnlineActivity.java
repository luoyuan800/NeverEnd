package cn.luo.yuan.maze.client.display.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.display.handler.AdHandler;
import cn.luo.yuan.maze.client.display.handler.OnlineActivityHandler;
import cn.luo.yuan.maze.client.display.handler.OnlineActivityOnClickHandler;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ServerService;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.skill.PropertySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class OnlineActivity extends Activity {
    public ServerService service;
    public NeverEnd gameContext;
    public ScheduledExecutorService executor;
    public OnlineActivityHandler handler = new OnlineActivityHandler(this);
    private OnlineActivityOnClickHandler onClickHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resource.init(this);
        gameContext = (NeverEnd) getApplication();
        service = gameContext.getServerService();
        LogHelper.initLogSystem(this);
        setContentView(R.layout.online_view);
        executor = Executors.newScheduledThreadPool(4);
        gameContext.setContext(this);
        onClickHandler = new OnlineActivityOnClickHandler(this, gameContext);
        initView();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Drawable bitmap = Resource.loadImageFromSD("bak_1.png");
                if (bitmap != null) {
                    findViewById(R.id.online_view_container).setBackground(bitmap);
                }
            }
        });


    }

    public NeverEnd getContext() {
        return gameContext;
    }

    public void setContext(NeverEnd context) {
        this.gameContext = context;
    }

    public void showErrorDialog() {
        try {
            SimplerDialogBuilder.build("网络错误，稍后再试", Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }, this, gameContext.getRandom());
        } catch (Exception e) {
            LogHelper.logException(e, "Show net error");
        }
    }

    public void finish() {
        executor.shutdown();
        super.finish();
    }

    public void showUploadDialog() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    new AlertDialog.Builder(OnlineActivity.this).setMessage("上传你的人物数据到战斗塔，他/她会自动寻找其他玩家进行战斗，获取奖励。上传之后，你可以关闭本地的游戏，实现在线挂机。").setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            upload();
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
                } catch (Exception e) {
                    LogHelper.logException(e, "show upload Dialog");
                }
            }
        });
    }

    public void onClick(View view) {
        onClickHandler.onClick(view);
    }

    public void postGiftCount() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                final String count = service.postOnlineGiftCount(gameContext);
                final String debris = service.postDebrisCount(gameContext);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isNotEmpty(debris)) {
                            Button debrisButton = (Button) findViewById(R.id.debris_shop);
                            debrisButton.setText("碎片商店 X" + debris);
                        }

                        if (StringUtils.isNotEmpty(count)) {
                            Button giftButton = (Button) findViewById(R.id.online_gifts);
                            giftButton.setText("礼包 X" + count);
                            if (Integer.valueOf(count) <= 0) {
                                giftButton.setEnabled(false);
                            } else {
                                giftButton.setEnabled(true);
                            }
                        }
                    }
                });
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private void initView() {
        final AlertDialog dialog = new AlertDialog.Builder(this).setMessage("正在同步服务器数据，请稍候……").setCancelable(true).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
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
            String data = service.postOnlineData(gameContext);
            if (StringUtils.isEmpty(data)) {
                handler.sendEmptyMessage(1);//showuploaddialog
            } else {
                startPost();
            }
        } catch (Exception e) {
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

    private void upload() {
        final ServerData uploadData = new ServerData();
        uploadData.setHero(gameContext.getHero());
        uploadData.setAccessories(new ArrayList<Accessory>(gameContext.getHero().getAccessories().size()));
        for (Accessory accessory : gameContext.getHero().getAccessories()) {
            uploadData.getAccessories().add((Accessory) gameContext.convertToServerObject(accessory));
        }
        uploadData.setPets(new ArrayList<>(gameContext.getHero().getPets()));
        ArrayList<Skill> skills = new ArrayList<>();
        skills.addAll(Arrays.asList(gameContext.getHero().getSkills()));
        for(Skill skill : gameContext.getDataManager().loadAllSkill()){
            if(skill instanceof PropertySkill && skill.isEnable()){
                skills.add(skill);
            }
        }
        uploadData.setSkills(skills);
        uploadData.setMaze(gameContext.getMaze());
        NeverEndConfig config = gameContext.getDataManager().loadConfig();
        uploadData.setElementer(config.isElementer());
        uploadData.setLong(config.isLongKiller());
        uploadData.setHead(config.getHead());
        final AlertDialog uploadDialog = new AlertDialog.Builder(OnlineActivity.this).setMessage("上传中……").setCancelable(true).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
            }
        }).show();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (service.uploadHero(uploadData)) {
                    initDialog(uploadDialog);
                } else {
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    private void startPost() {
        ((RollTextView) findViewById(R.id.online_battle_msg)).addMessage("等待中……");

        executor.submit(new Runnable() {
            @Override
            public void run() {
                RangeAward ra = service.postRangeAward(gameContext);
                if(ra!=null){
                    gameContext.showPopup(ra.toString());
                    for(Goods goods:ra.getGoods()){
                        gameContext.getDataManager().add(goods);
                    }
                }
            }
        });
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postBattleMsg();
            }
        }, 10, Data.REFRESH_SPEED * 10, TimeUnit.MILLISECONDS);
        postOnlineRange();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postAward();
            }
        }, 15, Data.REFRESH_SPEED * 10, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                postGroup();
            }
        }, 20, Data.REFRESH_SPEED * 10, TimeUnit.MILLISECONDS);
        postGiftCount();
       /* executor.submit(new Runnable() {
            @Override
            public void run() {
                postDefender();
            }
        });*/
    }

    private void postDefender() {
        long maxLevel = gameContext.getMaze().getMaxLevel();
        if(maxLevel > 100) {
            long level = gameContext.getRandom().randomRange(100, maxLevel + 150);
            Hero defender = service.postDefender(level);
            if (defender != null) {
                gameContext.getDataManager().addDefender(defender, level);
            }
        }
    }

    private void postBattleMsg() {
        final String msg = service.postSingleBattleMsg(gameContext);
        if (StringUtils.isNotEmpty(msg)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((RollTextView) findViewById(R.id.online_battle_msg)).addMessage(msg);
                }
            });
        }
    }

    private void postOnlineRange() {
        try {
            final String msg = service.postOnlineRange(gameContext);
            if (StringUtils.isNotEmpty(msg)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.online_range)).setText(Html.fromHtml(msg));
                    }
                });
            }
        } catch (Exception e) {
            handler.sendEmptyMessage(0);//showErrorDialog();
            if (!(e instanceof IOException)) {
                LogHelper.logException(e, "OnlineActivity -> postOnlineRange");
            }
        }
    }

    private void postGroup() {
        try {
            final String msg = service.postOnlineData(gameContext);
            if (StringUtils.isNotEmpty(msg)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.group_message)).setText(Html.fromHtml(msg));
                    }
                });
            }
        } catch (Exception e) {
            handler.sendEmptyMessage(0);//showErrorDialog();
            if (!(e instanceof IOException)) {
                LogHelper.logException(e, "OnlineActivity -> postGroup");
            }
        }
    }

    private void postAward() {
        final String award = service.queryAwardString(gameContext);
        if (StringUtils.isNotEmpty(award)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.online_award)).setText(Html.fromHtml(award));
                }
            });
        }
    }

}
