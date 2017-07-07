package cn.luo.yuan.maze.client.display.dialog;

import android.widget.ListView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.GoodsAdapter;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.goods.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/7/6.
 */
public class GoodsDialog {
    public void show(NeverEnd context){
        ListView list = new ListView(context.getContext());
        List<Goods> goods = context.getDataManager().loadAllGoods();
        List<Goods> rGoodes = new ArrayList<>(goods.size());
        for(Goods g : goods){
            if(g.getCount() > 0){
                rGoodes.add(g);
            }
        }
        list.setAdapter(new GoodsAdapter(context, rGoodes));
        SimplerDialogBuilder.build(list, Resource.getString(R.string.close), null,context.getContext());
    }
}
