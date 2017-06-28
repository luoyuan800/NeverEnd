package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.Maze
import cn.luo.yuan.maze.model.ServerRecord
import cn.luo.yuan.maze.server.model.Messager
import cn.luo.yuan.maze.server.model.SingleMessage
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.server.persistence.NPCTable
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.service.RunningServiceInterface
import cn.luo.yuan.maze.utils.Random
import java.io.File
import java.util.*

/**
 * Created by gluo on 6/26/2017.
 */
class HeroBattleService(private val tableCache: MutableMap<String, HeroTable>) : Runnable, RunningServiceInterface {
    override fun isPause(): Boolean {
        return false
    }

    val random = Random(System.currentTimeMillis());
    override fun run() {
        if(tableCache.size < 20){
            tableCache.put("npc", NPCTable(File("data/npc")))
        }else{
            tableCache.remove("npc");
        }
        for ((id, table) in tableCache) {
            val hero = table.getHero(id, 0)
            val messager = Messager()
            val record = table.getRecord(id)
            messager.addReceiver(record)
            if (hero.currentHp > 0) {
                val maze = table.getMaze(id, 0)
                val oid = filterMatch(id, maze.maxLevel)
                if(oid!=null) {
                    val otable: HeroTable = tableCache[oid] as HeroTable
                    val ohero = otable.getHero(oid, 0)
                    if (ohero!!.currentHp > 0) {
                        val omaze = otable.getMaze(oid, 0)
                        messager.addReceiver(otable.getRecord(oid))
                        val bs = BattleService(hero, ohero, random, this)
                        bs.setBattleMessage(messager)
                        val awardMaterial = random.nextLong(maze.maxLevel + omaze.maxLevel)
                        if (bs.battle(maze.level + omaze.level)) {
                            record.winCount ++
                            otable.getRecord(oid).lostCount ++
                            otable.getRecord(oid).dieCount++
                            otable.getRecord(oid).dieTime = System.currentTimeMillis()
                            messager.materialGet(hero.displayName, awardMaterial);
                            record.data!!.material += awardMaterial
                        } else {
                            messager.materialGet(ohero.displayName, awardMaterial);
                            record.dieCount++
                            record.dieTime = System.currentTimeMillis()
                            record.lostCount ++
                            otable.getRecord(oid).winCount ++
                            otable.getRecord(oid).data!!.material += awardMaterial
                        }
                        continue
                    }
                }
                //TODO Random events
            }else{
                val period = Data.RESTOREPERIOD - (System.currentTimeMillis() - record.dieTime)
                if(period <= 0){
                    hero.hp = hero.maxHp
                    messager.restore(hero.displayName)
                }else{
                    messager.waitingForRestore(hero.displayName, period)
                }
            }
        }
        range()
        for(table in tableCache.values){
            table.save()
        }
    }

    private fun range(){
        val sortedByDescending = tableCache.keys.sortedByDescending {
            val table = tableCache[it] as HeroTable
            val record = table.getRecord(it)
            record.winCount
        }
        for(id in sortedByDescending){
            tableCache[id]!!.getRecord(id).range = sortedByDescending.indexOf(id) + 1
        }
    }

    private fun filterMatch(id:String,level:Long):String?{
        val items = ArrayList<String>(tableCache.keys.filter {
            val table = tableCache[it] as HeroTable
            val maze = table.getMaze(it, 0)
            val hero = table.getHero(it, 0)
           it == "npc" || (hero.currentHp > 0 && it!=id && Math.abs(maze.maxLevel - level) < 100)
        })

        return random.randomItem(items)
    }
}
