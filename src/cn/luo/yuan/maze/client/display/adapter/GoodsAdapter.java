package cn.luo.yuan.maze.client.display.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.model.goods.BatchUseGoods;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsProperties;
import cn.luo.yuan.maze.model.goods.UsableGoods;
import cn.luo.yuan.maze.model.skill.SkillParameter;

import java.util.List;

/**
 * Copyright 2015 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 12/6/15.
 */
public class GoodsAdapter extends BaseAdapter {
    private int iteViewId;
    private List<Goods> goods;
    private NeverEnd context;

    public GoodsAdapter(NeverEnd activity, List<Goods> goods) {
        this.goods = goods;
        this.context = activity;
    }

    @Override
    public int getCount() {
        return goods.size();
    }

    @Override
    public Goods getItem(int i) {
        return goods.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context.getContext(), R.layout.goods_item,null);
        }
        Button button = (Button) view.findViewById(R.id.good_buy_button);
        final Goods type = getItem(i);
        if (button != null) {
            button.setEnabled(type.getCount() > 0);
            if(type instanceof UsableGoods){
                button.setText("使用");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(context.getContext()).create();
                        dialog.setTitle(type.getName());

                        LinearLayout linearLayout = new LinearLayout(dialog.getContext());
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        final NumberPicker totalPicker = new NumberPicker(dialog.getContext());
                        if(type instanceof BatchUseGoods) {
                            totalPicker.setMinValue(1);
                                totalPicker.setMaxValue(type.getCount());
                            linearLayout.addView(totalPicker);
                        }
                        TextView desc = new TextView(dialog.getContext());
                        desc.setText(Html.fromHtml(type.getDesc()));
                        linearLayout.addView(desc);
                        dialog.setView(linearLayout);
                        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"确认", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int count = totalPicker.getValue();
                                GoodsProperties gp = new GoodsProperties(context.getHero());
                                gp.put(SkillParameter.CONTEXT, context);
                                gp.put(SkillParameter.COUNT, count);
                                type.use(gp);
                                notifyDataSetChanged();
                            }
                        });
                        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }else{
                if(type.getLock()){
                    button.setText("点击解锁");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            type.setLock(false);
                            notifyDataSetChanged();
                        }
                    });
                }else {
                    button.setText("点击锁定");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            type.setLock(true);
                            notifyDataSetChanged();
                        }
                    });
                }
            }

        }
        ((TextView) view.findViewById(R.id.good_buy_name)).setText(type.getName());
        String lockmsg = "物品处于锁定状态，锁定时不会自动使用.";
        String unlockmsg = "物品处于未锁定状态，游戏过程中会自动使用";
        ((TextView) view.findViewById(R.id.good_buy_desc)).setText(type.getDesc() + ((type instanceof UsableGoods) ? "" : type.getLock() ? lockmsg : unlockmsg));
        ((TextView) view.findViewById(R.id.good_by_cost)).setText("个数：" + type.getCount());
        return view;
    }
}
