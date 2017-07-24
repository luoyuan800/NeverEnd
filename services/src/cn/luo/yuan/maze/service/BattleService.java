package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.listener.BattleEndListener;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Group;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.PetOwner;
import cn.luo.yuan.maze.model.SilentAbleObject;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.model.skill.result.DonothingResult;
import cn.luo.yuan.maze.model.skill.result.EndBattleResult;
import cn.luo.yuan.maze.model.skill.result.HarmResult;
import cn.luo.yuan.maze.model.skill.result.HasMessageResult;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn;
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
    private RunningServiceInterface runninfService;

    public BattleService(HarmAble hero, HarmAble monster, Random random, RunningServiceInterface runningService) {
        this.monster = monster;
        this.random = random;
        this.hero = hero;
        this.runninfService = runningService;
    }

    public boolean battle(long level) {
        long round = 1;
        boolean heroAtk = random.nextBoolean();
        battleMessage.startBattle(hero instanceof NameObject ? ((NameObject) hero).getDisplayName() : "", monster instanceof NameObject ? ((NameObject) monster).getDisplayName() : "");
        while (hero.getCurrentHp() > 0 && monster.getCurrentHp() > 0) {
            if (runninfService != null && runninfService.isPause()) {
                continue;
            }
            if (round > 60 && round % 61 == 0) {
                battleMessage.battleTooLong();
                hero.setHp(hero.getHp() / 2);
                monster.setHp(monster.getHp() / 2);
            }
            if (hero instanceof Group) {
                ((Group) hero).roll(random);
            }
            if (monster instanceof Group) {
                ((Group) monster).roll(random);
            }
            if (heroAtk) {
                atk(hero, monster, level);
            } else {
                atk(monster, hero, level);
            }
            if (hero instanceof Group) {
                ((Group) hero).reset();
            }
            if (monster instanceof Group) {
                ((Group) monster).reset();
            }
            heroAtk = !heroAtk;
            round++;
            try {
                Thread.sleep(Data.REFRESH_SPEED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        if (monster.getCurrentHp() <= 0) {
            if (hero instanceof NameObject && monster instanceof NameObject) {
                battleMessage.win((NameObject) hero, (NameObject) monster);
            }
            if (hero instanceof Hero) {
                for (BattleEndListener endListener : battleEndListeners.values()) {
                    endListener.end((Hero) hero, monster);
                }
            }
            return true;
        } else {
            if (hero instanceof NameObject && monster instanceof NameObject) {
                battleMessage.lost((NameObject) hero, (NameObject) monster);
            }
            if (hero instanceof Hero) {
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
        if (atker instanceof PetOwner) {
            petActionOnAtk((PetOwner) atker, defender);
        }
        if (defender instanceof PetOwner) {
            if (petActionOnDef((PetOwner) defender, atker)) {
                return;
            }
        }
        if (atker instanceof SkillAbleObject) {
            if (releaseSkill((SkillAbleObject) atker, defender, random, minHarm, true)) {
                return;
            }
        }
        if (defender instanceof SkillAbleObject) {
            if (releaseSkill((SkillAbleObject) defender, atker, random, minHarm, false)) {
                return;
            }
        }

        if (!defender.isDodge(random)) {
            long harm = BattleServiceBase.getHarm(atker, defender, minHarm, random, battleMessage);
            defender.setHp(defender.getHp() - harm);
            if (atker instanceof NameObject && defender instanceof NameObject)
                battleMessage.harm((NameObject) atker, (NameObject) defender, harm);
        } else {
            if (atker instanceof NameObject) {
                battleMessage.dodge((NameObject) atker, (NameObject) defender);
            }
        }
    }


    private void petActionOnAtk(PetOwner hero, HarmAble monster) {
        for (Pet pet : hero.getPets()) {
            if (isPetWork(pet, random, true)) {
                if (monster instanceof Monster && ((Monster) monster).getRank() > pet.getRank() && random.nextInt(5) < 1) {
                    battleMessage.petSuppress(pet, (NameObject) monster);
                } else {
                    long harm = pet.getAtk() - monster.getDef();
                    if (harm > 0) {
                        monster.setHp(monster.getHp() - harm);
                        if (hero instanceof NameObject && monster instanceof NameObject) {
                            battleMessage.harm(pet, (NameObject) monster, harm);
                        }
                    }
                }
            }
        }
    }

    private boolean petActionOnDef(PetOwner hero, HarmAble monster) {
        for (Pet pet : hero.getPets()) {
            if (isPetWork(pet, random, true)) {
                if (monster instanceof Monster && ((Monster) monster).getRank() > pet.getRank() && random.nextInt(5) < 1) {
                    battleMessage.petSuppress(pet, (NameObject) monster);
                } else {
                    long harm = monster.getAtk() - pet.getDef();
                    if (harm > 0) {
                        monster.setHp(monster.getHp() - harm);
                        battleMessage.petDefend(pet);
                        if (monster instanceof NameObject) {
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

    private boolean releaseSkill(SkillAbleObject owner, HarmAble target, Random random, long level, boolean atk) {
        Skill skill = null;
        SkillParameter skillPara;
        if (atk) {
            skillPara = getSkillParameter(owner, (HarmAble) owner, target, random, level);
            for (Skill s : owner.getSkills()) {
                if (s instanceof AtkSkill && ((AtkSkill) s).invokeAble(skillPara)) {
                    skill = s;
                    break;
                }
            }
        } else {
            skillPara = getSkillParameter(owner, target, (HarmAble) owner, random, level);

            for (Skill s : ((SkillAbleObject) target).getSkills()) {
                if (s instanceof DefSkill && ((DefSkill) s).invokeAble(skillPara)) {
                    skill = s;
                    break;
                }
            }
        }
        if (skill != null) {
            if (target instanceof SilentAbleObject) {
                boolean silent = random.nextInt(100) + random.nextFloat() < ((SilentAbleObject) target).getSilent();
                if (silent) {
                    battleMessage.silent(owner, target, skill);
                    return true;
                }
            }
            SkillResult result = skill.invoke(skillPara);
            battleMessage.releaseSkill((HarmAble) owner, skill);
            if (result instanceof HasMessageResult) {
                for (String msg : result.getMessages()) {
                    battleMessage.rowMessage(msg);
                }
            }
            if (result instanceof DonothingResult) {
                return false;
            }
            if (result instanceof EndBattleResult) {
                return true;
            }
            if (result instanceof SkipThisTurn) {
                return true;
            }
            if (result instanceof HarmResult) {
                if (owner instanceof NameObject && target instanceof NameObject) {
                    if (((HarmResult) result).isBack()) {
                        ((HarmAble) owner).setHp(((HarmAble) owner).getHp() - ((HarmResult) result).getHarm());
                        battleMessage.harm((NameObject) target, (NameObject) owner, ((HarmResult) result).getHarm());
                    } else {
                        target.setHp(target.getHp() - ((HarmResult) result).getHarm());
                        battleMessage.harm((NameObject) owner, (NameObject) target, ((HarmResult) result).getHarm());
                    }
                }
            }
            return true;
        }

        return false;
    }

    private SkillParameter getSkillParameter(SkillAbleObject owner, HarmAble atker, HarmAble defender, Random random, long level) {
        SkillParameter atkPara = new SkillParameter(owner);
        atkPara.set(SkillParameter.ATKER, atker);
        atkPara.set(SkillParameter.RANDOM, random);
        atkPara.set(SkillParameter.TARGET, atker == owner ? defender : atker);
        atkPara.set(SkillParameter.DEFENDER, defender);
        atkPara.set(SkillParameter.MINHARM, level);
        atkPara.set(SkillParameter.MESSAGE, battleMessage);
        atkPara.set(SkillParameter.CONTEXT, runninfService.getContext());
        return atkPara;
    }
}
