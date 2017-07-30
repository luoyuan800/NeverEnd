package cn.luo.yuan.maze.client.display.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.dialog.ClickSkillDialog;
import cn.luo.yuan.maze.client.display.dialog.PetDialog;
import cn.luo.yuan.maze.client.display.dialog.PropertiesDialog;
import cn.luo.yuan.maze.client.display.dialog.SkillDialog;
import cn.luo.yuan.maze.client.display.handler.AdHandler;
import cn.luo.yuan.maze.client.display.handler.GameActivityViewHandler;
import cn.luo.yuan.maze.client.display.handler.MenuItemClickListener;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.PetMonsterLoder;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/29.
 */
public class GameActivity extends BaseActivity {
    public DataManager dataManager;
    public NeverEnd control;
    private PopupMenu popupMenu;
    private Thread updateMonsterThread;

    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
//        ListenerService.init();
        dataManager = new DataManager(intent.getIntExtra("index", -1), this);
        super.onCreate(savedInstanceState);
        NeverEndConfig config = dataManager.getConfig();
        if (config.getTheme() != 0) {
            setTheme(config.getTheme());
        }
        setContentView(R.layout.game_layout);

        control = (NeverEnd) getApplication();
        control.setContext(this, dataManager);
        control.setViewHandler(new GameActivityViewHandler(this, control));
        control.setTextView((RollTextView) findViewById(R.id.info_view));
        initResources();
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
                Drawable bitmap = Resource.getImageFromSD("bak.png");
                if (bitmap != null) {
                    findViewById(R.id.game_view_container).setBackground(bitmap);
                }
                bitmap = Resource.getImageFromSD("h.png");
                if (bitmap != null) {
                    ((ImageView) findViewById(R.id.hero_pic)).setImageDrawable(bitmap);
                }
            }
        });
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
                    findViewById(R.id.info_view).setVisibility(View.INVISIBLE);
                    findViewById(R.id.monster_view).setVisibility(View.VISIBLE);
                    control.getViewHandler().post(new Runnable() {
                        @Override
                        public void run() {
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
                                            Thread.sleep(60000);
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
                if (control.getHero().getPoint() > 0) {
                    new PropertiesDialog(control).show();
                }
                break;

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
        control.setContext(this);
    }

    protected void onResume() {
        super.onResume();
        initResources();
    }

    private void randomMonsterBook() {
        final Monster monster = control.getPetMonsterHelper().randomMonster();
        if (monster != null) {
            final View view = findViewById(R.id.monster_view);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.revert);
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
        control.stopGame();
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        super.onStop();
        control.save(false);
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
