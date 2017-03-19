package cn.luo.yuan.maze.utils;

import cn.luo.yuan.maze.model.effect.*;

import java.util.Collection;

/**
 * Created by luoyuan on 2017/3/19.
 * 我们需要简化数据模型里面的代码
 * 所以将一些数据处理代码抽取出来
 * 这是一个静态工具类，处理效果相关的代码都放到这里比较好
 */
public class EffectHandler {
    public static final String HP = "hp", STR="str", AGI="agi", ATK="atk", DEF="def";
    public static long getEffectAdditionValue(String property, Collection<Effect> effects){
        switch (property){
            case "str":
                return getEffectAdditionStr(effects);
            case "agi":
                return getEffectAdditionAgi(effects);
            case "hp":
                return getEffectAdditionHP(effects);
            case "atk":
                return getEffectAdditionAtk(effects);
            case "def":
                return getEffectAdditionDef(effects);
        }
        return 0;
    }

    public static long getEffectAdditionHP(Collection<Effect> effects){
        long hp = 0;
        for(Effect effect : effects){
            if(effect instanceof HpEffect) {
                hp += ((HpEffect) effect).getHp();
            }
        }
        return hp;
    }
    public static long getEffectAdditionAtk(Collection<Effect> effects){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof AtkEffect) {
                value += ((AtkEffect) effect).getAtk();
            }
        }
        return value;
    }
    public static long getEffectAdditionDef(Collection<Effect> effects){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof DefEffect) {
                value += ((DefEffect) effect).getDef();
            }
        }
        return value;
    }
    public static long getEffectAdditionAgi(Collection<Effect> effects){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof AgiEffect) {
                value += ((AgiEffect) effect).getAgi();
            }
        }
        return value;
    }
    public static long getEffectAdditionStr(Collection<Effect> effects){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof StrEffect) {
                value += ((StrEffect) effect).getStr();
            }
        }
        return value;
    }
}
