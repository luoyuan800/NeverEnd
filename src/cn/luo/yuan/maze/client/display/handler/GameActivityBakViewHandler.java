package cn.luo.yuan.maze.client.display.handler;

import android.content.DialogInterface;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.GameActivity;
import cn.luo.yuan.maze.client.display.activity.GameActivityBak;
import cn.luo.yuan.maze.client.display.dialog.GiftDialog;
import cn.luo.yuan.maze.client.display.view.PetTextView;
import cn.luo.yuan.maze.client.display.view.RevealTextView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class GameActivityBakViewHandler extends GameActivityViewHandler {
    private GameActivityBak context;

    private Runnable refreshClickSkillTask  = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            ArrayList<ClickSkill> skills = hero.getClickSkills();
            Button first = (Button) context.findViewById(R.id.first_click_skill);
            first.setEnabled(false);
            Button second = (Button) context.findViewById(R.id.second_click_skill);
            second.setEnabled(false);
            Button third = (Button) context.findViewById(R.id.third_click_skill);
            third.setEnabled(false);
            if (skills.size() > 0) {
                first.setBackgroundResource(skills.get(0).getImageResource());
                first.setText(skills.get(0).getName());
                if (skills.get(0).isUsable()) {
                    first.setEnabled(true);
                }
            }
            if (skills.size() > 1) {
                second.setBackgroundResource(skills.get(1).getImageResource());
                second.setText(skills.get(1).getName());
                if (skills.get(1).isUsable()) {
                    second.setEnabled(true);
                }
            }
            if (skills.size() > 2) {
                third.setBackgroundResource(skills.get(2).getImageResource());
                third.setText(skills.get(2).getName());
                if (skills.get(2).isUsable()) {
                    third.setEnabled(true);
                }
            }
        }
    };
    private NeverEnd neverEnd;
    private Runnable refreshFreqPreperties = new Runnable() {
        @Override
        public void run() {
            //Hero properties
            ((TextView) context.findViewById(R.id.hero_level)).setText(StringUtils.formatNumber(context.control.getMaze().getLevel()));
            ((TextView) context.findViewById(R.id.hero_level_max)).setText(StringUtils.formatNumber(context.control.getMaze().getMaxLevel()));
            ((TextView) context.findViewById(R.id.hero_mate)).setText(StringUtils.formatNumber(context.control.getHero().getMaterial()));
            ((TextView) context.findViewById(R.id.hero_point)).setText(StringUtils.formatNumber(context.control.getHero().getPoint()));
            ((TextView) context.findViewById(R.id.hero_click)).setText(StringUtils.formatNumber(context.control.getHero().getClick()));
            ((TextView) context.findViewById(R.id.hero_hp)).setText(StringUtils.formatNumber(context.control.getHero().getCurrentHp()));
            ((TextView) context.findViewById(R.id.hero_max_hp)).setText(StringUtils.formatNumber(context.control.getHero().getCurrentMaxHp()));

        }
    };

    public GameActivityBakViewHandler(GameActivityBak activity, NeverEnd context) {
        super(activity, context);
        this.context = activity;
        this.neverEnd = context;
    }

    public void refreshHeadImage(Hero hero, Object target) {
        ImageView heroHead = (ImageView) context.findViewById(R.id.hero_pic);
    }

    public void refreshClickSkill() {
        post(refreshClickSkillTask);
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
                ((TextView) context.findViewById(R.id.hero_str)).setText(StringUtils.formatNumber(hero.getMaxStr()));
                ((TextView) context.findViewById(R.id.hero_agi)).setText(StringUtils.formatNumber(hero.getMaxAgi()));
            }
        });
    }

    //刷新变化频繁的属性
    public void refreshFreqProperties() {
        post(refreshFreqPreperties);

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
                        case Field.HAT_TYPE:
                            hasHat = true;
                            ((TextView) context.findViewById(R.id.hat_view)).setText(Html.fromHtml(accessory.toString()));
                            break;
                        case Field.RING_TYPE:
                            hasRing = true;
                            ((TextView) context.findViewById(R.id.ring_view)).setText(Html.fromHtml(accessory.toString()));
                            break;
                        case Field.NECKLACE_TYPE:
                            hasNecklace = true;
                            ((TextView) context.findViewById(R.id.necklace_view)).setText(Html.fromHtml(accessory.toString()));
                            break;
                        case Field.SWORD_TYPE:
                            hasSword = true;
                            ((TextView) context.findViewById(R.id.sword)).setText(Html.fromHtml(accessory.toString()));
                            break;
                        case Field.ARMOR_TYPR:
                            hasArmor = true;
                            ((TextView) context.findViewById(R.id.armor)).setText(Html.fromHtml(accessory.toString()));
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
                } else {
                    ((TextView) context.findViewById(R.id.first_skill)).setText(Resource.getString(R.string.not_mount));
                }
                if (heroSkills.length > 1 && heroSkills[1] != null && !(heroSkills[1] instanceof EmptySkill)) {
                    ((TextView) context.findViewById(R.id.secondary_skill)).setText(Html.fromHtml(heroSkills[1].getName()));
                } else {
                    ((TextView) context.findViewById(R.id.secondary_skill)).setText(R.string.not_mount);
                }
                if (heroSkills.length > 2 && heroSkills[2] != null && !(heroSkills[2] instanceof EmptySkill)) {
                    ((TextView) context.findViewById(R.id.third_skill)).setText(Html.fromHtml(heroSkills[2].getName()));
                } else {
                    ((TextView) context.findViewById(R.id.third_skill)).setText(Resource.getString(R.string.not_mount));
                }
                if (heroSkills.length > 3 && heroSkills[3] != null && !(heroSkills[3] instanceof EmptySkill)) {
                    ((TextView) context.findViewById(R.id.fourth_skill)).setText(Html.fromHtml(heroSkills[3].getName()));
                } else {
                    if (hero.getReincarnate() >= 2) {
                        ((TextView) context.findViewById(R.id.fourth_skill)).setText(R.string.not_mount);
                        context.findViewById(R.id.fourth_skill).setEnabled(true);
                    } else {
                        ((TextView) context.findViewById(R.id.fourth_skill)).setText(R.string.fourth_skill_enable);
                        context.findViewById(R.id.fourth_skill).setEnabled(false);
                    }
                }
                if (heroSkills.length > 4 && heroSkills[4] != null && !(heroSkills[4] instanceof EmptySkill)) {
                    ((TextView) context.findViewById(R.id.fifit_skill)).setText(Html.fromHtml(heroSkills[4].getName()));
                } else {
                    if (hero.getReincarnate() >= 4) {
                        ((TextView) context.findViewById(R.id.fifit_skill)).setText(R.string.not_mount);
                        context.findViewById(R.id.fifit_skill).setEnabled(true);
                    } else {
                        ((TextView) context.findViewById(R.id.fifit_skill)).setText(R.string.fifth_skill_enable);
                        context.findViewById(R.id.fifit_skill).setEnabled(false);
                    }
                }
                if (heroSkills.length > 5 && heroSkills[5] != null && !(heroSkills[5] instanceof EmptySkill)) {
                    ((TextView) context.findViewById(R.id.sixth_skill)).setText(Html.fromHtml(heroSkills[5].getName()));
                } else {
                    if (hero.getReincarnate() >= 8) {
                        ((TextView) context.findViewById(R.id.sixth_skill)).setText(R.string.not_mount);
                        context.findViewById(R.id.sixth_skill).setEnabled(true);
                    } else {
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
                ArrayList<Pet> pets = new ArrayList<>(hero.getPets());
                if (pets.size() > 0) {
                    for (int i = 0; i < pets.size(); i++) {
                        Pet pet = pets.get(i);
                        View view = petRoot.getChildAt(i);
                        if (view == null || !(view instanceof PetTextView)) {
                            if (view != null) {
                                petRoot.removeView(view);
                            }
                            view = new PetTextView(context, pet);
                            petRoot.addView(view);
                        } else {
                            ((PetTextView) view).changePet(pet);
                        }
                    }
                    if (pets.size() < petRoot.getChildCount()) {
                        for (int i = pets.size(); i < petRoot.getChildCount(); i++) {
                            petRoot.removeViewAt(i);
                        }
                    }
                } else {
                    String[] helps = Resource.getFilesInAssets("help");

                    View tv = petRoot.getChildAt(0);
                    if (tv == null || !(tv instanceof RevealTextView)) {
                        if (tv != null) {
                            petRoot.removeView(tv);
                        }
                        tv = new RevealTextView(context);
                        petRoot.addView(tv);
                    } else {
                        if (tv.getTag() instanceof Number && System.currentTimeMillis() - ((Number) tv.getTag()).longValue() < 30000) {
                            return;
                        }
                    }
                    ((RevealTextView) tv).setAnimatedText(Html.fromHtml(Resource.readStringFromAssets("help", context.control.getRandom().randomItem(helps))));
                    tv.setTag(System.currentTimeMillis());
                }
            }
        });

    }

    public void reCreate() {
        post(new Runnable() {
            @Override
            public void run() {
                context.recreate();
            }
        });
    }

    public void showGiftChoose() {
        post(new Runnable() {
            @Override
            public void run() {
               new GiftDialog(context, neverEnd.getHero(), new DialogInterface.OnDismissListener() {
                   @Override
                   public void onDismiss(DialogInterface dialog) {
                       if(neverEnd.getHero().getGift()!=null){
                           try {
                               neverEnd.getHero().getGift().handler(neverEnd);
                           } catch (Exception e) {
                               LogHelper.logException(e, "GameActivityViewHandler -> chooseGift -> ");
                           }
                       }
                       refreshProperties(neverEnd.getHero());
                   }
               });
            }
        });
    }
}

