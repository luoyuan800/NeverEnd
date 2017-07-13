package cn.luo.yuan.maze.client.display.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.SellItem;
import cn.luo.yuan.maze.model.goods.Goods;

import java.util.List;

/**
 * Copyright 2015 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 12/6/15.
 */
public class SellItemAdapter extends BaseAdapter {
    public static interface AfterSell{
        void sell(String id, int count);
    }
    private int iteViewId;
    private List<SellItem> items;
    private NeverEnd context;
    private AfterSell sellListener;

    public SellItemAdapter(NeverEnd context, List<SellItem> items, AfterSell listener) {
        this.items = items;
        this.context = context;
        this.sellListener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SellItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context.getContext(), R.layout.shop_item, (ViewGroup) null);
        }
        Button button = (Button) view.findViewById(R.id.buy_button);
        final SellItem item = getItem(i);
        if (button != null) {
            button.setEnabled(item.count > 0 && context.getHero().getMaterial() > item.price);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.getHero().setMaterial(context.getHero().getMaterial() - item.price);
                    item.count--;
                    if (item.instance instanceof Accessory) {
                        context.getDataManager().saveAccessory((Accessory) item.instance);
                    } else if (item.instance instanceof Goods) {
                        Goods goods = (Goods) item.instance;
                        goods.setCount(goods.getCount() + 1);
                    }
                    notifyDataSetChanged();
                    Toast.makeText(context.getContext(), "成功购买" + item.name, Toast.LENGTH_SHORT).show();
                    if(sellListener!=null){
                        sellListener.sell(item.id,1);
                    }
                }
            });
        }

        ((TextView) view.findViewById(R.id.item_name)).setText(Html.fromHtml(item.toString()));
        return view;
    }
}
