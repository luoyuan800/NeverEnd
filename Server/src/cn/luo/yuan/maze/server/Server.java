package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.GroupTable;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Spark;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static spark.Spark.post;

public class Server {
    /**
     * Created by gluo on 6/1/2017.
     */
    public static final Integer STATE_SUCCESS = 1, STATE_FAILED = 21;

    public static void main(String... args) throws IOException, ClassNotFoundException {

        File root = new File("data");
        GroupTable groupTable = new GroupTable(root);
        HeroTable heroTable = new HeroTable(root);
        ExchangeTable exchangeTable = new ExchangeTable(root);
        post("submit_exchange", (request, response) -> {
            Object ex = readObject(request);
            Object exchange = null;
            if (!(ex instanceof ExchangeObject) && ex instanceof IDModel) {
                exchange = new ExchangeObject((IDModel) ex, request.headers("owner_id"));
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
                    response.header("type", "none");
                    response.header("code", STATE_SUCCESS.toString());
                    return "success!";
                } else {
                    response.header("type", "string");
                    response.header("code", STATE_FAILED.toString());
                    return "Could not add exchange, maybe there already an exchange object has the same id existed!";
                }
            } else {
                response.header("type", "string");
                response.header("code", STATE_FAILED.toString());
                return "Could not add exchange, Wrong type!";
            }
        });

        post("exchange_pet_list", (request, response) -> {
            List<ExchangeObject> pets = exchangeTable.loadAll(1);
            response.header("code", STATE_SUCCESS.toString());
            response.header("type", "object");
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(pets);
            oos.flush();
            oos.close();

            return "OK";
        });

        post("exchange_accessory_list", (request, response) -> {
            List<ExchangeObject> pets = exchangeTable.loadAll(2);
            response.header("code", STATE_SUCCESS.toString());
            response.header("type", "object");
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(pets);
            oos.flush();
            oos.close();

            return "OK";
        });

        post("exchange_goods_list", (request, response) -> {
            List<ExchangeObject> pets = exchangeTable.loadAll(3);
            response.header("code", STATE_SUCCESS.toString());
            response.header("type", "object");
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(pets);
            oos.flush();
            oos.close();

            return "OK";
        });

        post("query_my_exchange", (request, response) -> {
            List<ExchangeObject> exs = exchangeTable.loadAll(request.headers("owner_id"));

            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            oos.writeObject(exs);
            response.header("code", STATE_SUCCESS.toString());
            response.header("type", "object");
            oos.flush();
            oos.close();

            return "OK";
        });

        post("acknowledge_my_exchange", (request, response) -> {
            ExchangeObject exchangeMy = exchangeTable.loadObject(request.headers("ex_id"));
            if (exchangeMy != null) {
                exchangeMy.setAcknowledge(true);
                if (exchangeMy.getChanged() != null && exchangeMy.getAcknowledge()) {
                    exchangeTable.getExchangeDb().delete(exchangeMy.getId());
                    exchangeTable.getExchangeDb().delete(exchangeMy.getExchange().getId());
                }
                response.header("type", "none");
                response.header("code", STATE_SUCCESS.toString());
                return "OK";
            } else {
                return "Error object could not found!";
            }
        });


        post("request_exchange", ((request, response) -> {
            Object objMy = readObject(request);
            ExchangeObject exServer = exchangeTable.loadObject(request.headers("exchange_id"));
            if (objMy == null) {
                response.header("code", STATE_FAILED.toString());
                return "Exchange Object submit error!";
            }
            if (exServer == null) {
                response.header("code", STATE_FAILED.toString());
                return "Could not find Object your request!";
            }
            ExchangeObject exMy = createExchangeObject(request.headers("owner_id"), objMy);
            if (exServer.change(exMy)) {
                exMy.setChanged(exServer);
                exMy.setChangedTime(System.currentTimeMillis());
                exMy.setAcknowledge(true);
                exchangeTable.addExchange(exMy);
                response.header("code", STATE_SUCCESS.toString());
                return "OK";
            }
            return "Could not do exchange because the target has been changed by other!";
        }));
        //run(heroTable, groupTable)
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