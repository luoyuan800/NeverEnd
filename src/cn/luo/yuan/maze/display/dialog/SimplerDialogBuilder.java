package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class SimplerDialogBuilder {
    public static AlertDialog build(String msg, String posivStr, DialogInterface.OnClickListener posiv, Context context){
        return new AlertDialog.Builder(context).setMessage(Html.fromHtml(msg)).setPositiveButton(posivStr, posiv).show();
    }

    public static AlertDialog build(String msg, Context context) {
        return new AlertDialog.Builder(context).setMessage(Html.fromHtml(msg)).show();
    }

    public static AlertDialog build(View view, String posivStr, DialogInterface.OnClickListener listener, Context context) {
        return new AlertDialog.Builder(context).setView(view).setPositiveButton(posivStr, listener).show();
    }
}
