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
                if(context.getRandom().nextBoolean()) {
                    showVideoAD();
                }else {
                    showSpotAD();
                }
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
                        Object o = activity.service.openOnlineGift(context);
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
                        Dialog sycDialog = SimplerDialogBuilder.build("正在同步服务器数据……", activity, false);
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

    private void showSpotAD() {
        // 展示插屏广告
        SpotManager.getInstance(activity).showSpot(activity, new SpotListener() {

            @Override
            public void onShowSuccess() {
                showToast("获得一个礼包，点击广告可以再获得一个礼包");
                addOnlineGift(1);
            }

            @Override
            public void onShowFailed(int errorCode) {
                Log.d("mazAD", "插屏展示失败");
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        showToast("网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        showToast("暂无插屏广告");
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        showToast("插屏资源还没准备好");
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        showToast("请勿频繁展示");
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        showToast("请设置插屏为可见状态");
                        break;
                    default:
                        showToast("请稍后再试%s", errorCode);
                        break;
                }
            }

            @Override
            public void onSpotClosed() {
                showToast("插屏被关闭");
            }

            @Override
            public void onSpotClicked(boolean isWebPage) {
                showToast("获得一个礼包");
                addOnlineGift(1);
            }
        });

    }

    private void showVideoAD() {
        // 设置视频广告
        final VideoAdSettings videoAdSettings = new VideoAdSettings();
        videoAdSettings.setInterruptTips("退出视频播放将无法获得奖励" + "\n确定要退出吗？");
// 展示视频广告
        VideoAdManager.getInstance(activity)
                .showVideoAd(activity, videoAdSettings, new VideoAdListener() {
                    @Override
                    public void onPlayStarted() {
                        Log.d("AD", "开始播放视频");
                    }

                    @Override
                    public void onPlayInterrupted() {
                        Log.i("AD", "播放视频被中断");
                    }

                    @Override
                    public void onPlayFailed(int errorCode) {
                        Log.i("AD", "视频播放失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                showToast("网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                showToast("视频暂无广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                showToast("视频资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                showToast("视频展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                showToast("视频控件处在不可见状态");
                                break;
                            default:
                                showToast("请稍后再试");
                                break;
                        }
                    }

                    @Override
                    public void onPlayCompleted() {
                        showToast("获得一个礼包");
                        addOnlineGift(2);
                    }
                });
    }

    private void addOnlineGift(int count) {
        activity.executor.execute(new Runnable() {
            @Override
            public void run() {
                activity.service.addOnlineGift(context, count);
                activity.postGiftCount();
            }
        });
    }

    private void getBackHeroData(Dialog progress) {
        String award = activity.service.queryAwardString(context);
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

    /**
     * 展示Toast
     *
     * @param format
     * @param args
     */
    private void showToast(String format, Object... args) {
        Toast.makeText(activity, String.format(format, args), Toast.LENGTH_SHORT).show();
    }
}
