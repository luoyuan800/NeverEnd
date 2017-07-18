package cn.luo.maze.web;

import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.server.LogHelper;
import cn.luo.yuan.maze.server.MainProcess;
import cn.luo.yuan.maze.server.persistence.HeroTable;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static cn.luo.yuan.maze.Path.*;

/**
 * Servlet implementation class NeverEndServlet
 */
@WebServlet("/app/*")
public class NeverEndServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final String root = "E:\\www1\\luoyuan800-0b44449e5d24473a015d30fddbd602dd\\webapp\\data";
    private MainProcess process = new MainProcess(root);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public NeverEndServlet() throws IOException, ClassNotFoundException {
        super();
        LogHelper.init(root);
        process.setDatabase(new MysqlConnection());
    }

    @Override
    public void destroy() {
        process.stop();
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = getPathInfo(request);
        String ownerId = request.getHeader(Field.OWNER_ID_FIELD);
        PrintWriter writer = response.getWriter();

        switch (path) {
            case "add_file":
                String file = request.getParameter("file");
                File f = new File(root + "\\" + file);

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                    bw.write(request.getRemoteAddr());
                    bw.write("\n");
                    bw.write(request.getRemoteHost());
                    bw.flush();
                    bw.close();
                }catch (Exception e){
                    writer.write("\n error:\n");
                    writer.write(e.getMessage());
                    LogHelper.error(e);
                    if(!f.exists()){
                        writer.write("File (" + f.getAbsolutePath() + ") create result: " + f.createNewFile());
                    }
                }
                break;
            case ADD_LIMIT:
                String id = request.getParameter("id");
                HeroTable table = process.heroTable;
                if(table!=null){
                    ServerRecord record = table.getRecord(id);
                    if(record!=null){
                        record.setRestoreLimit(record.getRestoreLimit() + Integer.parseInt(request.getParameter("r_l_c")));
                        process.updateRecord(record);
                    }
                }
                break;
            case STOP:
                process.stop();
                writer.write(Field.RESPONSE_RESULT_OK);
                break;
            case START:
                process.start();
                writer.write(Field.RESPONSE_RESULT_OK);
                break;
            case HERO_RANGE:
                writer.write(process.heroRange);
                break;
            default:
                throw new IOException("Not Mapping for " + path);
        }
        writer.flush();
        writer.close();
    }

    private String getPathInfo(HttpServletRequest request) {
        String path = request.getPathInfo();
        if(path.startsWith("/")){
            path = path.replaceFirst("/", StringUtils.EMPTY_STRING);
        }
        return path;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String path = getPathInfo(request);
        String ownerId = request.getHeader(Field.OWNER_ID_FIELD);
        String version = request.getHeader(Field.VERSION_FIELD);
        String sign = request.getHeader(Field.SIGN_FIELD);
        String limit = readEncodeHeader(request, Field.LIMIT_STRING);
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = null;
        Boolean success = null;
        switch (path) {
            case ADD_ONLINE_GIFT:
                process.addOnlineGift(ownerId,request.getIntHeader(Field.COUNT));
                success = true;
                break;
            case BUY_ONLINE:
                int buyCount = request.getIntHeader(Field.COUNT);
                String itemId = request.getHeader(Field.ITEM_ID_FIELD);
                process.buy(itemId, buyCount);
                success = true;
                break;
            case ONLINE_SHOP:
                writeObject(response,process.getOnlineSell());
                break;
            case POST_DEFENDER:
                Hero hero = process.postHeroByLevel(request.getIntHeader(Field.LEVEL));
                if(hero!=null){
                    writeObject(response, hero);
                }else{
                    success = false;
                }
                break;
            case ADD_BOSS:
                process.submitBoss(request.getParameter("name"), request.getParameter("element"),
                        request.getParameter("race"),request.getParameter("atk"),request.getParameter("def"),
                        request.getParameter("hp"),request.getParameter("grow"),request.getParameter("grow"),request.getParameter("grow"));
                success = true;
                break;
            case GET_GIFT_COUNT:
                writer = response.getWriter();
                writer.write(String.valueOf(process.getGiftCount(ownerId)));
                break;
            case GET_BACK_EXCHANGE:
                String exchange_id = request.getHeader(Field.ITEM_ID_FIELD);
                Object backExchange = process.get_back_exchange(exchange_id);
                if (backExchange == Integer.valueOf(1)) {
                    writer = response.getWriter();
                    writer.write("Could not found special exchange!");
                } else if (backExchange instanceof ExchangeObject) {
                    response.setHeader(Field.RESPONSE_CODE, Field.STATE_ACKNOWLEDGE);
                    writeObject(response, backExchange);
                } else {
                    writeObject(response,backExchange);
                }
            case ACKNOWLEDGE_MY_EXCHANGE:
                exchange_id = request.getHeader(Field.ITEM_ID_FIELD);
                if (process.acknowledge(exchange_id)) {
                    success = true;
                } else {
                    writer = response.getWriter();
                    writer.write("Error object could not found!");
                }
                break;
            case EXCHANGE_PET_LIST:
                List<ExchangeObject> pets = process.exchangeTable.loadAll(1, limit, ownerId);
                writeObject(response, pets);
                success = true;
                break;
            case EXCHANGE_ACCESSORY_LIST:
                List<ExchangeObject> acces = process.exchangeTable.loadAll(2, limit, ownerId);
                writeObject(response, acces);
                success = true;
                break;
            case EXCHANGE_GOODS_LIST:
                List<ExchangeObject> goods = process.exchangeTable.loadAll(3, limit, ownerId);
                writeObject(response, goods);
                success = true;
                break;
            case QUERY_MY_EXCHANGE:
                List<ExchangeObject> exs = process.exchangeTable.loadAll(ownerId);
                response.setHeader(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
                writeObject(response, exs);
                success = true;
                break;
            case REQUEST_EXCHANGE:
                Object objMy = readObject(request);
                ExchangeObject exServer = process.exchangeTable.loadObject(request.getHeader(Field.ITEM_ID_FIELD));
                writer = response.getWriter();
                if (objMy == null) {
                    writer.write("Exchange Object submit error!");
                }
                if (exServer == null) {
                    response.setHeader(Field.RESPONSE_CODE, Field.STATE_FAILED);
                    writer.write("Could not find Object your request!");
                }
                try {
                    if (process.requestExchange(objMy, exServer, ownerId)) {
                        writer.write(Field.RESPONSE_RESULT_OK);
                    }else{
                        writer.write("Could not do exchange because the target has been changed by other!");
                    }
                } catch (Exception e) {
                    LogHelper.error(e);
                    success = false;
                }
                break;
            case SUBMIT_EXCHANGE:
                ExchangeObject eo = readObject(request);
                limit = readEncodeHeader(request, Field.LIMIT_STRING);
                int expectType = Integer.parseInt(request.getHeader(Field.EXPECT_TYPE));
                writer = response.getWriter();
                if (process.submitExchange(ownerId, limit, eo, expectType)) {
                    success = true;
                } else {
                   writer.write("Could not add exchange, maybe there already an exchange object has the same id existed!");
                }
                break;
            case ONLINE_GIFT_OPEN:
                Serializable obj = process.openOnlineGift(ownerId);
                if (obj != null) {
                    writeObject(response, obj);
                } else {
                    writer = response.getWriter();
                    writer.write(StringUtils.EMPTY_STRING);
                }
                break;
            case SUBMIT_HERO:
                ServerData data = readObject(request);
                if (data != null) {
                    try {
                        process.submitHero(data);
                        success = true;
                    } catch (ClassNotFoundException e) {
                        LogHelper.error(e);
                        success = false;
                    }
                } else {
                    success = false;
                }
                break;
            case POOL_BATTLE_MSG:
                int count = request.getIntHeader(Field.COUNT);
                writer = response.getWriter();
                writer.write(process.queryBattleMessages(ownerId, count));
                break;
            case POOL_ONLINE_DATA_MSG:
                String sd = process.getGroupMessage(ownerId);
                writer = response.getWriter();
                writer.write(sd);
                break;
            case QUERY_BATTLE_AWARD:
                writer = response.getWriter();
                writer.write(process.queryBattleAward(ownerId));
                break;
            case QUERY_HERO_DATA:
                data = process.queryHeroData(ownerId);
                if (data != null) {
                    writeObject(response, data);
                } else {
                    success = false;
                }
                break;
            case GET_BACK_HERO:
                data = process.getBackHero(ownerId);
                if (data != null) {
                    writeObject(response, data);
                    success = true;
                } else {
                    success = false;
                }
                break;
            default:
                doGet(request,response);
        }
        if (success != null && writer==null) {
            writer = response.getWriter();
            if (success) {
                response.setHeader(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
                writer.write(Field.RESPONSE_RESULT_OK);
            } else {
                writer.write(Field.RESPONSE_RESULT_FAILED);
            }
        }
        if(writer!=null) {
            writer.flush();
            writer.close();
        }
    }

    private void writeObject(HttpServletResponse response, Object exs) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        oos.writeObject(exs);
        response.setHeader(Field.RESPONSE_TYPE, Field.RESPONSE_OBJECT_TYPE);
        oos.flush();
        oos.close();
    }

    private <T> T readObject(HttpServletRequest request) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(request.getInputStream())) {
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            LogHelper.error(e);
            return null;
        }
    }

    private String readEncodeHeader(HttpServletRequest request, String head) throws UnsupportedEncodingException {
        String limit = request.getHeader(head);
        limit = limit == null ? StringUtils.EMPTY_STRING : URLDecoder.decode(limit, "utf-8");
        return limit;
    }
}
