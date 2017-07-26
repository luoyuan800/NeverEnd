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
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.GameActivity;
import cn.luo.yuan.maze.client.display.dialog.GiftDialog;
import cn.luo.yuan.maze.client.display.view.PetTextView;
import cn.luo.yuan.maze.client.display.view.RevealTextView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.PetMonsterLoder;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class GameActivityViewHandler extends Handler {
    private GameActivity context;
    private NeverEnd neverEnd;
    private Runnable refreshClickSkillTask = new Runnable() {
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
            ((TextView) context.findViewById(R.id.hero_max_hp)).setText(StringUtils.formatNumber(context.control.getHero().getCurrentMaxHp()));

        }
    };
    private Runnable refreshPetTask = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            ArrayList<Pet> pets = new ArrayList<>(hero.getPets());
            if(pets.size() > 0){
                Pet pet = pets.get(0);
                ImageView petView = (ImageView)context.findViewById(R.id.pet_1);
                updatePetView(pet, petView);
            }
            if(pets.size() > 1){
                Pet pet = pets.get(1);
                ImageView petView = (ImageView)context.findViewById(R.id.pet_2);
                updatePetView(pet, petView);
            }
            if(pets.size() > 3){
                Pet pet = pets.get(2);
                ImageView petView = (ImageView)context.findViewById(R.id.pet_3);
                updatePetView(pet, petView);
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

    public void refreshClickSkill() {
        post(refreshClickSkillTask);
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

