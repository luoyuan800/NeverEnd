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
    private final static String sign_match = "308202f9308201e1a003020102020413c56148300d06092a864886f70d01010b0500302d310b3009060355040813024744310b3009060355040713025a483111300f060355040313084c756f205975616e301e170d3135303930313037353531315a170d3430303832353037353531315a302d310b3009060355040813024744310b3009060355040713025a483111300f060355040313084c756f205975616e30820122300d06092a864886f70d01010105000382010f003082010a028201010098f398450e3cf13f8f7106c59f157be54eac63a0237ae11596f5b5cefd2228e8befd012c4673bec4c1cc90f21a585c0d4006726c0f0056f6bdb2ddead630227918318e0d6432e4b8cc6b65d0193afcd42c6a9f85b549f66bea8f1297ca374e437a7da338b234bc2e1a4bb2860f7fca1699d1c6e34ed897784d4a728c511241d3e0fe3879ea24460bac0b07010bef3c61d868c2c65cd458e6f79e032d845134a0da3009f9f687d4917ffeeab701d2b933d68580e6e9e47c110afc6633867d74b93836a43d31b824f83f7b0f7b70abda65bfd7a673d7ae0cf2d4b30481d09a51ba3e8f6d8175d6425e3c6b37dc9463e098ac549e5cfda8b1e35de0a2d188ab9bcd0203010001a321301f301d0603551d0e041604147ab8e6bfe5f22df19046f038eff017784c04c694300d06092a864886f70d01010b050003820101005b780aeee5909829165b51dda614d7a73c6caeab3ac784d730a98ef0d98e55095bf9d9fe95fa435bdf4d1cd939b0f1285141431686906c6d9547ac798a076d5da36cfad51be641b3e020d2b6bc391d1532f9c48b9f0575a4e8e4c6b525eb343e501efa4ad263e8ba12dfd08090aa27bd69cb43937075fd7fbb038f574e4e3f801b6d93a45b6fdebb94b79c0acbe9e9f901d0b518c9efe18939144f0942163b994c63a0bdab2627f5ffc16859feca9df40a57b6841ed9593a3a0aab57d0db10f529fc399163fa2ce5e62070eecff2b09678d43bcf7b66b6c84b94f102311d64d6e7910cfadcf7bf52963824055276907329b84130a847820aff9fb3f4852203c3";
    private MainProcess process = new MainProcess(root);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public NeverEndServlet() throws IOException, ClassNotFoundException {
        super();
        LogHelper.init(root);
        process.setDatabase(new MysqlConnection());
        process.start();
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
        //LogHelper.info("sign: " + sign);
        if(StringUtils.isNotEmpty(sign_match)){
            if(!sign_match.equalsIgnoreCase(sign)){
                throw new IOException("Sign verify failed! " + sign);
            }
        }
        String limit = readEncodeHeader(request, Field.LIMIT_STRING);
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = null;
        Boolean success = null;
        switch (path) {
            case ADD_ACCESSORY:
                process.addAccessory(request.getParameter("name"),request.getParameter("tag"),
                        request.getParameter("type"), request.getParameter("author"),
                        request.getParameter("e1"),request.getParameter("e2"),request.getParameter("e3"),
                        request.getParameter("e4"),request.getParameter("e5"),request.getParameter("e6"),
                        request.getParameter("e7"),request.getParameter("e8"));
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
                return;
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
