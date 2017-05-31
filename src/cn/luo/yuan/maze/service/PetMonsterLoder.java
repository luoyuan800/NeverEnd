package cn.luo.yuan.maze.service;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class PetMonsterLoder implements MonsterLoader {
    private static PetMonsterLoder instance;
    private GameContext control;
    private ArrayMap<MonsterKey, WeakReference<Monster>> monsterCache = new ArrayMap<>();

    private PetMonsterLoder(GameContext control) {
        this.control = control;
        init();
    }

    public static PetMonsterLoder getOrCreate(GameContext control) {
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
        Drawable drawable = Resource.loadImageFromAssets("monster/" + id);
        if (drawable == null) {
            drawable = Resource.loadImageFromAssets("monster/" + id + ".jpg");
        }
        if (drawable == null) {
            drawable = Resource.loadImageFromAssets("monster/" + id + ".png");
        }
        if (drawable == null) {
            drawable = Resource.loadImageFromAssets("monster/wenhao.jpg");
        }
        return drawable;
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
                            if (name != null && name.equals(type)) {
                                return parser.nextText();
                            }
                            if (monsterIndex > 0 && monsterIndex == index) {
                                return parser.nextText();
                            }
                            nextMonsterTag(parser);
                            continue loop;
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
                            int currentEveIndex = Integer.parseInt(parser.nextText());
                            if (monsterIndex > 0 && monsterIndex == index) {
                                evolutionIndex = currentEveIndex;
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

    private void nextMonsterTag(XmlResourceParser parser) {
        try {
            parser.next();
            while (parser.getEventType() != XmlResourceParser.START_TAG && !parser.getName().equalsIgnoreCase("monster")) {
                parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            LogHelper.logException(e, "PetMonsterLoder->nextMonsterTag");
        }

    }

    private void setMonsterBaseProperties(Monster monster, XmlResourceParser parser) throws IOException, XmlPullParserException {
        switch (parser.getName()) {
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
