package cn.luo.yuan.maze.display.view;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.luo.yuan.maze.service.InfoControl;

import java.util.ArrayDeque;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class RollTextView extends LinearLayout{
    private ArrayDeque<TextView> caches = new ArrayDeque<>(50);
    private String info;
    private Runnable addText = new Runnable() {
        @Override
        public void run() {
            TextView tv;
            if (caches.size() < 50) {
                tv = new TextView(getContext());
                tv.setWidth(getWidth() - 5);
            } else {
                tv = caches.poll();
                removeView(tv);
            }

            tv.setText(Html.fromHtml(info));
            caches.add(tv);
            addView(tv);
        }
    };

    public RollTextView(Context context) {
        super(context);
    }


    public RollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public synchronized void addMessage(String info) {
        this.info = info;
        getHandler().post(addText);
    }
    private Handler handler;
    public Handler getHandler(){
        if(handler!=null){
            return handler;
        }
        if(super.getHandler()==null){
            handler = new Handler();
        }else{
            handler = super.getHandler();
        }
        return handler;
    }

}
