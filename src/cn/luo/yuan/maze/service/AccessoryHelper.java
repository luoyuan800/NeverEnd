package cn.luo.yuan.maze.service;


import android.content.Context;
import android.content.res.XmlResourceParser;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.effect.AgiEffect;
import cn.luo.yuan.maze.model.effect.Effect;
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
    public AccessoryHelper(Context context){
        this.context = context;
        random = new Random(System.currentTimeMillis());
    }
    public void fuse(){

    }

    public List<Accessory> loadFromAssets(){
        List<Accessory> accessories = new ArrayList<>();
        try (XmlResourceParser parser = context.getAssets().openXmlResourceParser("accessory")){
            Accessory accessory = null;
            List<Effect> effects = null;
            while (parser.getEventType()!=XmlResourceParser.END_DOCUMENT){
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
                                    switch (name) {
                                        case "AgiEffect":
                                            AgiEffect agiEffect = new AgiEffect();
                                            agiEffect.setAgi(min + random.nextLong(max - min));
                                            effects.add(agiEffect);
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("accessory")) {
                                accessories.add(accessory);
                                accessory = null;
                                effects = null;
                            }
                    }
                }catch (Exception e){

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
