package cn.luo.yuan.maze.display.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.PetAdapter;
import cn.luo.yuan.maze.display.dialog.ExchangeDialog;
import cn.luo.yuan.maze.display.dialog.PetDialog;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.service.LocalShop;

public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    private GameContext control;

    public MenuItemClickListener(Context context, GameContext control) {
        this.context = context;
        this.control = control;
    }

    //分享文本
    public void shareToNet() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_string));
        shareIntent.setType("text/*");
        Intent chooser = Intent.createChooser(shareIntent, "分享到");
        context.startActivity(chooser);
            /*if (System.currentTimeMillis() - heroN.getLastShare() > 24 * 60 * 60 * 1000) {
                //heroN.setLastShare(System.currentTimeMillis());
                //heroN.addMaterial(500000);
            } else {
                Toast.makeText(context, "每24小时只能获得一次分享奖励。", Toast.LENGTH_LONG).show();
            }*/
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.excahnge:
                new ExchangeDialog(control).show();
                break;
            case R.id.local_shop:
                new LocalShop(control).show();
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
                sharingTip.setMessage("如果你觉得这个游戏好玩，不妨帮忙分享到你的圈子中，让更多的人参与到我们的游戏建设中来，一起享受放置的快乐。");
                sharingTip.show();
                break;
            case R.id.save:
                control.save();
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
}