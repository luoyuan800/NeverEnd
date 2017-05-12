package cn.luo.yuan.maze.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2015 luoyuan.
 * ALL RIGHTS RESERVED
 * Created by luoyuan on 10/5/15.
 */
public enum Element {
    METAL("金"), WATER("水"), WOOD("木"), FIRE("火"), EARTH("土"), NONE("无");
private String cn;
    private Element(String name){
        this.cn = name;
    }

    public String getCn(){
        return cn;
    }
    //相生判断
    //返回结果是当前的生传入的对象
    public boolean isReinforce(Element e) {
        return (e == METAL && this == EARTH) || (e == WOOD && this == WATER) || (e == WATER && this == METAL) || (e == FIRE && this == WOOD) || (e == EARTH && this == FIRE);
    }
    private static final long serialVersionUID = 1L;

    //相克判断
    //返回结果是当前的克制传入的对象
    public boolean restriction(Element e) {
        return (e == METAL && this == FIRE) ||
                (e == WATER && this == EARTH) ||
                (e == WOOD && this == METAL) ||
                (e == FIRE && this == WATER) ||
                (e == EARTH && this == WOOD) ||
                (e == NONE && this != NONE);
    }

    //获取生当前元素的另一个元素
    public Element getReinforce() {
        switch (this){
            case METAL:
                return EARTH;
            case WATER:
                return METAL;
            case EARTH:
                return FIRE;
            case FIRE:
                return WOOD;
            case WOOD:
                return WATER;
            default:
                return NONE;
        }
    }

    //获取克制当前元素的另外一个元素
    public Element getRestriction() {
        switch (this){
            case METAL:
                return FIRE;
            case WATER:
                return EARTH;
            case EARTH:
                return WOOD;
            case FIRE:
                return WATER;
            case WOOD:
                return METAL;
            default:
                return FIRE;
        }
    }

    public static List<String> getAllName(){
        ArrayList<String> values = new ArrayList<>(6);
        for(Element element : values()){
            values.add(element.cn);
        }
        return values;
    }

    public static Element getByName(String name){
        for(Element element : values()){
            if(element.cn.equals(name)){
                return element;
            }
        }
        return NONE;
    }

    public String toString(){
        return cn;
    }
}