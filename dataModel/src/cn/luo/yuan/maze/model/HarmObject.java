package cn.luo.yuan.maze.model;

import cn.luo.yuan.utils.Random;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/26/2017.
 */
public abstract class HarmObject implements HarmAble {
    public long getUpperDef() {
        return getDef();
    }

    public long getUpperHp() {
        return getMaxHp();
    }

    public boolean isDodge(Random random) {
        return random.nextLong(100) > 97 + random.nextInt(100);
    }

    public boolean isHit(Random random) {
        return random.nextLong(100) > 97 + random.nextInt(100);
    }

    public boolean isParry(Random random) {
        return random.nextLong(100) > 97 + random.nextInt(100);
    }

    public long getUpperAtk(){
        return getAtk();
    }

    public long getCurrentHp() {
        return getHp();
    }

    public void setMaxHp(long hp) {

    }
}
