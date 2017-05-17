package cn.luo.yuan.maze.service;


import android.content.Context;
import android.content.res.XmlResourceParser;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.effect.AgiEffect;
import cn.luo.yuan.maze.model.effect.AtkEffect;
import cn.luo.yuan.maze.model.effect.DefEffect;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;
import cn.luo.yuan.maze.model.effect.HpEffect;
import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.model.effect.MeetRateEffect;
import cn.luo.yuan.maze.model.effect.PetRateEffect;
import cn.luo.yuan.maze.model.effect.StrEffect;
import cn.luo.yuan.maze.utils.Random;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gluo on 4/28/2017.
 */
public class AccessoryHelper {
    private Context context;
    private Random random;

    public AccessoryHelper(Context context, Random random) {
        this.context = context;
        this.random = random;
    }

    /**
     * For test
     */
    AccessoryHelper() {
        random = new Random(System.currentTimeMillis());
    }

    public boolean fuse(Accessory major, Accessory minor) {
        if (major.getName().equalsIgnoreCase(minor.getName()) && major.getType().equalsIgnoreCase(minor.getType())) {
            float colorReduce = Data.getColorReduce(major.getColor());
            if (random.nextLong(major.getLevel()) < Data.ACCESSORY_FLUSE_LIMIT) {
                List<Effect> majorEffect = major.getEffects();
                for (Effect effect : minor.getEffects()) {
                    boolean append = true;
                    for (Effect me : majorEffect) {
                        if (me.getClass().getName().equalsIgnoreCase(effect.getClass().getName())) {
                            append = false;
                            if (me instanceof LongValueEffect) {
                                long value = ((LongValueEffect) me).getValue();
                                long v = (long) random.nextFloat(((LongValueEffect) effect).getValue() * colorReduce);
                                if(v + value >= 0){
                                    ((LongValueEffect) me).setValue(value + v);
                                }
                            } else if (me instanceof FloatValueEffect) {
                                float v = random.nextFloat(((FloatValueEffect) effect).getValue() * colorReduce);
                                float value = ((FloatValueEffect) me).getValue();
                                //We need to check whether the rate larger too larger.
                                if(value + v < Data.RATE_MAX && random.nextFloat(value) < random.nextFloat(Data.RATE_MAX/2f)){
                                    ((FloatValueEffect) me).setValue(value + v);
                                }
                            }
                            break;
                        }
                    }
                    if (append) {
                        if (effect instanceof LongValueEffect) {
                            ((LongValueEffect) effect).setValue((long) random.randomRange(((LongValueEffect) effect).getValue() * colorReduce, ((LongValueEffect) effect).getValue()));
                        } else if (effect instanceof FloatValueEffect) {
                            ((FloatValueEffect) effect).setValue(random.randomRange(((FloatValueEffect) effect).getValue() * colorReduce, ((FloatValueEffect) effect).getValue()));
                        }
                        major.getEffects().add(effect);
                    }
                }
                major.setLevel(major.getLevel() + 1);
                if(!major.getColor().equals(Data.DARKGOLD_COLOR) && random.nextInt(100) < random.nextLong(major.getLevel())){
                    if(major.getColor().equals(Data.DEFAULT_QUALITY_COLOR)){
                        major.setColor(Data.BLUE_COLOR);
                    }else if(major.getColor().equals(Data.BLUE_COLOR)){
                        major.setColor(Data.RED_COLOR);
                    }else if(major.getColor().equals(Data.RED_COLOR)){
                        major.setColor(Data.ORANGE_COLOR);
                    } else if(major.getColor().equals(Data.ORANGE_COLOR) && random.nextInt(100) < random.nextLong(major.getLevel()/Data.DARKGOLD_RATE_REDUCE)){
                        major.setColor(Data.DARKGOLD_COLOR);
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<Accessory> loadFromAssets() {
        List<Accessory> accessories = new ArrayList<>();
        try (XmlResourceParser parser = context.getResources().getXml(R.xml.accessories)) {
            Accessory accessory = null;
            List<Effect> effects = null;
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                try {
                    switch (parser.getEventType()) {
                        case XmlResourceParser.START_TAG:
                            if (parser.getName().equals("accessory")) {
                                accessory = new Accessory();
                                break;
                            } else if (parser.getName().equals("name") && accessory != null) {
                                accessory.setName(parser.getAttributeValue(null, "value"));
                            } else if (parser.getName().equals("description") && accessory != null) {
                                accessory.setDesc(parser.getAttributeValue(null, "value"));
                            } else if (parser.getName().equals("color") && accessory != null) {
                                accessory.setColor(parser.getAttributeValue(null, "value"));
                            } else if (parser.getName().equals("author") && accessory != null) {
                                accessory.setAuthor(parser.getAttributeValue(null, "value"));
                            } else if (parser.getName().equals("price") && accessory != null) {
                                accessory.setPrice(Long.parseLong(parser.getAttributeValue(null, "value")));
                            } else if (parser.getName().equals("properties") && accessory != null) {
                                effects = new ArrayList<>();
                            } else if (parser.getName().equals("effect") && effects != null) {
                                try {
                                    String name = parser.getAttributeValue(null, "name");
                                    long max = Long.parseLong(parser.getAttributeValue(null, "max"));
                                    long min = Long.parseLong(parser.getAttributeValue(null, "min"));
                                    int rate = Integer.parseInt(parser.getAttributeValue(null, "rate"));
                                    if (random.nextLong(100) < rate) {
                                        switch (name) {
                                            case "AgiEffect":
                                                AgiEffect agiEffect = new AgiEffect();
                                                agiEffect.setAgi(random.randomRange(min, max));
                                                effects.add(agiEffect);
                                                break;
                                            case "AtkEffect":
                                                AtkEffect atkEffect = new AtkEffect();
                                                atkEffect.setAtk(random.randomRange(min, max));
                                                effects.add(atkEffect);
                                                break;
                                            case "DefEffect":
                                                DefEffect defEffect = new DefEffect();
                                                defEffect.setDef(random.randomRange(min, max));
                                                effects.add(defEffect);
                                                break;
                                            case "HpEffect":
                                                HpEffect hpEffect = new HpEffect();
                                                hpEffect.setHp(random.randomRange(min, max));
                                                effects.add(hpEffect);
                                                break;
                                            case "StrEffect":
                                                StrEffect strEffect = new StrEffect();
                                                strEffect.setStr(random.randomRange(min, max));
                                                effects.add(strEffect);
                                                break;
                                            case "MeetRateEffect":
                                                MeetRateEffect meetRateEffect = new MeetRateEffect();
                                                float meetRate = random.randomRange(Float.parseFloat(parser.getAttributeValue(null, "max")) * 100, Float.parseFloat(parser.getAttributeValue(null, "min")) * 100) / 100f;
                                                meetRateEffect.setMeetRate(meetRate);
                                                effects.add(meetRateEffect);
                                                break;
                                            case "PetRateEffect":
                                                PetRateEffect petRateEffect = new PetRateEffect();
                                                float petRate = random.randomRange(Float.parseFloat(parser.getAttributeValue(null, "max")) * 100, Float.parseFloat(parser.getAttributeValue(null, "min")) * 100) / 100f;
                                                petRateEffect.setPetRate(petRate);
                                                effects.add(petRateEffect);
                                                break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("accessory")) {
                                if (effects != null) {
                                    accessory.getEffects().addAll(effects);
                                }
                                accessories.add(accessory);
                                accessory = null;
                                effects = null;
                            }
                    }
                } catch (Exception e) {

                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessories;

    }

    /**
     * @param accessory mounted
     * @return Accessory that un mount
     */
    public Accessory mountAccessory(Accessory accessory, Hero hero) {
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
            for(Accessory mounted : hero.getAccessories()){
                mounted.resetEffectEnable();
                for(Accessory other : hero.getAccessories()){
                    if(other!=mounted){
                        mounted.effectEnable();
                    }
                }
            }
        }
        return uMount;
    }

    public void unMountAccessory(Accessory accessory, Hero hero) {
        hero.getAccessories().remove(accessory);
        hero.getEffects().removeAll(accessory.getEffects());
        judgeEffectEnable(hero);
    }

    public void judgeEffectEnable(Hero hero){
        for(Accessory mounted : hero.getAccessories()){
            mounted.resetEffectEnable();
            for(Accessory other : hero.getAccessories()){
                if(other!=mounted){
                    mounted.effectEnable();
                }
            }
        }
    }
}
