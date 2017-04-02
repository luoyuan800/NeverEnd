package cn.luo.yuan.maze.display.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
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

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.game_layout);
        dataManager = new DataManager(savedInstanceState.getInt("index"), this);
        control = new InfoControl(this);
        control.setDataManager(dataManager);
        control.setViewHandler(new ViewHandler(this));
        control.setTextView((RollTextView) findViewById(R.id.info_view));
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
        public void refreshAccessory(Hero hero) {
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

        public void refreshSkill() {

        }

    }
}
