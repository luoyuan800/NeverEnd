package cn.luo.yuan.maze.server.level

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.LevelRecord
import cn.luo.yuan.maze.model.Pet
import cn.luo.yuan.maze.model.real.RealTimeState
import cn.luo.yuan.maze.model.real.level.ElyosrRealLevel
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.serialize.ObjectTable
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

    fun pollState(id: String, msgIndex: Int): RealTimeState? {
        val record = recordDb.loadObject(id)
        if (record != null) {
            if (!waiting.isQueue(record)) {
                waiting.addQueue(record)
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
            } else {
                waiting.addQueue(record)
            }
        }
    }

    fun newOrUpdateRecord(record: LevelRecord){
        if(record.hero!=null){
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
        if(pets!=null) {
            record.pets.clear()
            record.pets.addAll(pets)
        }
        if(accessories!=null) {
            record.accessories.clear()
            record.accessories.addAll(accessories)
        }
        if(skills!=null) {
            record.skills.clear()
            record.skills.addAll(skills)
        }
        recordDb.save(record, record.id)
    }

    fun queryRecord(id: String): LevelRecord {
        return recordDb.loadObject(id)
    }

}