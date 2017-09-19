package cn.luo.yuan.maze.client.display.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import cn.luo.yuan.maze.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by luoyuan on 2017/9/16.
 */
public class MessageDialog extends Dialog {
    private List<String> msg = Collections.emptyList();
    private int index;
    private TextView showing;
    private boolean button;

    public MessageDialog(Context context, List<String> msg){
        this(context, R.style.popupDialog);
        setContentView(R.layout.message_dialog);
        setCancelable(false);
        this.msg = msg;
        button = true;
    }

    public void show(){
        super.show();
        if(button) {
            Window win = getWindow();
            win.setGravity(Gravity.BOTTOM);
        }
        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        showing = (TextView) findViewById(R.id.tell_message);
        showing.setText(Html.fromHtml(msg.get(0)));
        findViewById(R.id.next_msg_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < msg.size() - 1){
                    index ++;
                    showing.setText(Html.fromHtml(msg.get(index)));
                }
                if(index >= msg.size() - 1){
                    findViewById(R.id.next_msg_button).setEnabled(false);
                }
            }
        });
    }
    public MessageDialog(Context context) {
        super(context);
    }

    public MessageDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MessageDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
