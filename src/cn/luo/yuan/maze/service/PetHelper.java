package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by gluo on 4/28/2017.
 */
public class PetHelper {
    public static Pet monsterToPet(Monster monster, Hero hero){
        Pet pet = new Pet();
        for(Method method : Monster.class.getMethods()){
            if(method.getName().startsWith("get")){
                try {
                    Method set = Pet.class.getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                    set.invoke(pet, method.invoke(monster));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    LogHelper.logException(e, false, "Error while transforming monster to pet");
                }
            }
        }
        pet.setOwnerId(hero.getId());
        pet.setOwnerName(hero.getName());
        return pet;
    }

    public static boolean isCatchAble(Monster monster, Hero hero, Random random, int petCount){
        if(monster.getRace().ordinal() != hero.getRace().ordinal() + 1 || monster.getRace().ordinal()!= hero.getRace().ordinal() - 5) {
            float rate = monster.getPetRate() + random.nextInt(petCount + 1) / 10f;
            float current = random.nextInt(100) + random.nextFloat() + EffectHandler.getEffectAdditionFloatValue(EffectHandler.PET_RATE, hero.getEffects());
            if(current >= 100){
                current = 98.9f;
            }
            return current > rate;
        }else{
            return  false;
        }
    }
}
