package cn.luo.yuan.maze.service;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class PetMonsterHelper {
    private static PetMonsterHelper instance;
    private InfoControl control;
    private ArrayMap<MonsterKey, WeakReference<Monster>> monsterCache = new ArrayMap<>();

    private PetMonsterHelper(InfoControl control) {
        this.control = control;
    }

    public static PetMonsterHelper getOrCreate(InfoControl control) {
        if (instance == null) {
            synchronized (PetMonsterHelper.class) {
                if (instance == null) {
                    instance = new PetMonsterHelper(control);
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

    public static Pet monsterToPet(Monster monster, Hero hero) {
        Pet pet = new Pet();
        for (Method method : Monster.class.getMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    Method set = Pet.class.getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
                    set.invoke(pet, method.invoke(monster));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    LogHelper.logException(e, false, "Error while transforming monster to pet");
                }
            }
        }
        pet.setOwnerId(hero.getId());
        pet.setOwnerName(hero.getName());
        return pet;
    }

    public static boolean isCatchAble(Monster monster, Hero hero, Random random, int petCount) {
        if (monster.getRace().ordinal() != hero.getRace().ordinal() + 1 || monster.getRace().ordinal() != hero.getRace().ordinal() - 5) {
            float rate = (100 - monster.getPetRate()) + random.nextInt(petCount + 1) / 10f;
            float current = random.nextInt(100) + random.nextFloat() + EffectHandler.getEffectAdditionFloatValue(EffectHandler.PET_RATE, hero.getEffects());
            if (current >= 100) {
                current = 98.9f;
            }
            return current > rate;
        } else {
            return false;
        }
    }

    public Monster randomMonster() {
        if (monsterCache.size() == 0) {
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
                } catch (XmlPullParserException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (ArrayMap.Entry<MonsterKey, WeakReference<Monster>> entry : monsterCache.entrySet()) {
            MonsterKey key = entry.getKey();
            Monster monster = entry.getValue().get();
            if (key.min_level < control.getMaze().getLevel() && control.getRandom().nextInt(100) < key.meet_rate + key.count) {
                key.count++;
                if (monster == null) {
                    monster = loadMonsterByIndex(key.index);
                }
                if (monster != null)
                    return monster.clone();
            } else {
                key.count--;
            }
        }
        return null;
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
                            if (parser.nextText().equals(type)) {
                                name = parser.nextText();
                            }
                            break;
                        case "index":
                            if (Integer.parseInt(parser.nextText()) == index) {
                                monsterIndex = Integer.parseInt(parser.nextText());
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY_STRING;
    }

    public boolean upgrade(Pet major, Pet minor) {
        if (major != minor && control.getRandom().nextLong(major.getLevel()) + control.getRandom().nextLong(minor.getLevel() / 10) < Data.PET_UPGRADE_LIMIT) {
            major.setLevel(major.getLevel() + 1);
            long atk = major.getAtk() + control.getRandom().nextLong(minor.getAtk() * (minor.getLevel() + 1) / 2);
            if (atk > 0) {
                major.setAtk(atk);
            }
            long def = major.getDef() + control.getRandom().nextLong(minor.getDef() * (minor.getLevel() + 1) / 2);
            if (def > 0) {
                major.setDef(def);
            }
            long maxHP = major.getMaxHP() + control.getRandom().nextLong(minor.getMaxHP() * (minor.getLevel() + 1) / 2);
            if (maxHP > 0) {
                major.setMaxHP(maxHP);
            }
            return true;
        }
        return false;
    }

    public boolean evolution(Pet pet) {
        int eveIndex = pet.getIndex();
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            int monsterIndex = -1;
            String name = null;
            loop:
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    switch (parser.getName()) {
                        case "name":
                            if (parser.nextText().equals(pet.getType())) {
                                name = parser.nextText();
                            } else {
                                nextMonsterTag(parser);
                                continue loop;
                            }
                            break;
                        case "index":
                            if (Integer.parseInt(parser.nextText()) == pet.getIndex()) {
                                monsterIndex = Integer.parseInt(parser.nextText());
                            } else {
                                nextMonsterTag(parser);
                                continue loop;
                            }
                            break;
                        case "evolution":
                            if (name != null && name.equals(pet.getType())) {
                                eveIndex = Integer.parseInt(parser.nextText());
                            }
                            if (monsterIndex > 0 && monsterIndex == pet.getIndex()) {
                                eveIndex = Integer.parseInt(parser.nextText());
                            }
                            break loop;
                    }
                }
                parser.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        if (eveIndex != pet.getIndex()) {
            Monster eveMonster = loadMonsterByIndex(eveIndex);
            if (eveMonster != null) {
                pet.setIndex(eveMonster.getIndex());
                pet.setType(eveMonster.getType());
                pet.setAtk(pet.getAtk() + control.getRandom().nextLong(eveMonster.getAtk() / 3));
                pet.setDef(pet.getDef() + control.getRandom().nextLong(eveMonster.getDef() / 3));
                pet.setMaxHP(pet.getMaxHP() + control.getRandom().nextLong(eveMonster.getMaxHP() / 3));
                pet.setHitRate((pet.getHitRate() + eveMonster.getHitRate()) / 2);
                pet.setEggRate((pet.getEggRate() + eveMonster.getEggRate()) / 2);
                return true;
            }
            return false;
        }
        return false;
    }

    private static class MonsterKey {
        int count;
        float meet_rate;
        float min_level;
        int index;

        public int hashCode() {
            return index;
        }

        public boolean equals(Object o) {
            return o instanceof MonsterKey && ((MonsterKey) o).index == index;
        }
    }

    private Monster loadMonsterByIndex(int index) {
        Monster monster = null;
        MonsterKey key = new MonsterKey();
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            loop:
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                    case XmlResourceParser.START_TAG:
                        switch (parser.getName()) {
                            case "meet":
                                key.meet_rate = Float.parseFloat(parser.getAttributeValue(null, "meet_rate"));
                                key.min_level = Long.parseLong(parser.getAttributeValue(null, "min_level"));
                            case "monster":
                                monster = new Monster();
                                break;
                            case "name":
                                if (monster != null) {
                                    monster.setType(parser.nextText());
                                }
                                break;
                            case "index":
                                if (Integer.parseInt(parser.nextText()) != index) {
                                    monster = null;
                                    nextMonsterTag(parser);
                                    continue loop;
                                } else {
                                    key.index = Integer.parseInt(parser.nextText());
                                }
                                break;
                            default:
                                setMonsterBaseProperties(monster, parser);
                        }
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("monster")) {
                            if (monster != null) {
                                monsterCache.put(key, new WeakReference<Monster>(monster));
                                break loop;
                            }
                        }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return monster;
    }

    private void nextMonsterTag(XmlResourceParser parser) {
        try {
            parser.next();
            while (parser.getEventType() != XmlResourceParser.START_TAG && !parser.getName().equalsIgnoreCase("monster")) {
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setMonsterBaseProperties(Monster monster, XmlResourceParser parser) throws IOException, XmlPullParserException {
        switch (parser.getName()) {
            case "atk":
                if (monster != null) {
                    monster.setAtk(Long.parseLong(parser.nextText()));
                }
            case "def":
                if (monster != null) {
                    monster.setDef(Long.parseLong(parser.nextText()));
                }
                break;
            case "hp":
                if (monster != null) {
                    monster.setMaxHP(Long.parseLong(parser.nextText()));
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
