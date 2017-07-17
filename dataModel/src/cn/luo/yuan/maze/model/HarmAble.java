package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.Random;

/**
 * Created by gluo on 4/27/2017.
 */
public interface HarmAble {
    long getAtk();
    long getDef();
    long getHp();
    void setHp(long hp);

    default long getCurrentHp() {
        return getHp();
    }

    default void setMaxHp(long hp){

    }
    long getMaxHp();

    Element getElement();

    long getUpperAtk();

    default long getUpperDef(){
        return getDef();
    }

    default long getUpperHp(){
        return getMaxHp();
    }

    default boolean isDodge(Random random){
        return random.nextLong(100) > 97 + random.nextInt(100);
    }

    default boolean isHit(Random random){
        return random.nextLong(100) > 97 + random.nextInt(100);
    }

    default boolean isParry(Random random){
        return random.nextLong(100) > 97 + random.nextInt(100);
    }
}
