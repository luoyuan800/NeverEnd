package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.SkillHelper;
import com.huanglong.mylinearlayout.FixGridLayout;

import java.util.List;

/**
 * Created by luoyuan on 2017/5/28.
 */
public class SkillDialog implements View.OnClickListener {
    private NeverEnd context;
    private Dialog dialog;

    public SkillDialog(NeverEnd context) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext())
                .setTitle(Resource.getString(R.string.skill_title));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.skill_layout);
        } else {
            builder.setView(View.inflate(context.getContext(), R.layout.skill_layout, null));
        }
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
        dialog.findViewById(R.id.skill_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TabHost tabHost = (TabHost) dialog.findViewById(R.id.skill_tabs_main);
        tabHost.setup();
        //Hero Skills
        tabHost.addTab(tabHost.newTabSpec("hero_skill").setIndicator(Resource.getString(R.string.hero_skill)).setContent(R.id.hero_skill));
        initSkillButton((ImageButton) dialog.findViewById(R.id.hero_hit)
                , (ImageButton) dialog.findViewById(R.id.fight_back), (ImageButton) dialog.findViewById(R.id.dodge));

        //Evil skills
        tabHost.addTab(tabHost.newTabSpec("evil_skill").setIndicator(Resource.getString(R.string.evil_skill)).setContent(R.id.evil_skill));
        initSkillButton((ImageButton) dialog.findViewById(R.id.evil_talent), (ImageButton) dialog.findViewById(R.id.reinforce), (ImageButton) dialog.findViewById(R.id.stealth));

        //Element skills
        tabHost.addTab(tabHost.newTabSpec("element_skill").setIndicator(Resource.getString(R.string.element_skill)).setContent(R.id.element_skill));
        initSkillButton((ImageButton) dialog.findViewById(R.id.element_alist), (ImageButton) dialog.findViewById(R.id.element_Defend), (ImageButton) dialog.findViewById(R.id.element_bomb));

        //Secondary Skill
        TabHost tabHost1 = (TabHost) dialog.findViewById(R.id.skill_tag_second);
        tabHost1.setup();

        //Swindler Skills
        tabHost1.addTab(tabHost1.newTabSpec("swindler_skill").setIndicator(Resource.getString(R.string.swindler_skill)).setContent(R.id.swindler_skill));
        initSkillButton((ImageButton) dialog.findViewById(R.id.swindler), (ImageButton) dialog.findViewById(R.id.swindler_game), (ImageButton) dialog.findViewById(R.id.eat_harm));
        //Pet Skills
        tabHost1.addTab(tabHost1.newTabSpec("pet_skill").setIndicator(Resource.getString(R.string.pet_skill)).setContent(R.id.pet_skill));
        initSkillButton((ImageButton) dialog.findViewById(R.id.pet_master),(ImageButton) dialog.findViewById(R.id.pet_trainer), (ImageButton) dialog.findViewById(R.id.pet_foster));

        //Special Skill
        List<Skill> skills = context.getDataManager().loadSpecialSkills();
        if(skills.size() > 0) {
            tabHost1.addTab(tabHost1.newTabSpec("special_skill").setIndicator(Resource.getString(R.string.special_skill)).setContent(R.id.special_skill));
            FixGridLayout fgl = (FixGridLayout) dialog.findViewById(R.id.special_skill_detail);
            fgl.setmCellHeight(150);
            fgl.setmCellWidth(150);
            fgl.removeAllViews();
            ImageButton[] buttons = new ImageButton[skills.size()];
            for (int i = 0; i < skills.size(); i++) {
                ImageButton b = new ImageButton(context.getContext());
                b.setMinimumWidth(150);
                b.setMinimumHeight(150);
                b.setBackgroundResource(0);
                b.setScaleType(ImageView.ScaleType.FIT_CENTER);
                b.setTag(skills.get(i).getClass().getSimpleName());
                buttons[i] = b;
                fgl.addView(b);
            }
            initSkillButton(buttons);
        }else{
            dialog.findViewById(R.id.special_skill).setVisibility(View.GONE);
        }
    }

    public void onClick(final View view) {
        final Skill skill = SkillFactory.geSkillByName(view.getTag().toString(), context.getDataManager());
        if (skill != null) {
            final SkillParameter parameter = new SkillParameter(context.getHero());
            parameter.set(SkillParameter.CONTEXT, context);
            parameter.set(SkillParameter.RANDOM, context.getRandom());
            AlertDialog detail = new AlertDialog.Builder(context.getContext())
                    .setTitle(skill.getName() +
                            "<" + (skill instanceof AtkSkill ? "攻击" : (skill instanceof DefSkill ? "防御" : "属性")) + ">")
                    .setMessage(Html.fromHtml(skill.getDisplayName()))
                    .setNegativeButton(Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            if (skill.isEnable()) {
                detail.setButton(AlertDialog.BUTTON_POSITIVE, Resource.getString(
                        (skill instanceof MountAble ?
                                (((MountAble) skill).isMounted() ? R.string.need_un_mount : R.string.need_mount)
                                : R.string.empty)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (skill instanceof MountAble) {
                            if (((MountAble) skill).isMounted()) {
                                SkillHelper.unMountSkill((MountAble) skill, context.getHero());
                            } else {
                                if (((MountAble) skill).canMount(parameter)) {
                                    SkillHelper.mountSkill(skill, context.getHero());
                                }
                            }
                            context.getDataManager().save(skill);
                            refreshSkillDisplay(view);
                        }
                    }
                })
                ;
            } else {
                detail.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.enable),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showWarning("激活", "需要消耗" + Data.SKILL_ENABLE_COST + "能力点来激活" + skill.getName(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (skill.canEnable(parameter)) {
                                            SkillHelper.enableSkill(skill, context, parameter);
                                            refreshSkillDisplay(view);
                                        }
                                        context.getDataManager().save(skill);
                                    }
                                });
                            }
                        });
            }

            if (skill instanceof UpgradeAble) {
                detail.setButton(DialogInterface.BUTTON_NEUTRAL, Resource.getString(R.string.upgrade), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWarning("升级", "需要消耗" + ((UpgradeAble) skill).getLevel() * Data.SKILL_ENABLE_COST + "能力点来升级" + (((UpgradeAble) skill).getLevel() + 1), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (((UpgradeAble) skill).canUpgrade(parameter)) {
                                    SkillHelper.upgradeSkill((UpgradeAble) skill, parameter, context);
                                    refreshSkillDisplay(view);
                                }
                                context.getDataManager().save(skill);
                            }
                        });
                    }
                });

            }

            detail.show();
            if (!skill.isEnable()) {
                if (!skill.canEnable(parameter)) {
                    detail.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            } else if (skill instanceof MountAble) {
                if (!((MountAble) skill).isMounted()) {
                    if (!((MountAble) skill).canMount(parameter)) {
                        detail.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            }

            if (skill instanceof UpgradeAble) {
                if (!((UpgradeAble) skill).canUpgrade(parameter)) {
                    detail.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
                }
            }

        } else {
            SimplerDialogBuilder.build("敬请期待", Resource.getString(R.string.conform), context.getContext(), context.getRandom());
        }
    }

    private void refreshSkillDisplay(View view) {
        context.getViewHandler().refreshSkill();
        /*Skill skill = SkillFactory.geSkillByName(view.getTag().toString(), context.getDataManager());
        if(skill!=null && view instanceof Button){
            ((Button) view).setText(Html.fromHtml(skill.getSkillName()));
        }
*/
    }

    private void initSkillButton(ImageButton... skillButtons) {
        Skill skill;
        for (ImageButton b : skillButtons) {
            b.setOnClickListener(this);
            skill = SkillFactory.geSkillByName(b.getTag().toString(), context.getDataManager());
            if (skill != null) {
                b.setImageDrawable(Resource.getSkillDrawable(skill));
            }
        }
    }

    private void showWarning(String title, String message, Dialog.OnClickListener conform) {
        new AlertDialog.Builder(context.getContext()).setPositiveButton(Resource.getString(R.string.conform), conform).setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle(title).setMessage(Html.fromHtml(message)).show();
    }
}
