package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.Monster
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.server.persistence.serialize.ObjectDB
import cn.luo.yuan.maze.service.MonsterLoader
import cn.luo.yuan.maze.utils.Random
import java.io.Serializable
import kotlin.system.measureNanoTime

/**
 * Created by gluo on 5/23/2017.
 */
fun main(args: Array<String>) {
    val heroTable = HeroTable()
    val groupTable = GroupTable()
    val random = Random(measureNanoTime {  })
    val monsterLoader = cn.luo.yuan.maze.server.MonsterLoader()
    while (true) {
        var maxLevel = heroTable.maxLevel
        (1..maxLevel).forEach { level ->
            val nonGroupHeroIds = heroTable.getAllHeroIds(level);
            nonGroupHeroIds.removeAll(groupTable.getGroupHeroIds(level));
            val groups = groupTable.getGroups(level);
            for(id in nonGroupHeroIds){
                var nexLevel = true
                val hero = heroTable.getHero(id);
                if(random.nextInt(100) < 40 + random.nextInt(nonGroupHeroIds.size + groups.size)){
                    val monster = monsterLoader.randomMonster(level)
                }else if(random.nextInt(10) < random.nextInt(nonGroupHeroIds.size)){
                    var other:Hero = hero;
                    nonGroupHeroIds.forEach { id ->
                        val h = heroTable.getHero(id);
                        if(random.nextInt(h.name.hashCode()) < random.nextInt(hero.name.hashCode()) && random.nextLong(h.birthDay) <= random.nextLong(hero.birthDay)){//New Group
                            val group = Group()
                            group.heroIds.add(id)
                            group.heroIds.add(hero.id)
                            group.level = level
                            nonGroupHeroIds.removeAll(group.heroIds)
                            nexLevel = false;
                        }else{//Battle
                            other = h;
                        }
                    }
                    if(other!= hero){
                        //Battle
                    }
                }
                if(nexLevel){

                }
            }
        }
    }
}

class Group : Serializable {
    val heroIds = mutableSetOf<String>()
    var level = 1L
    val waitForBackHeroIds = mutableSetOf<String>()
}

class GroupTable {
    val groupDb = ObjectDB<Group>(Group::class.java)
    val cache = mutableListOf<Group>()
    fun getGroupHeroIds(level: Long): Set<String> {
        val ids = mutableSetOf<String>()
        for (group in cache) {
            ids.addAll(group.heroIds);
        }
        return ids
    }

    fun getGroups(level: Long): List<Group> {
        return cache.filter {
            it.level == level
        }
    }
}