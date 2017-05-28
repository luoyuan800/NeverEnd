package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.Resource;

/**
 * Created by luoyuan on 2017/5/28.
 */
public class SkillDialog implements View.OnClickListener {
    private GameContext context;
    private Dialog dialog;

    public SkillDialog(GameContext context) {
        this.context = context;
        dialog = new AlertDialog.Builder(context.getContext())
                .setTitle(Resource.getString(R.string.skill_title))
                .setView(R.layout.skill_layout)
                .create();
    }

    public void show() {
        dialog.show();
        dialog.findViewById(R.id.skill_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TabHost tabHost = (TabHost) dialog.findViewById(R.id.skill_tabs);
        tabHost.setup();
        //Hero Skills
        tabHost.addTab(tabHost.newTabSpec("hero_skill").setIndicator(Resource.getString(R.string.hero_skill)).setContent(R.id.hero_skill));
        Button heroHit = (Button) dialog.findViewById(R.id.hero_hit);
        heroHit.setOnClickListener(this);

        //Evil skills
        tabHost.addTab(tabHost.newTabSpec("evil_skill").setIndicator(Resource.getString(R.string.evil_skill)).setContent(R.id.evil_skill));
        Button evilTalent = (Button) dialog.findViewById(R.id.evil_talent);
        evilTalent.setOnClickListener(this);
    }

    public void onClick(View view) {
        Skill skill = SkillFactory.geSkillByName(view.getTag().toString(), context.getDataManager());
        if (skill != null) {
            SkillParameter parameter = new SkillParameter(context.getHero());
            AlertDialog detail = new AlertDialog.Builder(context.getContext())
                    .setTitle(skill.getName())
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
                        }
                    }
                })
                ;
            } else {
                detail.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.enable),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (skill.canEnable(parameter)) {
                                    SkillHelper.enableSkill(skill, context, parameter);
                                }
                            }
                        });
            }

            if(skill instanceof UpgradeAble){
                detail.setButton(DialogInterface.BUTTON_NEUTRAL, Resource.getString(R.string.upgrade), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(((UpgradeAble) skill).canUpgrade(parameter)){
                            ((UpgradeAble) skill).upgrade(parameter);
                        }
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

            if(skill instanceof UpgradeAble){
                if(!((UpgradeAble) skill).canUpgrade(parameter)){
                    detail.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
                }
            }

        }
    }
}
