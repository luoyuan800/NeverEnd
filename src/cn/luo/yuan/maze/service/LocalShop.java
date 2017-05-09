package cn.luo.yuan.maze.service;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.goods.GoodsType;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 5/9/2017.
 */
public class LocalShop {
    private Context context;
    private Random random;
    public LocalShop(Context context, Random random){
        this.context = context;
        this.random = random;
    }
    public LocalShop(Context context){
        this.context = context;
        this.random = new Random(System.currentTimeMillis());
    }

    public List<Item> randomAccessory(){
        List<Item> acces = new ArrayList<>();
        try {
            XmlResourceParser parser = context.getAssets().openXmlResourceParser("accessory");
            //parser.next()
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acces;
    }

    public List<Item> randomGoods(){
        List<Item> goods = new ArrayList<>();
        for(GoodsType type : GoodsType.values()){
            if(type.getLocalSell() && random.nextBoolean()){
                Item goodsItem = new Item();
                goodsItem.name = type.getInstance().getName();
                goodsItem.price = type.getInstance().getPrice();
                goodsItem.desc = type.getInstance().getDesc();
                goodsItem.count = random.nextInt(Data.MAX_SELL_COUNT);
                goods.add(goodsItem);
            }
        }
        return goods;
    }

    public List<Item> getSellItems(){
        List<Item> items = randomGoods();
        items.addAll(randomAccessory());
        return items;
    }

    public static class Item{
        String name;
        int count;
        long price;
        String type;
        List<Effect> effects;
        String desc;
        String color;
        public String toString(){
            if(effects == null)return name + " * " + count + " : " + StringUtils.formatNumber(price) + "<br>" + desc;
            return "<font color='" + color + "'>" + name + "</font>(" + type + ")" + " * " + count + " : " + StringUtils.formatNumber(price) + "<br>" + desc;
        }
    }
}
