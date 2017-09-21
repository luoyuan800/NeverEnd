package cn.luo.yuan.maze.client.display.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import cn.luo.yuan.maze.R;

import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/21/2017.
 */
public class ImageDialog extends Dialog {
    private List<Drawable> drawables;
    private int index = 0;
    public ImageDialog(Context context, List<Drawable> drawables ) {
        super(context, R.style.full_screen_dialog);
        setContentView(R.layout.images_list);
        this.drawables = drawables;
    }

    public void show(){
        super.show();
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
            }
        });
        findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawables.clear();
                dismiss();
            }
        });
    }
}
