package cn.luo.yuan.maze.service;

import android.content.Context;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/4/2.
 */
public class BattleService {
    private InfoControl control;
    private Monster monster;
    private Context context;

    public BattleService(InfoControl control, Monster monster){
        this.control = control;
        this.monster = monster;
        this.context = control.getContext();
    }

    public boolean battle(){
        Hero hero = control.getHero();
        Random random = control.getRandom();
        long round = 1;
        boolean heroAtk = random.nextBoolean();
        while (hero.getHp() > 0 && monster.getHp() > 0){
            if(round > 60 && round%61 == 0){
                control.addMessage(context.getResources().getString(R.string.battle_to_long));
                hero.setHp(hero.getHp()/2);
                monster.setHp(monster.getHp()/2);
            }
            if(heroAtk){
                heroAtk(hero, monster, random);
            }else {
                heroDefend(hero, monster, random);
            }
            round ++;
        }
        if(monster.getHp() <= 0){
            control.addMessage(String.format(context.getString(R.string.win_msg), hero.getDisplayName(), monster.getDisplayName()));
            return true;
        }else{
            control.addMessage(String.format(context.getString(R.string.lost_msg), hero.getDisplayName(), monster.getDisplayName()));
            return false;
        }
    }

    private void heroAtk(Hero hero, Monster monster, Random random) {
        long atk = hero.getUpperAtk();
        atk = atk/3;
        atk = atk * 2 + random.nextLong(atk);
        boolean isHit = random.nextLong(100) + hero.getStr() * Data.HIT_STR_RATE > 97 + random.nextInt(1000) + random.nextLong((long)(hero.getAgi() * Data.HIT_AGI_RATE));
        if(isHit){
            control.addMessage(String.format(context.getString(R.string.hit_happend), hero.getDisplayName()));
            atk *= 2;//暴击有效攻击力翻倍
        }
        long harm = atk - monster.getDef();
        if(harm <= 0){
            harm = random.nextLong(control.getMaze().getLevel());
        }
        harm = elementAffectHarm(hero.getElement(), monster.getElement(), harm);
        monster.setHp(monster.getHp() - harm);
        control.addMessage(String.format(context.getResources().getString(R.string.atk_harm_color_msg), hero.getDisplayName(), monster.getDisplayName(), StringUtils.formatNumber(harm)));
    }

    private void heroDefend(Hero hero, Monster monster, Random random) {
        boolean isDodge = random.nextLong(100) + hero.getAgi() * Data.DODGE_AGI_RATE > 97 + random.nextInt(1000) + random.nextLong((long)(hero.getStr() * Data.DODGE_STR_RATE));
        boolean isParry = false;
        if (isDodge) {
            control.addMessage(String.format(context.getString(R.string.doge_happend),hero.getDisplayName(), monster.getDisplayName()));
        } else {
            isParry = random.nextLong(100) + hero.getStr() * Data.PARRY_STR_RATE > 97 + random.nextInt(1000) + random.nextLong((long)(hero.getAgi() * Data.PARRY_AGI_RATE));
            long defend = hero.getUpperDef();
            defend = defend/2;
            defend = defend + random.nextLong(defend);
            if (isParry) {
                control.addMessage(String.format(context.getString(R.string.parry_happend),hero.getDisplayName()));
                //格挡，生效防御力三倍
                defend *= 3;
            }
            long harm = monster.getAtk() - defend;
            if(harm <= 0){
                harm = random.nextLong(control.getMaze().getLevel());
            }
            harm = elementAffectHarm(monster.getElement(),hero.getElement(),harm);
            hero.setHp(hero.getHpGrow()-harm);
            control.addMessage(String.format(context.getResources().getString(R.string.atk_harm_color_msg),
                    monster.getDisplayName(),hero.getDisplayName(),StringUtils.formatNumber(harm)));
        }
    }

    private long elementAffectHarm(Element atker, Element defer, long baseHarm){
        if(atker.restriction(defer)){
            baseHarm *= 1.5;
        }else if(defer.restriction(atker)){
            baseHarm *= 0.5;
        }
        return baseHarm;
    }
}
