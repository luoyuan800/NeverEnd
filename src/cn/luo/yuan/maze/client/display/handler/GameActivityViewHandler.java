package cn.luo.yuan.maze.client.display.handler;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.GameActivity;
import cn.luo.yuan.maze.client.display.dialog.GiftDialog;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ClientPetMonsterHelper;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.UpgradeAble;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class GameActivityViewHandler extends Handler {
    private GameActivity context;
    private NeverEnd neverEnd;
    private Runnable refreshSkillTask = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            clickSkill(hero);
            heroSkill(hero);

        }
    };

    private void updateSkillButton(Button button, Skill skill){
        Skill skillE = (Skill)button.getTag(R.string.item);
        if(skillE == null || !skillE.getId().equals(skill.getId())){
            button.setBackground(Resource.getSkillDrawable(skill));
            button.setTag(R.string.item, skill);
        }
        if(skill instanceof UpgradeAble){
            button.setText(String.format("X%d", ((UpgradeAble)skill).getLevel()));
        }else{
            button.setText(Resource.getString(R.string.empty));
        }
    }

    private void resetSkillButton(Button button){
        button.setTag(R.string.item, null);
        button.setBackgroundResource(0);
        button.setText(Resource.getString(R.string.not_mount));
    }

    private void heroSkill(Hero hero) {
        Skill[] heroSkills = hero.getSkills();
        Button first = (Button) context.findViewById(R.id.first_skill);
        if (heroSkills.length > 0 && heroSkills[0] != null && !(heroSkills[0] instanceof EmptySkill)) {
            updateSkillButton(first,heroSkills[0]);
        } else {
            resetSkillButton(first);
        }
        Button second = (Button) context.findViewById(R.id.secondary_skill);
        if (heroSkills.length > 1 && heroSkills[1] != null && !(heroSkills[1] instanceof EmptySkill)) {
            updateSkillButton(second, heroSkills[1]);
        } else {
            resetSkillButton(second);
        }
        Button third = (Button) context.findViewById(R.id.third_skill);
        if (heroSkills.length > 2 && heroSkills[2] != null && !(heroSkills[2] instanceof EmptySkill)) {
            updateSkillButton(third,heroSkills[2]);
        } else {
            resetSkillButton(third);
        }
        Button  fourth = (Button) context.findViewById(R.id.fourth_skill);
        if (heroSkills.length > 3 && heroSkills[3] != null && !(heroSkills[3] instanceof EmptySkill)) {
            updateSkillButton(fourth, heroSkills[3]);
        } else {
            resetSkillButton(fourth);
            if (hero.getReincarnate() >= 2 && heroSkills.length > 3) {
                fourth.setText(R.string.not_mount);
                fourth.setEnabled(true);
            } else {
                fourth.setText(R.string.fourth_skill_enable);
               fourth.setEnabled(false);
            }
        }
        Button fifth = (Button) context.findViewById(R.id.fifit_skill);
        if (heroSkills.length > 4 && heroSkills[4] != null && !(heroSkills[4] instanceof EmptySkill)) {
            updateSkillButton(fifth, heroSkills[4]);
        } else {
            resetSkillButton(fifth);
            if (hero.getReincarnate() >= 4  && heroSkills.length > 4) {
                fifth.setText(R.string.not_mount);
                fifth.setEnabled(true);
            } else {
                fifth.setText(R.string.fifth_skill_enable);
                fifth.setEnabled(false);
            }
        }
        Button sixth = (Button) context.findViewById(R.id.sixth_skill);
        if (heroSkills.length > 5 && heroSkills[5] != null && !(heroSkills[5] instanceof EmptySkill)) {
            updateSkillButton(sixth, heroSkills[5]);
        } else {
            resetSkillButton(sixth);
            if (hero.getReincarnate() >= 8  && heroSkills.length > 5) {
                sixth.setText(R.string.not_mount);
                sixth.setEnabled(true);
            } else {
                sixth.setText(R.string.sixth_skill_enable);
                sixth.setEnabled(false);
            }
        }
    }

    private void clickSkill(Hero hero) {
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
        }else{
            first.setEnabled(false);
            first.setBackgroundResource(0);
        }
        if (skills.size() > 1) {
            second.setBackgroundResource(skills.get(1).getImageResource());
            second.setText(skills.get(1).getName());
            if (skills.get(1).isUsable()) {
                second.setEnabled(true);
            }
        }else{
            second.setEnabled(false);
            second.setBackgroundResource(0);
        }
        if (skills.size() > 2) {
            third.setBackgroundResource(skills.get(2).getImageResource());
            third.setText(skills.get(2).getName());
            if (skills.get(2).isUsable()) {
                third.setEnabled(true);
            }
        }else{
            third.setEnabled(false);
            third.setBackgroundResource(0);
        }
    }

    private Runnable refreshFreqPrepertiesTask = new Runnable() {
        @Override
        public void run() {
            //Hero properties
            setText((TextView) context.findViewById(R.id.hero_level), StringUtils.formatNumber(context.control.getMaze().getLevel(), true));
            setText((TextView) context.findViewById(R.id.hero_level_max), StringUtils.formatNumber(context.control.getMaze().getMaxLevel(), false));
            setText((TextView) context.findViewById(R.id.hero_mate),StringUtils.formatNumber(context.control.getHero().getMaterial(), false));
            setText((TextView) context.findViewById(R.id.hero_point),StringUtils.formatNumber(context.control.getHero().getPoint(), false));
            setText((TextView) context.findViewById(R.id.hero_click),StringUtils.formatNumber(context.control.getHero().getClick(), false));
            setText((TextView) context.findViewById(R.id.hero_hp),StringUtils.formatNumber(context.control.getHero().getCurrentHp(), false));
            setText((TextView) context.findViewById(R.id.hero_max_hp),StringUtils.formatNumber(context.control.getHero().getUpperHp(), false));
            TextView additionHp = (TextView) context.findViewById(R.id.hero_addition_hp);
            long additionHpValue = context.control.getHero().getAdditionHp();
            setNumberText(additionHp, additionHpValue);
            setText((TextView) context.findViewById(R.id.hero_atk),StringUtils.formatNumber(context.control.getHero().getAtk(), false));
            setNumberText((TextView) context.findViewById(R.id.hero_atk_addition), context.control.getHero().getAdditionAtk());
            setText((TextView) context.findViewById(R.id.hero_def),StringUtils.formatNumber(context.control.getHero().getUpperDef(), false));
            setNumberText((TextView) context.findViewById(R.id.hero_def_addition),context.control.getHero().getAdditionDef());
        }
    };

    private void setNumberText(TextView textView, long value) {
        if(value >= 0) {
            setText(textView, " + " + StringUtils.formatNumber(value, false));
            textView.setTextColor(Color.BLUE);
        }else{
            setText(textView, " - " + StringUtils.formatNumber(value, false));
            textView.setTextColor(R.color.mobvista_reward_green);
        }
    }

    private void setText(TextView view, String text){
        if(text==null){
            text = StringUtils.EMPTY_STRING;
        }
        if(!text.equals(view.getTag(R.string.item))){
            view.setTag(R.string.item, text);
            view.setText(text);
        }
    }
    private Runnable refreshPetTask = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            ArrayList<Pet> pets = new ArrayList<>(hero.getPets());
            ImageView first = (ImageView)context.findViewById(R.id.pet_1);
            ImageView second = (ImageView)context.findViewById(R.id.pet_2);
            ImageView third = (ImageView)context.findViewById(R.id.pet_3);
            if(pets.size() > 0){
                first.setVisibility(View.VISIBLE);
                Pet pet = pets.get(0);
                updatePetView(pet, first);
            }else{
                resetPetButton(first);
            }
            if(pets.size() > 1){
                second.setVisibility(View.VISIBLE);
                Pet pet = pets.get(1);
                updatePetView(pet, second);
            }else{
               resetPetButton(second);
            }
            if(pets.size() > 2){
                third.setVisibility(View.VISIBLE);
                Pet pet = pets.get(2);
                updatePetView(pet, third);
            }else{
                resetPetButton(third);
            }
            ((Button)context.findViewById(R.id.more_pet)).setText(String.format("%d+",pets.size()));
        }
    };

    private void resetPetButton(ImageView first) {
        first.setTag(R.string.item, (Pet)null);
        first.setImageResource(0);
        first.setVisibility(View.INVISIBLE);
    }

    private void updatePetView(Pet pet, ImageView petView) {
        if(pet!=null){
            boolean notChange = false;
            Pet vp = (Pet)petView.getTag(R.string.item);
            Drawable drawable;
            if(vp == null || !vp.getId().equals(pet.getId())) {
                drawable = ClientPetMonsterHelper.loadMonsterImage(pet.getIndex());
            }else{
                drawable = petView.getDrawable();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if((pet.getCurrentHp() > 0 && drawable.getColorFilter() == null) || (pet.getCurrentHp() <= 0 && drawable.getColorFilter()!=null)){
                        //宠物状态没有变化
                        notChange = true;
                    }
                }
            }
            if(!notChange) {
                petView.setImageDrawable(drawable);
                if (pet.getCurrentHp() > 0) {
                    drawable.clearColorFilter();
                } else {
                    drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        petView.setTag(R.string.item, pet);
    }

    private Runnable refreshPropertiesTask = new Runnable() {
        @Override
        public void run() {
            ((TextView) context.findViewById(R.id.hero_name)).setText(Html.fromHtml(context.control.getHero().getDisplayName()));
            ((TextView) context.findViewById(R.id.hero_gift)).setText(Html.fromHtml(context.control.getHero().getGift().getName()));
        }
    };;

    public GameActivityViewHandler(GameActivity activity, NeverEnd context) {
        this.context = activity;
        this.neverEnd = context;
    }

    public void refreshHeadImage(Hero hero, Object target) {
        ImageView heroHead = (ImageView) context.findViewById(R.id.hero_pic);
    }

    public void refreshSkill() {
        post(refreshSkillTask);
    }

    public void refreshProperties(final Hero hero) {
        post(refreshPropertiesTask);
    }

    //刷新变化频繁的属性
    public void refreshFreqProperties() {
        post(refreshFreqPrepertiesTask);

    }

    public void refreshPets(final Hero hero) {

        post(refreshPetTask);

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
                        if (neverEnd.getHero().getGift() != null) {
                            try {
                                neverEnd.getHero().getGift().handler(neverEnd);
                            } catch (Exception e) {
                                LogHelper.logException(e, "GameActivityViewHandler -> chooseGift -> ");
                            }
                        }
                        refreshProperties(neverEnd.getHero());
                    }
                }).show();
            }
        });
    }
}

