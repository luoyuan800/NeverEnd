package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.effect.AgiEffect;
import cn.luo.yuan.maze.model.effect.AtkEffect;
import cn.luo.yuan.maze.model.effect.DefEffect;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.HpEffect;
import cn.luo.yuan.maze.model.effect.MeetRateEffect;
import cn.luo.yuan.maze.model.effect.PetRateEffect;
import cn.luo.yuan.maze.model.effect.SkillRateEffect;
import cn.luo.yuan.maze.model.effect.StrEffect;
import cn.luo.yuan.maze.model.effect.original.ClickMaterialEffect;
import cn.luo.yuan.maze.model.effect.original.EggRateEffect;

/**
 * Created by gluo on 6/16/2017.
 */
public class ClientEffectHandler {
    public static Effect buildClientEffect(Effect effect){
        Effect clientEffect = buildEffect(effect.getName(), effect.getValue().toString(), effect.isElementControl());
        if(clientEffect!=null){
            return  clientEffect;
        }else{
            return effect;
        }
    }
    public static Effect buildEffect(String effectName, String value, boolean elementControl){
        Effect effect = null;
        switch (effectName) {
            case "ClickMaterialEffect":
                effect = new ClickMaterialEffect();
                ((ClickMaterialEffect)effect).setValue(Long.parseLong(value));
            case "EggRateEffect":
                effect = new EggRateEffect();
                ((EggRateEffect)effect).setEggRate(Float.parseFloat(value));
                break;
            case "SkillRateEffect":
                effect = new SkillRateEffect();
                ((SkillRateEffect)effect).setSkillRate(Float.parseFloat(value));
                break;
            case "AgiEffect":
                effect = new AgiEffect();
                ((AgiEffect)effect).setAgi(Long.parseLong(value));
            case "AtkEffect":
                effect = new AtkEffect();
                ((AtkEffect)effect).setAtk(Long.parseLong(value));
            case "DefEffect":
                 effect = new DefEffect();
                ((DefEffect)effect).setDef(Long.parseLong(value));
            case "HpEffect":
                 effect = new HpEffect();
                ((HpEffect)effect).setHp(Long.parseLong(value));
            case "StrEffect":
                 effect = new StrEffect();
                ((StrEffect)effect).setStr(Long.parseLong(value));
            case "MeetRateEffect":
                 effect = new MeetRateEffect();
                ((MeetRateEffect)effect).setMeetRate(Float.parseFloat(value));
            case "PetRateEffect":
                 effect = new PetRateEffect();
                ((PetRateEffect)effect).setPetRate(Float.parseFloat(value));
        }
        if(effect!=null){
            effect.setElementControl(elementControl);
            if(elementControl){
                effect.setEnable(false);
            }else{
                effect.setEnable(true);
            }
        }
        return effect;
    }
}
