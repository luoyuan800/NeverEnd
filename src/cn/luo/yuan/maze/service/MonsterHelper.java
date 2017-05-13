package cn.luo.yuan.maze.service;

import android.content.res.XmlResourceParser;
import android.util.Xml;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.utils.annotation.LongValue;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by luoyuan on 2017/5/13.
 */
public class MonsterHelper {
    private InfoControl control;
    public MonsterHelper(InfoControl control){
        this.control = control;
    }

    public Monster randomMonster(){
        try(XmlResourceParser parser = control.getContext().getAssets().openXmlResourceParser("./monster/monsters.xml")) {
            try {
                Monster monster = null;
                while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    switch (parser.getEventType()){
                        case XmlResourceParser.START_TAG:
                            switch (parser.getName()){
                                case "monster":
                                    monster = new Monster();
                                    monster.setSex(control.getRandom().nextInt(2));
                                    break;
                                case "name":
                                    if(monster!=null) {
                                        monster.setType(parser.getText());
                                    }
                                    break;
                                case "index":
                                    if(monster!=null){
                                        monster.setIndex(Integer.parseInt(parser.getText()));
                                    }
                                    break;
                                case "atk":
                                    if(monster!=null){
                                        monster.setAtk(Long.parseLong(parser.getText()));
                                    }
                                    break;
                                case "def":
                                    if(monster!=null){
                                        monster.setDef(Long.parseLong(parser.getText()));
                                    }
                                    break;
                                case "hp":
                                    if(monster!=null){
                                        monster.setMaxHP(Long.parseLong(parser.getText()));
                                    }
                                    break;
                                case "meet":
                                    if(monster!=null){
                                        if(control.getMaze().getLevel() < Long.parseLong(parser.getAttributeValue(null, "min_level")) || control.getRandom().nextInt(100) > Integer.parseInt(parser.getAttributeValue(null,"meet_rate"))){
                                            monster = null;
                                            nextMonsterTag(parser);
                                            break;
                                        }
                                    }
                                    break;
                                case "hit":
                                    if(monster!=null){
                                        monster.setHitRate(Float.parseFloat(parser.getText()));
                                    }
                                    break;
                                case "silent":
                                    if(monster!=null){
                                        monster.setSilent(Float.parseFloat(parser.getText()));
                                    }
                                    break;
                                case "pet_rate":
                                    if(monster!=null){
                                        monster.setPetRate(Float.parseFloat(parser.getText()));
                                    }
                                    break;
                                case "egg_rate":
                                    if(monster!=null){
                                        monster.setEggRate(Float.parseFloat(parser.getText()));
                                    }
                                    break;
                                case "sex":
                                    if(monster!=null){
                                        monster.setSex(Integer.parseInt(parser.getText()));
                                    }
                                    break;
                                case "race":
                                    if(monster!=null){
                                        monster.setRace(Race.valueOf(parser.getText()));
                                    }
                                    break;
                            }
                            break;
                            case XmlPullParser.END_TAG:
                                if(parser.getName().equalsIgnoreCase("name")){
                                    if(monster!=null){
                                        return monster;
                                    }
                                }
                    }
                }
            }catch (XmlPullParserException e){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void nextMonsterTag(XmlResourceParser parser){
        try {
            parser.next();
            while (parser.getEventType()!=XmlResourceParser.START_TAG && !parser.getName().equalsIgnoreCase("monster")){
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
