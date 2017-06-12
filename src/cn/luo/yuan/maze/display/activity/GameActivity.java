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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.PetAdapter;
import cn.luo.yuan.maze.display.dialog.AccessoriesDialog;
import cn.luo.yuan.maze.display.dialog.PetDialog;
import cn.luo.yuan.maze.display.dialog.RangePointDialog;
import cn.luo.yuan.maze.display.dialog.SkillDialog;
import cn.luo.yuan.maze.display.view.PetTextView;
import cn.luo.yuan.maze.display.view.RollTextView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.service.PetMonsterLoder;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by luoyuan on 2017/3/29.
 */
public class GameActivity extends Activity {
    DataManager dataManager;
    GameContext control;
    private PopupMenu popupMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resource.init(this);
        LogHelper.initLogSystem(this);
        setContentView(R.layout.game_layout);
        Intent intent = getIntent();
//        ListenerService.init();
        dataManager = new DataManager(intent.getIntExtra("index", -1), this);
        control = new GameContext(this);
        control.setDataManager(dataManager);
        control.setViewHandler(new ViewHandler(this));
        control.setTextView((RollTextView) findViewById(R.id.info_view));
        /*ProgressDialog progressDialog = new ProgressDialog(GameActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();*/
        new Thread(new Runnable() {
            public void run() {
                control.startGame();
                //progressDialog.dismiss();

            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, @SuppressWarnings("NullableProblems") KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                showExitDialog();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void randomMonsterBook(){
        Monster monster = control.getPetMonsterHelper().randomMonster();
        if(monster != null) {
            View view = findViewById(R.id.monster_view);
            Animation animation= AnimationUtils.loadAnimation(this, R.anim.revert);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                    control.getViewHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) view.findViewById(R.id.monster_name)).setText(monster.getType());
                            ((TextView) view.findViewById(R.id.monster_sex)).setText(monster.getSex() < 0 ? "♂♀" : (monster.getSex() == 0 ? "♂" : "♀"));
                            ((TextView) view.findViewById(R.id.monster_rank)).setText(StringUtils.formatStar(monster.getRank()));
                            ((TextView) view.findViewById(R.id.monster_race)).setText(monster.getRace().getName());
                            ((TextView) view.findViewById(R.id.monster_atk_value)).setText(StringUtils.formatNumber(monster.getAtk()));
                            ((TextView) view.findViewById(R.id.monster_def_value)).setText(StringUtils.formatNumber(monster.getDef()));
                            ((TextView) view.findViewById(R.id.monster_hp_value)).setText(StringUtils.formatNumber(monster.getMaxHp()));
                            ((TextView) view.findViewById(R.id.monster_egg_rate)).setText(StringUtils.formatPercentage(monster.getEggRate()));
                            ((TextView) view.findViewById(R.id.monster_pet_rate)).setText(StringUtils.formatPercentage(monster.getPetRate()));
                            ((TextView) view.findViewById(R.id.monster_desc)).setText(control.getPetMonsterHelper().getDescription(monster.getIndex(), monster.getType()));
                            ((ImageView) view.findViewById(R.id.monster_image)).setImageDrawable(PetMonsterLoder.loadMonsterImage(monster.getIndex()));
                        }
                    }, 1000);
                }   //在动画开始时使用

                @Override
                public void onAnimationRepeat(Animation arg0) {}  //在动画重复时使用

                @Override
                public void onAnimationEnd(Animation arg0) {

                }
            });
            view.startAnimation(animation);
        }
    }

    private Thread updateMonsterThread;


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_msg_view:
                if(findViewById(R.id.info_view).getVisibility() == View.VISIBLE){
                    control.getViewHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.info_view).setVisibility(View.GONE);
                            findViewById(R.id.monster_view).setVisibility(View.VISIBLE);
                            updateMonsterThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Runnable update = new Runnable() {
                                        @Override
                                        public void run() {
                                           randomMonsterBook();
                                        }
                                    };
                                    while (findViewById(R.id.monster_view).getVisibility() == View.VISIBLE) {
                                        control.getViewHandler().post(update);
                                        try {
                                            Thread.sleep(10000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            updateMonsterThread.start();
                        }
                    });
                }else{
                    control.getViewHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if(updateMonsterThread!=null){
                                updateMonsterThread = null;
                            }
                            findViewById(R.id.info_view).setVisibility(View.VISIBLE);
                            findViewById(R.id.monster_view).setVisibility(View.GONE);
                        }
                    });
                }
                break;
            case R.id.hat_view:
            case R.id.ring_view:
            case R.id.armor:
            case R.id.necklace_view:
            case R.id.sword:
                new AccessoriesDialog(control).show();
                break;
            case R.id.pets:
            case R.id.pets_root:
                PetAdapter adapter = new PetAdapter(this, control.getDataManager(), "");
                new PetDialog(control, adapter).show();
                break;
            case R.id.first_skill:
            case R.id.secondary_skill:
            case R.id.third_skill:
            case R.id.fourth_skill:
            case R.id.fifit_skill:
            case R.id.sixth_skill:
                new SkillDialog(control).show();
                break;
            case R.id.first_click_skill:

                break;
            case R.id.second_click_skill:
                break;
            case R.id.third_click_skill:
                break;
            case R.id.range_point:
                if(control.getHero().getPoint() > 0){
                    new RangePointDialog(control).show();
                }
                break;

        }
    }

    public void showButtons(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.button_group, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new MenuItemClickListener(this));
        }
        popupMenu.show();
    }

    public void runBackground() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Running...");
        builder.setContentText("Click to open");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int id = (int) SystemClock.uptimeMillis();
        manager.notify(id, builder.build());
        this.moveTaskToBack(true);
    }

    public static class ViewHandler extends Handler {
        private GameActivity context;

        public ViewHandler(GameActivity activity) {
            this.context = activity;
        }

        public void refreshHeadImage(Hero hero, Object target) {
            ImageView heroHead = (ImageView) context.findViewById(R.id.hero_pic);
        }

        //刷新比较固定的属性值
        public void refreshProperties(final Hero hero) {
            post(new Runnable() {
                @Override
                public void run() {
                    ((TextView) context.findViewById(R.id.hero_name)).setText(Html.fromHtml(context.control.getHero().getDisplayName()));
                    ((TextView) context.findViewById(R.id.hero_gift)).setText(StringUtils.isNotEmpty(context.control.getHero().getGift().getName()) ? context.control.getHero().getGift().getName() : "");
                    ((TextView) context.findViewById(R.id.hero_atk)).setText(StringUtils.formatNumber(hero.getUpperAtk()));
                    ((TextView) context.findViewById(R.id.hero_def)).setText(StringUtils.formatNumber(hero.getUpperDef()));
                    ((TextView) context.findViewById(R.id.hero_str)).setText(StringUtils.formatNumber(hero.getStr()));
                    ((TextView) context.findViewById(R.id.hero_agi)).setText(StringUtils.formatNumber(hero.getAgi()));
                }
            });
        }

        //刷新变化频繁的属性
        public void refreshFreqProperties() {
            post(new Runnable() {
                @Override
                public void run() {
                    //Hero properties
                    ((TextView) context.findViewById(R.id.hero_level)).setText(StringUtils.formatNumber(context.control.getMaze().getLevel()));
                    ((TextView) context.findViewById(R.id.hero_level_max)).setText(StringUtils.formatNumber(context.control.getMaze().getMaxLevel()));
                    ((TextView) context.findViewById(R.id.hero_mate)).setText(StringUtils.formatNumber(context.control.getHero().getMaterial()));
                    ((TextView) context.findViewById(R.id.hero_point)).setText(StringUtils.formatNumber(context.control.getHero().getPoint()));
                    ((TextView) context.findViewById(R.id.hero_click)).setText(StringUtils.formatNumber(context.control.getHero().getClick()));
                    ((TextView) context.findViewById(R.id.hero_hp)).setText(StringUtils.formatNumber(context.control.getHero().getCurrentHp()));
                    ((TextView) context.findViewById(R.id.hero_max_hp)).setText(StringUtils.formatNumber(context.control.getHero().getMaxHp()));

                }
            });

        }

        //刷新装备
        public void refreshAccessory(final Hero hero) {
            post(new Runnable() {
                @Override
                public void run() {
                    boolean hasHat = false;
                    boolean hasRing = false;
                    boolean hasNecklace = false;
                    boolean hasSword = false;
                    boolean hasArmor = false;
                    for (Accessory accessory : hero.getAccessories()) {
                        switch (accessory.getType()) {
                            case "hat":
                                hasHat = true;
                                ((TextView) context.findViewById(R.id.hat_view)).setText(Html.fromHtml(accessory.getDisplayName()));
                                break;
                            case "ring":
                                hasRing = true;
                                ((TextView) context.findViewById(R.id.ring_view)).setText(Html.fromHtml(accessory.getDisplayName()));
                                break;
                            case "necklace":
                                hasNecklace = true;
                                ((TextView) context.findViewById(R.id.necklace_view)).setText(Html.fromHtml(accessory.getDisplayName()));
                                break;
                            case "sword":
                                hasSword = true;
                                ((TextView) context.findViewById(R.id.sword)).setText(Html.fromHtml(accessory.getDisplayName()));
                                break;
                            case "armor":
                                hasArmor = true;
                                ((TextView) context.findViewById(R.id.armor)).setText(Html.fromHtml(accessory.getDisplayName()));
                                break;
                        }
                    }
                    if (!hasHat) {
                        ((TextView) context.findViewById(R.id.hat_view)).setText(context.getString(R.string.not_mount));
                    }
                    if (!hasRing) {
                        ((TextView) context.findViewById(R.id.ring_view)).setText(context.getString(R.string.not_mount));
                    }
                    if (!hasNecklace) {
                        ((TextView) context.findViewById(R.id.necklace_view)).setText(context.getString(R.string.not_mount));
                    }
                    if (!hasSword) {
                        ((TextView) context.findViewById(R.id.sword)).setText(context.getString(R.string.not_mount));
                    }
                    if (!hasArmor) {
                        ((TextView) context.findViewById(R.id.armor)).setText(context.getString(R.string.not_mount));
                    }
                }
            });
        }

        public void refreshSkill(final Hero hero) {
            post(new Runnable() {
                @Override
                public void run() {
                    Skill[] heroSkills = hero.getSkills();
                    if (heroSkills.length > 0 && heroSkills[0] != null && !(heroSkills[0] instanceof EmptySkill)) {
                        ((TextView) context.findViewById(R.id.first_skill)).setText(Html.fromHtml(heroSkills[0].getName()));
                    }else {
                        ((TextView) context.findViewById(R.id.first_skill)).setText(Resource.getString(R.string.not_mount));
                    }
                    if (heroSkills.length > 1 && heroSkills[1] != null && !(heroSkills[1] instanceof EmptySkill)) {
                        ((TextView) context.findViewById(R.id.secondary_skill)).setText(Html.fromHtml(heroSkills[1].getName()));
                    }else {
                        ((TextView) context.findViewById(R.id.secondary_skill)).setText(R.string.not_mount);
                    }
                    if (heroSkills.length > 2 && heroSkills[2] != null && !(heroSkills[2] instanceof EmptySkill)) {
                        ((TextView) context.findViewById(R.id.third_skill)).setText(Html.fromHtml(heroSkills[2].getName()));
                    }else {
                        ((TextView) context.findViewById(R.id.third_skill)).setText(Resource.getString(R.string.not_mount));
                    }
                    if (heroSkills.length > 3 && heroSkills[3] != null && !(heroSkills[3] instanceof EmptySkill)) {
                        ((TextView) context.findViewById(R.id.fourth_skill)).setText(Html.fromHtml(heroSkills[3].getName()));
                    }else{
                        if(hero.getReincarnate() >= 2){
                            ((TextView) context.findViewById(R.id.fourth_skill)).setText(R.string.not_mount);
                            context.findViewById(R.id.fourth_skill).setEnabled(true);
                        }else{
                            ((TextView) context.findViewById(R.id.fourth_skill)).setText(R.string.fourth_skill_enable);
                            context.findViewById(R.id.fourth_skill).setEnabled(false);
                        }
                    }
                    if (heroSkills.length > 4 && heroSkills[4] != null && !(heroSkills[4] instanceof EmptySkill)) {
                        ((TextView) context.findViewById(R.id.fifit_skill)).setText(Html.fromHtml(heroSkills[4].getName()));
                    }else{
                        if(hero.getReincarnate() >= 4){
                            ((TextView) context.findViewById(R.id.fifit_skill)).setText(R.string.not_mount);
                            context.findViewById(R.id.fifit_skill).setEnabled(true);
                        }else{
                            ((TextView) context.findViewById(R.id.fifit_skill)).setText(R.string.fifth_skill_enable);
                            context.findViewById(R.id.fifit_skill).setEnabled(false);
                        }
                    }
                    if (heroSkills.length > 5 && heroSkills[5] != null && !(heroSkills[5] instanceof EmptySkill)) {
                        ((TextView) context.findViewById(R.id.sixth_skill)).setText(Html.fromHtml(heroSkills[5].getName()));
                    }else{
                        if(hero.getReincarnate() >= 8){
                            ((TextView) context.findViewById(R.id.sixth_skill)).setText(R.string.not_mount);
                            context.findViewById(R.id.sixth_skill).setEnabled(true);
                        }else{
                            ((TextView) context.findViewById(R.id.sixth_skill)).setText(R.string.sixth_skill_enable);
                            context.findViewById(R.id.sixth_skill).setEnabled(false);
                        }
                    }


                }
            });
        }

        public void refreshPets(final Hero hero) {
            post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout petRoot = (LinearLayout) context.findViewById(R.id.pets_root);
                    petRoot.removeAllViews();
                    ArrayList<Pet> pets = new ArrayList<>(hero.getPets());
                    for(int i = 0; i< pets.size(); i++){
                        Pet pet = pets.get(i);
                        View view = petRoot.getChildAt(i);
                        if(view == null || !(view instanceof PetTextView)){
                            view = new PetTextView(context, pet);
                            petRoot.addView(view);
                        }else{
                            ((PetTextView) view).changePet(pet);
                        }
                    }
                }
            });

        }

    }

    public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private Context context;

        public MenuItemClickListener(Context context) {
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
                case R.id.pets:
                    new PetDialog(control, new PetAdapter(context,control.getDataManager(),"")).show();
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
            }
            return false;
        }
    }

    /**
     * 弹出退出程序提示框
     */
    private void showExitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("是否退出游戏");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        control.save();
                        control.stopGame();
                        finish();
                        System.exit(0);
                    }

                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        dialog.show();
    }

    private void showPayDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("点赞？");
        ScrollView linearLayout = new ScrollView(this);
        TextView tv = new TextView(this);
        tv.setText("请关注我的公众号，每日发送兑换码！");
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        linearLayout.addView(tv);
        dialog.setView(linearLayout);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.conform), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }

        });
        dialog.show();
    }
}
