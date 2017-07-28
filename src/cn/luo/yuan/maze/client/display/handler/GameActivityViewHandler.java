package cn.luo.yuan.maze.client.display.handler;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.GameActivity;
import cn.luo.yuan.maze.client.display.dialog.GiftDialog;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.PetMonsterLoder;
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
import java.util.List;

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

    private void heroSkill(Hero hero) {
        Skill[] heroSkills = hero.getSkills();
        Button first = (Button) context.findViewById(R.id.first_skill);
        if (heroSkills.length > 0 && heroSkills[0] != null && !(heroSkills[0] instanceof EmptySkill)) {
            first.setBackground(Resource.getSkillDrawable(heroSkills[0]));
            if(heroSkills[0] instanceof UpgradeAble){
                first.setText(String.format("X%d", ((UpgradeAble)heroSkills[0]).getLevel()));
            }else{
                first.setText(Resource.getString(R.string.empty));
            }
        } else {
            first.setBackgroundResource(0);
            first.setText(Resource.getString(R.string.not_mount));
        }
        TextView second = (TextView) context.findViewById(R.id.secondary_skill);
        if (heroSkills.length > 1 && heroSkills[1] != null && !(heroSkills[1] instanceof EmptySkill)) {
            second.setBackground(Resource.getSkillDrawable(heroSkills[1]));
            if(heroSkills[1] instanceof UpgradeAble){
                second.setText(String.format("X%d", ((UpgradeAble)heroSkills[1]).getLevel()));
            }else{
                second.setText(Resource.getString(R.string.empty));
            }
        } else {
            second.setBackgroundResource(0);
            second.setText(R.string.not_mount);
        }
        TextView third = (TextView) context.findViewById(R.id.third_skill);
        if (heroSkills.length > 2 && heroSkills[2] != null && !(heroSkills[2] instanceof EmptySkill)) {
            third.setBackground(Resource.getSkillDrawable(heroSkills[2]));
            if(heroSkills[2] instanceof UpgradeAble){
                third.setText(String.format("X%d", ((UpgradeAble)heroSkills[2]).getLevel()));
            }else{
                third.setText(Resource.getString(R.string.empty));
            }
        } else {
            third.setBackgroundResource(0);
            third.setText(Resource.getString(R.string.not_mount));
        }
        TextView fourth = (TextView) context.findViewById(R.id.fourth_skill);
        if (heroSkills.length > 3 && heroSkills[3] != null && !(heroSkills[3] instanceof EmptySkill)) {
            fourth.setEnabled(true);
            fourth.setBackground(Resource.getSkillDrawable(heroSkills[3]));
            if(heroSkills[3] instanceof UpgradeAble){
                fourth.setText(String.format("X%d", ((UpgradeAble)heroSkills[3]).getLevel()));
            }else{
                fourth.setText(Resource.getString(R.string.empty));
            }
        } else {
            fourth.setBackgroundResource(0);
            if (hero.getReincarnate() >= 2) {
                fourth.setText(R.string.not_mount);
                fourth.setEnabled(true);
            } else {
                fourth.setText(R.string.fourth_skill_enable);
               fourth.setEnabled(false);
            }
        }
        TextView fifth = (TextView) context.findViewById(R.id.fifit_skill);
        if (heroSkills.length > 4 && heroSkills[4] != null && !(heroSkills[4] instanceof EmptySkill)) {
            fifth.setEnabled(true);
            fifth.setBackground(Resource.getSkillDrawable(heroSkills[4]));
            if(heroSkills[4] instanceof UpgradeAble){
                fifth.setText(String.format("X%d", ((UpgradeAble)heroSkills[4]).getLevel()));
            }else{
                fifth.setText(Resource.getString(R.string.empty));
            }
        } else {
            fifth.setBackgroundResource(0);
            if (hero.getReincarnate() >= 4) {
                fifth.setText(R.string.not_mount);
                fifth.setEnabled(true);
            } else {
                fifth.setText(R.string.fifth_skill_enable);
                fifth.setEnabled(false);
            }
        }
        TextView sixth = (TextView) context.findViewById(R.id.sixth_skill);
        if (heroSkills.length > 5 && heroSkills[5] != null && !(heroSkills[5] instanceof EmptySkill)) {
            sixth.setEnabled(true);
            sixth.setBackground(Resource.getSkillDrawable(heroSkills[5]));
            if(heroSkills[5] instanceof UpgradeAble){
                sixth.setText(String.format("X%d", ((UpgradeAble)heroSkills[5]).getLevel()));
            }else{
                sixth.setText(Resource.getString(R.string.empty));
            }
        } else {
            sixth.setBackgroundResource(0);
            if (hero.getReincarnate() >= 8) {
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
            ((TextView) context.findViewById(R.id.hero_level)).setText(StringUtils.formatNumber(context.control.getMaze().getLevel()));
            ((TextView) context.findViewById(R.id.hero_level_max)).setText(StringUtils.formatNumber(context.control.getMaze().getMaxLevel()));
            ((TextView) context.findViewById(R.id.hero_mate)).setText(StringUtils.formatNumber(context.control.getHero().getMaterial()));
            ((TextView) context.findViewById(R.id.hero_point)).setText(StringUtils.formatNumber(context.control.getHero().getPoint()));
            ((TextView) context.findViewById(R.id.hero_click)).setText(StringUtils.formatNumber(context.control.getHero().getClick()));
            ((TextView) context.findViewById(R.id.hero_hp)).setText(StringUtils.formatNumber(context.control.getHero().getCurrentHp()));
            ((TextView) context.findViewById(R.id.hero_max_hp)).setText(StringUtils.formatNumber(context.control.getHero().getUpperHp()));

        }
    };
    private Runnable refreshPetTask = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            ArrayList<Pet> pets = new ArrayList<>(hero.getPets());
            ImageView first = (ImageView)context.findViewById(R.id.pet_1);
            ImageView second = (ImageView)context.findViewById(R.id.pet_2);
            ImageView third = (ImageView)context.findViewById(R.id.pet_3);
            if(pets.size() > 0){
                Pet pet = pets.get(0);
                updatePetView(pet, first);
            }else{
                first.setImageResource(0);
            }
            if(pets.size() > 1){
                Pet pet = pets.get(1);
                updatePetView(pet, second);
            }else{
                second.setImageResource(0);
            }
            if(pets.size() > 2){
                Pet pet = pets.get(2);
                updatePetView(pet, third);
            }else{
                third.setImageResource(0);
            }
            ((Button)context.findViewById(R.id.more_pet)).setText(String.format("%d+",pets.size()));
        }
    };

    private void updatePetView(Pet pet, ImageView petView) {
        if(pet!=null){
            boolean notChange = false;
            Pet vp = (Pet)petView.getTag(R.string.item);
            Drawable drawable;
            if(vp == null || !vp.getId().equals(pet.getId())) {
                drawable = PetMonsterLoder.loadMonsterImage(pet.getIndex());
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
                if (pet.getCurrentHp() > 0) {
                    drawable.clearColorFilter();
                } else {
                    drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
                petView.setImageDrawable(drawable);
            }

        }
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
                });
            }
        });
    }
}

