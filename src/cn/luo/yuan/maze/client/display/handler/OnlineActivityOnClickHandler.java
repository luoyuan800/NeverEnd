package cn.luo.yuan.maze.client.display.handler;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;
import cn.luo.yuan.maze.client.display.dialog.ShopDialog;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.utils.StringUtils;
import sw.ls.ps.normal.common.ErrorCode;
import sw.ls.ps.normal.spot.SpotListener;
import sw.ls.ps.normal.spot.SpotManager;
import sw.ls.ps.normal.video.VideoAdListener;
import sw.ls.ps.normal.video.VideoAdManager;
import sw.ls.ps.normal.video.VideoAdSettings;

/**
 * Created by luoyuan on 2017/7/5.
 */
public class OnlineActivityOnClickHandler {
    private OnlineActivity activity;
    private NeverEnd context;

    public OnlineActivityOnClickHandler(OnlineActivity activity, NeverEnd context) {
        this.activity = activity;
        this.context = context;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ad_show:
                activity.adHandler.showAd();
                break;
            case R.id.online_shop:
                Message message = new Message();
                message.what = 3;
                message.obj = "正在开启";
                activity.handler.sendMessage(message);
                activity.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        new ShopDialog(context).showOnlineShop(activity.handler);
                        activity.handler.sendEmptyMessage(4);
                    }
                });
                break;
            case R.id.online_gifts:
                activity.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Object o = activity.service.openOnlineGift(context);
                        activity.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (o instanceof String) {
                                    SimplerDialogBuilder.build(o.toString(), Resource.getString(R.string.conform), null, context.getContext());
                                } else if (o == null) {
                                    SimplerDialogBuilder.build("谢谢惠顾！", Resource.getString(R.string.conform), null, activity);
                                } else {
                                    if (o instanceof IDModel) {
                                        context.getDataManager().add((IDModel) o);
                                    }
                                    if (o instanceof NameObject) {
                                        SimplerDialogBuilder.build("获得了 " + ((NameObject) o).getDisplayName(), Resource.getString(R.string.conform), null, activity);
                                    } else {
                                        SimplerDialogBuilder.build("获得了 " + o, Resource.getString(R.string.conform), null, activity);
                                    }
                                }
                                activity.postGiftCount();
                            }
                        });
                    }
                });
                break;
            case R.id.action_button:
                SimplerDialogBuilder.build("你确定退出战斗塔吗？", Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final Dialog sycDialog = SimplerDialogBuilder.build("正在同步服务器数据……", activity, false);
                        activity.executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                getBackHeroData(sycDialog);
                            }
                        });
                    }
                }, activity);
                break;
        }
    }

    private void getBackHeroData(Dialog progress) {
        final String award = activity.service.queryAwardString(context);
        ServerData data = activity.service.getBackHero(context);
        if (data != null && StringUtils.isNotEmpty(award)) {
            context.getHero().setMaterial(context.getHero().getMaterial() + data.getMaterial());
            if (data.getAwardAccessories() != null) {
                for (Accessory accessory : data.getAccessories()) {
                    context.getDataManager().saveAccessory(context.covertAccessoryToLocal(accessory));
                }
            }
            if (data.getAwardPets() != null) {
                for (Pet pet : data.getPets()) {
                    context.getDataManager().savePet(pet);
                }
            }
            if (progress != null) {
                progress.dismiss();
            }
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    SimplerDialogBuilder.build("获得奖励<br>" + award, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.finish();
                        }
                    }, activity);
                }
            });

        } else {
            activity.finish();
        }
    }


}
