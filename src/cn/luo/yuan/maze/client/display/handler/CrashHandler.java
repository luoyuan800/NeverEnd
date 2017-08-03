package cn.luo.yuan.maze.client.display.handler;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Looper;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.FileUtils;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/28/2017.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultHandler;
    private NeverEnd context;

    public CrashHandler(NeverEnd context) {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.context = context;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        LogHelper.logException(ex, "UnCatchException");
        for(StackTraceElement element : ex.getStackTrace()){
            if(element.getClassName().contains("OnlineActivity") || element.getClassName().contains("TGSDK") ||
                    element.getClassName().contains("tgsdk") || element.getClassName().contains("Vungle") ){
                return;
            }
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                SimplerDialogBuilder.build("发生了未知的错误，是否上传数据供开发者分析（存档、错误日志、手机系统信息）？", Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final ProgressDialog progress = new ProgressDialog(context.getContext());
                        progress.setMessage("上传中……");
                        progress.show();
                        context.getExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String filePath = FileUtils.zipSaveFiles(context.getHero().getId() + "," + android.os.Build.MODEL + ","
                                            + Build.VERSION.SDK_INT + ","
                                            + android.os.Build.VERSION.RELEASE, context.getContext(), true);
                                    context.getServerService().uploadSaveFile(filePath);
                                } catch (Exception e) {
                                    defaultHandler.uncaughtException(thread, ex);
                                }
                            }
                        });
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        context.save(false);
                        context.stopGame();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.stopGame();
                        defaultHandler.uncaughtException(thread, ex);
                    }
                }, context.getContext());
                Looper.loop();
            }
        }.start();
    }
}
