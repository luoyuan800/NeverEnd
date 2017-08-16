package cn.luo.yuan.maze.model.gift

import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Created by luoyuan on 2017/8/16.
 */
class Long:GiftHandler {
    override fun handler(control: InfoControlInterface?) {
        val config = control?.dataManager?.loadConfig();
        if(config!=null){
            config.isLongKiller = true
        }
    }

    override fun unHandler(control: InfoControlInterface?) {
        val config = control?.dataManager?.loadConfig();
        if(config!=null){
            config.isLongKiller = false
        }
    }
}