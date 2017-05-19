package cn.luo.yuan.maze.service;

import android.content.Context;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.listener.BattleEndListener;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.PetOwner;
import cn.luo.yuan.maze.model.SilentAbleObject;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.EndBattleResult;
import cn.luo.yuan.maze.model.skill.HarmResult;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.model.skill.SkillResult;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import static cn.luo.yuan.maze.service.ListenerService.battleEndListeners;
import static java.awt.SystemColor.control;

/**
 * Created by luoyuan on 2017/4/2.
 */
public class BattleService {
    private HarmAble monster;
    private HarmAble hero;
    private Random random;
    private BattleMessage battleMessage;

    public BattleService(HarmAble hero, HarmAble monster, Random random) {
        this.monster = monster;
        this.random = random;
        this.hero = hero;
    }

    public boolean battle() {
        long round = 1;
        boolean heroAtk = random.nextBoolean();
        while (hero.getHp() > 0 && monster.getHp() > 0) {
            if (round > 60 && round % 61 == 0) {
                battleMessage.battleTooLong();
                hero.setHp(hero.getHp() / 2);
                monster.setHp(monster.getHp() / 2);
            }
            if (heroAtk) {
                atk();
            } else {
                heroDefend();
            }
            round++;
        }
        if (monster.getHp() <= 0) {
            if(hero instanceof NameObject && monster instanceof NameObject){
                battleMessage.win((NameObject)hero, (NameObject)monster);
            }
            if(hero instanceof Hero) {
                for (BattleEndListener endListener : battleEndListeners.values()) {
                    endListener.end((Hero) hero, monster);
                }
            }
            return true;
        } else {
            if(hero instanceof NameObject && monster instanceof NameObject){
                battleMessage.lost((NameObject)hero, (NameObject)monster);
            }
            if(hero instanceof Hero) {
                for (BattleEndListener endListener : battleEndListeners.values()) {
                    endListener.end((Hero) hero, monster);
                }
            }
            return false;
        }
    }

    private void atk(HarmAble hero, HarmAble monster) {
        if(hero instanceof PetOwner) {
            petActionOnAtk((PetOwner) hero, monster);
        }
        if(monster instanceof PetOwner) {
            if (petActionOnDef((PetOwner) monster, hero)) {
                return;
            }
        }
        if(hero instanceof SkillAbleObject) {
            if (releaseSkill((SkillAbleObject)hero, monster, random)) {
                return;
            }
        }
        if(monster instanceof SkillAbleObject){
            if(releaseSkill((SkillAbleObject) monster, hero, random)){
                return;
            }
        }

        boolean isDodge = false;
        if(monster instanceof Hero) {
            isDodge = random.nextLong(100) + ((Hero)monster).getAgi() * Data.DODGE_AGI_RATE > 97 + random.nextInt(1000) + random.nextLong((long) (((Hero)monster).getStr() * Data.DODGE_STR_RATE));
        }
        if(!isDodge) {
            long atk = hero instanceof Hero ? ((Hero) hero).getUpperAtk() : hero.getAtk();
            atk = atk / 3;
            atk = atk * 2 + random.nextLong(atk);
            boolean isHit = random.nextLong(100) + (hero instanceof Hero ? ((Hero) hero).getStr() : 0) * Data.HIT_STR_RATE > 97
                    + random.nextInt(1000) +
                    random.nextLong((long) ((hero instanceof Hero ? ((Hero) hero).getAgi() : 0) * Data.HIT_AGI_RATE));
            if (isHit) {
                if (hero instanceof NameObject)
                    battleMessage.hit((NameObject) hero);
                atk *= 2;//暴击有效攻击力翻倍
            }
            boolean isParry = false;
            if(monster instanceof Hero) {
                isParry = random.nextLong(100) + ((Hero)monster).getStr() * Data.PARRY_STR_RATE > 97 + random.nextInt(1000) + random.nextLong((long) (((Hero)monster).getAgi() * Data.PARRY_AGI_RATE));
            }
            long defend = monster instanceof Hero ? ((Hero) monster).getUpperAtk() : monster.getAtk();
            defend = defend / 2;
            defend = defend + random.nextLong(defend);
            if (isParry) {
                //格挡，生效防御力三倍
                defend *= 3;
            }
            long harm = atk - defend;
            if (harm <= 0) {
                harm = 1;
            }
            harm = elementAffectHarm(hero.getElement(), monster.getElement(), harm);
            monster.setHp(monster.getHp() - harm);
            if (hero instanceof NameObject && monster instanceof NameObject)
                battleMessage.harm((NameObject) hero, (NameObject) monster, harm);
        }else{
            if(hero instanceof NameObject) {
                battleMessage.dodge((NameObject) hero, (NameObject) monster);
            }
        }
    }

    private void heroDefend() {
        if (petActionOnDef()) {
            return;
        }

        boolean isDodge = random.nextLong(100) + hero.getAgi() * Data.DODGE_AGI_RATE > 97 + random.nextInt(1000) + random.nextLong((long) (hero.getStr() * Data.DODGE_STR_RATE));
        boolean isParry = false;
        if (isDodge) {
            control.addMessage(String.format(context.getString(R.string.doge_happen), hero.getDisplayName(), monster.getDisplayName()));
        } else {
            isParry = random.nextLong(100) + hero.getStr() * Data.PARRY_STR_RATE > 97 + random.nextInt(1000) + random.nextLong((long) (hero.getAgi() * Data.PARRY_AGI_RATE));
            long defend = hero.getUpperDef();
            defend = defend / 2;
            defend = defend + random.nextLong(defend);
            if (isParry) {
                control.addMessage(String.format(context.getString(R.string.parry_happen), hero.getDisplayName()));
                //格挡，生效防御力三倍
                defend *= 3;
            }
            long harm = monster.getAtk() - defend;
            if (harm <= 0) {
                harm = random.nextLong(control.getMaze().getLevel());
            }
            harm = elementAffectHarm(monster.getElement(), hero.getElement(), harm);
            hero.setHp(hero.getHpGrow() - harm);
            control.addMessage(String.format(context.getResources().getString(R.string.atk_harm_color_msg),
                    monster.getDisplayName(), hero.getDisplayName(), StringUtils.formatNumber(harm)));
        }
    }

    private long elementAffectHarm(Element atker, Element defer, long baseHarm) {
        if (atker.restriction(defer)) {
            baseHarm *= 1.5;
        } else if (defer.restriction(atker)) {
            baseHarm *= 0.5;
        }
        return baseHarm;
    }

    private void petActionOnAtk(PetOwner hero, HarmAble monster) {
        for (Pet pet : hero.getPets()) {
            if (isPetWork(pet, random, true)) {
                if (monster instanceof Monster && ((Monster)monster).getIndex() > pet.getIndex() && random.nextInt(5) < 1) {
                   battleMessage.petSuppress(pet, (NameObject) monster);
                } else {
                    long harm = pet.getAtk() - monster.getDef();
                    if (harm > 0) {
                        monster.setHp(monster.getHp() - harm);
                        if(hero instanceof NameObject && monster instanceof NameObject){
                            battleMessage.harm((NameObject)hero, (NameObject)monster, harm);
                        }
                    }
                }
            }
        }
    }

    private boolean petActionOnDef(PetOwner hero, HarmAble monster) {
        for (Pet pet : hero.getPets()) {
            if (isPetWork(pet, random, true)) {
                if (monster instanceof Monster && ((Monster)monster).getIndex() > pet.getIndex() && random.nextInt(5) < 1) {
                    battleMessage.petSuppress(pet, (NameObject) monster);
                } else {
                    long harm = monster.getAtk() - pet.getDef();
                    if (harm > 0) {
                        monster.setHp(monster.getHp() - harm);
                        battleMessage.petDefend(pet);
                        if(monster instanceof NameObject) {
                            battleMessage.harm(pet, (NameObject) monster, harm);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPetWork(Pet pet, Random random, boolean addition) {
        if (addition) {
            return pet.getHp() > 0 && 10 + random.nextInt(100) < random.nextLong(pet.getIntimacy());
        }
        return pet.getHp() > 0 && 100 + random.nextInt(100) < random.nextLong(pet.getIntimacy());
    }

    private boolean releaseSkill(SkillAbleObject atker, HarmAble target, Random random) {
        AtkSkill atkSkill = null;
        SkillParameter atkPara = new SkillParameter(atker);
        atkPara.set("random", random);
        atkPara.set("target", target);
        for (Skill skill : atker.getSkills()) {
            if (skill instanceof AtkSkill && ((AtkSkill) skill).invokeAble(atkPara)) {
                atkSkill = (AtkSkill) skill;
                break;
            }
        }
        if (atkSkill != null) {
            if (target instanceof SilentAbleObject) {
                boolean silent = random.nextInt(100) + random.nextFloat() < ((SilentAbleObject) target).getSilent();
                if (silent) {
                    control.addMessage(String.format(Resource.getString(R.string.silent_skill),
                            (atker instanceof NameObject ? ((NameObject) atker).getDisplayName() : ""),
                            atkSkill.getName(),
                            (target instanceof NameObject ? ((NameObject) target).getDisplayName() : "")));
                    return true;
                }
            }
            if (target instanceof SkillAbleObject) {
                DefSkill defSkill = null;
                SkillParameter defPara = new SkillParameter(atker);
                defPara.set("random", random);
                defPara.set("target", atker);
                for (Skill skill : atker.getSkills()) {
                    if (skill instanceof DefSkill && ((DefSkill) skill).invokeAble(defPara)) {
                        defSkill = (DefSkill) skill;
                        break;
                    }
                }
                if(defSkill!=null){
                    SkillResult result = defSkill.invoke(defPara);
                    control.addMessage(String.format(Resource.getString(R.string.release_skill), target instanceof NameObject ? ((NameObject) target).getDisplayName() : "", defSkill.getDisplayName()));
                }
            }
            SkillResult result = atkSkill.invoke(atkPara);
            control.addMessage(String.format(Resource.getString(R.string.release_skill), atker instanceof NameObject ? ((NameObject) atker).getDisplayName() : "", atkSkill.getDisplayName()));
            if(result instanceof EndBattleResult){
                return true;
            }
            if(result instanceof HarmResult){
                control.addMessage(String.format(Resource.getString(R.string.atk_harm_color_msg),
                        (atker instanceof NameObject ? ((NameObject) atker).getDisplayName() : ""),
                        (target instanceof NameObject ? ((NameObject) target).getDisplayName() : ""),
                        StringUtils.formatNumber(((HarmResult) result).getHarm())
                        ));
            }
            return true;
        }
        return false;
    }
}
