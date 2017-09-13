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
import cn.luo.yuan.maze.utils.StringUtils
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/11/2017.
 */
class RealService(val mainProcess: MainProcess) : RealTimeBattle.RealBattleEndListener {
    override fun end(r1: LevelRecord?, r2: LevelRecord?, p1: HarmAble?, p2: HarmAble?) {
        if (r1 != null && r2 != null) {
            recordDb.save(r1)
            recordDb.save(r2)
        }
    }

    val waiting = WaitingQueue()
    val executor = Executors.newScheduledThreadPool(3)!!
    val battling = ConcurrentHashMap<String, RealTimeBattle>()
    val recordDb = ObjectTable<LevelRecord>(LevelRecord::class.java, mainProcess.root)
    val palaceDb = ObjectTable<LevelRecord>(LevelRecord::class.java, File(mainProcess.root, "palace"))
    val legacyDb = ObjectTable<LevelRecord>(LevelRecord::class.java, File(mainProcess.root, "legacy"))
    fun run() {
        executor.scheduleAtFixedRate({
            for (i in (0..ElyosrRealLevel.values().size - 1)) {
                val record = waiting.poolFirst(0)
                if (record != null) {
                    findBattleTarget(record)
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS)
        executor.schedule({
            val date = Date()
            if (date.day == Calendar.SUNDAY) {
                //detect max level
                val records = recordDb.loadAll().toMutableList()
                var max = records.maxBy { it.point }
                val palaceId = palaceDb.loadIds().toSet()
                while (max != null && palaceId.contains(max.id)) {
                    palaceDb.save(max)
                    records.remove(max)
                    max = records.maxBy { it.point }
                }
                if (max != null) {
                    recordDb.save(max)
                }

            }
        }, 20, TimeUnit.HOURS)
    }

    fun pollTopNRecord(n: Int): List<LevelRecord> {
        val sortRecords = recordDb.loadAll().toMutableList()
        sortRecords.sortByDescending { it.point }

        return sortRecords.subList(0, if (n < sortRecords.size) n else sortRecords.size - 1).toList()
    }

    fun stop() {
        executor.shutdown()
        executor.awaitTermination(20, TimeUnit.MILLISECONDS)
    }

    fun pollState(id: String, msgIndex: Int, battleId: String): RealTimeState? {
        val record = queryRecord(id)
        if (record != null) {
            val rtb = battling[record.id]
            if (rtb != null && rtb.id == battleId) {
                return rtb.pollState(msgIndex)
            } else {
                LogHelper.info("Wrong battle!")
                battling.remove(record.id, rtb)
            }
            if (StringUtils.isEmpty(battleId)) {
                synchronized(waiting) {
                    if (!waiting.isQueue(record)) {
                        val serverRecord = mainProcess.heroTable.getRecord(id)
                        if (serverRecord != null && serverRecord.debris >= Data.PALACE_RANGE_COST) {
                            waiting.addQueue(record)
                            LogHelper.info(record.hero?.name + " into range")
                        } else {
                            return NoDebrisState()
                        }
                    }
                }
            }

        }
        return null
    }

    private fun findBattleTarget(record: LevelRecord) {
        executor.execute {
            val targetRecord = waiting.findTarget(record)
            if (targetRecord != null) {
                val rtb = RealTimeBattle(record, targetRecord, 2, 1000, 5, 2)
                val serverRecord = mainProcess.heroTable.getRecord(record.id)
                if (serverRecord != null) {
                    serverRecord.debris -= Data.PALACE_RANGE_COST
                    mainProcess.heroTable.save(serverRecord)
                }
                val targetServerRecord = mainProcess.heroTable.getRecord(targetRecord.id)
                if (targetServerRecord != null) {
                    targetServerRecord.debris -= Data.PALACE_RANGE_COST
                    mainProcess.heroTable.save(targetServerRecord)
                }
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
        if (!onlyQuit) {
            val rtb = battling[id]
            rtb?.quit(id)
            battling.remove(id)
        }
        val record = queryRecord(id)
        if (record != null) {
            synchronized(waiting) {
                waiting.removeQueue(record)
            }
            LogHelper.info(record.hero?.name + " out range")
        }
    }

    fun newOrUpdateRecord(record: LevelRecord) {
        if (record.hero != null) {
            newOrUpdateRecord(record.hero as Hero, record.pets, record.accessories, record.skills, record.head)
        }
    }

    fun newOrUpdateRecord(hero: Hero, pets: List<Pet>?, accessories: List<Accessory>?, skills: List<Skill>?, head: String) {
        var record = queryRecord(hero.id)
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

    fun queryRecord(id: String): LevelRecord? {
        val record = recordDb.loadObject(id)
        if (record != null) {
            record.hero!!.pets.addAll(record.pets)
            record.hero!!.accessories.addAll(record.accessories)
            record.hero!!.skills = record.skills.toTypedArray()
        }
        return record
    }

}