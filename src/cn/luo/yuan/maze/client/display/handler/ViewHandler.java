package cn.luo.yuan.maze.client.display.handler;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/9/7.
 */
public class ViewHandler {
    public static void setText(TextView view, String text){
        if(text==null){
            text = StringUtils.EMPTY_STRING;
        }
        if(!text.equals(view.getTag(R.string.item))){
            view.setTag(R.string.item, text);
            view.setText(Html.fromHtml(text));
        }
    }
    public static void setImage(ImageView view, Drawable img){
        if(img==null){
            view.setImageResource(R.drawable.wenhao);
            return;
        }
        view.setImageDrawable(img);
    }

}
