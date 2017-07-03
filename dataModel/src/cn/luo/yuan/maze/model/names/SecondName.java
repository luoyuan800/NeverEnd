package cn.luo.yuan.maze.model.names;


import cn.luo.yuan.maze.utils.MathUtils;
import cn.luo.yuan.maze.utils.Random;

/**
 * Copyright 2015 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 12/29/15.
 */
public enum SecondName {
    small("小",0,1,2,0),
    middle("中",1,3,2,0),
    large("大",5,8,4,0),
    larger("大大",10,5,1,0),
    face("人面",20,15,2,20),
    Strong("强壮",40,-5,1,10),
    weak("无力",23,80,-11,30),
    redB("厚唇",85,10,-1,5),
    food("笨",30,33,-1,50),
    reckless("鲁莽",50,50,-1,0),
    violence("暴力",60,10,-1, 3),
    fat("胖",20,130,-21,0),
    red("瘦",50,3,-80,0),
    green("疯",50,13,-101,0),
    blue("狂",40,43,-11,10),
    brave("勇敢",20,63,-11,10),
    defender("【守护者】",20,13,0,60),
    empty("",0,0,0,0)
    ;
    private SecondName(String name, float askPref, float hpPref, int petRate, int silent){
        this.name = name;
        this.additionAtk = askPref;
        this.additionHp = hpPref;
        this.petRate = petRate;
        this.silent = silent;
    }
    private String name;
    private  float additionHp;
    private float additionAtk;
    private int petRate;
    private float silent;

    public String getName() {
        return name;
    }

    public static SecondName getRandom(long mazeLev, Random random) {
        int length = values().length - 2;
        int second = (int) random.nextLong(mazeLev < length ? mazeLev + 1 : length);
        if(second >= length){
            second = random.nextInt(length);
        }
        return values()[second];
    }


    public long getAtkAddition(long atk) {
        return (long)(atk * (double)additionAtk/100);
    }

    public long getHpAddition(long hp) {
        return (long)(hp * (double)additionHp/100);
    }

    public int getPetRate() {
        return petRate;
    }

    public float getSilent() {
        return silent;
    }

    public long getReducedAtk(long value){
        return MathUtils.getReducedValue(value, additionAtk);
    }



    public long getReducedHp(long value){
        return MathUtils.getReducedValue(value, additionHp);
    }

}
