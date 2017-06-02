package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MonsterToPetException;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.utils.Random;

import java.lang.reflect.Method;

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
                    //Ignore
                }

            }
        }
        pet.setOwnerId(hero.getId());
        pet.setOwnerName(hero.getName());
        return pet;
    }

    public  boolean isCatchAble(Monster monster, Hero hero, Random random, int petCount) {
        if (monster.getRace().ordinal() != hero.getRace().ordinal() + 1 || monster.getRace().ordinal() != hero.getRace().ordinal() - 5) {
            float rate = (100 - monster.getPetRate()) + petCount;
            if(rate > 100 && monster.getPetRate() > 0){
                rate = 100 - monster.getPetRate() + random.nextFloat(petCount + 10);
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
        return monsterLoader.randomMonster(level);
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
            long maxHP = major.getMaxHp() + random.nextLong(minor.getMaxHp() * (minor.getLevel() + 1) / 2);
            if (maxHP > 0) {
                major.setMaxHp(maxHP);
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


    private Monster loadMonsterByIndex(int index) {
       return monsterLoader.loadMonsterByIndex(index);
    }

}
