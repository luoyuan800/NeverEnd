package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.original.AgiEffect;
import cn.luo.yuan.maze.model.effect.original.AtkEffect;
import cn.luo.yuan.maze.model.effect.original.DefEffect;
import cn.luo.yuan.maze.model.effect.original.HpEffect;
import cn.luo.yuan.maze.model.effect.original.MeetRateEffect;
import cn.luo.yuan.maze.model.effect.original.PetRateEffect;
import cn.luo.yuan.maze.model.effect.original.SkillRateEffect;
import cn.luo.yuan.maze.model.effect.original.StrEffect;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import cn.luo.yuan.maze.server.persistence.WarehouseTable;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import cn.luo.yuan.maze.task.Task;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.luo.yuan.maze.utils.Field.*;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

public class Server {


    /**
     * Created by gluo on 6/1/2017.
     */
    private File root = new File("data");
    private File heroDir = new File(root, "hero");
    private WarehouseTable warehouseTable = new WarehouseTable(root);
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    private Map<String, HeroTable> heroTableCache = initHeroTableCache(heroDir);
    private ExchangeTable exchangeTable = new ExchangeTable(root);
    private ObjectTable<Task> taskTable = new ObjectTable<>(Task.class, root);

    public static void main(String...args){
        try {
            new Server().run();
        } catch (Exception e) {
            LogHelper.error(e);
        }
    }
    private void run() throws IOException, ClassNotFoundException {
        LogHelper.info("starting");
        port(166);
        executor.scheduleAtFixedRate(warehouseTable,0, 1, TimeUnit.DAYS);

        post("submit_exchange", (request, response) -> {
            Object ex = readObject(request);
            String ownerId = request.headers(Field.OWNER_ID_FIELD);
            if (exchangeTable.addExchange(ex, ownerId)) {
                response.header(Field.RESPONSE_TYPE, RESPONSE_NONE_TYPE);
                response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
                return Field.RESPONSE_RESULT_SUCCESS;
            } else {
                response.header(RESPONSE_TYPE, RESPONSE_STRING_TYPE);
                response.header(RESPONSE_CODE, Field.STATE_FAILED.toString());
                return "Could not add exchange, maybe there already an exchange object has the same id existed!";
            }
        });

        post("exchange_pet_list", (request, response) -> {
            List<ExchangeObject> pets = exchangeTable.loadAll(1);
            response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
            response.header(Field.RESPONSE_TYPE, RESPONSE_OBJECT_TYPE);
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(pets);
            oos.flush();
            oos.close();

            return Field.RESPONSE_RESULT_OK;
        });

        post("exchange_accessory_list", (request, response) -> {
            List<ExchangeObject> pets = exchangeTable.loadAll(2);
            response.header(RESPONSE_CODE, Field.STATE_SUCCESS);
            response.header(RESPONSE_TYPE, RESPONSE_OBJECT_TYPE);
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(pets);
            oos.flush();
            oos.close();

            return Field.RESPONSE_RESULT_OK;
        });

        post("exchange_goods_list", (request, response) -> {
            List<ExchangeObject> pets = exchangeTable.loadAll(3);
            response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
            response.header(Field.RESPONSE_TYPE, RESPONSE_OBJECT_TYPE);
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(pets);
            oos.flush();
            oos.close();

            return Field.RESPONSE_RESULT_OK;
        });

        post("query_my_exchange", (request, response) -> {
            List<ExchangeObject> exs = exchangeTable.loadAll(request.headers("owner_id"));
            response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
            writeObject(response, exs);
            return Field.RESPONSE_RESULT_OK;
        });

        post("acknowledge_my_exchange", (request, response) -> {
            ExchangeObject exchangeMy = exchangeTable.loadObject(request.headers(EXCHANGE_ID_FIELD));
            if (exchangeMy != null) {
                exchangeMy.setAcknowledge(true);
                if (exchangeMy.getChanged() != null && exchangeMy.getAcknowledge()) {
                    exchangeTable.removeObject(exchangeMy);
                }
                response.header(RESPONSE_TYPE, RESPONSE_NONE_TYPE);
                response.header(RESPONSE_CODE, STATE_SUCCESS);
                return Field.RESPONSE_RESULT_OK;
            } else {
                return "Error object could not found!";
            }
        });


        post("get_back_exchange", ((request, response) -> {
            ExchangeObject exchangeMy = exchangeTable.loadObject(request.headers(EXCHANGE_ID_FIELD));
            if(exchangeMy==null){
                response.header(RESPONSE_CODE, STATE_FAILED);
                return "Could not found special exchange!";
            }
            try {
                exchangeMy.getLock().tryLock();
                if (exchangeMy.getChanged() != null) {
                    response.header(RESPONSE_CODE, STATE_ACKNOWLEDGE);
                    writeObject(response, exchangeMy);
                    return "Exchange has been change, you need to acknowledge it!";
                }
                exchangeTable.removeObject(exchangeMy);
                return RESPONSE_RESULT_SUCCESS;
            }finally {
                exchangeMy.getLock().unlock();
            }
        }));

        post("request_exchange", ((request, response) -> {
            Object objMy = readObject(request);
            ExchangeObject exServer = exchangeTable.loadObject(request.headers(EXCHANGE_ID_FIELD));
            if (objMy == null) {
                response.header(Field.RESPONSE_CODE, Field.STATE_FAILED);
                return "Exchange Object submit error!";
            }
            if (exServer == null) {
                response.header(RESPONSE_CODE, Field.STATE_FAILED);
                return "Could not find Object your request!";
            }
            ExchangeObject exMy = createExchangeObject(request.headers(OWNER_ID_FIELD), objMy);
            if (exServer.change(exMy)) {
                exMy.setChanged(exServer);
                exMy.setChangedTime(System.currentTimeMillis());
                exMy.setAcknowledge(true);
                exchangeTable.addExchange(exMy);
                response.header(RESPONSE_CODE, STATE_SUCCESS);
                return Field.RESPONSE_RESULT_OK;
            }
            return "Could not do exchange because the target has been changed by other!";
        }));

        post("task_version", ((request, response) -> taskTable.size()));

        post("retrieve_new_task", ((request, response) -> {
            int clientVersion = Integer.parseInt(request.headers(Field.LOCAL_TASK_VERSION));
            response.header(RESPONSE_CODE, STATE_SUCCESS);
            if(clientVersion < taskTable.size()){
                writeObject(response,taskTable.loadLimit(clientVersion, taskTable.size() - clientVersion, null, (o1, o2) -> o1.getId().compareTo(o2.getId())));
            }
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("add_task", ((request, response) -> {
            Task task = new Task(request.queryParams("name"), request.queryParams("desc"));
            task.setId(task.getName());
            task.setMaterial(Integer.parseInt(request.queryParams("material")));
            task.setPoint(Integer.parseInt(request.queryParams("point")));
            if(StringUtils.isNotEmpty(request.queryParams("accessory_name"))){
                Accessory accessory = new Accessory();
                accessory.setName(request.queryParams("accessory_name"));
                accessory.setColor(request.queryParams("accessory_color"));
                for(String effect : request.queryParams("accessory_effects").split(";")){
                    String[] ev = effect.split(":");
                    if(ev.length > 1){
                        accessory.getEffects().add(buildEffect(ev[0], ev[1]));
                    }
                }
                accessory.setLevel(Long.parseLong(request.queryParams("accessory_level")));
                accessory.setElement(Element.getByName(request.queryParams("accessory_element")));
                accessory.setType(request.queryParams("accessory_type"));
            }
            //TODO Pet
            taskTable.save(task, task.getId());
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("store_warehouse", ((request, response) -> {
            Object o = readObject(request);
            if(o!=null){
                warehouseTable.store(o);
            }
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("retrieve_back_warehouse", ((request, response) -> {
            Object o = warehouseTable.retrieve(request.headers(WAREHOUSE_ID_FIELD), Integer.parseInt(request.headers(WAREHOUSE_TYPE_FIELD)));
            if(o instanceof OwnedAble){
                if(((OwnedAble) o).getKeeperId().equals(request.headers(OWNER_ID_FIELD))){
                    writeObject(response, o);
                    warehouseTable.delete(o);
                }else{
                    response.header(RESPONSE_CODE, NOT_YOUR_ITEM);
                    return RESPONSE_RESULT_FAILED;
                }
            }
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("retrieve_warehouse",((request, response) -> {
            writeObject(response, warehouseTable.retrieveAll(request.headers(OWNER_ID_FIELD)));
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("submit_hero", ((request, response) ->{
            ServerData data = readObject(request);
            if(data!=null && data.hero!=null && data.maze!=null) {
                data.maze.setId(data.hero.getId());
                HeroTable table = heroTableCache.get(data.hero.getId());
                if(table == null){
                    table = new HeroTable(new File(heroDir,data.hero.getId()));
                    heroTableCache.put(data.hero.getId(), table);
                }
                table.submitHero(data);
                return RESPONSE_RESULT_SUCCESS;
            }else{
                return RESPONSE_RESULT_FAILED;
            }
        }));

        post("get_back_hero", ((request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            if(StringUtils.isNotEmpty(id)){
                HeroTable table = heroTableCache.get(id);
                if(table!=null){
                    heroTableCache.remove(id);
                    ServerData data = table.getBackHero(id);
                    writeObject(response,data);
                    return Field.RESPONSE_RESULT_SUCCESS;
                }
            }
            return Field.RESPONSE_RESULT_FAILED;
        }));

        post("query_hero_data", ((request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            if(StringUtils.isNotEmpty(id)){
                HeroTable table = heroTableCache.get(id);
                if(table!=null){
                    ServerRecord record = table.getRecord(id);
                    writeObject(response,record.getData());
                    return Field.RESPONSE_RESULT_SUCCESS;
                }
            }
            return Field.RESPONSE_RESULT_FAILED;
        }));

        post("query_battle_award", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            if(StringUtils.isNotEmpty(id)){
                HeroTable table = heroTableCache.get(id);
                if(table!=null){
                    return table.queryBattleAward(id);
                }
            }
            return StringUtils.EMPTY_STRING;
        });

        post("pool_online_data_msg", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            if(StringUtils.isNotEmpty(id)){
                HeroTable table = heroTableCache.get(id);
                if(table!=null){
                    return table.queryDataString(id);
                }
            }
            return StringUtils.EMPTY_STRING;
        });

        post("pool_battle_msg", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            int count = Integer.parseInt(request.headers(Field.COUNT));
            if(StringUtils.isNotEmpty(id)){
                HeroTable table = heroTableCache.get(id);
                if(table!=null){
                    return table.pollBattleMsg(id, count);
                }
            }
            return StringUtils.EMPTY_STRING;
        });

        get("/stop",((request, response) -> {
            if("gavin.luo".equals(request.queryParams("pass"))){
                stop();
            }
            return RESPONSE_RESULT_OK;
        }));

        get("/status",((request, response) -> heroTableCache.size()));

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new HeroBattleService(new HashMap<String, HeroTable>(heroTableCache)).run();
            }
        },0, 1, TimeUnit.MINUTES);
        LogHelper.info("started");
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

    private static void writeObject(Response response, Object exs) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        oos.writeObject(exs);
        response.header(Field.RESPONSE_TYPE, RESPONSE_OBJECT_TYPE);
        oos.flush();
        oos.close();
    }

    @NotNull
    private static ExchangeObject createExchangeObject(String ownerId, Object objMy) throws Exception {
        ExchangeObject exMy = new ExchangeObject((IDModel) objMy, ownerId);
        if (objMy instanceof Pet) {
            exMy.setType(1);
        } else if (objMy instanceof Accessory) {
            exMy.setType(2);
        } else if (objMy instanceof Goods) {
            exMy.setType(3);
        } else {
            throw new Exception("Wrong exchange type!");
        }
        exMy.setSubmitTime(System.currentTimeMillis());
        return exMy;
    }

    private static <T> T readObject(Request request) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(request.bodyAsBytes()))) {
            return (T)ois.readObject();
        }
    }

     void stop() {
        LogHelper.info("shutDown");
        executor.shutdown();
        for(HeroTable table : heroTableCache.values()){
            try {
                table.save();
            } catch (IOException e) {
                LogHelper.error(e);
            }
        }
        Spark.stop();
    }

    //Only use for unit test
    static void clear() throws IOException, ClassNotFoundException {
        File root = new File("data");
        root.delete();
    }

    private static Map<String, HeroTable> initHeroTableCache(File root){
        Map<String, HeroTable> cache = new HashMap<>();
        if(root.exists()) {
            for (String name : root.list()) {
                try {
                    HeroTable table = new HeroTable(new File(root, name));
                    if (table.getHero(name, 0) != null) {
                        cache.put(name, table);
                    }
                } catch (Exception e) {
                    LogHelper.error(e);
                }
            }
        }else{
            LogHelper.info("create root file " + root.getAbsolutePath() + ", result: " + root.mkdirs());
        }
        return cache;
    }
}