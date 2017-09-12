package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.utils.MathUtils;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by luoyuan on 2017/5/13.
 */
public abstract class PetMonsterHelper implements PetMonsterHelperInterface, MonsterLoader {
    private Random random;
    public abstract String getLocalCatchPercent();
    public abstract String getGlobalCatchPercent();

    public Pet monsterToPet(Monster monster, Hero hero, long level) throws MonsterToPetException {
        Pet pet = new Pet();
        for (Method method : Monster.class.getMethods()) {
            if (method.getName().startsWith("get") && !method.getName().endsWith("Atk") && !method.getName().endsWith("Def") && !method.getName().endsWith("Hp")) {
                try {
                    Method set = Pet.class.getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                    set.invoke(pet, method.invoke(monster));
                }catch (Exception e){
                    //Ignore
                }

            }
        }
        pet.setId(null);
        pet.setColor(Data.DEFAULT_QUALITY_COLOR);
        pet.setMaxHp(monster.getMaxHp());
        pet.setHp(pet.getMaxHp());
        pet.setAtk(monster.getAtk());
        pet.setDef(monster.getDef());
        pet.setSecondName(null);
        pet.setFirstName(null);
        long atk_l = level * Data.MONSTER_ATK_RISE_PRE_LEVEL * (hero.getReincarnate() + 1)  + monster.getAtkAddition();
        long def_l = level * Data.MONSTER_DEF_RISE_PRE_LEVEL  * (hero.getReincarnate() + 1) ;
        long hp_l = level * Data.MONSTER_HP_RISE_PRE_LEVEL  * (hero.getReincarnate() + 1)  + monster.getHpAddition();
        pet.setAtk(pet.getAtk() - atk_l + getRandom().nextLong(getRandom().reduceToSpecialDigit(atk_l, 3)));
        pet.setDef(pet.getDef() - def_l + getRandom().nextLong(getRandom().reduceToSpecialDigit(def_l, 3)));
        pet.setMaxHp(pet.getMaxHp() - hp_l + getRandom().nextLong(getRandom().reduceToSpecialDigit(hp_l, 3)));
        pet.setHp(pet.getMaxHp());
        pet.setFirstName(monster.getFirstName());
        pet.setSecondName(monster.getSecondName());
        pet.setOwnerId(hero.getId());
        pet.setOwnerName(hero.getName());
        pet.setKeeperId(hero.getId());
        pet.setKeeperName(hero.getName());
        return pet;
    }
    public  abstract boolean isCatchAble(Monster monster, Hero hero, Random random, int petCount);
    public Monster randomMonster(long level) {
        return randomMonster(level, true);
    }

    public abstract  Monster randomMonster(long level, boolean addKey);

    public Monster randomMonster() {
        return randomMonster(99999, false);
    }


    public abstract String getDescription(int index, String type);

    public boolean upgrade(Pet major, Pet minor) {
        if(major.isDelete() || minor.isDelete()){
            return false;
        }
        int petUpgradeLimit = Data.PET_UPGRADE_LIMIT;
        if(major.getIndex() != minor.getIndex()){
            petUpgradeLimit /= 2;
        }
        if (major != minor && random.nextLong(major.getLevel()) + random.nextLong(minor.getLevel() / 10) < petUpgradeLimit) {
            major.setLevel(major.getLevel() + 1);
            long atk = major.getAtk() + random.nextLong(minor.getAtk() * (minor.getLevel() + 1) / 2);
            if (atk > 0) {
                major.setAtk(atk);
            }
            long def = major.getDef() + random.nextLong(minor.getDef() * (minor.getLevel() + 1) / 2);
            if (def > 0) {
                major.setDef(def);
            }
            long maxHP = major.getMaxHp() + random.nextLong(minor.getMaxHp() * (minor.getLevel() + 1) / 2);
            if (maxHP > 0) {
                major.setMaxHp(maxHP);
            }
            major.setIntimacy(major.getIntimacy() + 3);
            return true;
        }
        major.setIntimacy(major.getIntimacy() + 5);
        return false;
    }

    public boolean evolution(Pet pet, Hero hero) {
        if(pet.getIntimacy() > 100) {
            int eveIndex = getEvolutionIndex(pet, hero);
            eveIndex = shiershengiaoDetect(eveIndex, hero);
            return evolution(pet, eveIndex);
        }else{
            return false;
        }
    }

    private int getEvolutionIndex(Pet pet, Hero hero) {
        if(pet.getIndex() == 399){
            switch (hero.getElement()){
                case FIRE:
                    return 401;
                case WATER:
                    return 402;
                case WOOD:
                    return 403;
                case EARTH:
                    return 400;
                    default:
                        return pet.getIndex();
            }
        }
        if(pet.getIndex() == 101){
            return getEvoevoIndex(pet, hero);
        }
        return getEvolutionIndex(pet.getIndex());
    }

    public abstract int getEvolutionIndex(int index);

    private int shiershengiaoDetect(int next, Hero hero) {
        if (next >= 139 && next <= 150) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(hero.getBirthDay());
            if (StringUtils.getYear(date.get(Calendar.YEAR)).getIndex() != next) {
                next = 0;
            }
        }
        return next;
    }

    private int geThreeGodIndex(Pet pet, Hero hero) {
        Pet hit98 = null;
        Pet hit97 = null;
        Pet hit99 = null;
        int next = 0;
        switch (pet.getIndex()) {
            case 97:
                hit97 = pet;
                break;
            case 98:
                hit98 = pet;
                break;
            case 99:
                hit99 = pet;
                break;
        }
        for (Pet hpet : hero.getPets()) {
            if (hit98 == null && hpet.getIndex() == 98 && hpet.getIntimacy() >= 240) {
                hit98 = hpet;
            } else if (hit99 == null && hpet.getIndex() == 99 && hpet.getIntimacy() >= 240) {
                hit99 = hpet;
            } else if (hit97 == null && hpet.getIndex() == 97 && hpet.getIntimacy() >= 240) {
                hit97 = hpet;
            }
        }
        if (hit97 != null && hit98 != null && hit99 != null) {
            next = 100;
            if (hit97 != pet) {
                hit97.markDelete();
            }
            if (hit98 != pet) {
                hit98.markDelete();
            }
            if (hit99 != pet) {
                hit99.markDelete();
            }
        }
        Calendar calendar = Calendar.getInstance();
        if (next == 0 && (calendar.get(Calendar.HOUR_OF_DAY) > 18 || calendar.get(Calendar.HOUR_OF_DAY) < 6)) {
            switch (pet.getIndex()) {
                case 97:
                    next = 136;
                    break;
                case 98:
                    next = 137;
                    break;
                case 99:
                    next = 138;
                    break;
            }
        }
        return next;
    }

    private int evoZL(int next) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int due = hour / 4;
        switch (due) {
            case 0:
                if (getRandom().nextBoolean()) {
                    next = 54;
                } else {
                    next = 48;
                }
                break;
            case 1:
                next = 53;
                break;
            case 2:
                next = 52;
                break;
            case 3:
                next = 51;
                break;
            case 4:
                next = 50;
                break;
            case 5:
                next = 49;
                break;
        }
        return next;
    }

    private int getEvoevoIndex(Pet pet, Hero hero) {
        int next = 101;
        Calendar calendar = Calendar.getInstance();
        if (pet.getIntimacy() >= 240 && pet.getHp() <= 0) {//Ghost
            switch (getRandom().nextInt(3)) {
                case 0:
                    next = 102;
                    break;
                case 1:
                    next = 129;
                    break;
                case 2:
                    next = 130;
                    break;
            }
        } else if (pet.getIntimacy() < 10 && pet.getHp() <= 0) {//Dark
            switch (getRandom().nextInt(2)) {
                case 0:
                    next = 106;
                    break;
                case 1:
                    next = 131;
                    break;
            }
        } else if (pet.getIntimacy() <= 5 ) {
            next = 103;
        } else if (pet.getIntimacy() > 5 && pet.getIntimacy() < 20) {
            next = 104;
        } else if (pet.getIntimacy() >= 20 && pet.getIntimacy() < 50) {
            next = 105;
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            next = 125;
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            boolean hasLong = false;
            for (Pet p : new ArrayList<>(hero.getPets())) {
                if (p.getType().contains("龙")) {
                    hasLong = true;
                    break;
                }
            }
            if (hasLong) {
                next = 108;
            } else {
                next = 110;
            }
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            next = 109;
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            next = 110;
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            next = 111;
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            next = 113;
        } else if (pet.getIntimacy() >= 240 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            next = 112;
        } else if (pet.getIntimacy() >= 50 && pet.getIntimacy() < 90) {
            next = 114;
        } else if (pet.getIntimacy() >= 90 && pet.getIntimacy() < 120) {
            next = 115;
        } else if (pet.getIntimacy() >= 120 && pet.getIntimacy() < 210) {
            next = 116;
        } else if (pet.getIntimacy() >= 210 && pet.getIntimacy() < 240) {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 6 && calendar.get(Calendar.HOUR_OF_DAY) < 18) {//Day
                switch (hero.getElement()) {
                    case METAL:
                        next = 117;
                        break;
                    case WOOD:
                        next = 118;
                        break;
                    case WATER:
                        next = 119;
                        break;
                    case FIRE:
                        next = 120;
                        break;
                    case EARTH:
                        next = 121;
                        break;
                    case NONE:
                        next = 122;
                        break;
                }
            } else {//Night
                switch (hero.getElement()) {
                    case METAL:
                        next = 123;
                        break;
                    case WOOD:
                        next = 124;
                        break;
                    case WATER:
                        next = 107;
                        break;
                    case FIRE:
                        next = 126;
                        break;
                    case EARTH:
                        next = 127;
                        break;
                    case NONE:
                        next = 128;
                        break;
                }
            }
        }
        return next;
    }

    public boolean evolution(Pet pet, int eveIndex) {
        if (eveIndex != pet.getIndex()) {
            Monster eveMonster = loadMonsterByIndex(eveIndex);
            if (eveMonster != null && (eveMonster.getSex() == -1 || eveMonster.getSex() == pet.getSex())) {
                pet.setIndex(eveMonster.getIndex());
                pet.setType(eveMonster.getType());
                pet.setAtk(pet.getAtk() + random.nextLong(eveMonster.getAtk() / 3));
                pet.setDef(pet.getDef() + random.nextLong(eveMonster.getDef() / 3));
                pet.setMaxHp(pet.getMaxHp() + random.nextLong(eveMonster.getMaxHp() / 3));
                pet.setHitRate((pet.getHitRate() + eveMonster.getHitRate()) / 2);
                pet.setEggRate((pet.getEggRate() + eveMonster.getEggRate()) / 2);
                return true;
            }
            return false;
        }
        return false;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public boolean mountPet(Pet pet, Hero hero){
        if(hero.getPets().size() >= hero.getPetCount() + hero.getReincarnate()){
            return false;
        }else{
            pet.setMounted(true);
            hero.getPets().add(pet);
            hero.getEffects().addAll(pet.getContainsEffects());
            return true;
        }
    }

    public Egg buildEgg(Pet p1, Pet p2, InfoControlInterface gameControl) {
        if (!p1.getId().equals(p2.getId())) {
            if (p1.getSex() != p2.getSex()) {
                if (p1.getElement().isReinforce(p2.getElement())) {
                    float v = getRandom().nextFloat(p1.getEggRate() + p2.getEggRate()) +
                            getRandom().nextFloat(EffectHandler.getEffectAdditionFloatValue(EffectHandler.EGG, gameControl.getHero().getEffects()));
                    v/=Data.EGG_RATE_REDUCE;
                    Monster m1 = loadMonsterByIndex(p1.getIndex());
                    Monster m2 = loadMonsterByIndex(p2.getIndex());
                    if (m1!=null && m2!=null && gameControl.getRandom().nextFloat(300) < v) {
                        Egg egg = new Egg();
                        float abe = Data.ABE_BASE + EffectHandler.getEffectAdditionFloatValue(EffectHandler.ABE, gameControl.getHero().getEffects());
                        if(random.nextFloat(10000) < abe){
                            Monster monster = randomMonster();
                            if(m1.getSex() == 1){
                                m1 = monster;
                            }
                            if(m2.getSex() == 1){
                                m2 = monster;
                            }
                            egg.setColor(Data.ORANGE_COLOR);
                            egg.setMyFirstName("变异");
                        }
                        egg.setType(p1.getSex() == 1 ? p1.getType() : p2.getType());
                        egg.setElement(gameControl.getRandom().randomItem(Element.values()));
                        egg.setRace(gameControl.getRandom().randomItem(new Integer[]{p1.getRace().ordinal(), p2.getRace().ordinal()}));
                        egg.setRank(p1.getSex() == 1 ? p1.getRank() : p2.getRank());
                        egg.setIndex(p1.getSex() == 1 ? p1.getIndex() : p2.getIndex());
                        Skill skill = gameControl.getRandom().randomItem(new Skill[]{p1.getSkill(), p2.getSkill(), EmptySkill.EMPTY_SKILL});
                        if (skill != EmptySkill.EMPTY_SKILL) {
                            egg.setSkill(skill);
                        }
                        egg.setColor(p1.getSex() == 0 ? p1.getColor() : p2.getColor());
                        egg.step = (p1.getRank() + p2.getRank()) * 10;
                        egg.setFarther(p1.getSex() == 0? p1.getDisplayName() : p2.getDisplayName());
                        egg.setMother(p1.getSex() == 1? p1.getDisplayName() : p2.getDisplayName());
                        egg.setKeeperId(gameControl.getHero().getId());
                        egg.setOwnerId(gameControl.getHero().getId());
                        egg.setOwnerName(gameControl.getHero().getName());
                        egg.setKeeperName(gameControl.getHero().getName());
                        egg.setMaxHp(gameControl.getRandom().nextLong(m1.getMaxHp()) + m2.getMaxHp() + getRandom().reduceToSpecialDigit(gameControl.getMaze().getLevel(),2));
                        egg.setAtk(gameControl.getRandom().nextLong(m2.getAtk()) + m1.getAtk() + getRandom().reduceToSpecialDigit(gameControl.getMaze().getLevel(),2));
                        egg.setDef(gameControl.getRandom().nextLong(m1.getDef()) + m2.getDef() + getRandom().reduceToSpecialDigit(gameControl.getMaze().getLevel(),2));
                        egg.setHitRate(m1.getHitRate());
                        egg.setEggRate(p1.getSex() == 1 ? m1.getEggRate() : m2.getEggRate());
                        egg.setFirstName(p1.getSex() == 0 ? p1.getFirstName() : p2.getFirstName());
                        egg.setSecondName(p1.getSex() == 0 ? p2.getSecondName() : p2.getSecondName());
                        egg.setIntimacy(30);
                        egg.setSex(getRandom().nextInt(2));
                        return egg;
                    }
                }
            }
        }
        return null;
    }

    public abstract Monster loadMonsterByIndex(int index);

}
