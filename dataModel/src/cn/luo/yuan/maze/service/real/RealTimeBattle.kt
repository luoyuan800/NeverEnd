package cn.luo.yuan.maze.service.real

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.real.RealTimeState
import cn.luo.yuan.maze.model.real.action.AtkAction
import cn.luo.yuan.maze.model.real.action.AtkSkillAction
import cn.luo.yuan.maze.model.real.action.DefSkillAction
import cn.luo.yuan.maze.model.real.action.RealTimeAction
import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.model.skill.EmptySkill
import cn.luo.yuan.maze.model.skill.SkillAbleObject
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.utils.Random

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/17/2017.
 */
class RealTimeBattle(val p1: HarmAble, val p2: HarmAble, val random: Random) {
    val battle: BattleService = BattleService(p1, p2, random, null)
    val messager = RealBattleMessage()
    var p1ActionPoint = 150
    var p2ActionPoint = 150
    var actionControlThread: ControlThread? = null
    var running = -1;

    var actioner: HarmAble? = null
    var timer: TimerThread? = null
    var turn = 0L
    var performed = mutableSetOf<String>()
    var lastAction: RealTimeAction? = null

    init {
        battle.setBattleMessage(messager)
        if (random.nextBoolean()) {
            actioner = p1
        } else {
            actioner = p2
        }
    }

    fun start() {
        synchronized(running) {
            running += 1
            if (actionControlThread == null) {
                actionControlThread = ControlThread()
                actionControlThread!!.rtb = this
                actionControlThread!!.start()
            }
        }
    }

    fun stop() {
        running = -1
        if (timer != null && timer!!.cancel) {
            (timer as TimerThread).cancel = true
        }
    }

    fun action(action: RealTimeAction): Boolean {
        if (performed.contains(action.id)) {
            return true
        }
        val defender = if (actioner == p1) p2 else p1
        when (action) {
            is AtkAction -> {
                if (action.ownerId == actioner!!.id) {
                    if (defender is SkillAbleObject && !defender.skills.isEmpty() && defender.skills[0] is DefSkill &&
                            battle.releaseSkill(actioner, defender, random, false, defender.skills[0], getMinHarm())) {
                        //Release skill
                        defender.skills[0] = EmptySkill.EMPTY_SKILL
                    } else {
                        battle.normalAtk(actioner, if (actioner === p1) p2 else p1, turn)
                    }
                }
            }
            is AtkSkillAction -> {
                if (action.ownerId == actioner!!.id && actioner is SkillAbleObject) {
                    val skill = action.skill
                    var point = Data.getSkillActionPoint(skill);
                    if (actioner == p1) {
                        if(point > p1ActionPoint){
                            return false
                        }
                        p1ActionPoint -= point
                    } else {
                        if(point > p2ActionPoint){
                            return false
                        }
                        p2ActionPoint -= point
                    }
                    battle.releaseSkill(actioner, if (actioner == p1) p2 else p1, random, true, skill, getMinHarm())
                }
            }
            is DefSkillAction -> {
                if (action.ownerId == actioner!!.id && actioner is SkillAbleObject) {
                    val skill = action.skill
                    var point = Data.getSkillActionPoint(skill);
                    if (actioner == p1) {
                        if(point > p1ActionPoint){
                            return false
                        }
                        p1ActionPoint -= point
                    } else {
                        if(point > p2ActionPoint){
                            return false
                        }
                        p2ActionPoint -= point
                    }
                    (actioner!! as SkillAbleObject).skills[0] = skill
                }
            }
        }
        if (actioner == p1) {
            p2ActionPoint += 50;
        } else {
            p1ActionPoint += 50;
        }
        changeActioner()
        performed.add(action.id)
        lastAction = action
        return true
    }

    private fun getMinHarm() = if (actioner == p1) p1ActionPoint.toLong() else p2ActionPoint.toLong()

    fun pollState(msgIndex: Int): RealTimeState {
        val state = RealTimeState()
        state.actioner = actioner
        if (actioner == p1) {
            state.waiter = p2
            state.waiterPoint = p2ActionPoint
            state.actionerPoint = p1ActionPoint
        } else {
            state.waiter = p1
            state.waiterPoint = p1ActionPoint
            state.actionerPoint = p2ActionPoint
        }
        state.action = lastAction
        var i = msgIndex
        while (++i < messager.msg.size) {
            state.msg.add(messager.msg[i])
        }
        return state
    }

    fun pollRemainTime(): Long {
        if (timer != null && !(timer as TimerThread).cancel) {
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

    class ControlThread : Thread() {
        var rtb: RealTimeBattle? = null
        var time = 0L
        override fun run() {
            if (rtb != null) {
                while (rtb!!.running >= 0) {
                    if (time > 30 && rtb!!.running == 0) {
                        rtb!!.running = 1;
                    }
                    if (rtb!!.timer != null) {
                        if (!(rtb!!.timer as TimerThread).cancel && (rtb!!.timer as TimerThread).time <= 0) {
                            rtb!!.changeActioner()
                        }
                    }
                    time++
                    sleep(1000)
                }
            }
        }
    }


}