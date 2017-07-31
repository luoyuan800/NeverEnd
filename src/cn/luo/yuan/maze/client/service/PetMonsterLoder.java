package cn.luo.yuan.maze.client.service;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.Log;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.service.MonsterLoader;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class PetMonsterLoder implements MonsterLoader {
    private static PetMonsterLoder instance;
    private NeverEnd control;
    private ArrayMap<MonsterKey, WeakReference<Monster>> monsterCache = new ArrayMap<>();

    private PetMonsterLoder(NeverEnd control) {
        this.control = control;
        init();
    }

    public static PetMonsterLoder getOrCreate(NeverEnd control) {
        if (instance == null) {
            synchronized (PetMonsterLoder.class) {
                if (instance == null) {
                    instance = new PetMonsterLoder(control);
                }
            }
        }
        return instance;
    }

    public static Drawable loadMonsterImage(int id) {
        String ids = String.valueOf(id);
        return loadMonsterImage(ids);
    }

    private static Drawable loadMonsterImage(String id) {
        Drawable drawable = Resource.loadImageFromAssets("monster/" + id, false);
        if (drawable == null) {
            drawable = Resource.loadImageFromAssets("monster/" + id + ".jpg", false);
        }
        if (drawable == null) {
            drawable = Resource.loadImageFromAssets("monster/" + id + ".png", false);
        }
        if (drawable == null) {
            drawable = Resource.loadImageFromAssets("monster/wenhao.jpg", false);
        }
        return drawable;
    }

    public synchronized Monster randomMonster(long level, boolean addKey) {
        if (getMonsterCache().size() == 0) {
            Log.d("maze", "Init monster index");
            init();
        }
        Monster clone = null;
        ArrayList<MonsterKey> keys = getAvaiableMonsterKey(level, addKey);
        MonsterKey key = getRandom().randomItem(keys);
        int reTry = 0;
        while (reTry++ < 3 && (key == null || getRandom().nextInt(100 + key.count) >= key.meet_rate)) {
            if (addKey) {
                if (key != null) key.count--;
            }
            key = getRandom().randomItem(keys);
        }
        if (key != null) {
            Monster monster = getMonsterCache().get(key).get();
            if (monster == null) {
                monster = loadMonsterByIndex(key.index);
            }
            if (monster != null) {
                clone = monster.clone();
                if (clone != null) {
                    if (addKey) {
                        key.count++;
                    }
                    if (addKey) {
                        if (clone.getSex() < 0) {
                            clone.setSex(getRandom().nextInt(2));
                        }
                        clone.setAtk(clone.getAtk() + level * Data.MONSTER_ATK_RISE_PRE_LEVEL);
                        clone.setDef(clone.getDef() + level * Data.MONSTER_DEF_RISE_PRE_LEVEL);
                        clone.setMaxHp(clone.getMaxHp() + level * Data.MONSTER_HP_RISE_PRE_LEVEL);
                        clone.setHp(clone.getMaxHp());
                        clone.setElement(Element.values()[getRandom().nextInt(Element.values().length)]);
                        clone.setMaterial(Data.getMonsterMaterial(clone.getMaxHp(), clone.getAtk(), level, getRandom()));
                        clone.setFirstName(FirstName.getRandom(level, getRandom()));
                        clone.setSecondName(SecondName.getRandom(level, getRandom()));
                        clone.setColor(Data.DEFAULT_QUALITY_COLOR);
                    }
                }
            }
        }
        return clone;
    }

    public String getDescription(int index, String type) {
        if (index <= 0 && type == null) {
            return StringUtils.EMPTY_STRING;
        }
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            int monsterIndex = -1;
            String name = null;
            loop:
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    switch (parser.getName()) {
                        case "name":
                            String currentName = parser.nextText();
                            if (currentName.equals(type)) {
                                name = currentName;
                            }
                            break;
                        case "index":
                            int currentIndex = Integer.parseInt(parser.nextText());
                            if (currentIndex == index) {
                                monsterIndex = currentIndex;
                            }
                            break;
                        case "desc":
                            String desc = parser.nextText();
                            if ((index > 0 && monsterIndex != index) || (type != null && !type.equals(name))) {
                                nextMonsterTag(parser);
                                continue loop;
                            } else {
                                return desc;
                            }
                    }
                }
                parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            LogHelper.logException(e, "PetMonsterLoder->getDescription{" + index + ", " + type + "}");
        }
        return StringUtils.EMPTY_STRING;
    }

    public int getEvolutionIndex(int index) {
        int evolutionIndex = index;
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            int monsterIndex = -1;
            loop:
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    switch (parser.getName()) {
                        case "index":
                            int currentIndex = Integer.parseInt(parser.nextText());
                            if (currentIndex == index) {
                                monsterIndex = currentIndex;
                            } else {
                                nextMonsterTag(parser);
                                continue loop;
                            }
                            break;
                        case "evolution":
                            try {
                                int currentEveIndex = Integer.parseInt(parser.nextText());
                                if (monsterIndex > 0 && monsterIndex == index) {
                                    evolutionIndex = currentEveIndex;
                                }
                            } catch (Exception e) {
                                //LogHelper.logException(e,"Not need care");
                            }
                            break loop;
                    }
                }
                parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            LogHelper.logException(e, "PetMonsterLoder->getEvolutionIndex{" + index + "}");
        }
        return evolutionIndex;
    }

    public Monster loadMonsterByIndex(int index) {
        Monster monster = null;
        MonsterKey key = null;
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            loop:
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                    case XmlResourceParser.START_TAG:
                        switch (parser.getName()) {

                            case "meet":
                                if (key != null) {
                                    key.meet_rate = Float.parseFloat(parser.getAttributeValue(null, "meet_rate"));
                                    key.min_level = Long.parseLong(parser.getAttributeValue(null, "min_level"));
                                }
                                break;
                            case "monster":
                                monster = new Monster();
                                key = new MonsterKey();
                                break;
                            case "name":
                                if (monster != null) {
                                    monster.setType(parser.nextText());
                                }
                                break;
                            case "index":
                                int currentIndex = Integer.parseInt(parser.nextText());
                                if (currentIndex != index) {
                                    monster = null;
                                    nextMonsterTag(parser);
                                    continue loop;
                                } else {
                                    if (key != null)
                                        key.index = currentIndex;
                                    if (monster != null) {
                                        monster.setIndex(currentIndex);
                                    }
                                }
                                break;
                            default:
                                setMonsterBaseProperties(monster, parser);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("monster")) {
                            if (monster != null) {
                                monsterCache.put(key, new WeakReference<>(monster));
                                break loop;
                            }
                        }
                }
                parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            LogHelper.logException(e, "PetMonsterLoder->loadMonsterByIndex{" + index + "}");
        }
        return monster;
    }

    public void init() {
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            try {
                int currentIndex = -1;
                loop:
                while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    switch (parser.getEventType()) {
                        case XmlResourceParser.START_TAG:
                            switch (parser.getName()) {
                                case "index":
                                    currentIndex = Integer.parseInt(parser.nextText());
                                    break;
                                case "meet":
                                    if (currentIndex > 0) {
                                        MonsterKey key = new MonsterKey();
                                        key.meet_rate = Float.parseFloat(parser.getAttributeValue(null, "meet_rate"));
                                        key.min_level = Long.parseLong(parser.getAttributeValue(null, "min_level"));
                                        key.index = currentIndex;
                                        monsterCache.put(key, new WeakReference<>((Monster) null));
                                        currentIndex = -1;
                                        nextMonsterTag(parser);
                                        continue loop;
                                    }
                                    break;
                            }
                            break;
                    }
                    parser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                LogHelper.logException(e, "PetMonsterLoder->init");
            }
        }
    }

    @Override
    public Random getRandom() {
        return control.getRandom();
    }

    public Map<MonsterKey, WeakReference<Monster>> getMonsterCache() {
        return monsterCache;
    }

    private ArrayList<MonsterKey> getAvaiableMonsterKey(long level, boolean addKey) {
        ArrayList<MonsterKey> keys = new ArrayList<>();
        for (MonsterKey key : getMonsterCache().keySet()) {
            if (!addKey || key.min_level <= level) {
                keys.add(key);
            }
        }
        return keys;
    }

    private void nextMonsterTag(XmlResourceParser parser) {
        try {
            parser.next();
            while (parser.getEventType() != XmlResourceParser.START_TAG && !"monster".equalsIgnoreCase(parser.getName())) {
                parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            LogHelper.logException(e, "PetMonsterLoder->nextMonsterTag");
        }

    }

    private void setMonsterBaseProperties(Monster monster, XmlResourceParser parser) throws IOException, XmlPullParserException {
        switch (parser.getName()) {
            case "rank":
                if (monster != null) {
                    monster.setRank(Integer.parseInt(parser.nextText()));
                }
                break;
            case "atk":
                if (monster != null) {
                    monster.setAtk(Long.parseLong(parser.nextText()));
                }
                break;
            case "def":
                if (monster != null) {
                    monster.setDef(Long.parseLong(parser.nextText()));
                }
                break;
            case "hp":
                if (monster != null) {
                    monster.setMaxHp(Long.parseLong(parser.nextText()));
                    monster.setHp(monster.getMaxHp());
                }
                break;
            case "hit":
                if (monster != null) {
                    monster.setHitRate(Float.parseFloat(parser.nextText()));
                }
                break;
            case "silent":
                if (monster != null) {
                    monster.setSilent(Float.parseFloat(parser.nextText()));
                }
                break;
            case "pet_rate":
                if (monster != null) {
                    monster.setPetRate(Float.parseFloat(parser.nextText()));
                }
                break;
            case "egg_rate":
                if (monster != null) {
                    monster.setEggRate(Float.parseFloat(parser.nextText()));
                }
                break;
            case "sex":
                if (monster != null) {
                    monster.setSex(Integer.parseInt(parser.nextText()));
                }
                break;
            case "race":
                if (monster != null) {
                    monster.setRace(Race.valueOf(parser.nextText()));
                }
                break;
        }
    }
}
