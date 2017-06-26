package cn.luo.yuan.maze.client.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.ItemAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 5/9/2017.
 */
public class LocalShop {
    private GameContext context;
    private Random random;

    public LocalShop(GameContext context) {
        this.context = context;
        this.random = context.getRandom();
    }

    public void show() {
        AlertDialog shopDialog = new AlertDialog.Builder(context.getContext()).create();
        shopDialog.setTitle("本地商店");
        shopDialog.setButton(DialogInterface.BUTTON_POSITIVE, "退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        List<Item> items = randomAccessory();
        if (items.size() > 0) {
            LoadMoreListView list = new LoadMoreListView(context.getContext());
            ItemAdapter adapter = new ItemAdapter(context, items);
            list.setAdapter(adapter);
            list.onLoadMoreComplete(true);
            shopDialog.setView(list);
        } else {
            TextView tv = new TextView(context.getContext());
            tv.setText(R.string.shop_tip);
            shopDialog.setView(tv);
        }
        shopDialog.show();
    }

    public List<Item> randomAccessory() {
        List<Item> acces = new ArrayList<>();
        AccessoryHelper accessoryHelper = AccessoryHelper.getOrCreate(context);
        for (Accessory accessory : accessoryHelper.getRandomAccessories(random.nextInt(5))) {
            Item item = new Item();
            item.name = accessory.getName();
            item.color = accessory.getColor();
            item.count = 1;
            item.desc = accessory.getDesc();
            item.author = accessory.getAuthor();
            item.effects = accessory.getEffects();
            item.price = accessory.getPrice();
            item.instance = accessory;
            item.type = accessory.getType();
            acces.add(item);
        }
        return acces;
    }

    public List<Item> randomGoods() {
        List<Item> goods = new ArrayList<>();
        for (GoodsType type : GoodsType.values()) {
            if (type.getLocalSell() && random.nextBoolean()) {
                Item goodsItem = new Item();
                goodsItem.name = type.getInstance().getName();
                goodsItem.price = type.getInstance().getPrice();
                goodsItem.desc = type.getInstance().getDesc();
                goodsItem.count = random.nextInt(Data.MAX_SELL_COUNT);
                goodsItem.instance = type;
                goods.add(goodsItem);
            }
        }
        return goods;
    }

    public List<Item> getSellItems() {
        List<Item> items = randomGoods();
        items.addAll(randomAccessory());
        return items;
    }

    public static class Item {
        public String author;
        public String name;
        public int count;
        public long price;
        public Object instance;
        public boolean special;
        String type;
        List<Effect> effects;
        String desc;
        String color;

        public String toString() {
            if (effects == null)
                return name + " * " + count + (special ? " 特价" : " 价格") + " : " + StringUtils.formatNumber(price) + "<br>" + desc;
            return "<font color='" + color + "'>" + name + "</font>(" + type + ")" + " * "
                    + count + (special ? " 特价" : " 价格") + " : " + StringUtils.formatNumber(price)
                    + (StringUtils.isNotEmpty(author) ? "<br>" + author : "")
                    + (StringUtils.isNotEmpty(desc) ? "<br>" + desc : "")
                    + "<br>" + StringUtils.formatEffectsAsHtml(effects);
        }
    }
}
