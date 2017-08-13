package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.SellItemAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.AccessoryHelper;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ServerService;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.SellItem;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.utils.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/13/2017.
 */
public class ShopDialog {
    private NeverEnd context;
    private Random random;


    public ShopDialog(NeverEnd context) {
        this.context = context;
        this.random = context.getRandom();
    }

    public void showOnlineShop(Handler handler){
        final ServerService ss = new ServerService(context);
        final List<SellItem> sellItems = ss.getOnlineSellItems(context);
        handler.post(new Runnable() {
            @Override
            public void run() {
                show("在线商店", sellItems, new SellItemAdapter.AfterSell() {
                    @Override
                    public void sell(final String id, final int count) {
                        context.getExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                ss.buyOnlineItem(id, count);
                            }
                        });
                    }
                });
            }
        });

    }

    public void showLocalShop(){
        List<SellItem> items = randomAccessory();
        items.addAll(randomGoods());
        show("本地商店", items, null);
    }

    public void show(String title, List<SellItem> items, SellItemAdapter.AfterSell listener) {
        if(context.getHero().getMaterial() > Data.MATERIAL_LIMIT) {
            for (SellItem item : items) {
                item.price *= 2;
            }
        }
        AlertDialog shopDialog = new AlertDialog.Builder(context.getContext()).create();
        shopDialog.setTitle(title);
        shopDialog.setButton(DialogInterface.BUTTON_POSITIVE, "退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        if(items.size() > 0){
            LoadMoreListView list = new LoadMoreListView(context.getContext());
            list.setAdapter(new SellItemAdapter(context,items, listener));
            list.onLoadMoreComplete(true);
            shopDialog.setView(list);
        } else {
            TextView tv = new TextView(context.getContext());
            tv.setText(R.string.shop_tip);
            shopDialog.setView(tv);
        }
        shopDialog.show();
    }

    public List<SellItem> randomAccessory() {
        List<SellItem> acces = new ArrayList<>();
        AccessoryHelper accessoryHelper = AccessoryHelper.getOrCreate(context);
        for (Accessory accessory : accessoryHelper.getRandomAccessories(random.nextInt(5))) {
            SellItem item = new SellItem();
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

    public List<SellItem> randomGoods() {
        List<SellItem> goods = new ArrayList<>();
        for (Goods g : context.getDataManager().loadAllGoods(true)) {
            Goods ng = (Goods) g.clone();
            ng.setCount(1);
            if (g.canLocalSell() && random.nextBoolean()) {
                SellItem goodsItem = new SellItem();
                goodsItem.name = g.getName();
                goodsItem.price = g.getPrice();
                goodsItem.desc = g.getDesc();
                goodsItem.count = random.nextInt(Data.MAX_SELL_COUNT) + 1;
                goodsItem.instance = ng;
                goods.add(goodsItem);
            }
        }
        return goods;
    }

}
