package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.gift.Gift;

/**
 * Copyright 2016 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 1/1/16.
 */
public class GiftDialog implements View.OnClickListener {
    private AlertDialog mainDialog;
    private Context context;
    private Hero hero;

    public GiftDialog(final Context context, final Hero hero, DialogInterface.OnDismissListener dismissListener) {
        mainDialog = new AlertDialog.Builder(context).create();
        View giftView = View.inflate(context, R.layout.gift_list, null);
        mainDialog.setView(giftView);
        mainDialog.setTitle("选择一个天赋");
        mainDialog.setCancelable(false);
        mainDialog.setCanceledOnTouchOutside(false);
        this.context = context;
        this.hero = hero;
        mainDialog.setOnDismissListener(dismissListener);
    }

    public void show() {
        mainDialog.show();

        Button giftButton = (Button) mainDialog.findViewById(R.id.hero_heart);
        giftButton.setTag(Gift.HeroHeart);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.DarkHeard);
        giftButton.setTag(Gift.DarkHeard);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.Warrior);
        giftButton.setTag(Gift.Warrior);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.Searcher);
        giftButton.setTag(Gift.Searcher);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.Long);
        giftButton.setTag(Gift.Long);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.Element);
        giftButton.setTag(Gift.Element);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.Pokemon);
        giftButton.setTag(Gift.Pokemon);
        giftButton.setOnClickListener(this);

        giftButton = (Button) mainDialog.findViewById(R.id.FireBody);

        giftButton = (Button) mainDialog.findViewById(R.id.SkillMaster);

        giftButton = (Button) mainDialog.findViewById(R.id.RandomMaster);

        giftButton = (Button) mainDialog.findViewById(R.id.ElementReject);

        giftButton = (Button) mainDialog.findViewById(R.id.Maker);

        giftButton = (Button) mainDialog.findViewById(R.id.Epicure);

        giftButton = (Button) mainDialog.findViewById(R.id.Daddy);

        giftButton = (Button) mainDialog.findViewById(R.id.ChildrenKing);

    }

    @Override
    public void onClick(View view) {
        if(view.getTag() instanceof Gift){
            final Gift gift = (Gift) view.getTag();
            AlertDialog detailDialog = new AlertDialog.Builder(context).create();
            detailDialog.setTitle(gift.getName());
            detailDialog.setMessage(gift.getDesc() + (gift.getRecount() > 0 ? ("(" + gift.getRecount() + "转后可以选择)") : ""));
            detailDialog.setButton(DialogInterface.BUTTON_POSITIVE, "选择", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hero.setGift(gift);
                    mainDialog.dismiss();
                }
            });
            detailDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            detailDialog.show();
            Button activeButton = detailDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (activeButton != null) {
                activeButton.setEnabled(gift.getRecount() <= hero.getReincarnate());
            }
        }
    }

}
