package cn.luo.yuan.maze.service.real

import cn.luo.yuan.maze.model.Messager

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/29/2017.
 */
class RealBattleMessage:Messager() {
    val msg = mutableListOf<String>()
    override fun notification(msg: String?) {
        if(msg!=null) {
            this.msg.add(msg)
        }
    }
}