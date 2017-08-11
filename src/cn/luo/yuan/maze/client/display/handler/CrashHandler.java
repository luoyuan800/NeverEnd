package cn.luo.yuan.maze.client.display.handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                SharedPreferences sp = context.getContext().getSharedPreferences("mark", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("exception", true);
                editor.apply();

                SimplerDialogBuilder.build("发生了未知的错误，请重启游戏！", Resource.getString(R.string.conform),
                        (DialogInterface.OnClickListener) null,context.getContext(), null);
                Looper.loop();
            }
        }.start();
    }
}
