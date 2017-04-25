package cn.luo.yuan.maze.display.view;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.luo.yuan.maze.service.InfoControl;

import java.util.ArrayDeque;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class RollTextView extends ScrollView{
    private ArrayDeque<RevealTextView> caches = new ArrayDeque<>(50);
    private LinearLayout layout;
    private String info;
    private boolean scroll = true;
    private Runnable addText = new Runnable() {
        @Override
        public void run() {
            RevealTextView tv;
            if (caches.size() < 50) {
                tv = new RevealTextView(getContext());
                tv.setWidth(getWidth() - 5);
            } else {
                tv = caches.poll();
                removeView(tv);
            }

            tv.setAnimatedText(Html.fromHtml(info));
            caches.add(tv);
            layout.addView(tv);
            scrollToButton();
        }
    };

    private void scrollToButton(){
        if(scroll) {
            int off = layout.getMeasuredHeight() - getHeight();
            if (off > 0) {
                scrollTo(0, off);
            }
        }
    }

    public RollTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        addView(layout);
    }


    public RollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
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

    public boolean onTouchEvent(MotionEvent event) {
        int Action = event.getAction();
        switch (Action) {
            case MotionEvent.ACTION_DOWN:
                scroll = false;
                break;
            case MotionEvent.ACTION_MOVE:
                scroll = false;
                break;
            case MotionEvent.ACTION_UP:
                scroll = true;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
