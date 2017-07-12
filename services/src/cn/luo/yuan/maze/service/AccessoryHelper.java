package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.exception.MountLimitException;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.original.AgiEffect;
import cn.luo.yuan.maze.model.effect.original.AtkEffect;
import cn.luo.yuan.maze.model.effect.original.DefEffect;
import cn.luo.yuan.maze.model.effect.original.HpEffect;
import cn.luo.yuan.maze.model.effect.original.StrEffect;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Iterator;

/**
 * Created by gluo on 6/26/2017.
 */
public class AccessoryHelper {
    /**
     * @param accessory mounted
     * @param check
     * @return Accessory that un mount
     */
    public static Accessory mountAccessory(Accessory accessory, Hero hero, boolean check) throws MountLimitException {
        Accessory uMount = null;
        String needEffect = null;
        long needEffectValue = 0;
        if(check) {
            for (Effect effect : accessory.getEffects()) {
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
        if(needEffect!=null && needEffectValue > 0){
            switch (needEffect){
                case "力量":
                    if(hero.getMaxStr() < needEffectValue){
                        throw new MountLimitException("需要" + needEffect + "大于" + StringUtils.formatNumber(needEffectValue));
                    }
                    break;
                case "敏捷":
                    if(hero.getMaxAgi() < needEffectValue){
                        throw new MountLimitException("需要" + needEffect + "大于" + StringUtils.formatNumber(needEffectValue));
                    }
                    break;
            }
        }
        Iterator<Accessory> iterator = hero.getAccessories().iterator();
        while (iterator.hasNext()) {
            uMount = iterator.next();
            if (uMount.getType().equals(accessory.getType())) {
                iterator.remove();
                hero.getEffects().removeAll(uMount.getEffects());
                uMount.setMounted(false);
                break;
            }
        }
        if (hero.getAccessories().add(accessory)) {
            hero.getEffects().addAll(accessory.getEffects());
            accessory.setMounted(true);
            for (Accessory mounted : hero.getAccessories()) {
                mounted.resetElementEffectEnable();
                for (Accessory other : hero.getAccessories()) {
                    if (other != mounted && other.getElement().isReinforce(mounted.getElement())) {
                        mounted.elementEffectEnable();
                    }
                }
            }
        }
        return uMount;
    }

    public static void unMountAccessory(Accessory accessory, Hero hero) {
        hero.getAccessories().remove(accessory);
        hero.getEffects().removeAll(accessory.getEffects());
        judgeEffectEnable(hero);
        accessory.setMounted(false);
        accessory.resetElementEffectEnable();
    }

    public static void judgeEffectEnable(Hero hero) {
        for (Accessory mounted : hero.getAccessories()) {
            mounted.resetElementEffectEnable();
            for (Accessory other : hero.getAccessories()) {
                if (other != mounted) {
                    mounted.elementEffectEnable();
                }
            }
        }
    }

}
