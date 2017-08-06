package cn.luo.yuan.maze.server.bomb;


import cn.luo.yuan.maze.server.bomb.json.MyJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by luoyuan on 2016/6/18.
 */
public class BombRestConnection {
    private final static String BASE_URL = "https://api.bmob.cn/1/classes/";
    private final static String APP_KEY = "9f3e87eaf9b4a7e83a1410df2d9e7f87";
    private final static String API_KEY = "b16c8df253615473c31ea7a4a33c98d3";

    public MyJSON updateObject(String table, String id, String body){
        try {
            URL url = new URL(BASE_URL + table + "/" + id);
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while(line!=null){
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            return new MyJSON(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSON addObject(String table, String body){
        try {
            URL url = new URL(BASE_URL + table);
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while(line!=null){
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            return new MyJSON(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public MyJSON queryObject(String table, String id) {
        try {
            URL url = new URL(BASE_URL + table + "/" + id);
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            StringBuffer sb = new StringBuffer();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            MyJSON json = new MyJSON(sb.toString());
            reader.close();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSON queryObjects(String table){
        return queryObjects(table, "updatedAt", 100);
    }

    public MyJSON queryObjects(String table, String order, Integer limit){
        try {
            URL url = new URL(BASE_URL + table + "?order=" + order + "&limit=" + limit);
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            MyJSON json = new MyJSON(sb.toString());
            reader.close();
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSON queryObjects(String table, String order, Integer skip, Integer limit){
        try {
            URL url = new URL(BASE_URL + table + "?order=" + order + "&limit=" + limit + "&skip=" + skip);
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            String line = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            MyJSON json = new MyJSON(sb.toString());
            reader.close();
            return json;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MyJSON queryObjects(String table, int limit){
        return queryObjects(table,"updatedAt", limit);
    }

    public MyJSON deleteObject(String table, String objectId){
        try {
            URL url = new URL(BASE_URL + table + "/" + objectId);
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("DELETE");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            MyJSON json = new MyJSON(sb.toString());
            reader.close();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("X-Bmob-Application-Id", APP_KEY);
        connection.setRequestProperty("X-Bmob-REST-API-Key", API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    public long getRowCount(String table){
        try {
            URL url = new URL(BASE_URL + table + "?count=1&limit=0");
            HttpURLConnection connection = getHttpURLConnection(url);
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            MyJSON json = new MyJSON(sb.toString());
            reader.close();
            json.parse();
            return json.getTokens().get(0).getValue("count");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
