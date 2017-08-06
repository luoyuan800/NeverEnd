package cn.luo.yuan.maze.bomb;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.server.MainProcess;
import cn.luo.yuan.maze.server.bomb.BombRestConnection;
import cn.luo.yuan.maze.server.bomb.json.JSON;
import cn.luo.yuan.maze.server.bomb.json.JSONValue;
import cn.luo.yuan.maze.server.bomb.json.SimpleToken;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by luoyuan on 2017/8/5.
 */
public class TestConnection {
    @Test
    public void testBomb() throws IOException, ClassNotFoundException {
        BombRestConnection connection = new BombRestConnection();
        System.out.println("count : " + connection.getRowCount("SelfAccessory"));
        JSON json = connection.queryObjects("SelfAccessory", "createAt",8,1);
        json.parse();
        List<SimpleToken> tokens  = json.getTokens();
        if(tokens.size() == 3){
            SimpleToken nameToken = tokens.get(2);
            System.out.println("name:" + nameToken.getValue("name"));
            System.out.println("desc: " + nameToken.getValue("desc"));
            System.out.println("isConform: " + nameToken.getValue("isConform"));
            System.out.println("userName: " + nameToken.getValue("userName"));
        }
        SimpleToken effectToken = tokens.get(0);
        for(Map.Entry<String, JSONValue> entry:  effectToken.getData().entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().getValue());
        }

    }
}
