package cn.luo.yuan.maze.service;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class PetMonsterHelper {
    private InfoControl control;

    public PetMonsterHelper(InfoControl control) {
        this.control = control;
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

    public Monster randomMonster() {
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            try {
                Monster monster = null;
                loop: while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    switch (parser.getEventType()) {
                        case XmlResourceParser.START_TAG:
                            switch (parser.getName()) {
                                case "monster":
                                    monster = new Monster();
                                    monster.setSex(control.getRandom().nextInt(2));
                                    monster.setElement(Element.values()[control.getRandom().nextInt(Element.values().length)]);
                                    break;
                                case "name":
                                    if (monster != null) {
                                        monster.setType(parser.nextText());
                                    }
                                    break;
                                case "index":
                                    if (monster != null) {
                                        monster.setIndex(Integer.parseInt(parser.nextText()));
                                    }
                                    break;
                                case "meet":
                                    if (monster != null) {
                                        if (control.getMaze().getLevel() < Long.parseLong(parser.getAttributeValue(null, "min_level")) || control.getRandom().nextInt(100) > Integer.parseInt(parser.getAttributeValue(null, "meet_rate"))) {
                                            monster = null;
                                            nextMonsterTag(parser);
                                            continue loop;
                                        }
                                    }
                                    break;
                                default:
                                    setMonsterBaseProperties(monster, parser);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equalsIgnoreCase("monster")) {
                                if (monster != null) {
                                    return monster;
                                }
                            }
                    }
                    parser.next();
                }
            } catch (XmlPullParserException e) {

            } catch (IOException e) {
                e.printStackTrace();
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
            loop : while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
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

    public boolean upgrade(Pet major, Pet minor){
        if(major!=minor && control.getRandom().nextLong(major.getLevel()) + control.getRandom().nextLong(minor.getLevel()/10) < Data.PET_UPGRADE_LIMIT){
            major.setLevel(major.getLevel() + 1);
            long atk = major.getAtk() + control.getRandom().nextLong(minor.getAtk() * (minor.getLevel() + 1) / 2);
            if(atk > 0) {
                major.setAtk(atk);
            }
            long def = major.getDef() + control.getRandom().nextLong(minor.getDef() * (minor.getLevel() + 1) / 2);
            if(def > 0) {
                major.setDef(def);
            }
            long maxHP = major.getMaxHP() + control.getRandom().nextLong(minor.getMaxHP() * (minor.getLevel() + 1) / 2);
            if(maxHP > 0) {
                major.setMaxHP(maxHP);
            }
            return true;
        }
        return false;
    }

    public boolean evolution(Pet pet){
        int eveIndex = pet.getIndex();
        try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
            int monsterIndex = -1;
            String name = null;
            loop: while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    switch (parser.getName()) {
                        case "name":
                            if (parser.nextText().equals(pet.getType())) {
                                name = parser.nextText();
                            }else{
                                nextMonsterTag(parser);
                                continue loop;
                            }
                            break;
                        case "index":
                            if (Integer.parseInt(parser.nextText()) == pet.getIndex()) {
                                monsterIndex = Integer.parseInt(parser.nextText());
                            }else{
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
        if(eveIndex!=pet.getIndex()) {
            Monster eveMonster = null;
            try (XmlResourceParser parser = control.getContext().getResources().getXml(R.xml.monsters)) {
                loop: while (parser.getEventType()!=XmlPullParser.END_DOCUMENT){
                    switch (parser.getEventType()){
                        case XmlResourceParser.START_TAG:
                            switch (parser.getName()){
                                case "monster":
                                    eveMonster = new Monster();
                                    break;
                                case "name":
                                    if(eveMonster!=null) {
                                        eveMonster.setType(parser.nextText());
                                    }
                                    break;
                                case "index":
                                    if(Integer.parseInt(parser.nextText()) != eveIndex){
                                        eveMonster = null;
                                        nextMonsterTag(parser);
                                        continue loop;
                                    }
                                    break;
                                default:
                                    setMonsterBaseProperties(eveMonster, parser);
                            }
                            case XmlPullParser.END_TAG:
                                if(parser.getName().equals("monster")){
                                    if(eveMonster!=null){
                                        break loop;
                                    }
                                }
                    }
                    parser.next();
                }
                if(eveMonster!=null){
                    pet.setIndex(eveMonster.getIndex());
                    pet.setType(eveMonster.getType());
                    pet.setAtk(pet.getAtk() + control.getRandom().nextLong(eveMonster.getAtk()/3));
                    pet.setDef(pet.getDef() + control.getRandom().nextLong(eveMonster.getDef()/3));
                    pet.setMaxHP(pet.getMaxHP() + control.getRandom().nextLong(eveMonster.getMaxHP()/3));
                    pet.setHitRate((pet.getHitRate() + eveMonster.getHitRate())/2);
                    pet.setEggRate((pet.getEggRate() + eveMonster.getEggRate())/2);
                    return true;
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return false;
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
}
