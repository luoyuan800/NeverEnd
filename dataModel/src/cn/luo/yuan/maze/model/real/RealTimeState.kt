package cn.luo.yuan.maze.model.real

import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.real.action.RealTimeAction

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/17/2017.
 */
class RealTimeState {
    var actioner:HarmAble? = null
    var waiter:HarmAble?=null
    var action:RealTimeAction?=null
    val msg = mutableListOf<String>()
    var actionerPoint = 0
    var waiterPoint = 0
}