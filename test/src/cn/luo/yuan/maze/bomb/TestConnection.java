package cn.luo.yuan.maze.bomb;

import cn.luo.yuan.maze.server.bomb.BombRestConnection;
import cn.luo.yuan.maze.server.bomb.json.JSON;
import cn.luo.yuan.maze.server.bomb.json.SimpleToken;
import org.testng.annotations.Test;

/**
 * Created by luoyuan on 2017/8/5.
 */
public class TestConnection {
    @Test
    public void testBomb(){
        BombRestConnection connection = new BombRestConnection();
        System.out.println("count : " + connection.getRowCount("SelfAccessory"));
        JSON json = connection.queryObjects("SelfAccessory", 1);
        for(SimpleToken token : json.getTokens()){
            System.out.println("name ; " + token.getValue("name"));
            System.out.println("properties ; " + token.getValue("properties"));
        }
    }
}
