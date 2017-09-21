package cn.luo.yuan.maze.client.display.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/21/2017.
 */
public class ImageActivity extends Activity {
    private List<Drawable> drawables;
    private int index = 0;
    public void setDrawables(List<Drawable> drawables ) {
        this.drawables = drawables;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images_list);
        drawables = new ArrayList<>();
        drawables.add(Resource.loadImageFromAssets("help/base_properties.png", true));
        drawables.add(Resource.loadImageFromAssets("help/pet_view.png", true));
        drawables.add(Resource.loadImageFromAssets("help/skill.jpg", true));
        drawables.add(Resource.loadImageFromAssets("help/button.jpg", true));
        show();
    }

    public void show(){
        if(drawables!=null && drawables.size() > 0){
            ((ImageView)findViewById(R.id.image_view)).setImageDrawable(drawables.get(index++));
        }
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < drawables.size()) {
                    ((ImageView) findViewById(R.id.image_view)).setImageDrawable(drawables.get(index++));
                }else{
                    findViewById(R.id.next_button).setVisibility(View.INVISIBLE);
                }
                findViewById(R.id.prior_button).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.prior_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0 && drawables.size() > 0) {
                    ((ImageView) findViewById(R.id.image_view)).setImageDrawable(drawables.get(--index));
                }else{
                    findViewById(R.id.prior_button).setVisibility(View.INVISIBLE);
                }
                findViewById(R.id.next_button).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawables.clear();
                finish();
                ((NeverEnd)getApplicationContext()).getViewHandler().showStartTip();
            }
        });
    }
}
