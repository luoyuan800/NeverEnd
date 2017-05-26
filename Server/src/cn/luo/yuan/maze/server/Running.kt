package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.Maze
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.server.persistence.serialize.ObjectDB
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.utils.Random
import java.io.Serializable
import java.util.*
import kotlin.system.measureNanoTime

/**
 * Created by gluo on 5/23/2017.
 */

var msg = Message();
var heroTable = HeroTable()
var groupTable = GroupTable()
var random = Random(measureNanoTime { })
var monsterLoader = cn.luo.yuan.maze.server.MonsterLoader()
var running = true;
fun main(args: Array<String>) {

    while (running) {
        val maxLevel = heroTable.maxLevel
        (1..maxLevel).forEach { level ->
            val nonGroupHeroIds = heroTable.getAllHeroIds(level)
            nonGroupHeroIds.removeAll(groupTable.getGroupHeroIds(level))
            val groups = groupTable.getGroups(level)
            groupAction(groupTable, groups, heroTable, level, monsterLoader, msg, nonGroupHeroIds, random)
            nonGroupAction(groupTable, heroTable, level, monsterLoader, msg, nonGroupHeroIds, random)
        }
    }
}

private fun groupAction(groupTable: GroupTable, groups: MutableList<Group>, heroTable: HeroTable, level: Long, monsterLoader: MonsterLoader, msg: Message, nonGroupHeroIds: MutableSet<String>, random: Random) {
    for (group in groups.toList()) {
        if (group.heroIds.size == 1) {
            val id = group.heroIds.first()
            nonGroupHeroIds.add(id)
            group.heroIds.clear();
        }
        if (group.heroIds.size == 0) {
            groupTable.remove(group)
            groups.remove(group)
            continue
        }
        var battled = false
        if (random.nextInt(100) < 40 + random.nextInt(nonGroupHeroIds.size + groups.size)) {
            for (heroId in nonGroupHeroIds) {
                if (random.nextInt(heroId.hashCode()) < random.nextInt(group.id.hashCode())) {
                    val nonGHero = heroTable.getHero(heroId);
                    if (nonGHero != null) {
                        val maze = heroTable.getMaze(heroId);
                        if (maze.maxLevel > level && random.nextBoolean()) {
                            group.heroIds.add(heroId)
                            nonGroupHeroIds.remove(heroId);
                            groupTable.save(group);
                        } else {
                            //Battle
                            battled = true;
                        }
                    }
                    break
                }
            }
            if (!battled) {
                for (other in groups.toList()) {
                    if (other.heroIds.size > 1 && other != group) {
                        if (random.nextInt(other.id.hashCode()) < random.nextInt(group.id.hashCode())) {
                            //Group battle
                            battled = true;
                            break;
                        }
                    }
                }
            }
        } else {
            val monster = monsterLoader.randomMonster(level);
            if (monster != null) {
                battled = true;
            } else {
                groupNextLevel(group, heroTable)
                groups.remove(group)
            }
        }
        if (battled && random.nextInt(100) < random.nextInt(group.heroIds.size)) {
            groupNextLevel(group, heroTable)
            groups - group
        }
        groupTable.save(group)
    }
}

private fun groupNextLevel(group: Group, heroTable: HeroTable) {
    //Goto next level
    group.level++
    for(id in group.heroIds){
        val maze = heroTable.getMaze(id);
        if(maze!=null){
            maze.level = group.level;
            if(maze.level > maze.maxLevel){
                maze.maxLevel = maze.level
            }
        }
    }
    if (group.level > heroTable.maxLevel) {
        heroTable.maxLevel = group.level;
    }
}

private fun nonGroupAction(groupTable: GroupTable, heroTable: HeroTable, level: Long, monsterLoader: MonsterLoader, msg: Message, nonGroupHeroIds: MutableSet<String>, random: Random) {
    for (id in nonGroupHeroIds.toList()) {
        if (id in nonGroupHeroIds) {
            var nexLevel = false
            val hero = heroTable.getHero(id);
            val maze = heroTable.getMaze(hero.id)
            if (random.nextInt(100) < 40 + random.nextInt(nonGroupHeroIds.size)) {
                val monster = monsterLoader.randomMonster(level)
                val bs = BattleService(hero, monster, random);
                bs.setBattleMessage(msg);
                if (bs.battle(level)) {
                    maze.streaking++
                    nexLevel = random.nextLong(maze.streaking) > 3 + random.nextInt(100)
                } else {
                    nexLevel = false;
                    failed(hero, heroTable, maze)
                    nonGroupHeroIds.remove(hero.id)
                }
            } else if (random.nextInt(10) < random.nextInt(nonGroupHeroIds.size)) {
                var other: Hero = hero;
                for(otherId in nonGroupHeroIds.toList()){
                    val h = heroTable.getHero(otherId);
                    if (random.nextInt(h.name.hashCode()) < random.nextInt(hero.name.hashCode()) && random.nextLong(h.birthDay) <= random.nextLong(hero.birthDay)) {//New Group
                        val group = Group()
                        group.heroIds.add(otherId)
                        group.heroIds.add(hero.id)
                        group.level = level
                        nonGroupHeroIds.removeAll(group.heroIds)
                        msg.createGroup(hero, h)
                        groupTable.newGroup(group)
                        nexLevel = false;
                        break
                    } else {//Battle
                        other = h;
                        break
                    }
                }
                if (other != hero) {
                    //Battle
                    val bs = BattleService(hero, other, random)
                    bs.setBattleMessage(msg);
                    if (bs.battle(level)) {
                        maze.streaking++
                        nexLevel = random.nextLong(maze.streaking) > 3 + random.nextInt(100)
                        val oMaze = heroTable.getMaze(other.id)
                        failed(other, heroTable, oMaze)
                        nonGroupHeroIds.remove(other.id)
                    } else {
                        failed(hero, heroTable, maze)
                        nonGroupHeroIds.remove(hero.id)
                    }
                } else {
                    nexLevel = true;
                }
            }
            if (nexLevel) {
                maze.level += 1
                if (maze.level > maze.maxLevel) {
                    maze.maxLevel = maze.level;
                }
                heroTable.saveMaze(maze, hero.id)
                nonGroupHeroIds.remove(hero.id)
            }
        }
    }
}

private fun failed(hero: Hero, heroTable: HeroTable, maze: Maze) {
    hero.hp = hero.maxHp
    maze.level = 1
    heroTable.saveHero(hero);
    heroTable.saveMaze(maze, hero.id)
}

class Group : Serializable {

    var id = UUID.randomUUID().toString()
    val heroIds = mutableSetOf<String>()
    var level = 1L
}

class GroupTable {
    val groupDb = ObjectDB<Group>(Group::class.java)
    val cache = mutableListOf<Group>()
    fun getGroupHeroIds(level: Long): Set<String> {
        val ids = mutableSetOf<String>()
        for (group in cache) {
            if (group.level == level) {
                ids.addAll(group.heroIds);
            }
        }
        return ids
    }

    fun getGroups(level: Long): MutableList<Group> {
        return cache.filter {
            it.level == level
        }.toMutableList()
    }

    fun newGroup(group: Group) {
        cache.add(group);
        groupDb.save(group, group.id)
    }

    fun remove(group: Group) {
        groupDb.delete(group.id)
    }

    fun save(group: Group) {
        groupDb.save(group, group.id)
    }
}