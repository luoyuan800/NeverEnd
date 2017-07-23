package cn.luo.yuan.maze.client.display.handler;

import android.content.Intent;
import android.util.Log;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;
import cn.luo.yuan.maze.client.utils.LogHelper;
import com.soulgame.sgsdk.tgsdklib.TGSDK;
import com.soulgame.sgsdk.tgsdklib.TGSDKServiceResultCallBack;
import com.soulgame.sgsdk.tgsdklib.ad.ITGADListener;
import com.soulgame.sgsdk.tgsdklib.ad.ITGPreloadListener;
import com.soulgame.sgsdk.tgsdklib.ad.ITGRewardVideoADListener;
import sw.ls.ps.AdManager;
import sw.ls.ps.normal.common.ErrorCode;
import sw.ls.ps.normal.spot.SpotListener;
import sw.ls.ps.normal.spot.SpotManager;
import sw.ls.ps.normal.video.VideoAdManager;

import java.util.Map;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/20/2017.
 */
public class AdHandler implements ITGPreloadListener, ITGADListener, ITGRewardVideoADListener, SpotListener {
    private final static String yomobappId = "C948Vgq3sHbo3L12iUCm";
    private final static String youmiappId = "1b3d76d520cfe2eb";
    private final static String youmiappSecret = "338009391786dd1d";
    private final static String adcenseid = "c7ytLzE6IjZr5oo0KnJ";
    private int award = 1;
    private OnlineActivity context;
    private boolean yomob = false;
    private boolean debug = false;

    public AdHandler(OnlineActivity a) {
        this.context = a;
    }

    public void setupAD() {
        setUpYomobAd();
    }

    public boolean isYomob() {
        return yomob;
    }

    @Override
    public void onPreloadSuccess(String s) {
        debug("PreloadSuccess: %s",s);
        yomob = true;
        try {
            award = Integer.parseInt(TGSDK.parameterFromAdScene(adcenseid, "base_award").toString());
        } catch (Exception e) {
            LogHelper.logException(e, "Set Award count!");
        }
    }

    @Override
    public void onPreloadFailed(String s, String s1) {
        debug("%s: %s", s, s1);
        setUpYouMiAd();
    }

    @Override
    public void onCPADLoaded(String s) {

    }

    @Override
    public void onVideoADLoaded(String s) {

    }

    @Override
    public void onShowSuccess(String s) {
        debug("ShowSuccess: %s",s);
    }

    @Override
    public void onShowFailed(String s, String s1) {
        debug("%s: %s", s, s1);
    }

    @Override
    public void onADComplete(String s) {
        debug("ADComplete: %s", s);
    }

    @Override
    public void onADClick(String s) {
        context.handler.showToast("获得一个礼包");
        context.handler.addOnlineGift(1);
    }

    @Override
    public void onADClose(String s) {

    }

    @Override
    public void onADAwardSuccess(String s) {
        context.handler.showToast("获得一个礼包，点击广告可以再获得一个礼包");
        context.handler.addOnlineGift(1);
    }

    @Override
    public void onADAwardFailed(String s, String s1) {
        debug("ADAwardFailed: %s, %s", s, s1);
    }

    public void onStart() {
        if (yomob) {
            TGSDK.onStart(context);
        }
    }

    public void onStop() {
        if (yomob) {
            TGSDK.onStop(context);
        } else {
            SpotManager.getInstance(context).onStop();
        }
    }

    public void onPause() {
        if (yomob) {
            TGSDK.onPause(context);
        } else {
            SpotManager.getInstance(context).onPause();
        }
    }

    public void onResume() {
        if (yomob)
            TGSDK.onResume(context);
    }

    public void onDestroy() {
        TGSDK.onDestroy(context);
        SpotManager.getInstance(context).onDestroy();
    }

    public void showAd() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (yomob && TGSDK.couldShowAd(adcenseid)) {
                    TGSDK.showTestView(context, adcenseid);
                } else {
                    if(SpotManager.getInstance(context).checkSpotAdConfig()) {
                        SpotManager.getInstance(context).showSpot(context, AdHandler.this);
                    }else{
                        setUpYouMiAd();
                    }
                    debug("Could not show!");
                }
            }
        });

    }

    @Override
    public void onShowSuccess() {
        context.handler.showToast("获得" + award + "个礼包，点击广告可以再获得" + award + "个礼包");
        context.handler.addOnlineGift(award);
    }

    @Override
    public void onShowFailed(int errorCode) {
        Log.d("mazAD", "插屏展示失败");
        switch (errorCode) {
            case ErrorCode.NON_NETWORK:
                debug("网络异常");
                break;
            case ErrorCode.NON_AD:
                debug("暂无插屏广告");
                break;
            case ErrorCode.RESOURCE_NOT_READY:
                debug("插屏资源还没准备好");
                break;
            case ErrorCode.SHOW_INTERVAL_LIMITED:
                context.handler.showToast("不能频繁观看广告");
                break;
            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                debug("请设置插屏为可见状态");
                break;
            default:
                debug("请稍后再试%s", errorCode);
                break;
        }
    }

    @Override
    public void onSpotClosed() {
    }

    @Override
    public void onSpotClicked(boolean isWebPage) {
        context.handler.showToast("获得" + award + "个礼包");
        context.handler.addOnlineGift(award);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        TGSDK.onActivityResult(context, reqCode, resCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        TGSDK.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
    }

    private void setUpYomobAd() {
        try {
        TGSDK.setDebugModel(true);
            TGSDK.initialize(context, yomobappId, new TGSDKServiceResultCallBack() {

                @Override
                public void onFailure(Object arg0, String arg1) {
                    debug("Failure: %s , %s", arg0, arg1);
                    setUpYouMiAd();
                }

                @Override
                public void onSuccess(Object arg0, Map<String, String> arg1) {
                    yomob = true;
                    debug("Init AD success");

                    TGSDK.preloadAd(context, AdHandler.this);
                }
            });
            TGSDK.setADListener(this);
            TGSDK.setRewardVideoADListener(this);
        }catch (Exception e){
            LogHelper.logException(e, "Setup AD");
        }
    }

    private void setUpYouMiAd() {
        try {
            AdManager.getInstance(context).init(youmiappId, youmiappSecret, false);
// 只需要调用一次，由于在主页窗口中已经调用了一次，所以此处无需调用
            VideoAdManager.getInstance(context).requestVideoAd(context);
            /**
             * 设置插屏广告
             */
            // 设置插屏图片类型，默认竖图
            //		// 横图
            //		SpotManager.getInstance(mContext).setImageType(SpotManager
            // .IMAGE_TYPE_HORIZONTAL);
            // 竖图
            SpotManager.getInstance(context).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);

            // 设置动画类型，默认高级动画
            //		// 无动画
            //		SpotManager.getInstance(mContext).setAnimationType(SpotManager
            // .ANIMATION_TYPE_NONE);
            //		// 简单动画
            //		SpotManager.getInstance(mContext).setAnimationType(SpotManager
            // .ANIMATION_TYPE_SIMPLE);
            // 高级动画
            SpotManager.getInstance(context)
                    .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
            yomob = false;
        } catch (Exception e) {
            LogHelper.logException(e, "setupAD");
        }
    }

    public void debug(String s, Object...objs){
        if(debug){
            context.handler.showToast(s, objs);
        }
    }
}
