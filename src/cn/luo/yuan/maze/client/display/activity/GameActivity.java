package cn.luo.yuan.maze.client.display.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.dialog.*;
import cn.luo.yuan.maze.client.display.handler.AdHandler;
import cn.luo.yuan.maze.client.display.handler.GameActivityViewHandler;
import cn.luo.yuan.maze.client.display.handler.MenuItemClickListener;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.service.ClientPetMonsterHelper;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.goods.types.Invincible;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

/**
 * Created by luoyuan on 2017/3/29.
 */
public class GameActivity extends BaseActivity {
    public DataManager dataManager;
    public NeverEnd control;
    private PopupMenu popupMenu;
    private Thread updateMonsterThread;
    private boolean updateRandomMonster;
    private boolean birthDay;
    private AdHandler adHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
        Intent intent = getIntent();
//        ListenerService.init();
        dataManager = new DataManager(intent.getIntExtra("index", -1), this);
        final NeverEndConfig config = dataManager.loadConfig();
        if (!config.getSign().equals(Resource.getSingInfo())) {
            finish();
        }
        if (config.getTheme() != 0) {
            setTheme(config.getTheme());
        }
        setContentView(R.layout.game_layout);
        control = (NeverEnd) getApplication();
        control.setContext(this, dataManager);
        control.setViewHandler(new GameActivityViewHandler(this, control));
        control.setTextView((RollTextView) findViewById(R.id.info_view));
        if (StringUtils.isEmpty(config.getHead())) {
            config.setHead(control.getRandom().randomItem(Arrays.asList("Actor1_1.png", "Actor2_1.png", "Actor2_2.png", "Actor2_5.png", "Actor2_6.png", "Actor1_2.png", "Actor3_1.png", "Actor3_6.png")));
            control.getViewHandler().showFirstInTip();
        }else if(!getVersion().equals(config.getCurrentVersion())){
            config.setCurrentVersion(getVersion());
            MessageDialog md = new MessageDialog(this, Collections.singletonList("感谢您的支持，送您一瓶无敌药水。祝你快乐！"));
            md.show();
            Invincible invincible = new Invincible();
            invincible.setCount(1);
            dataManager.add(invincible);
        }

        dataManager.save(config);
        Resource.askWritePermissions(new PermissionRequestListener() {
            @Override
            public void result(int requestCode, String[] permissions, int[] grantResults) {
                if (grantResults[0] + grantResults[1] == 0) {
                    Toast.makeText(GameActivity.this, "已经获得记录数据的权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameActivity.this, "无法获得记录数据的权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new Thread(new Runnable() {
            public void run() {
                control.startGame();
                //progressDialog.dismiss();

            }
        }).start();
        if (control.getHero().getClickSkills().isEmpty()) {
            new ClickSkillDialog(control).show(control.getIndex(), false);
        }
        control.getViewHandler().post(new Runnable() {
            @Override
            public void run() {
                Drawable bitmap = Resource.loadImageFromSD("bak.png");
                if (bitmap != null) {
                    findViewById(R.id.game_view_container).setBackground(bitmap);
                }
                bitmap = Resource.loadImageFromSD("h.png");
                if (bitmap != null) {
                    ((ImageView) findViewById(R.id.hero_pic)).setImageDrawable(bitmap);
                } else {
                    if (StringUtils.isNotEmpty(config.getHead())) {
                        bitmap = Resource.loadImageFromAssets(config.getHead(), false);
                        if (bitmap != null) {
                            ((ImageView) findViewById(R.id.hero_pic)).setImageDrawable(bitmap);
                        }
                    }
                }
            }
        });

        GregorianCalendar date = new GregorianCalendar();
        date.setTimeInMillis(control.getHero().getBirthDay());
        GregorianCalendar current = new GregorianCalendar();
        if (date.get(Calendar.MONTH) == current.get(Calendar.MONTH) && date.get(Calendar.DAY_OF_MONTH) == current.get(Calendar.DAY_OF_MONTH)) {
            birthDay = true;
            control.showPopup(Resource.getString(R.string.birthday_tip));
        } else {
            birthDay = false;
        }
        adHandler = new AdHandler(control);
        adHandler.setupAD();
        control.setAdHandler(adHandler);
        control.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                control.getServerService().syncDebrisAndGift(control);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        adHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    public void onClick(View v) {
        control.getHero().setClick(control.getHero().getClick() + 1);
        HarmAble currentBattleTarget = control.getCurrentBattleTarget();
        switch (v.getId()) {
            case R.id.into_invicible:
                if(control.dieCountClear()){
                    findViewById(R.id.into_invicible).setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.die_msg_button:
                control.getViewHandler().showDieList();
                break;
            case R.id.monster_random:
                updateRandomMonster = !updateRandomMonster;
                updateMonsterControlButton();
                break;
            case R.id.monster_next:
                updateRandomMonster = false;
                View monsterView = findViewById(R.id.monster_view);
                Monster monster = (Monster) monsterView.getTag(R.string.item);
                if (monster != null) {
                    int index = monster.getIndex() + 1;
                    Monster nexMonster = control.getPetMonsterHelper().loadMonsterByIndex(index);
                    int count = 0;
                    while (nexMonster == null && count++ < 55) {
                        if(index > 500 || index < 0){
                            index = 1;
                        }
                        nexMonster = control.getPetMonsterHelper().loadMonsterByIndex(index ++);
                    }
                    updateMonsterDetails(nexMonster);
                }
                updateMonsterControlButton();
                break;
            case R.id.monster_previous:
                updateRandomMonster = false;
                View preMonsterView = findViewById(R.id.monster_view);
                Monster currMonster = (Monster) preMonsterView.getTag(R.string.item);
                if (currMonster != null) {
                    int index = currMonster.getIndex() - 1;
                    Monster nexMonster = control.getPetMonsterHelper().loadMonsterByIndex(index);
                    int count = 0;
                    while (nexMonster == null && count++ < 55) {
                        if(index > 500 || index < 0){
                            index = 1;
                        }
                        nexMonster = control.getPetMonsterHelper().loadMonsterByIndex(index--);
                    }
                    updateMonsterDetails(nexMonster);
                }
                updateMonsterControlButton();
                break;
            case R.id.pause:
                boolean pause = control.pauseGame();
                if (v instanceof ImageButton) {
                    ((ImageButton) v).setImageResource(pause ? android.R.drawable.ic_media_play : android.R.drawable.ic_media_pause);
                }
                break;
            case R.id.first_skill:
            case R.id.secondary_skill:
            case R.id.third_skill:
            case R.id.fourth_skill:
            case R.id.fifit_skill:
            case R.id.sixth_skill:
                new SkillDialog(control).show();
                break;
            case R.id.more_pet:
                new PetDialog(control, new PetAdapter(this, control.getDataManager(), "")).show();
                break;
            case R.id.switch_msg_view:
                if (findViewById(R.id.info_view).getVisibility() == View.VISIBLE) {
                    updateRandomMonster = true;
                    findViewById(R.id.info_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.monster_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.monster_book_control).setVisibility(View.VISIBLE);
                    control.getViewHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateMonsterThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Runnable update = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (updateRandomMonster)
                                                randomMonsterBook();
                                        }
                                    };
                                    while (findViewById(R.id.monster_view).getVisibility() == View.VISIBLE) {
                                        control.getViewHandler().post(update);
                                        try {
                                            Thread.sleep(40000);
                                        } catch (InterruptedException e) {
                                            LogHelper.logException(e, "GameActivity ->111Switch");
                                        }
                                    }
                                }
                            });
                            updateMonsterThread.start();
                        }
                    });
                } else {
                    control.getViewHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.info_view).setVisibility(View.VISIBLE);
                            findViewById(R.id.monster_view).setVisibility(View.INVISIBLE);
                            findViewById(R.id.monster_book_control).setVisibility(View.GONE);
                            if (updateMonsterThread != null) {
                                updateMonsterThread = null;
                            }

                        }
                    });
                }
                break;
            case R.id.first_click_skill:
                if (control.getHero().getClickSkills().size() >= 1) {
                    if (currentBattleTarget != null) {
                        ClickSkill ck = control.getHero().getClickSkills().get(0);
                        ck.use(control.getHero(), currentBattleTarget, control);
                    }
                }
                break;
            case R.id.second_click_skill:
                if (control.getHero().getClickSkills().size() >= 2) {
                    ClickSkill ck = control.getHero().getClickSkills().get(1);
                    ck.use(control.getHero(), currentBattleTarget, control);
                }
                break;
            case R.id.third_click_skill:
                if (control.getHero().getClickSkills().size() >= 3) {
                    ClickSkill ck = control.getHero().getClickSkills().get(2);
                    ck.use(control.getHero(), currentBattleTarget, control);
                }
                break;
            case R.id.range_point:
                new PropertiesDialog(control).show();
                break;

        }
    }

    public void npcImgClick(View view){
        control.getViewHandler().showNPCDialog();
    }

    public boolean isBirthDay() {
        return birthDay;
    }

    private void updateMonsterControlButton() {
        if(updateRandomMonster){
            ((ImageView)findViewById(R.id.monster_random)).setImageResource(R.drawable.mobvista_cm_refresh);
        }else{
            ((ImageView)findViewById(R.id.monster_random)).setImageResource(R.drawable.mobvista_cm_exits);
        }
    }

    public void showButtons(View view) {
        if (popupMenu == null) {
            popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.button_group, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new MenuItemClickListener(this, control));
        }
        popupMenu.show();
    }

    private void initResources() {
        Resource.init(this);
        LogHelper.initLogSystem(this);
    }

    protected void onResume() {
        super.onResume();
        initResources();
        control.setContext(this);
        adHandler.onResume();
    }

    private void randomMonsterBook() {
        final Monster monster = control.getPetMonsterHelper().randomMonster();
        updateMonsterDetails(monster);
    }

    private void updateMonsterDetails(final Monster monster) {
        if (monster != null) {
            final View view = findViewById(R.id.monster_view);
            view.setTag(R.string.item, monster);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.flip_horizontal_in);
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
                            NeverEndConfig config = dataManager.loadConfig();
                            boolean isCatched = config!=null ? config.isMonsterCatched(monster.getIndex()) : control.getDataManager().loadPets(0, 1, monster.getType(), null).isEmpty();
                            if(!isCatched) {
                                ((TextView) view.findViewById(R.id.monster_atk_value)).setText("?");
                                ((TextView) view.findViewById(R.id.monster_def_value)).setText("?");
                                ((TextView) view.findViewById(R.id.monster_hp_value)).setText("?");
                                ((TextView) view.findViewById(R.id.monster_egg_rate)).setText("?");
                                ((TextView) view.findViewById(R.id.monster_pet_rate)).setText("?");
                                view.findViewById(R.id.monster_cathced).setVisibility(View.INVISIBLE);
                            }else{
                                ((TextView) view.findViewById(R.id.monster_atk_value)).setText(StringUtils.formatNumber(monster.getAtk(), false));
                                ((TextView) view.findViewById(R.id.monster_def_value)).setText(StringUtils.formatNumber(monster.getDef(), false));
                                ((TextView) view.findViewById(R.id.monster_hp_value)).setText(StringUtils.formatNumber(monster.getMaxHp(), false));
                                ((TextView) view.findViewById(R.id.monster_egg_rate)).setText(StringUtils.formatPercentage(monster.getEggRate()));
                                ((TextView) view.findViewById(R.id.monster_pet_rate)).setText(StringUtils.formatPercentage(monster.getPetRate()));
                                view.findViewById(R.id.monster_cathced).setVisibility(View.VISIBLE);
                            }
                            ((TextView) view.findViewById(R.id.monster_desc)).setText(control.getPetMonsterHelper().getDescription(monster.getIndex(), monster.getType()));
                            ((ImageView) view.findViewById(R.id.monster_image)).setImageDrawable(ClientPetMonsterHelper.loadMonsterImage(monster.getIndex()));
                        }
                    }, 300);
                }   //在动画开始时使用

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }  //在动画重复时使用

                @Override
                public void onAnimationEnd(Animation arg0) {

                }
            });
            view.startAnimation(animation);
        }
    }

    @Override
    protected void onDestroy() {
        adHandler.onDestroy();
        control.stopGame();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adHandler.onPause();
    }

    @Override
    protected void onStop() {
        adHandler.onStop();
        super.onStop();
        control.save(false);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        adHandler.onActivityResult(reqCode, resCode, data);
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
                        control.save(false);
                        AdHandler.onAppExit(GameActivity.this);
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


}
