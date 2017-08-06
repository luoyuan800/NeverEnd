package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import cn.luo.yuan.maze.utils.Random;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class SimplerDialogBuilder {
    public static Dialog build(String msg, String posivStr, DialogInterface.OnClickListener posiv, Context context, Random random) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withMessageColor(Color.RED)
                .withMessage(Html.fromHtml(msg))                     //.withMessage(null)  no Msg
                .withButton1Text(posivStr)                                      //def gone
                .withDuration(700)
                .withDialogColor(Color.WHITE)
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        posiv.onClick(dialogBuilder, DialogInterface.BUTTON_POSITIVE);
                        dialogBuilder.dismiss();
                    }
                })
                .show();
        return dialogBuilder;
    }

    public static AlertDialog build(String msg, String positiveStr, DialogInterface.OnClickListener positive,
                                    String negativeStr, DialogInterface.OnClickListener negative, Context context) {
        return new AlertDialog.Builder(context).setMessage(Html.fromHtml(msg))
                .setPositiveButton(positiveStr, positive).setNegativeButton(negativeStr, negative).show();
    }

    public static AlertDialog build(View view, String positiveStr, DialogInterface.OnClickListener positive,
                                    String negativeStr, DialogInterface.OnClickListener negative, Context context) {
        return new AlertDialog.Builder(context).setView(view)
                .setPositiveButton(positiveStr, positive).setNegativeButton(negativeStr, negative).show();
    }

    public static AlertDialog build(String msg, Context context, boolean lazy) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(Html.fromHtml(msg));
        if (lazy) {
            return builder.create();
        } else {
            return builder.show();
        }
    }

    public static Dialog build(View view, String posivStr, DialogInterface.OnClickListener listener, Context context) {
        if (listener == null) {
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder
                    .withDialogColor(Color.WHITE)
                    .setCustomView(view, context)                   //.withMessage(null)  no Msg
                    .withButton1Text(posivStr)                                      //def gone
                    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .show();
            return dialogBuilder;
        }
        return new AlertDialog.Builder(context).setView(view).setPositiveButton(posivStr, listener).show();
    }

    public static Dialog build(View view, String title, Context context, Random random) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withDialogColor(Color.WHITE)
                .withTitle(title)
                .setCustomView(view, context)                   //.withMessage(null)  no Msg
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .show();
        return dialogBuilder;

    }
}
