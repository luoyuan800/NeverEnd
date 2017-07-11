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
     * @return Accessory that un mount
     */
    public static Accessory mountAccessory(Accessory accessory, Hero hero) throws MountLimitException {
        Accessory uMount = null;
        String needEffect = null;
        long needEffectValue = 0;
        for(Effect effect : accessory.getEffects()){
            if(effect instanceof AgiEffect && effect.getValue().longValue() > hero.getStr()/2){
                needEffect = "力量";
                needEffectValue = effect.getValue().longValue() * 2;
                break;
            }
            if(effect instanceof StrEffect && effect.getValue().longValue() > hero.getAgi()/2){
                needEffect = "敏捷";
                needEffectValue = effect.getValue().longValue() * 2;
                break;
            }
            long value = effect.getValue().longValue() / hero.getHpGrow();
            if(effect instanceof HpEffect && value > hero.getStr()/2){
                needEffect = "力量";
                needEffectValue = value * 2;
                break;
            }
            value = effect.getValue().longValue() / hero.getDefGrow();
            if(effect instanceof DefEffect && value > hero.getAgi()/2){
                needEffect = "敏捷";
                needEffectValue = value * 2;
                break;
            }
            value = effect.getValue().longValue() / hero.getAtkGrow();
            if(effect instanceof AtkEffect && value > hero.getStr()/2){
                needEffect = "力量";
                needEffectValue = value * 2;
                break;
            }
        }
        if(needEffect!=null && needEffectValue > 0){
            throw new MountLimitException("需要" + needEffect + "大于" + StringUtils.formatNumber(needEffectValue));
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
