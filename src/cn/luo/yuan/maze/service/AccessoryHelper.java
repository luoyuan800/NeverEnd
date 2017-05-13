package cn.luo.yuan.maze.service;


import android.content.Context;
import android.content.res.XmlResourceParser;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
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
import java.util.List;

/**
 * Created by gluo on 4/28/2017.
 */
public class AccessoryHelper {
    private Context context;
    private Random random;

    public AccessoryHelper(Context context) {
        this.context = context;
        random = new Random(System.currentTimeMillis());
    }

    public boolean fuse(Accessory major, Accessory minor) {
        if (major.getName().equalsIgnoreCase(minor.getName()) && major.getType().equalsIgnoreCase(minor.getType())) {
            if (random.nextLong(major.getLevel()) < Data.ACCESSORY_FLUSE_LIMIT) {
                List<Effect> majorEffect = major.getEffects();
                for (Effect effect : minor.getEffects()) {
                    boolean append = true;
                    for (Effect me : majorEffect) {
                        if (me.getClass().getName().equalsIgnoreCase(effect.getClass().getName())) {
                            append = false;
                            if (me instanceof LongValueEffect) {
                                ((LongValueEffect) me).setValue(((LongValueEffect) me).getValue() + random.nextLong(((LongValueEffect) effect).getValue()/2));
                            }else if(me instanceof FloatValueEffect){
                                ((FloatValueEffect) me).setValue(((FloatValueEffect) me).getValue() + random.nextLong((long)((FloatValueEffect) effect).getValue() * 100/2)/100f);
                            }
                            break;
                        }
                    }
                    if(append){
                        if (effect instanceof LongValueEffect) {
                            ((LongValueEffect) effect).setValue(random.randomRange(((LongValueEffect) effect).getValue()/3,((LongValueEffect) effect).getValue()));
                        }else if(effect instanceof FloatValueEffect){
                            ((FloatValueEffect) effect).setValue(random.randomRange(((FloatValueEffect) effect).getValue()/3f,((FloatValueEffect) effect).getValue()));
                        }
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
        try (XmlResourceParser parser = context.getAssets().openXmlResourceParser("./accessory/accessories.xml")) {
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
                                    if(random.nextLong(100) < rate) {
                                        switch (name) {
                                            case "AgiEffect":
                                                AgiEffect agiEffect = new AgiEffect();
                                                agiEffect.setAgi(random.randomRange(max, min));
                                                effects.add(agiEffect);
                                                break;
                                            case "AtkEffect":
                                                AtkEffect atkEffect = new AtkEffect();
                                                atkEffect.setAtk(random.randomRange(max, min));
                                                effects.add(atkEffect);
                                                break;
                                            case "DefEffect":
                                                DefEffect defEffect = new DefEffect();
                                                defEffect.setDef(random.randomRange(max, min));
                                                effects.add(defEffect);
                                                break;
                                            case "HpEffect":
                                                HpEffect hpEffect = new HpEffect();
                                                hpEffect.setHp(random.randomRange(max, min));
                                                effects.add(hpEffect);
                                                break;
                                            case "StrEffect":
                                                StrEffect strEffect = new StrEffect();
                                                strEffect.setStr(random.randomRange(max, min));
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
                                if(effects!=null) {
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

}
