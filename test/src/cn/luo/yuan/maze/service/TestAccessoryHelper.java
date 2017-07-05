package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.client.service.AccessoryHelper;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.effect.AgiEffect;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.model.effect.original.AtkEffect;
import cn.luo.yuan.maze.model.effect.original.MeetRateEffect;
import org.testng.annotations.Test;

/**
 * Created by gluo on 5/17/2017.
 */
public class TestAccessoryHelper {
    @Test
    public void testFuse(){
        AccessoryHelper helper = new AccessoryHelper();
        Accessory major = new Accessory();
        major.setElement(Element.FIRE);
        AgiEffect agiEffect = new AgiEffect();
        agiEffect.setAgi(10);
        major.getEffects().add(agiEffect);
        major.setType("ring");
        major.setName("test1");
        AtkEffect atkEffect = new AtkEffect();
        atkEffect.setAtk(1000);
        major.getEffects().add(atkEffect);

        Accessory minor = new Accessory();
        minor.setElement(Element.WOOD);
        MeetRateEffect meetRateEffect = new MeetRateEffect();
        meetRateEffect.setMeetRate(10.0f);
        minor.getEffects().add(meetRateEffect);
        minor.getEffects().add(agiEffect);
        minor.setType("ring");
        minor.setName("test1");
        int count =10000;
        for(int i = 0; i< count; i++){
            String color = major.getColor();
            if(helper.fuse(major, minor) ) {
                if(!color.equals(major.getColor())){
                    System.out.println("Color Upgrade: " + major.getLevel() + "-------" + color + "---------->" + major.getColor());
                }
                System.out.println(major.getDisplayName());
                for (Effect effect : major.getEffects()) {
                    if (effect instanceof LongValueEffect) {
                        System.out.println(effect.getClass().getSimpleName() + ": " + ((LongValueEffect) effect).getValue());
                    } else if (effect instanceof FloatValueEffect) {
                        System.out.println(effect.getClass().getSimpleName() + ": " + ((FloatValueEffect) effect).getValue());
                    }
                }
            }
            minor = new Accessory();
            meetRateEffect = new MeetRateEffect();
            meetRateEffect.setMeetRate(10.0f);
            minor.getEffects().add(meetRateEffect);
            agiEffect = new AgiEffect();
            agiEffect.setAgi(10);
            minor.getEffects().add(agiEffect);
            minor.setType("ring");
            minor.setName("test1");
        }
        System.out.println("Rate: " + major.getLevel()* 100 / count + "%");
    }

    public void assertLarger(Object o1, Object o2) {
        System.out.println(o1 + " should larger than " + o2);
    }

}
