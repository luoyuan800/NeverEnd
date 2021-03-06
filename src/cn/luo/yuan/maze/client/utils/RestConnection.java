package cn.luo.yuan.maze.client.utils;

import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gluo on 11/30/2016.
 */
public class RestConnection {
    public final static String password = "maze4.00";
    public final static String PUT = "PUT";
    public final static String POST = "POST";
    public final static String GET = "GET";
    public final static String DELETE = "DELETE";
    public final static Integer STATE_SUCCESS = 100, STATE_FAILED = 601;
    private String server;
    private String version;
    private String Sing;

    public RestConnection(String serverUrl, String version, String sing) {
        this.server = serverUrl;
        this.version = version;
        this.Sing = sing;
    }

    public Object connect(Serializable serializable, HttpURLConnection connection) throws IOException {
        connection.getOutputStream().write(getObjectSerialize(serializable));
        connection.connect();
        return getResult(connection);
    }

    public Object connect(HttpURLConnection connection) throws IOException {
        connection.connect();
        return getResult(connection);
    }

    public Object getResult(HttpURLConnection connection) throws IOException{
        if(Field.RESPONSE_OBJECT_TYPE.equals(connection.getHeaderField(Field.RESPONSE_TYPE))){
            return getRestObjectResult(connection);
        }else{
            return getRestStringResult(connection);
        }
    }

    public Object getRestObjectResult(HttpURLConnection conn){
        try (ObjectInputStream ois = new ObjectInputStream(conn.getInputStream())){
            return ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public HttpURLConnection getHttpURLConnection(String path, String method) throws IOException {
        URL url = new URL(server + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        //connection.addRequestProperty("signe", MazeContents.getSingInfo(MainGameActivity.context));
        connection.addRequestProperty(Field.VERSION_FIELD, version);
        connection.addRequestProperty(Field.SIGN_FIELD, Sing);
        return connection;
    }

    private byte[] getObjectSerialize(Serializable serializable) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(serializable);
            oos.flush();
            oos.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    private String getRestStringResult(HttpURLConnection connection) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String result = reader.readLine();
            while (result != null) {
                builder.append(result);
                result = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            //LogHelper.logException(e,false);
        }
        return builder.toString();
    }

}
