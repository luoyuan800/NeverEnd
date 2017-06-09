package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.GroupTable;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import cn.luo.yuan.maze.utils.Field;
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
import java.util.List;

import static cn.luo.yuan.maze.utils.Field.*;
import static spark.Spark.post;

public class Server {


    /**
     * Created by gluo on 6/1/2017.
     */

    public static void main(String... args) throws IOException, ClassNotFoundException {

        File root = new File("data");
        GroupTable groupTable = new GroupTable(root);
        HeroTable heroTable = new HeroTable(root);
        ExchangeTable exchangeTable = new ExchangeTable(root);
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
        //run(heroTable, groupTable)
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