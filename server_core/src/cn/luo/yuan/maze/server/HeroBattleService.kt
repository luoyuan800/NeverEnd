package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.server.model.Messager
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.server.persistence.NPCTable
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.service.RunningServiceInterface
import cn.luo.yuan.maze.utils.Random
import cn.luo.yuan.maze.utils.StringUtils
import java.io.File
import java.util.*

/**
 * Created by gluo on 6/26/2017.
 */
class HeroBattleService(private val tableCache: MutableMap<String, HeroTable>, val groups: MutableList<GroupHolder>, val main:MainProcess) : Runnable, RunningServiceInterface {
    override fun isPause(): Boolean {
        return false
    }

    val random = Random(System.currentTimeMillis());
    override fun run() {
        LogHelper.info("Start battle, number:" + tableCache.size)
        if (tableCache.size < 20) {
            tableCache.put("npc", NPCTable(File("data/npc")))
        } else {
            tableCache.remove("npc");
        }
        for ((id, table) in tableCache) {
            if (id == "npc") {
                continue
            }
            val hero = table.getHero(id, 0)
            val messager = Messager()
            val record = table.getRecord(id)
            if (random.nextBoolean()) {
                record.gift++
            }
            val group = getGroup(id)
            registerMessageReceiver(messager, id)
            if (hero.currentHp > 0) {
                val maze = table.getMaze(id, 0)
                val oid = filterMatch(id, maze.maxLevel)
                if (oid != null) {
                    val otable: HeroTable = tableCache[oid] as HeroTable
                    val ohero = otable.getHero(oid, 0)
                    val ogroup = getGroup(oid)
                    if (ohero!!.currentHp > 0) {

                        val omaze = otable.getMaze(oid, 0)
                        val otherReciod = otable.getRecord(oid)
                        registerMessageReceiver(messager,oid)
                        if(group==null && ogroup==null && random.nextInt(hero.displayName.length) == random.nextInt(ohero.displayName.length)){
                            main.addGroup(id,oid);
                            if (StringUtils.isNotEmpty(record.data!!.helloMsg["group"])) {
                                messager.speak(hero.displayName, record.data!!.helloMsg["group"]);
                            }
                            if (StringUtils.isNotEmpty(otherReciod.data!!.helloMsg["group"])) {
                                messager.speak(ohero.displayName, otherReciod.data!!.helloMsg["group"]);
                            }
                            continue;
                        }
                        val bs = BattleService(group ?: hero, ogroup ?: ohero, random, this)
                        bs.setBattleMessage(messager)
                        if (StringUtils.isNotEmpty(record.data!!.helloMsg["meet"])) {
                            messager.speak(hero.displayName, record.data!!.helloMsg["meet"]);
                        }
                        if (StringUtils.isNotEmpty(otherReciod.data!!.helloMsg["meet"])) {
                            messager.speak(ohero.displayName, otherReciod.data!!.helloMsg["meet"]);
                        }
                        val awardMaterial = random.nextLong(maze.maxLevel + omaze.maxLevel) + 1
                        if (bs.battle(maze.level + omaze.level)) {
                            if (StringUtils.isNotEmpty(record.data!!.helloMsg["win"])) {
                                messager.speak(hero.displayName, record.data!!.helloMsg["win"]);
                            }
                            if (StringUtils.isNotEmpty(otherReciod.data!!.helloMsg["lost"])) {
                                messager.speak(ohero.displayName, otherReciod.data!!.helloMsg["lost"]);
                            }
                            win(awardMaterial, hero, messager, record)
                            lost(otherReciod)
                        } else {
                            if (StringUtils.isNotEmpty(record.data!!.helloMsg["lost"])) {
                                messager.speak(hero.displayName, record.data!!.helloMsg["lost"]);
                            }
                            if (StringUtils.isNotEmpty(otherReciod.data!!.helloMsg["win"])) {
                                messager.speak(ohero.displayName, otherReciod.data!!.helloMsg["win"]);
                            }
                            messager.materialGet(ohero.displayName, awardMaterial);
                            lost(record)
                            win(awardMaterial, ohero, messager, otherReciod)

                        }
                        continue
                    }
                }
                //TODO Random events
            } else {
                if (record.dieCount > record.restoreLimit) {
                    messager.restoreLimit(hero.displayName);
                } else {
                    val period = Data.RESTOREPERIOD - (System.currentTimeMillis() - record.dieTime)
                    if (period <= 0) {
                        hero.hp = hero.maxHp
                        messager.restore(hero.displayName)
                        main.removeGroup(id)
                    } else {
                        messager.waitingForRestore(hero.displayName, period)
                    }
                }
            }
        }
        range()
        for (table in tableCache.values) {
            table.save()
        }
        LogHelper.info("Finished battle!")
    }

    private fun lost(otherReciod: ServerRecord) {
        otherReciod.lostCount++
        otherReciod.dieCount++
        otherReciod.dieTime = System.currentTimeMillis()
    }

    private fun win(awardMaterial: Long, hero: Hero, messager: Messager, record: ServerRecord) {
        record.winCount++
        record.currentWin++
        record.data!!.material += awardMaterial
        messager.materialGet(hero.displayName, awardMaterial);
    }

    private fun registerMessageReceiver(messager: Messager, id:String){
        val group = getGroup(id)
        if (group != null) {
            for (h in group.heroes) {
                messager.addReceiver(tableCache[h.id]!!.getRecord(h.id))
            }
        } else {
            messager.addReceiver(tableCache[id]!!.getRecord(id))
        }
    }

    private fun getGroup(id: String): Group? {
        var inGroup = true
        var group = Group()
        loop@ for (holder in groups) {
            if (holder.isInGroup(id)) {
                for (hid in holder.heroIds) {
                    val table = tableCache[hid]
                    if (table != null) {
                        val hero = table.getHero(hid)
                        if (hero != null) {
                            group.heroes.add(hero)
                        } else {
                            main.removeGroup(id)
                            inGroup = false;
                            break@loop
                        }
                    } else {
                        inGroup = false
                    }
                }
            } else {
                inGroup = false;
            }
            break
        }
        if (inGroup) {
            return group
        } else {
            return null
        }
    }

    private fun range() {
        val sortedByDescending = tableCache.keys.sortedByDescending {
            val table = tableCache[it] as HeroTable
            val record = table.getRecord(it)
            record.currentWin
        }
        for (id in sortedByDescending) {
            tableCache[id]!!.getRecord(id).range = sortedByDescending.indexOf(id) + 1
        }
    }

    private fun filterMatch(id: String, level: Long): String? {
        val items = ArrayList<String>(tableCache.keys.filter {
            val table = tableCache[it] as HeroTable
            val maze = table.getMaze(it, 0)
            val hero = table.getHero(it, 0)
            it == "npc" || (hero.currentHp > 0 && it != id && Math.abs(maze.maxLevel - level) < 100)
        })

        return random.randomItem(items)
    }
}
