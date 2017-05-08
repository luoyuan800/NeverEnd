package cn.luo.yuan.maze.service;

import android.content.Context;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.listener.BattleEndListener;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import static cn.luo.yuan.maze.service.ListenerService.battleEndListeners;

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
            for(BattleEndListener endListener : battleEndListeners.values()){
                endListener.end(hero, monster);
            }
            return true;
        }else{
            control.addMessage(String.format(context.getString(R.string.lost_msg), hero.getDisplayName(), monster.getDisplayName()));
            for(BattleEndListener endListener : battleEndListeners.values()){
                endListener.end(hero, monster);
            }
            return false;
        }
    }

    private void heroAtk(Hero hero, Monster monster, Random random) {
        petActionOnAtk(hero, monster, random);
        long atk = hero.getUpperAtk();
        atk = atk/3;
        atk = atk * 2 + random.nextLong(atk);
        boolean isHit = random.nextLong(100) + hero.getStr() * Data.HIT_STR_RATE > 97 + random.nextInt(1000) + random.nextLong((long)(hero.getAgi() * Data.HIT_AGI_RATE));
        if(isHit){
            control.addMessage(String.format(context.getString(R.string.hit_happen), hero.getDisplayName()));
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
        if(petActionOnDef(hero, monster, random)){
            return;
        }
        boolean isDodge = random.nextLong(100) + hero.getAgi() * Data.DODGE_AGI_RATE > 97 + random.nextInt(1000) + random.nextLong((long)(hero.getStr() * Data.DODGE_STR_RATE));
        boolean isParry = false;
        if (isDodge) {
            control.addMessage(String.format(context.getString(R.string.doge_happen),hero.getDisplayName(), monster.getDisplayName()));
        } else {
            isParry = random.nextLong(100) + hero.getStr() * Data.PARRY_STR_RATE > 97 + random.nextInt(1000) + random.nextLong((long)(hero.getAgi() * Data.PARRY_AGI_RATE));
            long defend = hero.getUpperDef();
            defend = defend/2;
            defend = defend + random.nextLong(defend);
            if (isParry) {
                control.addMessage(String.format(context.getString(R.string.parry_happen),hero.getDisplayName()));
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

    private void petActionOnAtk(Hero hero, Monster monster, Random random){
        for(Pet pet : hero.getPets()){
            if(isPetWork(pet, random, true)){
                if(monster.getIndex() > pet.getIndex() && random.nextInt(5) < 1){
                    control.addMessage(String.format(context.getString(R.string.pet_index_suppression), pet.getDisplayName(), monster.getDisplayName()));
                }else{
                    long harm = pet.getAtk() - monster.getDef();
                    if(harm > 0){
                        monster.setHp(monster.getHp() - harm);
                        control.addMessage(String.format(context.getResources().getString(R.string.atk_harm_color_msg), pet.getDisplayName(), monster.getDisplayName(), StringUtils.formatNumber(harm)));
                    }
                }
            }
        }
    }

    private boolean petActionOnDef(Hero hero, Monster monster, Random random){
        for(Pet pet : hero.getPets()){
            if(isPetWork(pet, random, true)){
                if(monster.getIndex() > pet.getIndex() && random.nextInt(5) < 1){
                    control.addMessage(String.format(context.getString(R.string.pet_index_suppression), pet.getDisplayName(), monster.getDisplayName()));
                }else{
                    long harm = monster.getAtk() - pet.getDef();
                    if(harm > 0){
                        monster.setHp(monster.getHp() - harm);
                        control.addMessage(String.format(context.getString(R.string.pet_defend), pet.getDisplayName()));
                        control.addMessage(String.format(context.getResources().getString(R.string.atk_harm_color_msg), monster.getDisplayName(), pet.getDisplayName(), StringUtils.formatNumber(harm)));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPetWork(Pet pet, Random random, boolean addition){
        if(addition){
            return pet.getHp() > 0 && 10 + random.nextInt(100) < random.nextLong(pet.getIntimacy());
        }
        return pet.getHp() > 0 && 100 + random.nextInt(100) < random.nextLong(pet.getIntimacy());
    }
}
