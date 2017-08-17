package cn.luo.yuan.maze.model.real.action

import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/17/2017.
 */
interface RealTimeAction: Serializable {
    var id:String
    var ownerId:String
}