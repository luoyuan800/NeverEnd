package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static java.awt.SystemColor.control;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class PetMonsterHelper {
    public static PetMonsterHelper instance = new PetMonsterHelper();
    private MonsterLoader monsterLoader;
    private Random random;

    public  Pet monsterToPet(Monster monster, Hero hero) throws MonsterToPetException {
        Pet pet = new Pet();
        for (Method method : Monster.class.getMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Method set = Pet.class.getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                    set.invoke(pet, method.invoke(monster));
                }catch (Exception e){
                    throw  new MonsterToPetException(e);
                }

            }
        }
        pet.setOwnerId(hero.getId());
        pet.setOwnerName(hero.getName());
        return pet;
    }

    public  boolean isCatchAble(Monster monster, Hero hero, Random random, int petCount) {
        if (monster.getRace().ordinal() != hero.getRace().ordinal() + 1 || monster.getRace().ordinal() != hero.getRace().ordinal() - 5) {
            float rate = (100 - monster.getPetRate()) + random.nextInt(petCount + 1) / 10f;
            float current = random.nextInt(100) + random.nextFloat() + EffectHandler.getEffectAdditionFloatValue(EffectHandler.PET_RATE, hero.getEffects());
            if (current >= 100) {
                current = 98.9f;
            }
            return current > rate;
        } else {
            return false;
        }
    }

    public Monster randomMonster() {
        return monsterLoader.randomMonster();
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
        if (major != minor && random.nextLong(major.getLevel()) + random.nextLong(minor.getLevel() / 10) < Data.PET_UPGRADE_LIMIT) {
            major.setLevel(major.getLevel() + 1);
            long atk = major.getAtk() + random.nextLong(minor.getAtk() * (minor.getLevel() + 1) / 2);
            if (atk > 0) {
                major.setAtk(atk);
            }
            long def = major.getDef() + random.nextLong(minor.getDef() * (minor.getLevel() + 1) / 2);
            if (def > 0) {
                major.setDef(def);
            }
            long maxHP = major.getMaxHP() + random.nextLong(minor.getMaxHP() * (minor.getLevel() + 1) / 2);
            if (maxHP > 0) {
                major.setMaxHP(maxHP);
            }
            return true;
        }
        return false;
    }

    public boolean evolution(Pet pet) {
        int eveIndex = monsterLoader.getEvolutionIndex(pet.getIndex());

        if (eveIndex != pet.getIndex()) {
            Monster eveMonster = loadMonsterByIndex(eveIndex);
            if (eveMonster != null) {
                pet.setIndex(eveMonster.getIndex());
                pet.setType(eveMonster.getType());
                pet.setAtk(pet.getAtk() + random.nextLong(eveMonster.getAtk() / 3));
                pet.setDef(pet.getDef() + random.nextLong(eveMonster.getDef() / 3));
                pet.setMaxHP(pet.getMaxHP() + random.nextLong(eveMonster.getMaxHP() / 3));
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

    static class MonsterKey {
        int count;
        float meet_rate;
        float min_level;
        int index;

        public int hashCode() {
            return index;
        }

        public boolean equals(Object o) {
            return o instanceof MonsterKey && ((MonsterKey) o).index == index;
        }
    }

    private Monster loadMonsterByIndex(int index) {
       return monsterLoader.loadMonsterByIndex(index);
    }

}
