package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.Messager
import cn.luo.yuan.maze.server.persistence.GroupTable
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.server.persistence.NPCTable
import cn.luo.yuan.maze.server.servcie.ServerDataManager
import cn.luo.yuan.maze.server.servcie.ServerGameContext
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.service.RunningServiceInterface
import cn.luo.yuan.maze.utils.Random
import cn.luo.yuan.maze.utils.StringUtils
import java.io.File
import java.util.*
import java.util.concurrent.ScheduledExecutorService

/**
 *
 * Created by gluo on 6/26/2017.
 */
class HeroBattleService(private val table: HeroTable, val groups:GroupTable, val executor:ScheduledExecutorService, val battleInterval :Long) : Runnable, RunningServiceInterface {
    override fun getContext(): InfoControlInterface {
        return GameContext();
    }

    override fun isPause(): Boolean {
        return false
    }
    val random = Random(System.currentTimeMillis())
    override fun run() {
        try {
            LogHelper.info("Start battle, number:" + table.size())
            val npc = NPCTable(File("npc"))
            loop@ for (id in table.allHeroIds) {
                if (id == "npc") {
                    continue
                }
                val record = table.getRecord(id)
                val hero = table.getHero(id, 0)
                if (record != null && hero != null) {
                    val messager = Messager()
                    val group = getGroup(id)
                    registerMessageReceiver(messager, id)
                    if (record.dieCount <= record.restoreLimit && hero.currentHp > 0 && (group == null || group.totalHp() > 0) ) {
                        val maze = table.getMaze(id, 0)
                        var oid = filterMatch(id, maze.maxLevel)
                        var ohero: Hero
                        var ogroup: Group? = null
                        var omaze: Maze
                        var otherRecord: ServerRecord
                        if (oid != null && oid != "npc") {
                            ohero = table.getHero(oid, 0)
                            ogroup = getGroup(oid)
                            omaze = table.getMaze(oid, 0)
                            otherRecord = table.getRecord(oid)
                        } else {
                            oid = "npc"
                            ohero = npc.getHero(oid, 0)
                            omaze = npc.getMaze(oid, maze.maxLevel)
                            otherRecord = npc.getRecord(oid)
                        }
                        if (ohero!!.currentHp > 0 || (ogroup != null && ogroup.totalHp() > 0)) {
                            if (oid != "npc") {
                                registerMessageReceiver(messager, oid)
                            }
                            if (group == null && ogroup == null && random.nextInt(hero.displayName.length) > random.nextInt(ohero.displayName.length)) {
                                val newgroup = groups.add(id, oid)
                                if(oid == "npc"){
                                    newgroup.npc = ohero;
                                }
                                messager.buildGroup(hero.displayName, ohero.displayName)
                                if (StringUtils.isNotEmpty(record.data!!.helloMsg["group"])) {
                                    messager.speak(hero.displayName, record.data!!.helloMsg["group"])
                                }
                                if (StringUtils.isNotEmpty(otherRecord.data!!.helloMsg["group"])) {
                                    messager.speak(ohero.displayName, otherRecord.data!!.helloMsg["group"])
                                }
                                continue@loop
                            }
                            val bs = BattleService(group ?: hero, ogroup ?: ohero, random, this)
                            bs.heroContext = buildGameContext(record);
                            bs.monsterContext = buildGameContext(otherRecord);
                            bs.setBattleMessage(messager)
                            if (StringUtils.isNotEmpty(record.data!!.helloMsg["meet"])) {
                                messager.speak(hero.displayName, record.data!!.helloMsg["meet"])
                            }
                            if (StringUtils.isNotEmpty(otherRecord.data!!.helloMsg["meet"])) {
                                messager.speak(ohero.displayName, otherRecord.data!!.helloMsg["meet"])
                            }
                            val awardMaterial = random.nextLong((maze.maxLevel + omaze.maxLevel)/2) + 50
                            if (bs.battle(maze.maxLevel + omaze.maxLevel)) {
                                if (StringUtils.isNotEmpty(record.data!!.helloMsg["win"])) {
                                    messager.speak(hero.displayName, record.data!!.helloMsg["win"])
                                }
                                if (StringUtils.isNotEmpty(otherRecord.data!!.helloMsg["lost"])) {
                                    messager.speak(ohero.displayName, otherRecord.data!!.helloMsg["lost"])
                                }
                                win(awardMaterial, hero, messager, record)
                                lost(otherRecord, awardMaterial, messager)
                            } else {
                                if (StringUtils.isNotEmpty(record.data!!.helloMsg["lost"])) {
                                    messager.speak(hero.displayName, record.data!!.helloMsg["lost"])
                                }
                                if (StringUtils.isNotEmpty(otherRecord.data!!.helloMsg["win"])) {
                                    messager.speak(ohero.displayName, otherRecord.data!!.helloMsg["win"])
                                }
                                lost(record, awardMaterial, messager)
                                win(awardMaterial, ohero, messager, otherRecord)
                            }
                            continue@loop
                        }
                        //TODO Random events
                    } else {
                        if (record.dieCount > record.restoreLimit) {
                            messager.restoreLimit(hero.displayName)
                            if(group!=null){
                                messager.leaveGroup(hero.displayName)
                            }
                            groups.remove(id)
                        } else {
                            val period = Data.RESTOREPERIOD - (System.currentTimeMillis() - record.dieTime)
                            if (period <= 0) {
                                hero.hp = hero.maxHp
                                messager.restore(hero.displayName)
                            } else {
                                messager.waitingForRestore(hero.displayName, period)
                            }
                        }
                        record.data!!.material += 10
                        messager.materialGet(record.data!!.hero!!.displayName, 10)
                    }
                }
            }
            range()
            table.allHeroIds
                    .mapNotNull { table.getRecord(it) }
                    .forEach { if(it.data!=null && it.dieCount < it.restoreLimit) it.messages.add("下一场战斗 ${battleInterval} 分钟后开始") }
            table.save()
            LogHelper.info("Finished battle!")
        } catch (exp: Exception) {
            LogHelper.error(exp)
        }
    }

    private fun buildGameContext(record: ServerRecord): ServerGameContext {
        val heroConfig = NeverEndConfig()
        heroConfig.isElementer = record.data!!.isElementer
        heroConfig.isLongKiller = record.data!!.isLong
        val hdm = ServerDataManager(record.data!!.hero, heroConfig);
        val hContext = ServerGameContext(record.data!!.hero, hdm, record.data!!.maze)
        hContext.executor = executor
        return hContext
    }

    private fun lost(otherReciod: ServerRecord, awardMaterial: Long, messager: Messager) {
        otherReciod.lostCount++
        otherReciod.dieCount++
        otherReciod.dieTime = System.currentTimeMillis()
        otherReciod.data!!.material += (awardMaterial/2)
        if(otherReciod.data?.hero!=null) {
            messager.materialGet(otherReciod.data!!.hero!!.displayName, awardMaterial / 2)
        }
    }

    private fun win(awardMaterial: Long, hero: Hero, messager: Messager, record: ServerRecord) {
        record.winCount++
        record.currentWin++
        record.data!!.material += awardMaterial
        messager.materialGet(hero.displayName, awardMaterial)
    }

    private fun registerMessageReceiver(messager: Messager, id: String) {
        val group = getGroup(id)
        if (group != null) {
            for (h in group.heroes) {
                messager.addReceiver(table.getRecord(h.id))
            }
        } else {
            messager.addReceiver(table.getRecord(id))
        }
    }

    private fun getGroup(id: String): Group? {
        if (id == "npc") {
            return null
        }
        val group = groups.groups.find {
            it.isInGroup(id)
        }
        if(group!=null){
            val g = Group()
            for(hid in group.heroIds){
                if(hid != "npc") {
                    val hero = table.getHero(hid)
                    if (hero != null) {
                        g.heroes.add(hero)
                    }
                }
            }
            if(group.npc!=null){
                g.heroes.add(group.npc!!)
            }
            if(g.heroes.size >= 2){
                return g
            }else{
                groups.remove(group)
            }
        }
        return null
    }

    private fun range() {
        LogHelper.info("Ranging heroes")
        val sortedByDescending = table.allHeroIds.sortedByDescending {
            val record = table.getRecord(it)
            val maze = record?.data?.maze
            maze?.maxLevel ?: 0L
        }
        for (id in sortedByDescending) {
            val record = table.getRecord(id)
            if (record != null) {
                record.range = sortedByDescending.indexOf(id) + 1
            }
        }
    }

    private fun filterMatch(id: String, level: Long): String? {
        val group = getGroupHolder(id);
        val items = ArrayList<String>(table.allHeroIds.filter {
            val maze = table.getMaze(it, 0)
            val hero = table.getHero(it, 0)
            val isSomeGroup = (group != null && group.isInGroup(it))
            it == "npc" || (!isSomeGroup && hero != null && maze != null && hero.currentHp > 0 && it != id && Math.abs(maze.maxLevel - level) < 100)
        })
        items.add("npc")
        val oid = random.randomItem(items)
        return if (oid == "npc") null else oid
    }

    private fun getGroupHolder(id: String): GroupHolder? {
        return groups.find(id)
    }
}
