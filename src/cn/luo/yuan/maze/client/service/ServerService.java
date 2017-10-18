package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.client.utils.SDFileUtils;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.luo.yuan.maze.Path.*;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class ServerService {
    private RestConnection server;

    public ServerService(NeverEnd context) {
        server = new RestConnection(Field.SERVER_URL, context.getVersion(), Resource.getSingInfo());
    }


    public ServerService(String version) {
        server = new RestConnection(Field.SERVER_URL, version, Resource.getSingInfo());
    }

    public ServerService(String url, String version) {
        server = new RestConnection(url, version, Resource.getSingInfo());
    }

    public ServerService(String url, String version, String sign) {
        server = new RestConnection(url, version, sign);
    }

    public ServerData queryOnlineHeroData(NeverEnd gameContext) throws IOException {
        HttpURLConnection connection = server.getHttpURLConnection(QUERY_HERO_DATA, RestConnection.POST);
        connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
        Object obj = server.connect(connection);
        if (obj instanceof ServerData) {
            return (ServerData) obj;
        } else {
            return null;
        }
    }

    public boolean uploadHero(ServerData uploaddData) {
        try {
            uploaddData.setMac(Resource.getDeviceId());
            HttpURLConnection connection = server.getHttpURLConnection(SUBMIT_HERO, RestConnection.POST);
            return Field.RESPONSE_RESULT_SUCCESS.equals(server.connect(uploaddData, connection));
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->uploadHero");
        }
        return false;
    }

    public String postOnlineRange(NeverEnd context) throws IOException {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(HERO_RANGE, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            String rs = server.connect(connection).toString();
            String verify = connection.getHeaderField(Field.VERIFY_RESULT);
            if(StringUtils.isNotEmpty(verify) && Integer.parseInt(verify) <= 0){
                context.showToast("版本错误，请更新到最新版本！");
            }
            return rs;
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postOnlineRange");
            if (e instanceof IOException) {
                throw e;
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public String postOnlineData(NeverEnd context) throws IOException {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(POOL_ONLINE_DATA_MSG, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            String rs = server.connect(connection).toString();
            String verify = connection.getHeaderField(Field.VERIFY_RESULT);
            if(StringUtils.isNotEmpty(verify) && Integer.parseInt(verify) <= 0){
                context.showToast("版本错误，请更新到最新版本！");
            }
            return rs;
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postOnlineData");
            if (e instanceof IOException) {
                throw e;
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public String postSingleBattleMsg(NeverEnd context) {
        return postBattleMsg(context.getHero().getId(), 1);
    }

    public String postBattleMsg(String id, int count) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(POOL_BATTLE_MSG, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, id);
            connection.addRequestProperty(Field.COUNT, String.valueOf(count));
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postBattleMsg");
        }
        return StringUtils.EMPTY_STRING;
    }

    public String queryAwardString(NeverEnd gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(QUERY_BATTLE_AWARD, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->queryAwardString");
        }
        return StringUtils.EMPTY_STRING;
    }

    public ServerData getBackHero(NeverEnd gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(GET_BACK_HERO, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            Object connect = server.connect(connection);
            return connect instanceof ServerData ? (ServerData) connect : null;
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->getBackHero");
        }
        return null;
    }

    public Object openOnlineGift(NeverEnd context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(ONLINE_GIFT_OPEN, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->openOnlineGift");
        }
        return null;
    }

    public String postOnlineGiftCount(NeverEnd context) {
        String connection = getCount(GET_GIFT_COUNT, context.getHero().getId());
        if (connection != null) return connection;
        return null;
    }

    @Nullable
    private String getCount(String getGiftCount, String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(getGiftCount, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, id);
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postOnlineGiftCount");
        }
        return null;
    }

    public Hero postDefender(long level) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(POST_DEFENDER, RestConnection.POST);
            connection.addRequestProperty(Field.LEVEL, String.valueOf(level));
            Object o = server.connect(connection);
            if (o instanceof Hero) {
                return (Hero) o;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postDefender");
        }
        return null;
    }

    public List<SellItem> getOnlineSellItems(NeverEnd context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(ONLINE_SHOP, RestConnection.POST);
            Object o = server.connect(connection);
            if (o instanceof ArrayList) {
                for (SellItem item : (ArrayList<SellItem>) o) {
                    if (item.instance instanceof Accessory) {
                        item.instance = context.covertAccessoryToLocal((Accessory) item.instance);
                    }
                }
                return (ArrayList<SellItem>) o;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->getOnlineSellItems");
        }
        return Collections.emptyList();
    }

    public void buyOnlineItem(String id, int count) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(BUY_ONLINE, RestConnection.POST);
            connection.addRequestProperty(Field.ITEM_ID_FIELD, id);
            connection.addRequestProperty(Field.COUNT, String.valueOf(count));
            server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->buyOnlineItem");
        }
    }

    public void addOnlineGift(NeverEnd context, int count) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(ADD_ONLINE_GIFT, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.COUNT, String.valueOf(count));
            server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->addOnlineGift");
        }
    }

    public void addDebris(NeverEnd context, int count) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(ADD_DEBRIS, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.COUNT, String.valueOf(count));
            server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->addDebris");
        }
    }

    public Boolean uploadLogFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                HttpURLConnection connection = server.getHttpURLConnection(UPLOAD_FILE, RestConnection.POST);
                connection.addRequestProperty(Field.FILE_NAME, file.getName());
                FileInputStream fis = new FileInputStream(file);
                int i = fis.read();
                OutputStream outputStream = connection.getOutputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while (i != -1) {
                    baos.write(i);
                    i = fis.read();
                }
                fis.close();
                outputStream.write(baos.toByteArray());
                return Field.RESPONSE_RESULT_OK.equals(server.connect(connection).toString());
            } catch (IOException e) {
                LogHelper.logException(e, "uploadLogFile: " + fileName);
            }
        }
        return false;
    }
    public String uploadSaveFile(String fileName, String ownerId) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                HttpURLConnection connection = server.getHttpURLConnection(UPLOAD_SAVE, RestConnection.POST);
                connection.addRequestProperty(Field.OWNER_ID_FIELD, ownerId);
                FileInputStream fis = new FileInputStream(file);
                int i = fis.read();
                OutputStream outputStream = connection.getOutputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while (i != -1) {
                    baos.write(i);
                    i = fis.read();
                }
                fis.close();
                outputStream.write(baos.toByteArray());
                return server.connect(connection).toString();
            } catch (IOException e) {
                LogHelper.logException(e, "uploadLogFile: " + fileName);
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public File downloadSaveFile(String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("download_save", RestConnection.POST);
            connection.addRequestProperty("id", id);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                File file = SDFileUtils.newFileInstance("", "" + ".maze", true);
                if(file!=null) {
                    FileOutputStream fos = new FileOutputStream(file);
                    int i = inputStream.read();
                    while (i != -1) {
                        fos.write(i);
                        i = inputStream.read();
                    }
                    fos.flush();
                    fos.close();
                    return file;
                }
            }
        } catch (IOException e) {
            LogHelper.logException(e, "downloadSaveFile: " + id);
        }
        return null;
    }

    public DLC getDlcDetail(String id, NeverEnd context){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.QUERY_DLC_DETAIL, RestConnection.POST);
            connection.addRequestProperty(Field.ITEM_ID_FIELD, URLEncoder.encode(id, "utf-8"));
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object o = server.connect(connection);
            if(o instanceof DLC){
                return (DLC) o;
            }
        } catch (IOException e) {
            LogHelper.logException(e, "Query dlc details");
        }
        return null;
    }

    public List<DLCKey> getMonsterDlcKey(NeverEnd context){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.QUERY_DLC, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object o = server.connect(connection);
            if(o instanceof List){
                return (List<DLCKey>) o;
            }
        } catch (IOException e) {
            LogHelper.logException(e, "Query dlc list");
        }
        return Collections.emptyList();
    }

    public boolean buyDlc(String id, NeverEnd context){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.BUY_DLC, RestConnection.POST);
            connection.addRequestProperty(Field.ITEM_ID_FIELD, URLEncoder.encode(id, "utf-8"));
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object o = server.connect(connection);
            if(o.toString().equals(Field.RESPONSE_RESULT_OK)){
                return true;
            }
        } catch (IOException e) {
            LogHelper.logException(e, "Query dlc list");
        }
        return false;
    }

    public String postDebrisCount(NeverEnd gameContext) {
        String connection = getCount(GET_DEBRIS_COUNT, gameContext.getHero().getId());
        if (connection != null) return connection;
        return null;
    }

    public boolean storeWarehouse(Serializable object, NeverEnd context){
        try {
            if(object instanceof OwnedAble){
                ((OwnedAble) object).setKeeperId(context.getHero().getId());
                ((OwnedAble) object).setKeeperName(context.getHero().getName());
            }
            HttpURLConnection connection = server.getHttpURLConnection(STORE_WAREHOUSE, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object connect = server.connect(connection);
            if(Field.RESPONSE_RESULT_SUCCESS.equals(connect.toString())){
                return context.getDataManager().storeWarehouse(object);
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->storeWarehouse");
        }
        return false;
    }
    public List<OwnedAble> queryWarehouse(int type, NeverEnd context){
        try {
            return context.getDataManager().queryWarehouse(type);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->queryWarehouse");
        }
        return Collections.emptyList();
    }
    public boolean getBackWarehouse(String id, int type, NeverEnd context){
        try {
            Serializable ser = context.getDataManager().getBackWarehouse(id, type);
            if(ser!=null){
                return true;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->queryWarehouse");
        }
        return false;
    }
    public boolean batchGetBackWarehouse(ArrayList<String> ids, int type, NeverEnd context){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(RETRIEVE_BACK_WAREHOUSE, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.EXPECT_TYPE, String.valueOf(type));
            Object connect = server.connect(ids, connection);
            return Field.RESPONSE_RESULT_SUCCESS.equals(connect);

        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->queryWarehouse");
        }
        return false;
    }

    public Object useCdkey(String id, NeverEnd context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(USE_KEY, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.ITEM_ID_FIELD, id);
            return server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->useCdkey");
        }
        return "校验失败, 稍后重试！";
    }

    public RangeAward postRangeAward(NeverEnd context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(QUERY_RANGE_AWARD, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object connect = server.connect(connection);
            if(connect instanceof RangeAward){
                return (RangeAward) connect;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postRangeAward");
        }
        return null;
    }

    public String changeDebris(int value, NeverEnd context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(DEBRIS_CHANGE_KEY, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.COUNT,String.valueOf(value));
            Object connect = server.connect(connection);
            return connect.toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->changeDebris");
        }
        return null;
    }

    public List<CDKey> queryMyKeys(NeverEnd control) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(QUERY_DEBRIS_CHANGE_KEY, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, control.getHero().getId());
            Object connect = server.connect(connection);
            if(connect instanceof List){
                return (List<CDKey>) connect;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->changeDebris");
        }
        return Collections.emptyList();
    }
}
