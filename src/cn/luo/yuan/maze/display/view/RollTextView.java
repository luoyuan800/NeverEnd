package cn.luo.yuan.maze.display.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class RollTextView extends SurfaceView implements SurfaceHolder.Callback {
    private ArrayDeque<String> infos = new ArrayDeque<>();
    private List<TextView> caches = new ArrayList<>();
    public void add(String info){
        while (infos.size() >= 50){
            infos.poll();
        }
        infos.add(info);
    }

    public void draw(){
        Canvas canvas = getHolder().lockCanvas(null);
        canvas.drawColor(Color.argb(0, 0, 0, 0), android.graphics.PorterDuff.Mode.CLEAR);
        int x = 1,y = 1;
        for(String info : infos){
            Html.fromHtml(info);

        }

    }

    public RollTextView(Context context) {
        super(context);
    }

    public RollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void init() {
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
