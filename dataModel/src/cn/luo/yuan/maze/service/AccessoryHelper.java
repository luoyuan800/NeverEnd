package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MountLimitException;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.original.*;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 6/26/2017.
 */
public class AccessoryHelper {

    public static String getLimitString(Accessory accessory, Hero hero){
        try{
            checkMountLimit(accessory.getEffects(), hero);
        } catch (MountLimitException e) {
            return e.getMessage();
        }
        return StringUtils.EMPTY_STRING;
    }

    public static void checkMountLimit(List<Effect> effects, Hero hero) throws MountLimitException {
        String needEffect = null;
        long needEffectValue = 0;
        try {
            for (Effect effect : effects) {
                if (effect != null) {
                    long count = effect.getValue().longValue() * 2;
                    if (effect instanceof AgiEffect && count > needEffectValue) {
                        needEffect = "力量";
                        needEffectValue = count;
                        break;
                    }
                    if (effect instanceof StrEffect && count > needEffectValue) {
                        needEffect = "敏捷";
                        needEffectValue = count;
                        break;
                    }
                    long value = count / hero.getHpGrow();
                    if (effect instanceof HpEffect && value > needEffectValue) {
                        needEffect = "力量";
                        needEffectValue = value * 2;
                        break;
                    }
                    value = count / hero.getDefGrow();
                    if (effect instanceof DefEffect && value > needEffectValue) {
                        needEffect = "敏捷";
                        needEffectValue = value;
                        break;
                    }
                    value = count / hero.getAtkGrow();
                    if (effect instanceof AtkEffect && value > needEffectValue) {
                        needEffect = "力量";
                        needEffectValue = value;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (needEffect != null && needEffectValue > 0) {
            switch (needEffect) {
                case "力量":
                    if (hero.getMaxStr() < needEffectValue) {
                        throw new MountLimitException("需要" + needEffect + "大于" + StringUtils.formatNumber(needEffectValue, false));
                    }
                    break;
                case "敏捷":
                    if (hero.getMaxAgi() < needEffectValue) {
                        throw new MountLimitException("需要" + needEffect + "大于" + StringUtils.formatNumber(needEffectValue, false));
                    }
                    break;
            }
        }
    }

    /**
     * @param accessory mounted
     * @param check
     * @param context
     * @return Accessory that un mount
     */
    public synchronized static Accessory mountAccessory(Accessory accessory, Hero hero, boolean check, InfoControlInterface context) throws MountLimitException {
        Accessory uMount = null;
        if (check) {
            checkMountLimit(accessory.getEffects(), hero);
        }
        List<Accessory> iterator = new ArrayList<>(hero.getAccessories());
        for (Accessory a : iterator) {
            uMount = a;
            if (uMount != null && uMount.getType().equals(accessory.getType())) {
                hero.getAccessories().remove(a);
                for (Effect effect : uMount.getEffects()) {
                    if (effect != null) {
                        hero.getEffects().remove(effect);
                    }
                }
                uMount.setMounted(false);
                break;
            }
            uMount = null;
        }
        if (hero.getAccessories().add(accessory)) {
            hero.getEffects().addAll(accessory.getEffects());
            accessory.setMounted(true);
            judgeEffectEnable(hero, context);
        }
        return uMount;
    }

    public static void unMountAccessory(Accessory accessory, Hero hero, InfoControlInterface context) {
        hero.getAccessories().remove(accessory);
        hero.getEffects().removeAll(accessory.getEffects());
        judgeEffectEnable(hero, context);
        accessory.setMounted(false);
        accessory.resetElementEffectEnable();
    }

    public static void judgeEffectEnable(Hero hero, InfoControlInterface context) {
        boolean isElement = false;
        if(context!=null) {
            NeverEndConfig config = context.getDataManager().loadConfig();
            isElement = config.isElementer();
        }
        judgeElementEnable(hero, isElement);
    }

    public static void judgeElementEnable(Hero hero, boolean isElement) {
        for (Accessory mounted : hero.getAccessories()) {
            mounted.resetElementEffectEnable();
            if(isElement && hero.getElement().isReinforce(mounted.getElement())){
                mounted.elementEffectEnable();
                continue;
            }
            for (Accessory other : hero.getAccessories()) {
                if (other != mounted && other.getElement().isReinforce(mounted.getElement())) {
                    mounted.elementEffectEnable();
                    break;
                }
            }
        }
    }

}
