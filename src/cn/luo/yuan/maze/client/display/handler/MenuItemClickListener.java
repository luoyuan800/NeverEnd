package cn.luo.yuan.maze.client.display.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.dialog.*;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.service.InfoControlInterface;

public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    private NeverEnd control;

    public MenuItemClickListener(Context context, NeverEnd control) {
        this.context = context;
        this.control = control;
    }

    //分享文本
    public void shareToNet() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_string));
        shareIntent.setType("text/*");
        Intent chooser = Intent.createChooser(shareIntent, "分享到");
        context.startActivity(chooser);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cdkey:
                EditText editText = new EditText(context);
                editText.setHint("输入兑换码");
                SimplerDialogBuilder.build(editText, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, context);
                break;
            case R.id.warehouse:
                new WarehouseDialog(control).show();
                break;
            case R.id.accessory:
                new AccessoriesDialog(control, new AccessoriesDialog.OnAccessoryChangeListener() {
                    @Override
                    public void change(InfoControlInterface context) {
                        control.getViewHandler().refreshFreqProperties();
                    }
                }).show();
                break;
            case R.id.crash:
                throw new RuntimeException("test exception");
            case R.id.skills:
                new SkillDialog(control).show();
                break;
            case R.id.pay:
                showPayDialog();
                break;
            case R.id.reincarnation:
                SimplerDialogBuilder.build("转生，增加难度，重新开始，保留宠物和装备。",
                        Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long orein = control.getHero().getReincarnate();
                                if(control.reincarnate() > orein){
                                    control.getViewHandler().showGiftChoose();
                                }
                            }
                        }, Resource.getString(R.string.close), null, context);
                break;
            case R.id.goods:
                new GoodsDialog().show(control);
                break;
            case R.id.theme:
                NeverEndConfig config = control.getDataManager().getConfig();
                if(config.getTheme() == android.R.style.Theme_Holo_NoActionBar_Fullscreen){
                   config.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                }else{
                    config.setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                }
                control.getDataManager().save(config);
                control.getViewHandler().reCreate();
                break;
            case R.id.online_battle:
                Intent gameIntent = new Intent(context, OnlineActivity.class);
                context.startActivity(gameIntent);
                break;
            case R.id.excahnge:
                new ExchangeDialog(control).show();
                break;
            case R.id.local_shop:
                new ShopDialog(control).showLocalShop();
                break;
            case R.id.pets:
                new PetDialog(control, new PetAdapter(context, control.getDataManager(), "")).show();
                break;
            case R.id.pause:
                boolean pause = control.pauseGame();
                item.setTitle(pause ? "继续" : "暂停");
                break;
            case R.id.share:
                final AlertDialog sharingTip = new AlertDialog.Builder(context).create();
                sharingTip.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharingTip.dismiss();
                        shareToNet();
                    }
                });
                sharingTip.setMessage("如果你觉得这个游戏好玩，不妨帮忙分享到你的圈子中，让更多的人参与到我们的游戏建设中来，一起享受放置的快乐。欢迎关注作者的的微信订阅号：某鸟碎碎。 分享后发送截图到公众号，可以获得兑换码奖励。");
                sharingTip.show();
                break;
            case R.id.save:
                control.save(true);
                break;
            default:
                AlertDialog dialog = new AlertDialog.Builder(context).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.working);
                dialog.setView(imageView);
                dialog.show();
        }
        return false;
    }

    private void showPayDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("点赞？");
        ScrollView linearLayout = new ScrollView(context);
        TextView tv = new TextView(context);
        tv.setText("您的肯定是对我最好的鼓励！请关注作者的公众号<某鸟碎碎>，偶尔会发送兑换码哦！分享游戏到你的朋友圈、基友圈、游戏群、论坛，然后截图发送到公众号，可以获得兑换码。");
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        linearLayout.addView(tv);
        dialog.setView(linearLayout);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                shareToNet();
            }

        });
        dialog.show();
    }
}