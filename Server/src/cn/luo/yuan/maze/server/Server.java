package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.server.persistence.ExchangeTable;
import cn.luo.yuan.maze.server.persistence.GroupTable;
import cn.luo.yuan.maze.server.persistence.HeroTable;
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
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(request.bodyAsBytes()));
            Object ex= ois.readObject();
            Object exchange = null;
            if(!(ex instanceof ExchangeObject) && ex instanceof IDModel){
                exchange = new ExchangeObject((IDModel) ex,request.headers("owner_id"));
            }else{
                exchange = ex;
            }

            if (exchange instanceof ExchangeObject) {
                if(ex instanceof Pet){
                    ((ExchangeObject)exchange).setType(1);
                }else if(ex instanceof Accessory){
                    ((ExchangeObject)exchange).setType(2);
                }else if(ex instanceof Goods){
                    ((ExchangeObject)exchange).setType(3);
                }
                ((ExchangeObject) exchange).setSubmitTime(System.currentTimeMillis());
                if(exchangeTable.addExchange((ExchangeObject) exchange)){
                    response.header("type", "none");
                    response.header("code", STATE_SUCCESS.toString());
                    return "success!";
                }else {
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

        post("query_my_exchange", (request, response) ->{
                List<ExchangeObject> exs = exchangeTable.loadAll(request.headers("owner_id"));
                ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
                oos.writeObject(exs);
                oos.flush();
                oos.close();
            response.header("code", STATE_SUCCESS.toString());
                response.header("type", "object");
        return "OK";
        });

        post("acknowledge_my_exchange", (request, response) ->{
                ExchangeObject exchangeMy = exchangeTable.loadObject(request.headers("ex_id"));
                exchangeMy.setAcknowledge(true);
        if (exchangeMy.getChanged() != null && exchangeMy.getAcknowledge()) {
            exchangeTable.getExchangeDb().delete(exchangeMy.getId());
            exchangeTable.getExchangeDb().delete(exchangeMy.getExchange().getId());
        }
        response.header("type", "none");
        response.header("code", STATE_SUCCESS.toString());
        return "OK";
        });


        //run(heroTable, groupTable)
    }

    public static void stop() {
        Spark.stop();
    }
}