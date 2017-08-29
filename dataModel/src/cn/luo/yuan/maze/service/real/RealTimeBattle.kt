package cn.luo.yuan.maze.service.real

import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.real.RealTimeState
import cn.luo.yuan.maze.model.real.action.AtkAction
import cn.luo.yuan.maze.model.real.action.RealTimeAction
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.utils.Random

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/17/2017.
 */
class RealTimeBattle(val p1: HarmAble, val p2: HarmAble, val random: Random) {
    val battle: BattleService = BattleService(p1, p2, random, null)
    val messager = RealBattleMessage()
    val actionControlThread = ControlThread()
    var running = false;
    init {
        battle.setBattleMessage(messager)
        actionControlThread.rtb = this
        actionControlThread.start()
    }


    var actioner: HarmAble? = null
    var timer: TimerThread? = null
    var turn = 0L
    var performed = mutableSetOf<String>()
    var lastAction: RealTimeAction? = null

    fun start(){
        running = true
    }

    fun stop(){
        running = false
        if(timer!=null && timer!!.cancel){
            (timer as TimerThread).cancel = true
        }
    }

    fun action(action: RealTimeAction): Boolean {
        if (performed.contains(action.id)) {
            return true
        }
        when (action) {
            is AtkAction -> {
                if (action.ownerId == actioner!!.id) {
                    battle.normalAtk(actioner, if (actioner === p1) p2 else p1, turn)
                }
            }
        }
        changeActioner()
        performed.add(action.id)
        lastAction = action
        return true
    }

    fun pollState(index: Int): RealTimeState {
        val state = RealTimeState()
        state.actioner = actioner
        if (actioner == p1) {
            state.waiter = p2
        } else {
            state.waiter = p1
        }
        state.action = lastAction
        var i = index
        while(++i < messager.msg.size) {
            state.msg.add(messager.msg[i])
        }
        return state
    }

    fun pollRemainTime():Long{
        if(timer!=null && !(timer as TimerThread).cancel){
            return (timer as TimerThread).time
        }
        return 0L
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

    class ControlThread:Thread(){
        var rtb : RealTimeBattle? = null
        override fun run() {
            if(rtb!=null){
                while (rtb!!.running) {
                    if (rtb!!.timer != null) {
                        if (!(rtb!!.timer as TimerThread).cancel && (rtb!!.timer as TimerThread).time <= 0) {
                            rtb!!.changeActioner()
                        }
                    }
                    sleep(1000)
                }
            }
        }
    }


}