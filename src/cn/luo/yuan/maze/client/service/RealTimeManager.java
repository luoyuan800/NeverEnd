package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.utils.Field;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/4/2017.
 */
public class RealTimeManager {
    private NeverEnd context;
    private RestConnection server;
    public RealTimeManager(NeverEnd context){
        this.context = context;
        server = new RestConnection(Field.SERVER_URL,context.getVersion(), Resource.getSingInfo());
    }


}
