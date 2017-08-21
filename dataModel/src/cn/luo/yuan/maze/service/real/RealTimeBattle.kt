package cn.luo.yuan.maze.service.real

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.real.RealTimeState
import cn.luo.yuan.maze.model.real.action.RealTimeAction
import cn.luo.yuan.maze.server.model.Messager

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/17/2017.
 */
class RealTimeBattle {
    var p1: Hero? = null
    var p2: Hero? = null
    var actioner: Hero? = null
    var timer: TimerThread? = null
    var performed = mutableSetOf<String>()
    var messager: cn.luo.yuan.maze.server.model.Messager? = null

    fun action(action: RealTimeAction): Boolean {
        if (performed.contains(action.id)) {
            return true
        }
        //Do action
        changeActioner()
        performed.add(action.id)
        return true
    }

    fun pollState(): RealTimeState {
        val state = RealTimeState()
        state.actioner = actioner
        if(actioner == p1){
            state.waiter = p2
        }else{
            state.waiter = p1
        }
        return state
    }

    private fun changeActioner() {
        if (actioner == p1) {
            actioner = p2
        } else {
            actioner = p1
        }
        if (timer != null) {
            (timer as TimerThread).cancel = true
        }
        timer = TimerThread()
        (timer as TimerThread).start()

    }

    class TimerThread : Thread() {
        var time = 60L
        var cancel = false
        override fun run() {
            while (!cancel && time > 0) {
                sleep(1000)
                time--
            }
        }
    }


}