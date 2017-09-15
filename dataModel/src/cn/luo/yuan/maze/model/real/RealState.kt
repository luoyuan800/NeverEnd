package cn.luo.yuan.maze.model.real

import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/15/2017.
 */
abstract class RealState: Serializable {
    var id:String = ""
    open var priorState:RealState? = null
    open var nextState:RealState? = null

    fun newEmptyInstance():RealState{
        val ni =  this.javaClass.newInstance()
        ni.priorState = priorState?.newEmptyInstance()
        ni.nextState = nextState?.newEmptyInstance()
        return ni
    }
}