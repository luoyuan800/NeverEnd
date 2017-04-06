package cn.luo.yuan.maze.display.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.view.RollTextView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.utils.StringUtils;

import java.lang.ref.WeakReference;

/**
 * Created by luoyuan on 2017/3/29.
 */
public class GameActivity extends Activity {
    DataManager dataManager;
    InfoControl control;
    private PopupMenu popupMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        Intent intent = getIntent();
        dataManager = new DataManager(intent.getIntExtra("index", -1), this);
        control = new InfoControl(this);
        control.setDataManager(dataManager);
        control.setViewHandler(new ViewHandler(this));
        control.setTextView((RollTextView) findViewById(R.id.info_view));
        control.startGame();
    }

    public void showButtons(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.button_group, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new MenuItemClickListener(this));
        }
        popupMenu.show();
    }

    public void runBackground(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Running...");
        builder.setContentText("Click to open");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int id = (int) SystemClock.uptimeMillis();
        manager.notify(id,builder.build());
        this.moveTaskToBack(true);
    }

    public class ViewHandler extends Handler {
        private WeakReference<GameActivity> context;

        public ViewHandler(GameActivity activity) {
            this.context = new WeakReference<GameActivity>(activity);
        }

        //刷新比较固定的属性值
        public void refreshProperties(final Hero hero) {
            post(new Runnable() {
                @Override
                public void run() {
                    ((TextView) context.get().findViewById(R.id.hero_name)).setText(control.getHero().getDisplayName());
                    ((TextView) context.get().findViewById(R.id.hero_gift)).setText(StringUtils.isNotEmpty(control.getHero().getGift()) ? control.getHero().getGift() : "");
                }
            });
        }

        //刷新变化频繁的属性
        public void refreshFreqProperties() {
            post(new Runnable() {
                @Override
                public void run() {
                    ((TextView) context.get().findViewById(R.id.hero_level)).setText(StringUtils.formatNumber(control.getMaze().getLevel()));
                    ((TextView) context.get().findViewById(R.id.hero_level_max)).setText(StringUtils.formatNumber(control.getMaze().getMaxLevel()));
                    ((TextView) context.get().findViewById(R.id.hero_mate)).setText(StringUtils.formatNumber(control.getHero().getMaterial()));
                    ((TextView) context.get().findViewById(R.id.hero_point)).setText(StringUtils.formatNumber(control.getHero().getMaterial()));
                    ((TextView) context.get().findViewById(R.id.hero_click)).setText(StringUtils.formatNumber(control.getHero().getClick()));
                    ((TextView) context.get().findViewById(R.id.hero_hp)).setText(StringUtils.formatNumber(control.getHero().getCurrentHp()));
                    ((TextView) context.get().findViewById(R.id.hero_max_hp)).setText(StringUtils.formatNumber(control.getHero().getMaxHp()));
                }
            });

        }

        //刷新装备
        public void refreshAccessory(final Hero hero) {
            post(new Runnable() {
                @Override
                public void run() {
                    for (Accessory accessory : hero.getAccessories()) {
                        switch (accessory.getType()) {
                            case "hat":
                                ((TextView) context.get().findViewById(R.id.hat_view)).setText(accessory.getDisplayName());
                                break;
                            case "ring":
                                ((TextView) context.get().findViewById(R.id.ring_view)).setText(accessory.getDisplayName());
                                break;
                            case "necklace":
                                ((TextView) context.get().findViewById(R.id.necklace_view)).setText(accessory.getDisplayName());
                                break;
                            case "sword":
                                ((TextView) context.get().findViewById(R.id.sword)).setText(accessory.getDisplayName());
                                break;
                            case "armor":
                                ((TextView) context.get().findViewById(R.id.armor)).setText(accessory.getDisplayName());
                                break;
                        }
                    }
                }
            });
        }

        public void refreshSkill(final Hero hero) {

        }

    }

    public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
        private Context context;
        public MenuItemClickListener(Context context){
            this.context = context;
        }

        //分享文本
        public void shareToNet() {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_string));
            shareIntent.setType("text/*");
            Intent chooser = Intent.createChooser(shareIntent, "分享到");
            startActivity(chooser);
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
                    sharingTip.setMessage("如果你觉得这个游戏好玩，不妨帮忙分享到你的圈子中，让更多的人参与到我们的游戏建设中来，一起享受放置的快乐。同时你的每次分享可以获得50W锻造点的额外奖励（每天一次）。");
                    sharingTip.show();
                    break;
            }
            return false;
        }
    }
}
