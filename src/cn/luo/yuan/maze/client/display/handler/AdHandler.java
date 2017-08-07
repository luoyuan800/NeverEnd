package cn.luo.yuan.maze.client.display.handler;

import android.content.Context;
import android.content.Intent;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;
import cn.luo.yuan.maze.client.utils.LogHelper;
import com.soulgame.sgsdk.tgsdklib.TGSDK;
import com.soulgame.sgsdk.tgsdklib.TGSDKServiceResultCallBack;
import com.soulgame.sgsdk.tgsdklib.ad.ITGADListener;
import com.soulgame.sgsdk.tgsdklib.ad.ITGPreloadListener;
import com.soulgame.sgsdk.tgsdklib.ad.ITGRewardVideoADListener;

import java.util.Map;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/20/2017.
 */
public class AdHandler implements ITGPreloadListener, ITGADListener, ITGRewardVideoADListener {
    private final static String yomobappId = "C948Vgq3sHbo3L12iUCm";
    private final static String youmiappId = "1b3d76d520cfe2eb";
    private final static String youmiappSecret = "338009391786dd1d";
    private final static String adcenseid = "c7ytLzE6IjZr5oo0KnJ";
    private int award = 1;
    private OnlineActivity context;
    private boolean yomob = false;
    private boolean debug = false;
    private boolean isCPA = false;

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
        debug("PreloadSuccess: %s", s);
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
    }

    @Override
    public void onCPADLoaded(String s) {
        isCPA = true;
    }

    @Override
    public void onVideoADLoaded(String s) {
        isCPA = false;
    }

    @Override
    public void onShowSuccess(String s) {
    }

    @Override
    public void onShowFailed(String s, String s1) {
        debug("%s: %s", s, s1);
    }

    @Override
    public void onADComplete(String s) {
        context.handler.showToast("获得一个礼包");
        context.handler.addOnlineGift(1);
    }

    @Override
    public void onADClick(String s) {

    }

    @Override
    public void onADClose(String s) {

    }

    @Override
    public void onADAwardSuccess(String s) {
        context.handler.showToast("获得一个礼包");
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
        }
    }

    public void onPause() {
        if (yomob) {
            TGSDK.onPause(context);
        }
    }

    public void onResume() {
        try {
            if (yomob)
                TGSDK.onResume(context);
        } catch (Exception e) {
            LogHelper.logException(e, "ADHandler->onResume");
        }
    }

    public void onDestroy() {
        TGSDK.onDestroy(context);
    }

    public void showAd() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (yomob && TGSDK.couldShowAd(adcenseid)) {
                        if (debug) {
                            TGSDK.showTestView(context, adcenseid);
                        } else {
                            TGSDK.showAd(context, adcenseid);
                        }
                    } else {
                        context.handler.showToast("网络连接不顺畅，请稍后...");
                        debug("Could not show!");
                    }
                }catch (Exception e){
                    LogHelper.logException(e, "Show ad");
                }
            }
        });

    }



    public void onActivityResult(int reqCode, int resCode, Intent data) {
        TGSDK.onActivityResult(context, reqCode, resCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        TGSDK.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
    }

    public static void onAppExit(Context context) {

    }

    public void debug(String s, Object... objs) {
        if (debug) {
            context.handler.showToast(s, objs);
        }
    }

    private void setUpYomobAd() {
        try {
            TGSDK.setDebugModel(debug);
            TGSDK.initialize(context, yomobappId, new TGSDKServiceResultCallBack() {

                @Override
                public void onFailure(Object arg0, String arg1) {
                    debug("Failure: %s , %s", arg0, arg1);
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
        } catch (Exception e) {
            LogHelper.logException(e, "Setup AD");
        }
    }

}
