package cn.luo.yuan.maze.data;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.effect.original.AtkPercentEffect;
import cn.luo.yuan.maze.model.effect.original.ClickMaterialEffect;
import cn.luo.yuan.maze.model.effect.original.DefPercentEffect;
import cn.luo.yuan.maze.model.effect.original.HPPercentEffect;
import cn.luo.yuan.object.serializable.ObjectTable;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
public class GeneralAccessory {
    public static void main(String...args){
        ObjectTable<Accessory> table = new ObjectTable<Accessory>(Accessory.class, new File("data/self/accessory"));
        /*Accessory accessory = getAccessory();
        addAccessory(table, accessory);*/
        System.out.println(table.loadAll());
    }

    private static void addAccessory(ObjectTable<Accessory> table, Accessory accessory) {
        try {
            table.save(accessory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static Accessory getAccessory() {
        Accessory accessory = new Accessory();
        accessory.setColor(Data.ORANGE_COLOR);
        accessory.setType(Field.NECKLACE_TYPE);
        accessory.setName("蝶祁的眷恋");
        accessory.setElement(Element.FIRE);
        accessory.setDesc("<font color=\"#FF4500\">我会在你身后一直默默的陪着你</font>");
        DefPercentEffect def = new DefPercentEffect();
        def.setElementControl(false);
        def.setPercent(14);
        accessory.getEffects().add(def);
        AtkPercentEffect atk = new AtkPercentEffect();
        atk.setElementControl(false);
        atk.setPercent(10);
        accessory.getEffects().add(atk);
        ClickMaterialEffect material = new ClickMaterialEffect();
        material.setValue(10);
        material.setElementControl(false);
        accessory.getEffects().add(material);
        HPPercentEffect hp = new HPPercentEffect();
        hp.setPercent(62);
        hp.setElementControl(true);
        accessory.getEffects().add(hp);
        System.out.println(accessory.getDisplayName());
        System.out.println(StringUtils.formatEffectsAsHtml(accessory.getEffects()));
        return accessory;
    }
}
