package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.utils.Random;

import java.lang.reflect.Method;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class PetMonsterHelper implements PetMonsterHelperInterface {
    public static PetMonsterHelper instance = new PetMonsterHelper();
    private MonsterLoader monsterLoader;
    private Random random;

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
        pet.setMaxHp(monster.getMaxHp());
        pet.setHp(pet.getMaxHp());
        pet.setAtk(monster.getAtk());
        pet.setDef(monster.getDef());
        pet.setSecondName(null);
        pet.setFirstName(null);
        long atk_l = level * Data.MONSTER_ATK_RISE_PRE_LEVEL;
        long def_l = level * Data.MONSTER_DEF_RISE_PRE_LEVEL;
        long hp_l = level * Data.MONSTER_HP_RISE_PRE_LEVEL;
        pet.setAtk(pet.getAtk() - atk_l + random.nextLong(random.reduceToSpecialDigit(atk_l, 3)));
        pet.setDef(pet.getDef() - def_l + random.nextLong(random.reduceToSpecialDigit(def_l, 3)));
        pet.setMaxHp(pet.getMaxHp() - hp_l + random.nextLong(random.reduceToSpecialDigit(hp_l, 3)));
        pet.setHp(pet.getMaxHp());
        pet.setFirstName(monster.getFirstName());
        pet.setSecondName(monster.getSecondName());
        pet.setOwnerId(hero.getId());
        pet.setOwnerName(hero.getName());
        return pet;
    }

    public  boolean isCatchAble(Monster monster, Hero hero, Random random, int petCount) {
        if (monster.getPetRate() > 0 && (petCount < Data.MAX_PET_COUNT && monster.getRace().ordinal() != hero.getRace().ordinal() + 1 && monster.getRace().ordinal() != hero.getRace().ordinal() - 5)) {
            float rate = (100 - monster.getPetRate()) + random.nextInt(petCount + 10);
            if(rate > 100 && monster.getPetRate() > 0){
                rate = 100 - monster.getPetRate() + random.nextFloat(petCount - 5);
            }
            float current = random.nextInt(100) + random.nextFloat() + EffectHandler.getEffectAdditionFloatValue(EffectHandler.PET_RATE, hero.getEffects());
            if (current >= 100) {
                current = 99.5f;
            }
            return current > rate;
        } else {
            return false;
        }
    }

    public Monster randomMonster(long level) {
        return monsterLoader.randomMonster(level, true);
    }

    public Monster randomMonster() {
        return monsterLoader.randomMonster(999, false);
    }

    public MonsterLoader getMonsterLoader() {
        return monsterLoader;
    }

    public void setMonsterLoader(MonsterLoader monsterLoader) {
        this.monsterLoader = monsterLoader;
    }

    public String getDescription(int index, String type) {
        return monsterLoader.getDescription(index, type);
    }

    public boolean upgrade(Pet major, Pet minor) {
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

    public boolean evolution(Pet pet) {
        if(pet.getIntimacy() > 100) {
            int eveIndex = monsterLoader.getEvolutionIndex(pet.getIndex());
            return evolution(pet, eveIndex);
        }else{
            return false;
        }
    }

    public boolean evolution(Pet pet, int eveIndex) {
        if (eveIndex != pet.getIndex()) {
            Monster eveMonster = loadMonsterByIndex(eveIndex);
            if (eveMonster != null) {
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

    public Pet eggToPet(Pet pet, Hero hero) {
        try {
            return monsterToPet(pet, hero, 1);
        } catch (MonsterToPetException e) {
            return null;
        }
    }

    public boolean mountPet(Pet pet, Hero hero){
        if(hero.getPets().size() >= Data.BASE_PET_COUNT + hero.getReincarnate()){
            return false;
        }else{
            pet.setMounted(true);
            hero.getPets().add(pet);
            return true;
        }
    }

    public Egg buildEgg(Pet p1, Pet p2, InfoControlInterface gameControl) {
        if (!p1.getId().equals(p2.getId())) {
            if (p1.getSex() != p2.getSex()) {
                if (p1.getElement().isReinforce(p2.getElement())) {
                    if (gameControl.getRandom().nextInt(200) < (p1.getEggRate() + p2.getEggRate()) + EffectHandler.getEffectAdditionFloatValue(EffectHandler.EGG, gameControl.getHero().getEffects())) {
                        Egg egg = new Egg();
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
                        Monster m1 = loadMonsterByIndex(p1.getIndex());
                        Monster m2 = loadMonsterByIndex(p2.getIndex());
                        if (m1 != null && m2 != null) {
                            egg.setMaxHp(gameControl.getRandom().nextLong(m1.getMaxHp()) + m2.getMaxHp());
                            egg.setAtk(gameControl.getRandom().nextLong(m2.getAtk()) + m1.getAtk());
                            egg.setAtk(gameControl.getRandom().nextLong(m1.getDef()) + m2.getDef());
                            egg.setHitRate(m1.getHitRate());
                            egg.setEggRate(p1.getSex() == 1 ? m1.getEggRate() : m2.getEggRate());
                        }
                        egg.setFirstName(p1.getSex() == 0 ? p1.getFirstName() : p2.getFirstName());
                        egg.setSecondName(p1.getSex() == 0 ? p2.getSecondName() : p2.getSecondName());
                        egg.setIntimacy(30);
                        return egg;
                    }
                }
            }
        }
        return null;
    }

    private Monster loadMonsterByIndex(int index) {
        return monsterLoader.loadMonsterByIndex(index);
    }

}
