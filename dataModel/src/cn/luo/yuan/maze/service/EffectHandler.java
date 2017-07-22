package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.effect.*;
import cn.luo.yuan.maze.model.effect.original.*;

import java.util.Collection;

/**
 * Created by luoyuan on 2017/3/19.
 * 我们需要简化数据模型里面的代码
 * 所以将一些数据处理代码抽取出来
 * 这是一个静态工具类，处理效果相关的代码都放到这里比较好
 */
public class EffectHandler {
    public static final String HP = "hp", STR="str", AGI="agi", ATK="atk", DEF="def", MEET_RATE="meet",
            PET_RATE="pet", SKILL_RATE = "skill_rate", EGG="egg_rate", CLICK_MATERIAL="material",
            DOGE="doge";

    public static long getEffectAdditionLongValue(String property, Collection<Effect> effects, Hero hero){
        switch (property){
            case CLICK_MATERIAL:
                return getEffectAdditionMate(effects, hero);
            case STR:
                return getEffectAdditionStr(effects, hero);
            case AGI:
                return getEffectAdditionAgi(effects, hero);
            case HP:
                return getEffectAdditionHP(effects, hero);
            case ATK:
                return getEffectAdditionAtk(effects, hero);
            case DEF:
                return getEffectAdditionDef(effects, hero);
        }
        return 0;
    }

    public static float getEffectAdditionFloatValue(String property, Collection<Effect> effects){
        float value = 0.0f;
        switch (property){
            case DOGE:
                for(Effect effect : effects){
                    if(effect instanceof DogeRateEffect){
                        value += ((DogeRateEffect) effect).getValue();
                    }
                }
                break;
            case EGG:
                for(Effect effect : effects){
                    if(effect instanceof EggRateEffect){
                        value += ((EggRateEffect) effect).getEggRate();
                    }
                }
                break;
            case MEET_RATE:
                for(Effect effect : effects){
                    if(effect instanceof MeetRateEffect){
                        value += ((MeetRateEffect) effect).getMeetRate();
                    }
                }
                break;
            case PET_RATE:
                for(Effect effect : effects){
                    if(effect instanceof PetRateEffect){
                        value += ((PetRateEffect) effect).getPetRate();
                    }
                }
                break;
            case SKILL_RATE:
                for(Effect effect : effects){
                    if(effect instanceof SkillRateEffect){
                        value += ((SkillRateEffect) effect).getSkillRate();
                    }
                }
                break;

        }
        return value;
    }

    private static long getEffectAdditionHP(Collection<Effect> effects, Hero hero){
        long hp = 0;
        for(Effect effect : effects){
            if(effect instanceof HpEffect && effect.isEnable()) {
                hp += ((HpEffect) effect).getHp();
            }else if(effect instanceof HPPercentEffect && effect.isEnable()){
                hp += ((HPPercentEffect) effect).getAdditionValue(hero.getMaxHp());
            }
        }
        return hp;
    }
    private static long getEffectAdditionAtk(Collection<Effect> effects, Hero hero){
        long value = 0;
        for(Effect effect : effects){
            if(effect.isEnable() && effect instanceof AtkEffect) {
                value += ((AtkEffect) effect).getAtk();
            } else if(effect instanceof AtkPercentEffect && effect.isEnable()){
                value += ((AtkPercentEffect) effect).getAdditionValue(hero.getAtk());
            }
        }
        return value;
    }
    private static long getEffectAdditionDef(Collection<Effect> effects, Hero hero){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof DefEffect && effect.isEnable()) {
                value += ((DefEffect) effect).getDef();
            } else if (effect.isEnable() && effect instanceof DefPercentEffect){
                value += ((DefPercentEffect) effect).getAdditionValue(hero.getDef());
            }
        }
        return value;
    }
    private static long getEffectAdditionAgi(Collection<Effect> effects, Hero hero){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof AgiEffect && effect.isEnable()) {
                value += ((AgiEffect) effect).getAgi();
            }
        }
        return value;
    }
    private static long getEffectAdditionStr(Collection<Effect> effects, Hero hero){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof StrEffect && effect.isEnable()) {
                value += ((StrEffect) effect).getStr();
            }
        }
        return value;
    }
    private static long getEffectAdditionMate(Collection<Effect> effects, Hero hero){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof ClickMaterialEffect && effect.isEnable()) {
                value += ((ClickMaterialEffect) effect).getValue();
            }
        }
        return value;
    }

    public static Effect buildEffect(String effectName, String value) {
        switch (effectName) {
            case "DogeRateEffect":
                DogeRateEffect dogeRateEffect = new DogeRateEffect();
                dogeRateEffect.setValue(Float.parseFloat(value));
                return dogeRateEffect;
            case "ClickMaterialEffect":
                ClickMaterialEffect clickMaterialEffect = new ClickMaterialEffect();
                clickMaterialEffect.setValue(Long.parseLong(value));
                return clickMaterialEffect;
            case "DefPercentEffect":
                DefPercentEffect defPercentEffect = new DefPercentEffect();
                defPercentEffect.setPercent(Float.parseFloat(value));
                return defPercentEffect;
            case "AtkPercentEffect":
                AtkPercentEffect atkPercentEffect = new AtkPercentEffect();
                atkPercentEffect.setPercent(Float.parseFloat(value));
                return atkPercentEffect;
            case "HPPercentEffect":
                HPPercentEffect hpPercentEffect = new HPPercentEffect();
                hpPercentEffect.setPercent(Float.parseFloat(value));
                return hpPercentEffect;
            case "SkillRateEffect":
                SkillRateEffect skillRateEffect = new SkillRateEffect();
                skillRateEffect.setSkillRate(Float.parseFloat(value));
                return skillRateEffect;
            case "AgiEffect":
                AgiEffect agiEffect = new AgiEffect();
                agiEffect.setAgi(Long.parseLong(value));
                return agiEffect;
            case "AtkEffect":
                AtkEffect atkEffect = new AtkEffect();
                atkEffect.setAtk(Long.parseLong(value));
                return atkEffect;
            case "DefEffect":
                DefEffect defEffect = new DefEffect();
                defEffect.setDef(Long.parseLong(value));
                return defEffect;
            case "HpEffect":
                HpEffect hpEffect = new HpEffect();
                hpEffect.setHp(Long.parseLong(value));
                return hpEffect;
            case "StrEffect":
                StrEffect strEffect = new StrEffect();
                strEffect.setStr(Long.parseLong(value));
                return strEffect;
            case "MeetRateEffect":
                MeetRateEffect meetRateEffect = new MeetRateEffect();
                meetRateEffect.setMeetRate(Float.parseFloat(value));
                return meetRateEffect;
            case "PetRateEffect":
                PetRateEffect petRateEffect = new PetRateEffect();
                petRateEffect.setPetRate(Float.parseFloat(value));
                return petRateEffect;
        }
        return null;
    }
}
