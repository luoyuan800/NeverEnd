package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.effect.AgiEffect;
import cn.luo.yuan.maze.model.effect.AtkEffect;
import cn.luo.yuan.maze.model.effect.DefEffect;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.HpEffect;
import cn.luo.yuan.maze.model.effect.MeetRateEffect;
import cn.luo.yuan.maze.model.effect.PetRateEffect;
import cn.luo.yuan.maze.model.effect.SkillRateEffect;
import cn.luo.yuan.maze.model.effect.StrEffect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.LogHelper;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 6/8/2017.
 */
public class ExchangeManager {
    private RestConnection server;
    private GameContext context;

    public ExchangeManager(GameContext context) {
        this.context = context;
        server = new RestConnection(Field.SERVER_URL, context.getVersion());
    }

    private Accessory covertAccessoryToLocal(Accessory a){
        List<Effect> effects = new ArrayList<>(a.getEffects());
        a.getEffects().clear();
        for(Effect effect : effects){
            a.getEffects().add(buildEffect(effect.getName(), effect.getValue().toString()));
        }
        return a;
    }

    private static Effect buildEffect(String effectName, String value){
        switch (effectName) {
            case "SkillRateEffect":
                SkillRateEffect skillRateEffect = new SkillRateEffect();
                skillRateEffect.setSkillRate(Float.parseFloat(value));
                return skillRateEffect;
            case "AgiEffect":
                AgiEffect agiEffect = new AgiEffect();
                agiEffect.setAgi(Long.parseLong(value));
                return agiEffect;
            case "AtkEffect":
                AtkEffect atkEffect = new AtkEffect();
                atkEffect.setAtk(Long.parseLong(value));
                return atkEffect;
            case "DefEffect":
                DefEffect defEffect = new DefEffect();
                defEffect.setDef(Long.parseLong(value));
                return defEffect;
            case "HpEffect":
                HpEffect hpEffect = new HpEffect();
                hpEffect.setHp(Long.parseLong(value));
                return hpEffect;
            case "StrEffect":
                StrEffect strEffect = new StrEffect();
                strEffect.setStr(Long.parseLong(value));
                return strEffect;
            case "MeetRateEffect":
                MeetRateEffect meetRateEffect = new MeetRateEffect();
                meetRateEffect.setMeetRate(Float.parseFloat(value));
                return meetRateEffect;
            case "PetRateEffect":
                PetRateEffect petRateEffect = new PetRateEffect();
                petRateEffect.setPetRate(Float.parseFloat(value));
                return petRateEffect;
        }
        return null;
    }

    public boolean submitExchange(Serializable object) {
        if(object instanceof Accessory){
            Accessory accessory = new Accessory();
            accessory.setName(((Accessory) object).getName());
            accessory.setId(((Accessory) object).getId());
            accessory.setColor(((Accessory) object).getColor());
            accessory.setElement(((Accessory) object).getElement());
            accessory.setLevel(((Accessory) object).getLevel());
            accessory.setType(((Accessory) object).getType());
            for(Effect effect : ((Accessory) object).getEffects()){
                accessory.getEffects().add(effect.covertToOriginal());
            }
            object = accessory;
        }
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/submit_exchange", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            server.connect(object, connection);
            if (connection.getHeaderField(Field.RESPONSE_CODE).equals(Field.STATE_SUCCESS)) {
                context.getDataManager().delete(object);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ExchangeObject> querySubmittedExchangeOfMine() {
        try {
            HttpURLConnection urlConnection = server.getHttpURLConnection("/query_my_exchange", RestConnection.POST);
            urlConnection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return (List<ExchangeObject>) server.connect(urlConnection);
        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return null;
    }

    public <T> T unBox(ExchangeObject eo){
        IDModel model = eo.getExchange();
        if(model instanceof Accessory){
            covertAccessoryToLocal((Accessory) model);
        }
        return (T)model;
    }

    public <T> T getBackMyExchange(String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/get_back_exchange", RestConnection.POST);
            connection.addRequestProperty(Field.EXCHANGE_ID_FIELD, id);
            Object object = server.connect(connection);
            if(object instanceof Accessory){
                object = covertAccessoryToLocal((Accessory) object);
            }
            return (T) object;
        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return null;
    }

    public List<ExchangeObject> queryAvailableExchanges(int limitType) {
        List<ExchangeObject> eos = new ArrayList<>();
        try {
            HttpURLConnection connection;
            if (limitType == Field.PET_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection("/exchange_pet_list", RestConnection.POST);
                eos.addAll((List<ExchangeObject>) server.connect(connection));
            }
            if (limitType == Field.ACCESSORY_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection("/exchange_accessory_list", RestConnection.POST);
                eos.addAll((List<ExchangeObject>) server.connect(connection));
            }
            if (limitType == Field.GOODS_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection("/exchange_goods_list", RestConnection.POST);
                eos.addAll((List<ExchangeObject>) server.connect(connection));
            }

        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return eos;
    }

    public boolean requestExchange(Serializable myObject, ExchangeObject targetExchange) {
        try {
            HttpURLConnection conn = server.getHttpURLConnection("/request_exchange", RestConnection.POST);
            conn.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            conn.addRequestProperty(Field.EXCHANGE_ID_FIELD, targetExchange.getId());
            server.connect(myObject,conn);
            if(conn.getHeaderField(Field.RESPONSE_CODE).equals(Field.STATE_SUCCESS)){
                return true;
            }
        }catch (Exception e){
            LogHelper.logException(e, "");
        }
        return false;
    }

}
