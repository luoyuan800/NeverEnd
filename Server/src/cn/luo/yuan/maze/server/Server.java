package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.model.Pet;
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
import cn.luo.yuan.maze.server.model.SingleMessage;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.GroupTable;
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
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cn.luo.yuan.maze.utils.Field.*;
import static spark.Spark.post;

public class Server {


    /**
     * Created by gluo on 6/1/2017.
     */

    public static void main(String... args) throws IOException, ClassNotFoundException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        File root = new File("data");
        GroupTable groupTable = new GroupTable(root);
        HeroTable heroTable = new HeroTable(root);
        WarehouseTable warehouseTable = new WarehouseTable(root);
        executor.scheduleAtFixedRate(warehouseTable,0, 1, TimeUnit.DAYS);
        ExchangeTable exchangeTable = new ExchangeTable(root);
        ObjectTable<Task> taskTable = new ObjectTable<>(Task.class, root);
        post("submit_exchange", (request, response) -> {
            Object ex = readObject(request);
            Object exchange = null;
            if (!(ex instanceof ExchangeObject) && ex instanceof IDModel) {
                exchange = new ExchangeObject((IDModel) ex, request.headers(Field.OWNER_ID_FIELD));
            } else {
                exchange = ex;
            }

            if (exchange instanceof ExchangeObject) {
                if (ex instanceof Pet) {
                    ((ExchangeObject) exchange).setType(1);
                } else if (ex instanceof Accessory) {
                    ((ExchangeObject) exchange).setType(2);
                } else if (ex instanceof Goods) {
                    ((ExchangeObject) exchange).setType(3);
                }
                ((ExchangeObject) exchange).setSubmitTime(System.currentTimeMillis());
                if (exchangeTable.addExchange((ExchangeObject) exchange)) {
                    response.header(Field.RESPONSE_TYPE, RESPONSE_NONE_TYPE);
                    response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
                    return Field.RESPONSE_RESULT_SUCCESS;
                } else {
                    response.header(RESPONSE_TYPE, RESPONSE_STRING_TYPE);
                    response.header(RESPONSE_CODE, Field.STATE_FAILED.toString());
                    return "Could not add exchange, maybe there already an exchange object has the same id existed!";
                }
            } else {
                response.header(Field.RESPONSE_TYPE, RESPONSE_STRING_TYPE);
                response.header(Field.RESPONSE_CODE, Field.STATE_FAILED);
                return "Could not add exchange, Wrong type!";
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
            Hero hero = (Hero) readObject(request);
            Maze maze = (Maze) readObject(request);
            if(hero!=null && maze!=null) {
                heroTable.saveHero(hero);
                heroTable.saveMaze(maze,hero.getId());
                heroTable.saveMessager(new SingleMessage(), hero.getId());
                return RESPONSE_RESULT_SUCCESS;
            }else{
                return RESPONSE_RESULT_FAILED;
            }
        }));
        //run(heroTable, groupTable)
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

    private static Object readObject(Request request) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(request.bodyAsBytes()))) {
            return ois.readObject();
        }
    }

    public static void stop() {
        Spark.stop();
    }

    //Only use for unit test
    public static void clear() throws IOException, ClassNotFoundException {
        File root = new File("data");
        GroupTable groupTable = new GroupTable(root);
        groupTable.getGroupDb().clear();
        HeroTable heroTable = new HeroTable(root);
        heroTable.clear();
        ExchangeTable exchangeTable = new ExchangeTable(root);
        exchangeTable.getExchangeDb().clear();
    }
}