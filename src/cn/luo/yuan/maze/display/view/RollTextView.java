package cn.luo.yuan.maze.display.view;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class RollTextView extends ScrollView {
    private final Handler handler = new Handler();
    private LinearLayout layout;
    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    private boolean scroll = true;
    private Runnable addText = new Runnable() {
        @Override
        public void run() {
            if(messages.isEmpty()){
                return;
            }
            String info = messages.poll();
            if(StringUtils.isNotEmpty(info)) {
                TextView tv;
                if (layout.getChildCount() < 50) {
                    tv = new TextView(getContext());
                    tv.setWidth(getWidth() - 5);
                } else {
                    tv = (TextView) layout.getChildAt(0);
                    layout.removeView(tv);
                }

                tv.setText(Html.fromHtml(info));
                layout.addView(tv);
                scrollToButton();
            }
        }
    };

    public RollTextView(Context context) {
        super(context);
        init(context);
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
        this.messages.add(info);
        getHandler().post(addText);
    }

    public Handler getHandler() {

        if (super.getHandler() != null) {
            return super.getHandler();
        } else {
            return handler;
        }
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

    private void scrollToButton() {
        if (scroll) {
            int off = layout.getMeasuredHeight() - getHeight();
            if (off > 0) {
                scrollTo(0, off);
            }
        }
    }

    private void init(Context context) {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        addView(layout);
    }
}
