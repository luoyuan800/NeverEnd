package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.FileUtils;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.SellItem;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import cn.luo.yuan.maze.utils.annotation.StringValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
        server = new RestConnection(Field.SERVER_URL, version,Resource.getSingInfo() );
    }

    public ServerService(String url, String version) {
        server = new RestConnection(url, version,Resource.getSingInfo() );
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
            HttpURLConnection connection = server.getHttpURLConnection(SUBMIT_HERO, RestConnection.POST);
            return Field.RESPONSE_RESULT_SUCCESS.equals(server.connect(uploaddData, connection));
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->uploadHero");
        }
        return false;
    }

    public String postOnlineData(NeverEnd context) throws IOException {
        return postOnlineData(context.getHero().getId());
    }

    public String postOnlineRange(NeverEnd context) throws IOException {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(HERO_RANGE, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postOnlineRange");
            if (e instanceof IOException) {
                throw e;
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public String postOnlineData(String id) throws IOException {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(POOL_ONLINE_DATA_MSG, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, id);
            return server.connect(connection).toString();
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
            return (ServerData) server.connect(connection);
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

    public String postOnlineGiftCount(NeverEnd context){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(GET_GIFT_COUNT, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
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
            if(o instanceof Hero){
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
            if(o instanceof ArrayList){
                for(SellItem item : (ArrayList<SellItem>)o){
                    if(item.instance instanceof Accessory){
                        item.instance = context.covertAccessoryToLocal((Accessory) item.instance);
                    }
                }
                return (ArrayList<SellItem>)o;
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

    public String uploadSaveFile(String fileName) {
        File file = new File(fileName);
        if(file.exists()){
            try {
                HttpURLConnection connection = server.getHttpURLConnection(UPLOAD_SAVE, RestConnection.POST);
                connection.addRequestProperty(Field.FILE_NAME, file.getName());
                FileInputStream fis = new FileInputStream(file);
                int i = fis.read();
                OutputStream outputStream = connection.getOutputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while(i!=-1){
                    baos.write(i);
                    i = fis.read();
                }
                fis.close();
                outputStream.write(baos.toByteArray());
                return server.connect(connection).toString();
            } catch (IOException e) {
                LogHelper.logException(e, "uploadSaveFile: " + fileName);
            }
        }
        return null;
    }

    public File downloadSaveFile(String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("download_save", RestConnection.POST);
            connection.addRequestProperty("id", id);
            connection.connect();
            if(connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                File file = FileUtils.newFileInstance("", "" + ".maze", true);

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
        } catch (IOException e) {
            LogHelper.logException(e, "downloadSaveFile: " + id);
        }
        return null;
    }
}
