package cn.luo.yuan.maze.model.names;


import cn.luo.yuan.maze.utils.Random;

/**
 * Copyright 2015 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 12/29/15.
 */
public enum FirstName {
    normal("普通",1,1,0,0),
    weird("怪异",5,5,10 ,0),
    fly("飞翔",20,10,0 ,0),
    rate("稀有",15,30,0 ,-50),
    crazy("发狂",50,20,0.1f,0),
    magical("神奇",60,40,0 ,-5),
    neural("神经",50,75,1 ,-10),
    legendary("传奇",100,80,1 ,-30),
    unbeatable("无敌",150,110,5 ,-100),
    really("真正",150,110,5 ,-100),
    image("镜像",150,110,5 ,-100),
    stupid("傻乎乎",150,110,5 ,-100),
    frailty("心灵脆弱",150,110,5 ,-100),
    angry("愤怒",150,110,5 ,-100),
    tire("心好累",150,110,5 ,-100),
    empty("",150,110,5 ,-100)
    ;
    private float atkPercent;
    private float hpPercent;
    private float silent;
    private float eggRate;
    private  String name;
    private FirstName(String name, float atkPercent, float hpPercent, float silent, float eggRate){
        this.name = name;
        this.atkPercent = atkPercent;
        this.hpPercent = hpPercent;
        this.silent = silent;
        this.eggRate = eggRate;
    }
    public long getAtkAddition(long atk) {
        return (long) (atk * (double)atkPercent/100);
    }

    public long getHPAddition(long hp) {
        return (long) (hp * (double)hpPercent/100);
    }
    public float getSilent(){
        return silent;
    }
    public  float getEggRate(){
        return eggRate;
    }
    public String getName(){
        return name;
    }

    public static FirstName getRandom(long lev, Random random){
        int length = values().length - 7;
        int first = (int) random.nextLong(lev / 50 < length ? lev / 50 + 1 : length);
        if(first >= length){
            first = random.nextInt(length);
        }
        return values()[first];
    }

    public static FirstName getByName(String name){
        for(FirstName firstName : values()){
            if(firstName.getName().equalsIgnoreCase(name)){
                return firstName;
            }
        }
        return null;
    }
}
