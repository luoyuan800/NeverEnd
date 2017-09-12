package cn.luo.yuan.maze.service.real

import cn.luo.yuan.maze.model.*
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
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/17/2017.
 */
class RealTimeBattle(val p1: HarmAble, val p2: HarmAble, var pointAward:Long, var mateAward:Long, val random: Random) {
    val battle: BattleService = BattleService(p1, p2, random, null)
    val messager = RealBattleMessage()
    var p1ActionPoint = 110
    var p2ActionPoint = 110
    var actionControlThread: ControlThread? = null
    var running = -1
    var timeLimit = 30
    var p1Head = ""
    var p2Head = ""
    var p1Level = -1L
    var p2Level = -1L
    var p1Pet = mutableListOf<Int>()
    var p2Pet = mutableListOf<Int>()
    var actioner: HarmAble? = null
    var timer: TimerThread? = null
    var turn = 0L
    var performed = mutableSetOf<String>()
    var lastAction: RealTimeAction? = null
    var winner: HarmAble? = null
    var loser: HarmAble? = null
    var levelPointAward = 0L
    var levelPointReduce = 0L
    var p1Record:LevelRecord? = null
    var p2Record:LevelRecord? = null
    val quiter = mutableListOf<String>()

    constructor(p1:LevelRecord, p2:LevelRecord, pointAward: Long, mateAward: Long, levelPointAward: Long, levelPointReduce: Long):
            this(p1.hero!!, p2.hero!!, p1.point, p2.point, p1.head, p2.head, pointAward, mateAward,levelPointAward,levelPointReduce, Random(System.currentTimeMillis())){
        p1Record = p1
        p2Record = p2
    }
    constructor(p1: HarmAble, p2: HarmAble, p1Level: Long, p2Level: Long,
                p1Head: String, p2Head: String, pointAward: Long, mateAward:Long, levelPointAward:Long, levelPointReduce:Long, random: Random) :
            this(p1, p2, pointAward, mateAward, random) {
        this.p1Level = p1Level
        if(p1 is PetOwner) {
            p1Pet = mutableListOf<Int>()
            for (pet in p1.pets) {
                p1Pet.add(pet.index)
            }
        }
        if(p2 is PetOwner) {
            p2Pet = mutableListOf<Int>()
            for (pet in p2.pets) {
                p2Pet.add(pet.index)
            }
        }
        this.p2Level = p2Level
        this.p2Head = p2Head
        this.p1Head = p1Head
        this.levelPointAward = levelPointAward
        this.levelPointReduce = levelPointReduce
    }

    init {
        p1.hp = p1.maxHp
        p2.hp = p2.maxHp
        battle.setBattleMessage(messager)
        if (random.nextBoolean()) {
            actioner = p1
        } else {
            actioner = p2
        }
    }

    fun quit(id:String){
        quiter.add(id)
        if(running > 0 && quiter.size == 1){
            if(p1.id == id){
                p1.hp -= p1.currentHp
            }else if(p2.id == id){
                p2.hp -= p2.currentHp
            }
            detectWinner()
        }
    }

    fun start() {
        synchronized(running) {
            running += 1
            if (timeLimit > 0 && actionControlThread == null) {
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
        if(running > 0) {
            if (performed.contains(action.id)) {
                return true
            }
            turn ++
            val defender = if (actioner == p1) p2 else p1
            when (action) {
                is AtkAction -> {
                    if (action.ownerId == actioner!!.id) {
                        if (defender is SkillAbleObject && !defender.skills.isEmpty() && defender.skills[0] is DefSkill &&
                                battle.releaseSkill(actioner, defender, random, false, defender.skills[0], getMinHarm())) {
                            //Release skill
                            defender.skills[0] = EmptySkill.EMPTY_SKILL
                        } else {
                            if(!(defender is PetOwner && battle.petActionOnDef(defender, actioner))){
                                if(actioner is PetOwner){
                                    battle.petActionOnAtk(actioner as PetOwner,defender);
                                }
                                battle.normalAtk(actioner, if (actioner === p1) p2 else p1, if(actioner == p1) p1ActionPoint.toLong() else p2ActionPoint.toLong())
                            }
                        }
                    }
                }
                is AtkSkillAction -> {
                    if (action.ownerId == actioner!!.id && actioner is SkillAbleObject) {
                        val skill = action.skill
                        val point = Data.getSkillActionPoint(skill)
                        if (actioner == p1) {
                            if (point > p1ActionPoint) {
                                return false
                            }
                            p1ActionPoint -= point
                        } else {
                            if (point > p2ActionPoint) {
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
                        val point = Data.getSkillActionPoint(skill)
                        if (actioner == p1) {
                            if (point > p1ActionPoint) {
                                return false
                            }
                            p1ActionPoint -= point
                        } else {
                            if (point > p2ActionPoint) {
                                return false
                            }
                            p2ActionPoint -= point
                        }
                        (actioner!! as SkillAbleObject).skills[0] = skill
                    }
                }
            }
            if (actioner == p1) {
                p2ActionPoint += 50
            } else {
                p1ActionPoint += 50
            }
            detectWinner()
            changeActioner()
            performed.add(action.id)
            lastAction = action
        }
        return true
    }

    private fun detectWinner() {
        if (actioner == p1) {
            if (p2.currentHp <= 0) {
                battleEnd(p1, p2)
            } else if (p1.currentHp <= 0) {
                battleEnd(p2, p1)
            }
        } else {
            if (p1.currentHp <= 0) {
               battleEnd(p2,p1)
            } else if (p2.currentHp <= 0) {
                battleEnd(p1, p2)
            }
        }
        if(winner!=null && loser!=null){
            stop()
            if(winner is NameObject && loser is NameObject) {
                messager.rowMessage("${(winner as NameObject).displayName} 击败了 ${(loser as NameObject).displayName}")
            }
        }
    }

    private fun battleEnd(win:HarmAble, lost:HarmAble) {
        winner = win
        loser = lost
        if(winner == p1){
            if(p1Record!=null){
                p1Record!!.point += levelPointAward
            }
            if(p2Record!=null){
                p2Record!!.point -= levelPointReduce
            }
        }else{
            if(p2Record!=null){
                p2Record!!.point += levelPointAward
            }
            if(p1Record!=null){
                p1Record!!.point -= levelPointReduce
            }
        }
    }

    private fun getMinHarm() = if (actioner == p1) p1ActionPoint.toLong() else p2ActionPoint.toLong()

    fun pollState(msgIndex: Int): RealTimeState {
        val state = RealTimeState()
        if(winner!=null && loser!=null){
            running = -1
            state.winner = winner
            state.loser =loser
            state.awardMate = mateAward
            state.awardPoint = pointAward;
            state.winTip =
                    "获得了 ${if(pointAward > 0) "能力点：" + StringUtils.formatNumber(pointAward) else ""}   ${if(mateAward > 0) "锻造点" + StringUtils.formatNumber(mateAward) else ""}"
        }else {
            state.actioner = actioner
            if (actioner == p1) {
                state.waiter = p2
                state.waiterPoint = p2ActionPoint
                state.actionerPoint = p1ActionPoint
                state.actionerHead = p1Head
                state.waiterHead = p2Head
                state.actionerLevel = p1Level
                state.waiterLevel = p2Level
                state.actionerPetIndex = p1Pet
                state.waiterPetIndex = p2Pet
            } else {
                state.waiter = p1
                state.waiterPoint = p1ActionPoint
                state.actionerPoint = p2ActionPoint
                state.actionerHead = p2Head
                state.waiterHead = p1Head
                state.actionerLevel = p2Level
                state.waiterLevel = p1Level
                state.actionerPetIndex = p2Pet
                state.waiterPetIndex = p1Pet
            }
            state.action = lastAction
        }
        var i = msgIndex
        while (i < messager.msg.size) {
            state.msg.add(messager.msg[i])
            i++
        }
        state.remainTime = pollRemainTime()

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
                    if (time > rtb!!.timeLimit && rtb!!.running == 0) {
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

    interface RealBattleEndListener{
        fun end(x: HarmAble?, y: HarmAble?, awardPoint:Int, awardMate:Int): Unit
    }

}