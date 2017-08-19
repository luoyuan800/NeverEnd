package cn.luo.yuan.maze.client.service;


import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.ArrayMap;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.effect.*;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.model.effect.original.ClickMaterialEffect;
import cn.luo.yuan.maze.utils.Random;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 4/28/2017.
 */
public class AccessoryHelper extends cn.luo.yuan.maze.service.AccessoryHelper {
    private static AccessoryHelper instance;
    private Context context;
    private Random random;
    private ArrayMap<AccessoryKey, WeakReference<Accessory>> accessoryCache;
    private NeverEnd control;

    private AccessoryHelper(Context context, Random random) {
        this.context = context;
        this.random = random;
        accessoryCache = new ArrayMap<>();
    }

    /**
     * For test
     */
    public AccessoryHelper() {
        random = new Random(System.currentTimeMillis());
    }

    public static AccessoryHelper getOrCreate(NeverEnd control) {
        if (instance == null) {
            synchronized (AccessoryHelper.class) {
                if (instance == null) {
                    instance = new AccessoryHelper(control.getContext(), control.getRandom());
                    instance.control = control;
                    instance.initAccessories();
                }
            }
        }
        return instance;
    }

    public boolean fuse(Accessory major, Accessory minor) {
        if (major.getType().equalsIgnoreCase(minor.getType()) && !major.isDelete() && !minor.isDelete()) {
            float colorReduce = Data.getColorReduce(major.getColor());
            int fluseLimit = Data.ACCESSORY_FLUSE_LIMIT;
            if(!major.getName().equalsIgnoreCase(minor.getName())){
                fluseLimit *= 2;
            }
            if (random.nextLong(major.getLevel()) < fluseLimit) {
                if(major.getColor().equals(Data.DARKGOLD_COLOR)){
                    if(random.nextLong(major.getLevel()) > Data.DARKGOLD_RATE_REDUCE){
                        return false;
                    }
                }
                List<Effect> majorEffect = major.getEffects();
                for (Effect effect : minor.getEffects()) {
                    boolean append = true;
                    for (Effect me : majorEffect) {
                        if (me.getName().equalsIgnoreCase(effect.getName())) {
                            append = false;
                            if (me instanceof LongValueEffect) {
                                long value = ((LongValueEffect) me).getValue();
                                long v = (long) random.randomRange(((LongValueEffect) effect).getValue().floatValue() * colorReduce, ((LongValueEffect) effect).getValue().floatValue());
                                if (v + value >= 0) {
                                    if(!(me instanceof ClickMaterialEffect) || v < 10){
                                        ((LongValueEffect) me).setValue(value + v);
                                    }
                                }
                            } else if (me instanceof FloatValueEffect) {
                                float v = random.randomRange(((FloatValueEffect) effect).getValue() * colorReduce, ((FloatValueEffect) effect).getValue());
                                float value = ((FloatValueEffect) me).getValue();
                                //We need to check whether the rate too larger.
                                if (value + v < Data.RATE_MAX/3 && random.nextFloat(value) < random.nextFloat(Data.RATE_MAX / 2f)) {
                                    ((FloatValueEffect) me).setValue(value + v);
                                }
                            }
                            break;
                        }
                    }
                    if (append) {
                        if (effect instanceof LongValueEffect) {
                            long value = (long) random.randomRange(((LongValueEffect) effect).getValue() * colorReduce, ((LongValueEffect) effect).getValue());
                            if(value == 0){
                                value = 1;
                            }
                            ((LongValueEffect) effect).setValue(value);
                        } else if (effect instanceof FloatValueEffect) {
                            float value = random.randomRange(((FloatValueEffect) effect).getValue() * colorReduce, ((FloatValueEffect) effect).getValue());
                            if(value == 0){
                                value = 0.1f;
                            }
                            ((FloatValueEffect) effect).setValue(value);
                        }
                        major.getEffects().add(effect);
                    }
                }
                major.setLevel(major.getLevel() + 1);
                if (!major.getColor().equals(Data.DARKGOLD_COLOR) && random.nextInt(1000) < random.nextLong(major.getLevel())) {
                    switch (major.getColor()) {
                        case Data.DEFAULT_QUALITY_COLOR:
                            major.setColor(Data.BLUE_COLOR);
                            break;
                        case Data.BLUE_COLOR:
                            major.setColor(Data.RED_COLOR);
                            break;
                        case Data.RED_COLOR:
                            major.setColor(Data.ORANGE_COLOR);
                            break;
                    }
                    /*else if (major.getColor().equals(Data.ORANGE_COLOR) && random.nextInt(100) < random.nextLong(major.getLevel() / Data.DARKGOLD_RATE_REDUCE)) {
                        major.setColor(Data.DARKGOLD_COLOR);
                    }*/
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    public Accessory loadAccessoryByName(String name) {
        Accessory accessory = null;
        AccessoryKey key = new AccessoryKey();
        key.name = name;
        WeakReference<Accessory> ref = accessoryCache.get(key);
        if (ref != null) {
            accessory = ref.get();
        }
        if (accessory == null) {
            try (XmlResourceParser parser = context.getResources().getXml(R.xml.accessories)) {
                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        switch (parser.getName()) {
                            case "accessory":
                                accessory = new Accessory();
                                key = new AccessoryKey();
                                key.local = parser.getAttributeBooleanValue(null, "local", true);
                                break;
                            case "name":
                                if (accessory != null && parser.getAttributeValue(null, "value").equals(name)) {
                                    key.name = parser.getAttributeValue(null, "value");
                                    accessory.setName(key.name);
                                    accessoryCache.put(key, new WeakReference<>(accessory));
                                } else {
                                    accessory = null;
                                    key = null;
                                }
                                break;
                            case "type":
                                if (accessory != null)
                                    accessory.setType(parser.getAttributeValue(null, "value"));
                                break;
                            case "description":
                                if (accessory != null)
                                    accessory.setDesc(parser.getAttributeValue(null, "value"));
                                break;
                            case "color":
                                if (accessory != null) {
                                    if (key != null) {
                                        key.color = parser.getAttributeValue(null, "value");
                                    }
                                    accessory.setColor(parser.getAttributeValue(null, "value"));
                                }
                                break;
                            case "author":
                                if (accessory != null)
                                    accessory.setAuthor(parser.getAttributeValue(null, "value"));
                                break;
                            case "element":
                                if (accessory != null)
                                    accessory.setElement(Element.getByName(parser.getAttributeValue(null, "value")));
                                break;
                            case "price":
                                if (accessory != null)
                                    accessory.setPrice(Long.parseLong(parser.getAttributeValue(null, "value")));
                                break;
                            case "effect":
                                if (accessory != null) {
                                    try {
                                        String effectName = parser.getAttributeValue(null, "name");
                                        long max = Long.parseLong(parser.getAttributeValue(null, "max"));
                                        long min = Long.parseLong(parser.getAttributeValue(null, "min"));
                                        int rate = Integer.parseInt(parser.getAttributeValue(null, "rate"));
                                        boolean elementControl = Boolean.parseBoolean(parser.getAttributeValue(null,"element"));
                                        if (random.nextLong(100) < rate) {
                                            Effect effect = ClientEffectHandler.buildEffect(effectName,String.valueOf(random.randomRange(min,max)), elementControl);
                                            accessory.getEffects().add(effect);
                                        }
                                    } catch (Exception e) {
                                        LogHelper.logException(e, "AccessoryHelper->loadAccessoryByName->effects parse {" + accessory.getName() + "}");
                                    }
                                }
                                break;
                        }
                    }
                    parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                LogHelper.logException(e, "AccessoryHelper->loadAccessoryByName{" + name + "}");
            }
        }
        accessory = accessory != null ? accessory.clone() : null;
        if (accessory != null && accessory.getElement() == null) {
            accessory.setElement(random.randomItem(Element.values()));
        }
        return accessory;
    }

    public List<Accessory> getRandomAccessories(int num) {
        List<Accessory> accessories = new ArrayList<>(num);
        for (ArrayMap.Entry<AccessoryKey, WeakReference<Accessory>> entry : accessoryCache.entrySet()) {
            AccessoryKey key = entry.getKey();
            if (key.local) {
                int rate = 50;
                switch (key.color) {
                    case Data.BLUE_COLOR:
                        rate -= 10;
                        break;
                    case Data.RED_COLOR:
                        rate -= 20;
                        break;
                    case Data.ORANGE_COLOR:
                        rate -= 30;
                        break;
                    case Data.DARKGOLD_COLOR:
                        rate -= 40;
                        break;
                }
                if (random.nextInt(100) < rate) {
                    Accessory accessory = loadAccessoryByName(key.name);
                    if (accessory != null) {
                        accessories.add(accessory);
                    }
                }
                if(accessories.size() > 20){
                    break;
                }
            }
        }
        return accessories;
    }

    private static class AccessoryKey {
        public String color;
        String name;
        boolean local;

        public int hashCode() {
            return name.hashCode();
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof AccessoryKey && ((AccessoryKey) other).name != null) {
                return ((AccessoryKey) other).name.equals(name);
            }
            return false;
        }

    }

    private void initAccessories() {
        try (XmlResourceParser parser = context.getResources().getXml(R.xml.accessories)) {
            AccessoryKey key = null;
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                try {
                    switch (parser.getEventType()) {
                        case XmlResourceParser.START_TAG:
                            if (parser.getName().equals("accessory")) {
                                key = new AccessoryKey();
                                key.local = parser.getAttributeBooleanValue(null, "local", true);
                                break;
                            } else if (parser.getName().equals("name")) {
                                if (key != null)
                                    key.name = parser.getAttributeValue(null, "value");
                            } else if (parser.getName().equals("color")) {
                                if (key != null) {
                                    key.color = parser.getAttributeValue(null, "value");
                                }
                            }
                            if (key != null && key.name != null && key.color != null) {
                                accessoryCache.put(key, new WeakReference<Accessory>(null));
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("accessory")) {
                                if (key != null) {
                                    key = null;
                                }
                            }
                            break;
                    }
                } catch (Exception e) {
                    LogHelper.logException(e, "Accessory->initAccessories {" + (key != null ? key.name : "") + "}");
                }
                parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            LogHelper.logException(e, "Accessory->initAccessories");
        }
    }

    public long recycle(Accessory accessory){
        if(accessory.isDelete()){
            return 0;
        }
        if(accessory.isMounted()){
            unMountAccessory(accessory,control.getHero(), control);
        }
        control.getDataManager().delete(accessory);
        long m = Data.RECYCLE_MATERIAL * (1 + accessory.getLevel());
        control.getHero().setMaterial(control.getHero().getMaterial() + m);
        return m;
    }
}
