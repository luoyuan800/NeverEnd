package cn.luo.yuan.maze.client.display.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.luo.yuan.maze.client.service.NeverEnd;
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
    private NeverEnd context;
    private boolean yomob = false;
    private boolean debug = false;
    private boolean isCPA = false;
    private boolean isAward = false;

    public AdHandler(NeverEnd a) {
        this.context = a;
    }

    public static void onAppExit(Context context) {

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
            Object baseAward = TGSDK.parameterFromAdScene(adcenseid, "base_award");
            if(baseAward!=null) {
                award = Integer.parseInt(baseAward.toString());
            }
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
        isAward = true;
    }

    @Override
    public void onADClick(String s) {

    }

    @Override
    public void onADClose(String s) {
        if (isAward) {
            context.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    award();
                }
            });
        }
    }

    @Override
    public void onADAwardSuccess(String s) {
        isAward = true;
    }

    @Override
    public void onADAwardFailed(String s, String s1) {
        debug("ADAwardFailed: %s, %s", s, s1);
    }

    public void onStart() {
        if (yomob) {
            TGSDK.onStart((Activity) context.getContext());
        }
    }

    public void onStop() {
        if (yomob) {
            TGSDK.onStop((Activity) context.getContext());
        }
    }

    public void onPause() {
        if (yomob) {
            TGSDK.onPause((Activity) context.getContext());
        }
    }

    public void onResume() {
        try {
            if (yomob)
                TGSDK.onResume((Activity) context.getContext());
        } catch (Exception e) {
            LogHelper.logException(e, "ADHandler->onResume");
        }
    }

    public void onDestroy() {
        TGSDK.onDestroy((Activity) context.getContext());
    }

    public void showAd() {
        isAward = false;
        ((Activity) context.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TGSDK.couldShowAd(adcenseid)) {
                        if (debug) {
                            TGSDK.showTestView((Activity) context.getContext(), adcenseid);
                        } else {
                            TGSDK.showAd((Activity) context.getContext(), adcenseid);
                        }
                    } else {
                        context.showToast("网络连接不顺畅，请稍后...");
                        debug("Could not show!");
                    }
                } catch (Exception e) {
                    LogHelper.logException(e, "Show ad");
                }
            }
        });

    }


    public void onActivityResult(int reqCode, int resCode, Intent data) {
        TGSDK.onActivityResult((Activity) context.getContext(), reqCode, resCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        TGSDK.onRequestPermissionsResult((Activity) context.getContext(), requestCode, permissions, grantResults);
    }

    public void debug(String s, Object... objs) {
        if (debug) {
            context.showToast(s, objs);
        }
    }

    private void award() {
        context.getServerService().addDebris(context, 1, true);
        if (context.getServerService().addOnlineGift(context, 1, true)) {
            context.showToast("获得一个礼包和一个碎片");
        }
        isAward = false;
    }

    private void setUpYomobAd() {
        try {
            TGSDK.setDebugModel(debug);
            TGSDK.initialize((Activity) context.getContext(), yomobappId, new TGSDKServiceResultCallBack() {

                @Override
                public void onFailure(Object arg0, String arg1) {
                    debug("Failure: %s , %s", arg0, arg1);
                }

                @Override
                public void onSuccess(Object arg0, Map<String, String> arg1) {
                    yomob = true;
                    debug("Init AD success");

                    TGSDK.preloadAd((Activity) context.getContext(), AdHandler.this);
                }
            });
            TGSDK.setADListener(this);
            TGSDK.setRewardVideoADListener(this);
        } catch (Exception e) {
            LogHelper.logException(e, "Setup AD");
        }
    }

}
