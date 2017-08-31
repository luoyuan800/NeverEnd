package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.listener.BattleEndListener;
import cn.luo.yuan.maze.listener.HarmListener;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.model.skill.result.*;
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
        specialDetect();
        long round = 1;
        boolean heroAtk = random.nextBoolean();
        battleMessage.startBattle(hero instanceof NameObject ? (((NameObject) hero).getDisplayName() + "(HP: " + hero.getCurrentHp() + ")") : "",
                monster instanceof NameObject ? (((NameObject) monster).getDisplayName() + "(HP: " + monster.getCurrentHp() + ")") : "");
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
                if(runninfService!=null && runninfService.getContext()!=null) {
                    for (HarmListener listener : ListenerService.harmListeners.values()) {
                        listener.har(hero, monster, runninfService.getContext());
                    }
                }
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

    private void specialDetect() {
        if(runninfService!=null){
            if(runninfService.getContext()!=null){
                if(monster instanceof NameObject){
                    if(((NameObject) monster).getName().contains("龙")){
                        if(runninfService.getContext().getDataManager()!=null) {
                            NeverEndConfig config = runninfService.getContext().getDataManager().loadConfig();
                            if (config!=null && config.isLongKiller()) {
                                battleMessage.rowMessage("龙的传人！");
                                monster.setHp(0);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setBattleMessage(BattleMessage battleMessage) {
        this.battleMessage = battleMessage;
    }

    private void atk(HarmAble atker, HarmAble defender, long minHarm) {
        if(atker instanceof NameObject) {
            battleMessage.myTurn(((NameObject) atker).getDisplayName());
        }
        if (atker instanceof PetOwner) {
            petActionOnAtk((PetOwner) atker, defender);
        }
        if (defender instanceof PetOwner) {
            if (petActionOnDef((PetOwner) defender, atker)) {
                return;
            }
        }
        if (atker instanceof SkillAbleObject) {
            if (releaseSkill(atker, defender, random, minHarm, true)) {
                return;
            }
        }
        if (defender instanceof SkillAbleObject) {
            if (releaseSkill(atker, defender, random, minHarm, false)) {
                return;
            }
        }

        normalAtk(atker, defender, minHarm);
    }

    public long normalAtk(HarmAble atker, HarmAble defender, long minHarm) {
        long harm = 0;
        if (!defender.isDodge(random)) {
            harm = BattleServiceBase.getHarm(atker, defender, minHarm, random, battleMessage);
            defender.setHp(defender.getHp() - harm);
            if (atker instanceof NameObject && defender instanceof NameObject)
                battleMessage.harm((NameObject) atker, (NameObject) defender, harm);
        } else {
            if (atker instanceof NameObject) {
                battleMessage.dodge((NameObject) atker, (NameObject) defender);
            }
        }
        return harm;
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
                    long harm = BattleServiceBase.getHarm(monster,pet, 1,random,battleMessage);
                    if (harm > 0) {
                        pet.setHp(pet.getHp() - harm);
                        battleMessage.petDefend(pet);
                        if (monster instanceof NameObject) {
                            battleMessage.harm((NameObject) monster, pet,  harm);
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

    private boolean releaseSkill(HarmAble atker, HarmAble defender, Random random, long level, boolean atk) {
        Skill skill = null;
        SkillParameter skillPara;
        if (atk) {
            skillPara = getSkillParameter((SkillAbleObject) atker, (HarmAble) atker, defender, random, level);
            for (Skill s : ((SkillAbleObject) atker).getSkills()) {
                if (s instanceof AtkSkill && ((AtkSkill) s).invokeAble(skillPara)) {
                    skill = s;
                    break;
                }
            }
        } else {
            skillPara = getSkillParameter((SkillAbleObject) defender, atker, (HarmAble) defender, random, level);

            for (Skill s : ((SkillAbleObject) defender).getSkills()) {
                if (s instanceof DefSkill && ((DefSkill) s).invokeAble(skillPara)) {
                    skill = s;
                    break;
                }
            }
        }
        if (skill != null) {
            if (atk) {
                if (defender instanceof SilentAbleObject) {
                    boolean silent = random.nextInt(100) + random.nextFloat() < ((SilentAbleObject) defender).getSilent();
                    if (silent) {
                        battleMessage.silent((SkillAbleObject) atker, defender, skill);
                        return true;
                    }
                }
            }else {
                if (atker instanceof SilentAbleObject) {
                    boolean silent = random.nextInt(100) + random.nextFloat() < ((SilentAbleObject) atker).getSilent();
                    if (silent) {
                        battleMessage.silent((SkillAbleObject) defender, atker, skill);
                        return false;
                    }
                }
            }
            SkillResult result = skill.invoke(skillPara);
            if (atk) {
                battleMessage.releaseSkill(atker, skill);
            } else {
                battleMessage.releaseSkill(defender, skill);
            }
            if (result instanceof HasMessageResult) {
                for (String msg : result.getMessages()) {
                    battleMessage.rowMessage(msg);
                }
            }
            if (result instanceof DoNoThingResult) {
                return false;
            }
            if (result instanceof EndBattleResult) {
                return true;
            }
            if (result instanceof SkipThisTurn) {
                return true;
            }
            if (result instanceof HarmResult) {
                if (atker instanceof NameObject && defender instanceof NameObject) {
                    if (((HarmResult) result).isBack()) {
                        ((HarmAble) atker).setHp(((HarmAble) atker).getHp() - ((HarmResult) result).getHarm());
                        battleMessage.harm((NameObject) defender, (NameObject) atker, ((HarmResult) result).getHarm());
                    } else {
                        defender.setHp(defender.getHp() - ((HarmResult) result).getHarm());
                        battleMessage.harm((NameObject) atker, (NameObject) defender, ((HarmResult) result).getHarm());
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
        if(runninfService!=null) {
            atkPara.set(SkillParameter.CONTEXT, runninfService.getContext());
        }
        return atkPara;
    }
}
