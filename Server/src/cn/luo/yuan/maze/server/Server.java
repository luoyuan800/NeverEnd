package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static cn.luo.yuan.maze.utils.Field.*;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;
import static spark.SparkBase.staticFileLocation;

public class Server {


    /**
     * Created by gluo on 6/1/2017.
     */

    private MainProcess process = new MainProcess();

    public static void main(String... args) {
        try {
            new Server().run();
        } catch (Exception e) {
            LogHelper.error(e);
        }
    }


    private void run() throws IOException, ClassNotFoundException {
        LogHelper.info("starting");
        port(4568);
        staticFileLocation("/pages");

        post("online_gift_open", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            writeObject(response, process.openOnlineGift(id));
            return RESPONSE_RESULT_OK;
        });
        get("/hero_range", ((request, response) -> process.heroRange));
        post("hero_range", (request, response) -> process.heroRange);
        get("/", ((request, response) -> {
            if (process.user.login) {
                return "";
            } else {
                response.redirect("/login.html");
                return response;
            }
        }));
        post("/login", ((request, response) -> {
            String password = request.queryParams("user_pass");
            if (password != null && String.valueOf(process.user.pass.getValue()).equals(password)) {
                request.session().attribute("user_id", process.user.name);
                request.session().attribute("login", true);
                request.session().attribute("user_name", process.user.name);
                process.user.login = true;
                return RESPONSE_RESULT_OK;
            } else {
                return "Verify failed（校验失败！）";
            }
        }));

        post("submit_exchange", (request, response) -> {
            Object ex = readObject(request);
            String ownerId = request.headers(Field.OWNER_ID_FIELD);
            String limit = readEncodeHeader(request, Field.LIMIT_STRING);
            int expectType = Integer.parseInt(request.headers(Field.EXPECT_TYPE));
            if (process.exchangeTable.addExchange(ex, ownerId, limit, expectType)) {
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
            String limit = readEncodeHeader(request, Field.LIMIT_STRING);
            String keeper = request.headers(Field.OWNER_ID_FIELD);
            List<ExchangeObject> pets = process.exchangeTable.loadAll(1, limit, keeper);
            writeObject(response, pets);
            return Field.RESPONSE_RESULT_OK;
        });

        post("exchange_accessory_list", (request, response) -> {
            String limit = readEncodeHeader(request, Field.LIMIT_STRING);
            String keeper = request.headers(Field.OWNER_ID_FIELD);
            List<ExchangeObject> accessories = process.exchangeTable.loadAll(2, limit, keeper);
            response.header(RESPONSE_CODE, Field.STATE_SUCCESS);
            writeObject(response, accessories);

            return Field.RESPONSE_RESULT_OK;
        });

        post("exchange_goods_list", (request, response) -> {
            String limit = readEncodeHeader(request, Field.LIMIT_STRING);
            String keeper = request.headers(Field.OWNER_ID_FIELD);
            List<ExchangeObject> goodses = process.exchangeTable.loadAll(3, limit, keeper);
            response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
            writeObject(response, goodses);

            return Field.RESPONSE_RESULT_OK;
        });

        post("query_my_exchange", (request, response) -> {
            List<ExchangeObject> exs = process.exchangeTable.loadAll(request.headers("owner_id"));
            response.header(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
            writeObject(response, exs);
            return Field.RESPONSE_RESULT_OK;
        });

        post("acknowledge_my_exchange", (request, response) -> {
            String id = request.headers(EXCHANGE_ID_FIELD);
            if (process.acknowledge(id)) {
                response.header(RESPONSE_TYPE, RESPONSE_NONE_TYPE);
                response.header(RESPONSE_CODE, STATE_SUCCESS);
                return Field.RESPONSE_RESULT_OK;
            } else {
                return "Error object could not found!";
            }
        });


        post("get_back_exchange", ((request, response) -> {
            String id = request.headers(EXCHANGE_ID_FIELD);
            Object backExchange = process.get_back_exchange(id);
            if (backExchange == Integer.valueOf(1)) {
                response.header(RESPONSE_CODE, STATE_FAILED);
                return "Could not found special exchange!";
            } else if (backExchange instanceof ExchangeObject) {
                response.header(RESPONSE_CODE, STATE_ACKNOWLEDGE);
                writeObject(response, backExchange);
                return "Exchange has been change, you need to acknowledge it!";
            } else {
                return backExchange;
            }
        }));

        post("request_exchange", ((request, response) -> {
            Object objMy = readObject(request);
            ExchangeObject exServer = process.exchangeTable.loadObject(request.headers(EXCHANGE_ID_FIELD));
            if (objMy == null) {
                response.header(Field.RESPONSE_CODE, Field.STATE_FAILED);
                return "Exchange Object submit error!";
            }
            if (exServer == null) {
                response.header(RESPONSE_CODE, Field.STATE_FAILED);
                return "Could not find Object your request!";
            }
            String id = request.headers(OWNER_ID_FIELD);
            if (process.requestExchange(objMy, exServer, id)) return Field.RESPONSE_RESULT_OK;
            return "Could not do exchange because the target has been changed by other!";
        }));

        post("task_version", ((request, response) -> process.taskTable.size()));

        post("retrieve_new_task", ((request, response) -> {
            int clientVersion = Integer.parseInt(request.headers(Field.LOCAL_TASK_VERSION));
            response.header(RESPONSE_CODE, STATE_SUCCESS);
            if (clientVersion < process.taskTable.size()) {
                writeObject(response, process.taskTable.loadLimit(clientVersion, process.taskTable.size() - clientVersion, null, (o1, o2) -> o1.getId().compareTo(o2.getId())));
            }
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("add_task", ((request, response) -> {
            try {
                String name = request.queryParams("name");
                String desc = request.queryParams("desc");
                String material = request.queryParams("material");
                String accessory_name = request.queryParams("accessory_name");
                String point = request.queryParams("point");
                String accessory_color = request.queryParams("accessory_color");
                String accessory_level = request.queryParams("accessory_level");
                String accessory_element = request.queryParams("accessory_element");
                String accessory_type = request.queryParams("accessory_type");
                String accessory_effects = request.queryParams("accessory_effects");
                process.addTask(name, desc, material, accessory_name, point, accessory_color, accessory_level, accessory_element, accessory_type, accessory_effects);
                return RESPONSE_RESULT_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return RESPONSE_RESULT_FAILED;
            }
        }));

        post("store_warehouse", ((request, response) -> {
            Object o = readObject(request);
            if (o != null) {
                process.warehouseTable.store(o);
            }
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("retrieve_back_warehouse", ((request, response) -> {
            Object o = process.warehouseTable.retrieve(request.headers(WAREHOUSE_ID_FIELD), Integer.parseInt(request.headers(WAREHOUSE_TYPE_FIELD)));
            if (o instanceof OwnedAble) {
                if (((OwnedAble) o).getKeeperId().equals(request.headers(OWNER_ID_FIELD))) {
                    writeObject(response, o);
                    process.warehouseTable.delete(o);
                } else {
                    response.header(RESPONSE_CODE, NOT_YOUR_ITEM);
                    return RESPONSE_RESULT_FAILED;
                }
            }
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("retrieve_warehouse", ((request, response) -> {
            writeObject(response, process.warehouseTable.retrieveAll(request.headers(OWNER_ID_FIELD)));
            return RESPONSE_RESULT_SUCCESS;
        }));

        post("submit_hero", ((request, response) -> {
            ServerData data = readObject(request);
            if (data != null && data.hero != null && data.maze != null) {
                process.submitHero(data);
                return RESPONSE_RESULT_SUCCESS;
            } else {
                return RESPONSE_RESULT_FAILED;
            }
        }));

        post("get_back_hero", ((request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            if (StringUtils.isNotEmpty(id)) {
                ServerData data = process.getBackHero(id);
                if (data != null) {
                    writeObject(response, data);
                    return Field.RESPONSE_RESULT_SUCCESS;
                }
            }
            return Field.RESPONSE_RESULT_FAILED;
        }));

        post("query_hero_data", ((request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            ServerData serverData = process.queryHeroData(id);
            if (serverData != null) {
                writeObject(response, serverData);
                return Field.RESPONSE_RESULT_SUCCESS;
            }
            return Field.RESPONSE_RESULT_FAILED;
        }));

        post("query_battle_award", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            return process.queryBattleAward(id);
        });

        post("pool_online_data_msg", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            return process.queryHeroData(id).toString();
        });

        post("pool_battle_msg", (request, response) -> {
            String id = request.headers(Field.OWNER_ID_FIELD);
            int count = Integer.parseInt(request.headers(Field.COUNT));
            return process.queryBattleMessages(id, count);
        });

        get("/stop", ((request, response) -> {
            if ("gavin.luo".equals(request.queryParams("pass"))) {
                stop();
            }
            return RESPONSE_RESULT_OK;
        }));

        get("/status", ((request, response) -> process.heroTableCache.size()));


        LogHelper.info("started");
    }


    private String readEncodeHeader(Request request, String head) throws UnsupportedEncodingException {
        String limit = request.headers(head);
        limit = limit == null ? StringUtils.EMPTY_STRING : URLDecoder.decode(limit, "utf-8");
        return limit;
    }

    private void writeObject(Response response, Object exs) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        oos.writeObject(exs);
        response.header(Field.RESPONSE_TYPE, RESPONSE_OBJECT_TYPE);
        oos.flush();
        oos.close();
    }


    private <T> T readObject(Request request) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(request.bodyAsBytes()))) {
            return (T) ois.readObject();
        }
    }

    void stop() {
        LogHelper.info("shutDown");
        process.stop();
        Spark.stop();
    }


}