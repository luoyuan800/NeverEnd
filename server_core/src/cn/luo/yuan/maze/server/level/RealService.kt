package cn.luo.yuan.maze.server.level

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.real.*
import cn.luo.yuan.maze.model.real.action.RealTimeAction
import cn.luo.yuan.maze.model.real.level.ElyosrRealLevel
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.serialize.ObjectTable
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.MainProcess
import cn.luo.yuan.maze.service.AccessoryHelper
import cn.luo.yuan.maze.service.real.RealTimeBattle
import cn.luo.yuan.maze.utils.Random
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

    val random = Random(System.currentTimeMillis())
    val waiting = WaitingQueue()
    val executor = Executors.newScheduledThreadPool(3)!!
    val battling = ConcurrentHashMap<String, RealTimeBattle>()
    val recordDb = ObjectTable<LevelRecord>(LevelRecord::class.java, mainProcess.root)
    val palaceDb = ObjectTable<LevelRecord>(LevelRecord::class.java, File(mainProcess.root, "palace"))
    val legacyDb = ObjectTable<LevelRecord>(LevelRecord::class.java, File(mainProcess.root, "legacy"))
    val waitingTime = mutableMapOf<String, Long>()
    fun run() {
        executor.scheduleAtFixedRate({
            for (i in (0 until ElyosrRealLevel.values().size)) {
                synchronized(waiting) {
                    val record = waiting.poolFirst(0)
                    if (record != null) {
                        findBattleTarget(record)
                    }
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
        if (sortRecords.isEmpty()) {
            return sortRecords
        }
        sortRecords.sortByDescending { it.point }

        return sortRecords.subList(0, if (n <= sortRecords.size) n else sortRecords.size).toList()
    }

    fun stop() {
        executor.shutdown()
        executor.awaitTermination(20, TimeUnit.MILLISECONDS)
    }

    fun updateRealRecordPriorPoint(id:String){
        val record = queryRecord(id)
        if(record!=null){
            record.priorPoint = record.point
            recordDb.save(record)
        }
    }

    fun pollState(id: String, msgIndex: Int, battleId: String?, cstate: RealState?): RealState? {
        val record = queryRecord(id)
        if (record != null) {
            if (cstate != null) {
                if (cstate is Waiting && (cstate.priorState == null || cstate.priorState is Waiting)) {
                    return inQueue(id, record)
                }
            }
            val rtb = battling[record.id]
            if (rtb != null) {
                return rtb.pollState(msgIndex)
            }
        }
        return null
    }

    fun inQueue(id: String, record: LevelRecord): RealState {
        synchronized(waiting) {
            if (battling[record.id] == null) {
                if (!waiting.isQueue(record)) {
                    val serverRecord = mainProcess.heroTable.getRecord(id)
                    if (serverRecord != null && serverRecord.debris >= Data.PALACE_RANGE_COST) {
                        waiting.addQueue(record)
                        //LogHelper.info(record.hero?.name + " into range")
                    } else {
                        return NoDebris()
                    }
                }
                return Waiting()
            }
            val battling1 = Battling()
            battling1.id = battling[record.id]!!.id
            return battling1
        }
    }

    private fun findBattleTarget(record: LevelRecord) {
        executor.execute {
            val targetRecord = waiting.findTarget(record)
            if (targetRecord != null && targetRecord.id != record.id) {
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
                val rtb = RealTimeBattle(record, targetRecord, 2, 1000, 5, 2)
                battling.put(record.id, rtb)
                battling.put(targetRecord.id, rtb)
                LogHelper.info(record.hero?.name + " & " + targetRecord.hero?.name + " start range battle")
            } else {
                val l = waitingTime[record.id]
                if (l != null && l >= 45000) {
                    val npc = randomLegacyNPC(record.point)
                    if (npc != null) {
                        val serverRecord = mainProcess.heroTable.getRecord(record.id)
                        if (serverRecord != null) {
                            serverRecord.debris -= Data.PALACE_RANGE_COST
                            mainProcess.heroTable.save(serverRecord)
                        }
                        val rtb = RealTimeBattle(record, npc, 2, 1000, 5, 2)
                        battling.put(record.id, rtb)
                        LogHelper.info(record.hero?.name + " & " + npc.hero?.name + " start range battle")
                    } else {
                        waitingTime[record.id] = l + 1
                        waiting.addQueue(record)
                    }
                } else {
                    waitingTime[record.id] = l!! + 1
                    waiting.addQueue(record)
                }
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

    fun singleQuit(id: String, cstate: RealState): RealState {
        val state = Quit()
        val record = recordDb.loadObject(id)
        if (cstate is Waiting) {
            waiting.removeQueue(record)
            if (battling[id] != null) {
                state.nextState = battling[id]?.pollState(0)
                LogHelper.info(record.hero?.displayName + " want out range, but he already in battling!")
            } else {
                LogHelper.info(record.hero?.displayName + " out range")
            }
        } else if (cstate is Battling || cstate is BattleEnd) {
            battling[id]?.quit(id)
            LogHelper.info(record.hero?.displayName + " quit battling")
        }
        battling.remove(id)
        return state
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
        initRecord(record)
        return record
    }

    private fun initRecord(record: LevelRecord?) {
        if (record != null && record.hero != null) {
            if (record.hero!!.pets.isEmpty()) {
                record.hero!!.pets.addAll(record.pets)
            }
            if (record.hero!!.accessories.isEmpty()) {
                for (acc in record.accessories) {
                    AccessoryHelper.mountAccessory(acc, record.hero!!, false, null)
                }
                AccessoryHelper.judgeElementEnable(record.hero!!, record.isElementer)
            }
        }
    }

    fun randomLegacyNPC(point: Long): LevelRecord? {
        val c = ElyosrRealLevel.getCurrentLevel(point);
        val record = random.randomItem(legacyDb.loadAll().filter {
            ElyosrRealLevel.getCurrentLevel(it.point) == c
        })
        if (record != null) {
            initRecord(record)
        }
        return record
    }

    fun pollTargetRecord(id: String): LevelRecord? {
        val rtb = battling[id]
        if (rtb != null) {
            if (rtb.p1.id == id) {
                return rtb.p2Record!!
            } else {
                return rtb.p1Record!!
            }
        }
        return null
    }

    fun pollBattleTurn(id: String): Long {
        val rtb = battling[id]
        if (rtb != null) {
            return rtb.turn
        }
        return 0
    }

}