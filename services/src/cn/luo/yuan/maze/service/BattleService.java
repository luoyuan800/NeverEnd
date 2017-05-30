package cn.luo.yuan.maze.service;

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
import cn.luo.yuan.maze.model.skill.result.*;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.utils.Random;

import static cn.luo.yuan.maze.service.ListenerService.battleEndListeners;

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

    public boolean battle(long level) {
        long round = 1;
        boolean heroAtk = random.nextBoolean();
        while (hero.getHp() > 0 && monster.getHp() > 0) {
            if (round > 60 && round % 61 == 0) {
                battleMessage.battleTooLong();
                hero.setHp(hero.getHp() / 2);
                monster.setHp(monster.getHp() / 2);
            }
            if (heroAtk) {
                atk(hero, monster, level);
            } else {
                atk(monster, hero, level);
            }
            heroAtk = !heroAtk;
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

    public void setBattleMessage(BattleMessage battleMessage) {
        this.battleMessage = battleMessage;
    }

    private void atk(HarmAble atker, HarmAble defender, long minHarm) {
        if(atker instanceof PetOwner) {
            petActionOnAtk((PetOwner) atker, defender);
        }
        if(defender instanceof PetOwner) {
            if (petActionOnDef((PetOwner) defender, atker)) {
                return;
            }
        }
        if(atker instanceof SkillAbleObject) {
            if (releaseSkill((SkillAbleObject)atker, defender, random)) {
                return;
            }
        }
        if(defender instanceof SkillAbleObject){
            if(releaseSkill((SkillAbleObject) defender, atker, random)){
                return;
            }
        }

        boolean isDodge = false;
        if(defender instanceof Hero) {
            isDodge = random.nextLong(100) + ((Hero)defender).getAgi() * Data.DODGE_AGI_RATE > 97 + random.nextInt(100) + random.nextLong((long) (((Hero)defender).getStr() * Data.DODGE_STR_RATE));
        }
        if(!isDodge) {
            long atk = atker instanceof Hero ? ((Hero) atker).getUpperAtk() : atker.getAtk();
            atk = atk / 3;
            atk = atk * 2 + random.nextLong(atk);
            boolean isHit = random.nextLong(100) + (atker instanceof Hero ? ((Hero) atker).getStr() : 0) * Data.HIT_STR_RATE > 97
                    + random.nextInt(100) +
                    random.nextLong((long) ((atker instanceof Hero ? ((Hero) atker).getAgi() : 0) * Data.HIT_AGI_RATE));
            if (isHit) {
                if (atker instanceof NameObject)
                    battleMessage.hit((NameObject) atker);
                atk *= 2;//暴击有效攻击力翻倍
            }
            boolean isParry = false;
            if(defender instanceof Hero) {
                isParry = random.nextLong(100) + ((Hero)defender).getStr() * Data.PARRY_STR_RATE > 97 + random.nextInt(100) + random.nextLong((long) (((Hero)defender).getAgi() * Data.PARRY_AGI_RATE));
            }
            long defend = defender instanceof Hero ? ((Hero) defender).getUpperDef() : defender.getDef();
            defend = defend / 2;
            defend = defend + random.nextLong(defend);
            if (isParry) {
                battleMessage.parry((NameObject)defender);
                //格挡，生效防御力三倍
                defend *= 3;
            }
            long harm = atk - defend;
            if (harm <= 0) {
                harm = random.nextLong(minHarm);
            }
            harm = elementAffectHarm(atker.getElement(), defender.getElement(), harm);
            defender.setHp(defender.getHp() - harm);
            if (atker instanceof NameObject && defender instanceof NameObject)
                battleMessage.harm((NameObject) atker, (NameObject) defender, harm);
        }else{
            if(atker instanceof NameObject) {
                battleMessage.dodge((NameObject) atker, (NameObject) defender);
            }
        }
    }

    private long elementAffectHarm(Element atker, Element defer, long baseHarm) {
        if (atker.restriction(defer) || (defer == Element.NONE && atker != Element.NONE)) {
            baseHarm *= 1.5;
        } else if (defer.restriction(atker) || (atker == Element.NONE && defer != Element.NONE)) {
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
                    battleMessage.silent(atker,target, atkSkill);
                    return true;
                }
            }
            if (target instanceof SkillAbleObject) {
                DefSkill defSkill = null;
                SkillParameter defPara = new SkillParameter(atker);
                defPara.set("random", random);
                defPara.set("target", atker);
                for (Skill skill : ((SkillAbleObject) target).getSkills()) {
                    if (skill instanceof DefSkill && ((DefSkill) skill).invokeAble(defPara)) {
                        defSkill = (DefSkill) skill;
                        break;
                    }
                }
                if(defSkill!=null){
                    SkillResult result = defSkill.invoke(defPara);
                    battleMessage.releaseSkill(target,defSkill );
                    if(result instanceof HasMessageResult){
                        for(String msg : result.getMessages()){
                            battleMessage.rowMessage(msg);
                        }
                    }
                }
            }
            SkillResult result = atkSkill.invoke(atkPara);
            battleMessage.releaseSkill((HarmAble)atker, atkSkill);
            if(result instanceof HasMessageResult){
                for(String msg : result.getMessages()){
                    battleMessage.rowMessage(msg);
                }
            }
            if(result instanceof EndBattleResult){
                return true;
            }
            if(result instanceof SkipThisTurn){
                return true;
            }
            if(result instanceof HarmResult){
                if(atker instanceof NameObject && target instanceof NameObject) {
                    target.setHp(target.getHp() - ((HarmResult) result).getHarm());
                    battleMessage.harm((NameObject) atker, (NameObject) target, ((HarmResult) result).getHarm());
                }
            }
            return true;
        }
        return false;
    }
}
