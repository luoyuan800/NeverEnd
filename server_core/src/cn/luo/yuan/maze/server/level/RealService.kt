package cn.luo.yuan.maze.server.level

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.real.NoDebrisState
import cn.luo.yuan.maze.model.real.RealTimeState
import cn.luo.yuan.maze.model.real.action.RealTimeAction
import cn.luo.yuan.maze.model.real.level.ElyosrRealLevel
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.serialize.ObjectTable
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.MainProcess
import cn.luo.yuan.maze.service.real.RealTimeBattle
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/11/2017.
 */
class RealService(val mainProcess: MainProcess) {
    val waiting = WaitingQueue()
    val executor = Executors.newScheduledThreadPool(3)
    val battling = ConcurrentHashMap<String, RealTimeBattle>()
    val recordDb = ObjectTable<LevelRecord>(LevelRecord::class.java, mainProcess.root)
    fun run() {
        executor.scheduleAtFixedRate({
            for (i in (0..ElyosrRealLevel.values().size - 1)) {
                val record = waiting.poolFirst(0)
                if (record != null) {
                    findBattleTarget(record)
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        executor.shutdown()
        executor.awaitTermination(20, TimeUnit.MILLISECONDS)
    }

    fun pollState(id: String, msgIndex: Int): RealTimeState? {
        val record = recordDb.loadObject(id)
        if (record != null) {
            if (!waiting.isQueue(record)) {
                val serverRecord = mainProcess.heroTable.getRecord(id)
                if (serverRecord != null && serverRecord.debris >= Data.PALACE_RANGE_COST) {
                    serverRecord.debris -= Data.PALACE_RANGE_COST
                    waiting.addQueue(record)
                    LogHelper.info(record.hero?.name + " into range")
                }else{
                    return NoDebrisState()
                }
            }
            val rtb = battling[record.id]
            return rtb?.pollState(msgIndex)
        }
        return null
    }

    private fun findBattleTarget(record: LevelRecord) {
        executor.execute {
            val targetRecord = waiting.findTarget(record)
            if (targetRecord != null) {
                val rtb = RealTimeBattle(record, targetRecord, 1000, 0, 5, 2)
                battling.put(record.id, rtb)
                battling.put(targetRecord.id, rtb)
                executor.scheduleAtFixedRate({
                    if (rtb.quiter.size >= 2) {
                        for (i in rtb.quiter) {
                            battling.remove(i)
                        }
                    }
                }, 1, 100, TimeUnit.MILLISECONDS)
                LogHelper.info(record.hero?.name + " & " + targetRecord.hero?.name + " start range battle")

            } else {
                waiting.addQueue(record)
            }
        }
    }

    fun ready(id: String) {
        battling[id]?.start()
    }

    fun action(action: RealTimeAction): Boolean {
        val rtb = battling[action.ownerId]
        if (rtb != null) {
            return rtb.action(action)
        }
        return false
    }

    fun singleQuit(id: String, onlyQuit: Boolean) {
        if(!onlyQuit) {
            val rtb = battling[id]
            rtb?.quit(id)
        }
        val record = recordDb.loadObject(id);
        if(record!=null) {
            waiting.removeQueue(record)
            LogHelper.info(record.hero?.name + " out range")
        }
    }

    fun newOrUpdateRecord(record: LevelRecord) {
        if (record.hero != null) {
            newOrUpdateRecord(record.hero as Hero, record.pets, record.accessories, record.skills, record.head)
        }
    }

    fun newOrUpdateRecord(hero: Hero, pets: List<Pet>?, accessories: List<Accessory>?, skills: List<Skill>?, head: String) {
        var record = recordDb.loadObject(hero.id)
        if (record == null) {
            record = LevelRecord(hero.id)
        }
        record.hero = hero
        record.head = head
        if (pets != null) {
            record.pets.clear()
            record.pets.addAll(pets)
        }
        if (accessories != null) {
            record.accessories.clear()
            record.accessories.addAll(accessories)
        }
        if (skills != null) {
            record.skills.clear()
            record.skills.addAll(skills)
        }
        recordDb.save(record, record.id)
    }

    fun queryRecord(id: String): LevelRecord {
        return recordDb.loadObject(id)
    }

}