package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.effect.Effect;

import java.util.Iterator;

/**
 * Created by gluo on 6/26/2017.
 */
public class AccessoryHelper {
    /**
     * @param accessory mounted
     * @return Accessory that un mount
     */
    public static Accessory mountAccessory(Accessory accessory, Hero hero) {
        Accessory uMount = null;
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

    protected static void initEffect(Effect effect, boolean elementControl){
        effect.setElementControl(elementControl);
        if(elementControl){
            effect.setEnable(false);
        }else{
            effect.setEnable(true);
        }
    }
}
