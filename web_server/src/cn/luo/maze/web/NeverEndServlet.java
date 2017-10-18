package cn.luo.maze.web;

import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.LevelRecord;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.model.RangeAward;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.real.RealState;
import cn.luo.yuan.maze.model.real.action.RealTimeAction;
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
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.luo.yuan.maze.Path.*;

/**
 * Servlet implementation class NeverEndServlet
 */
@WebServlet("/app/*")
public class NeverEndServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static int count = 0;
    private final String root = Field.SERVER_DIR;
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
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = null;
        switch (path) {
            case "test":
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                writer = writeMessage(response, "responsed:  " + count++);
                break;
            case GET_CURRENT_VERSION:
                writer = writeMessage(response, String.valueOf(process.getReleaseVersion()));
                break;
            case GET_RELEASE_NOTE:
                writer = writeMessage(response, process.getLatestReleaseNotes());
                break;
            case DELETE_SAVE:
                process.deleteSaveFile(request.getHeader(Field.ITEM_ID_FIELD));
                break;
            case "new_cd_key":
                writer = writeMessage(response, process.newCdKey());
                break;
            case "get_exchange_list":
                writer = writeMessage(response, process.exchangeJson());
                break;
            case "add_cribber":
                process.addCribber(request.getParameter(Field.OWNER_ID_FIELD));
                break;
            case "update_shop_accessory":
                writer = writeMessage(response, "<html>\n" +
                        "<head><meta charset=\"utf-8\"></head>\n" +
                        "<body>" + process.updateShopAccessory(Integer.parseInt(request.getParameter("start"))) + "</body>" +
                        "\n" +
                        "</html>");
                break;
            case "get_hero_list":
                writer = writeMessage(response, process.getOnlineHeroList());
                break;
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
                } catch (Exception e) {
                    writer = writeMessage(response, "\n error:\n" + e.getMessage());
                    LogHelper.error(e);
                    if (!f.exists()) {
                        //writer.write("File (" + f.getAbsolutePath() + ") create result: " + f.createNewFile());
                    }
                }
                break;
            case ADD_LIMIT:
                String id = request.getParameter("id");
                HeroTable table = process.heroTable;
                if (table != null) {
                    ServerRecord record = table.getRecord(id);
                    if (record != null) {
                        record.setRestoreLimit(record.getRestoreLimit() + Integer.parseInt(request.getParameter("r_l_c")));
                        process.updateRecord(record);
                    }
                }
                break;
            case STOP:
                process.stop();
                writer = writeMessage(response, Field.RESPONSE_RESULT_OK);
                break;
            case START:
                process.start();
                writer = writeMessage(response, Field.RESPONSE_RESULT_OK);
                break;
            case HERO_RANGE:
                writer = writeMessage(response, process.heroRange);
                break;
            default:
                throw new IOException("Not Mapping for " + path);
        }
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    private String getPathInfo(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", StringUtils.EMPTY_STRING);
        }
        return path;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = getPathInfo(request);
        try {
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            String ownerId = request.getHeader(Field.OWNER_ID_FIELD);
            String version = request.getHeader(Field.VERSION_FIELD);
            String sign = request.getHeader(Field.SIGN_FIELD);
            //LogHelper.info("sign: " + sign);
            int verify = process.isSignVerify(sign, version);
            if (verify <= 0) {
                LogHelper.info("Error verify:" + "Sign verify failed! sign: " + sign + ", version: " + version);
                response.setIntHeader(Field.VERIFY_RESULT, verify);
                return;
            }
            String limit = readEncodeHeader(request, Field.LIMIT_STRING);
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = null;
            Boolean success = null;
            switch (path) {
                case QUERY_DEBRIS_CHANGE_KEY:
                    writeObject(response, process.queryMyKeys(ownerId));
                    break;
                case DEBRIS_CHANGE_KEY:
                    writer = writeMessage(response, process.changeDebris(request.getIntHeader(Field.COUNT), ownerId));
                    break;
                case REAL_BATTLE_TARGET_RECORD:
                    writeObject(response, process.pollBattleTargetRecord(ownerId));
                    break;
                case REAL_BATTLE_TURN:
                    writeObject(response, process.pollRealBattleTurn(ownerId));
                    break;
                case TOP_N_PALACE:
                    writer = writeMessage(response, process.pollTopNPalaceRecords(request.getIntHeader(Field.COUNT)));
                    break;
                case REAL_BATTLE_QUIT:
                    RealState state = readObject(request);
                    writeObject(response, process.quitRealBattle(ownerId, state));
                    break;
                case REAL_BATTLE_ACTION:
                    RealTimeAction action = readObject(request);
                    success = action != null && process.realBattleAction(action);
                    break;
                case REAL_BATTLE_READY:
                    process.realBattleReady(ownerId);
                    success = true;
                    break;
                case UPDATE_REAL_RECORD:
                    LevelRecord record = readObject(request);
                    if (record != null) {
                        process.updateRealData(record);
                        success = true;
                    }
                    break;
                case UPDATE_REAL_RECORD_PRIOR_POINT:
                    process.updateRealRecordPriorPoint(ownerId);
                    success = true;
                    break;
                case POLL_REAL_BATTLE_STATE:
                    String msgId = request.getHeader(Field.REAL_MSG_ID);
                    RealState cstate = readObject(request);
                    writeObject(response, process.pollCurrentState(ownerId, request.getIntHeader(Field.INDEX), msgId, cstate));
                    break;
                case POLL_REAL_RECORD:
                    writeObject(response, process.pollRealRecord(ownerId));
                    break;
                case UPLOAD_SAVE:
                    writer = writeMessage(response, process.uploadSaveFile(request.getInputStream(), ownerId));
                    break;
                case DOWNLOAD_SAVE:
                    byte[] saveZip = process.downloadSaveZip(request.getHeader(Field.ITEM_ID_FIELD));
                    if (saveZip != null) {
                        response.setStatus(200);
                        response.getOutputStream().write(saveZip);
                    }
                    break;
                case QUERY_RANGE_AWARD:
                    RangeAward ra = process.getRangeAward(ownerId);
                    if (ra != null) {
                        writeObject(response, ra);
                    } else {
                        success = false;
                    }
                    break;
                case USE_KEY:
                    writeObject(response, process.useCdkey(request.getHeader(Field.ITEM_ID_FIELD), ownerId));
                    break;
                case DOWNLOAD_APK:
                    byte[] apk = process.downloadApk();
                    response.getOutputStream().write(apk);
                    break;
                case RETRIEVE_BACK_WAREHOUSE:
                    int warehouseType = request.getIntHeader(Field.EXPECT_TYPE);
                    List<String> ids = readObject(request);
                    if(ids!=null){
                        for(String id : ids){
                            process.deleteFromWarehouse(id, warehouseType, ownerId);
                        }
                        success = true;
                    }else {
                        success = null != process.deleteFromWarehouse(request.getHeader(Field.ITEM_ID_FIELD), warehouseType, ownerId);
                    }
                    break;
                case RETRIEVE_WAREHOUSE_LIST:
                    writeObject(response, process.warehouseList(ownerId, request.getIntHeader(Field.EXPECT_TYPE)));
                    break;
                case STORE_WAREHOUSE:
                    success = process.storeIntoWarehouse(ownerId);
                    break;
                case BUY_DLC:
                    success = process.buyDlc(ownerId, readEncodeHeader(request, Field.ITEM_ID_FIELD));
                    break;
                case QUERY_DLC_DETAIL:
                    writeObject(response, process.getDlc(ownerId, readEncodeHeader(request, Field.ITEM_ID_FIELD)));
                    break;
                case QUERY_DLC:
                    writeObject(response, process.queryDLCKeys(ownerId));
                    break;
                case ADD_DEBRIS:
                    process.addDebris(ownerId, request.getIntHeader(Field.COUNT));
                    success = true;
                    break;
                case GET_DEBRIS_COUNT:
                    String debrisCount = String.valueOf(process.getDebris(ownerId));
                    writer = writeMessage(response, debrisCount);
                    break;
                case QUERY_TASK_SCENES:
                    String taskId = request.getHeader(Field.TASK_ID);
                    writeObject(response, process.queryScenes(taskId));
                    break;
                case QUERY_ONLINE_TASK:
                    int start = request.getIntHeader(Field.INDEX);
                    int row = request.getIntHeader(Field.COUNT);
                    Set<String> filter = readObject(request);
                    writeObject(response, process.queryTask(start, row, filter));
                    break;
                case UPLOAD_FILE:
                    String name = process.uploadFile(request.getHeader(Field.FILE_NAME), request.getInputStream());
                    writer = writeMessage(response, name);
                    break;
                case ADD_ACCESSORY:
                    process.addAccessory(request.getParameter("name"), request.getParameter("tag"),
                            request.getParameter("type"), request.getParameter("author"),
                            request.getParameter("e1"), request.getParameter("e2"), request.getParameter("e3"),
                            request.getParameter("e4"), request.getParameter("e5"), request.getParameter("e6"),
                            request.getParameter("e7"), request.getParameter("e8"));
                case ADD_ONLINE_GIFT:
                    process.addOnlineGift(ownerId, request.getIntHeader(Field.COUNT));
                    success = true;
                    break;
                case BUY_ONLINE:
                    int buyCount = request.getIntHeader(Field.COUNT);
                    String itemId = request.getHeader(Field.ITEM_ID_FIELD);
                    process.buy(itemId, buyCount);
                    success = true;
                    break;
                case ONLINE_SHOP:
                    writeObject(response, process.getOnlineSell());
                    break;
                case POST_DEFENDER:
                    Hero hero = process.postHeroByLevel(request.getIntHeader(Field.LEVEL));
                    if (hero != null) {
                        writeObject(response, hero);
                    } else {
                        success = false;
                    }
                    break;
                case ADD_BOSS:
                    process.submitBoss(request.getParameter("name"), request.getParameter("element"),
                            request.getParameter("race"), request.getParameter("atk"), request.getParameter("def"),
                            request.getParameter("hp"), request.getParameter("grow"), request.getParameter("grow"), request.getParameter("grow"));
                    success = true;
                    break;
                case GET_GIFT_COUNT:
                    String giftCount = String.valueOf(process.getGiftCount(ownerId));
                    writer = writeMessage(response, giftCount);
                    break;
                case GET_BACK_EXCHANGE:
                    String exchange_id = request.getHeader(Field.ITEM_ID_FIELD);
                    Object backExchange = process.get_back_exchange(exchange_id);
                    if (backExchange == Integer.valueOf(1)) {
                        String msg = "Could not found special exchange!";
                        writer = writeMessage(response, msg);
                    } else if (backExchange instanceof ExchangeObject) {
                        response.setHeader(Field.RESPONSE_CODE, Field.STATE_ACKNOWLEDGE);
                        writeObject(response, backExchange);
                    } else {
                        writeObject(response, backExchange);
                    }
                    break;
                case ACKNOWLEDGE_MY_EXCHANGE:
                    exchange_id = request.getHeader(Field.ITEM_ID_FIELD);
                    if (process.acknowledge(exchange_id)) {
                        success = true;
                    } else {
                        String errorMsg = "Error object could not found!";
                        writer = writeMessage(response, errorMsg);
                    }
                    break;
                case EXCHANGE_PET_LIST:
                    List<ExchangeObject> pets = process.exchangeTable.loadAll(1, limit, ownerId);
                    writeObject(response, pets);
                    break;
                case EXCHANGE_ACCESSORY_LIST:
                    List<ExchangeObject> acces = process.exchangeTable.loadAll(2, limit, ownerId);
                    writeObject(response, acces);
                    break;
                case EXCHANGE_GOODS_LIST:
                    List<ExchangeObject> goods = process.exchangeTable.loadAll(3, limit, ownerId);
                    writeObject(response, goods);
                    break;
                case QUERY_MY_EXCHANGE:
                    List<ExchangeObject> exs = process.exchangeTable.loadAll(ownerId);
                    response.setHeader(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
                    writeObject(response, exs);
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
                        } else {
                            writer.write("Could not do exchange because the target has been changed by other!");
                        }
                    } catch (Exception e) {
                        LogHelper.error(e);
                        success = false;
                    }
                    break;
                case SUBMIT_EXCHANGE:
                    Object eo = readObject(request);
                    limit = readEncodeHeader(request, Field.LIMIT_STRING);
                    int expectType = Integer.parseInt(request.getHeader(Field.EXPECT_TYPE));
                    writer = response.getWriter();
                    if (process.submitExchange(ownerId, limit, eo, expectType)) {
                        writer.write(Field.RESPONSE_RESULT_OK);
                    } else {
                        writer.write("Could not add exchange, maybe there already an exchange object has the same id existed!");
                    }
                    break;
                case ONLINE_GIFT_OPEN:
                    Serializable obj = process.openOnlineGift(ownerId);
                    if (obj != null) {
                        writeObject(response, obj);
                    } else {
                        writer = writeMessage(response, StringUtils.EMPTY_STRING);
                    }
                    break;
                case SUBMIT_HERO:
                    ServerData data = readObject(request);
                    if (data != null) {
                        try {
                            success = process.submitHero(data);
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
                    String battleMsg = process.queryBattleMessages(ownerId, count);
                    writer = writeMessage(response, battleMsg);
                    break;
                case POOL_ONLINE_DATA_MSG:
                    String sd = process.getGroupMessage(ownerId);
                    writer = writeMessage(response, sd);
                    break;
                case QUERY_BATTLE_AWARD:
                    String battleAward = process.queryBattleAward(ownerId);
                    writer = writeMessage(response, battleAward);
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
                    } else {
                        success = false;
                    }
                    break;
                default:
                    doGet(request, response);
                    return;
            }
            try {
                if (success != null && writer == null) {
                    writer = response.getWriter();
                    if (success) {
                        response.setHeader(Field.RESPONSE_CODE, Field.STATE_SUCCESS);
                        writer.write(Field.RESPONSE_RESULT_OK);
                    } else {
                        writer.write(Field.RESPONSE_RESULT_FAILED);
                    }
                }
            } catch (IllegalStateException e) {
                LogHelper.error(new Exception(path, e));
            }
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            LogHelper.error(new Exception(path, e));
        }
    }

    private PrintWriter writeMessage(HttpServletResponse response, String msg) throws IOException {
        PrintWriter writer;
        writer = response.getWriter();
        writer.write(msg);
        return writer;
    }

    private void writeObject(HttpServletResponse response, Object exs) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        response.setHeader(Field.RESPONSE_TYPE, Field.RESPONSE_OBJECT_TYPE);
        oos.writeObject(exs);
        oos.flush();
        oos.close();
    }

    private <T> T readObject(HttpServletRequest request) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(request.getInputStream())) {
            return (T) ois.readObject();
        } catch (Exception e) {
            LogHelper.error(new Exception(request.getPathInfo() + " from " + request.getRemoteAddr(), e));
            return null;
        }
    }

    private List<Object> readObjects(HttpServletRequest request) throws IOException {
        List<Object> objects = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(request.getInputStream())) {
            try {
                objects.add(ois.readObject());
            } catch (EOFException eof) {
                //Do Nothing
            }
        } catch (Exception e) {
            LogHelper.error(new Exception(request.getPathInfo() + " from " + request.getRemoteAddr(), e));
        }
        return objects;
    }


    private String readEncodeHeader(HttpServletRequest request, String head) throws UnsupportedEncodingException {
        String limit = request.getHeader(head);
        limit = limit == null ? StringUtils.EMPTY_STRING : URLDecoder.decode(limit, "utf-8");
        return limit;
    }
}
