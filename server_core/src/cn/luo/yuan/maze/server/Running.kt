/*
package cn.luo.yuan.maze.server

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.Maze
import cn.luo.yuan.maze.server.model.Group
import cn.luo.yuan.maze.server.model.Messager
import cn.luo.yuan.maze.server.persistence.GroupTable
import cn.luo.yuan.maze.server.persistence.HeroTable
import cn.luo.yuan.maze.service.BattleService
import cn.luo.yuan.maze.service.RunningServiceInterface
import cn.luo.yuan.maze.utils.Random
import kotlin.system.measureNanoTime

*/
/**
 * Created by gluo on 5/23/2017.
 *//*


public class Running: RunningServiceInterface {
    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPause(): Boolean {
        return false
    }

    fun run(heroTable: HeroTable, groupTable: GroupTable) {
        var random = Random(measureNanoTime { })
        var monsterLoader = cn.luo.yuan.maze.server.MonsterLoader()
        var running = true;
        while (running) {
            val maxLevel = heroTable.maxLevel
            (1..maxLevel).forEach { level ->
                val nonGroupHeroIds = heroTable.getAllHeroIds(level)
                nonGroupHeroIds.removeAll(groupTable.getGroupHeroIds(level))
                val groups = groupTable.getGroups(level)
                groupAction(groupTable, groups, heroTable, level, monsterLoader, nonGroupHeroIds, random)
                nonGroupAction(groupTable, heroTable, level, monsterLoader, nonGroupHeroIds, random)
            }
        }
    }

    private fun groupAction(groupTable: GroupTable, groups: MutableList<Group>, heroTable: HeroTable, level: Long, monsterLoader: MonsterLoader, nonGroupHeroIds: MutableSet<String>, random: Random) {
        grouploop@ for (group in groups.toList()) {
            val msger = Messager();
            for (id in group.heroIds) {
                msger.addReceiver(heroTable.getMessager(id))
            }
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
                            msger.addReceiver(heroTable.getMessager(heroId));
                            val maze = heroTable.getMaze(heroId);
                            if (maze.maxLevel > level && random.nextBoolean()) {
                                group.heroIds.add(heroId)
                                msger.joinGroup(nonGHero.displayName, heroTable.getHero(group.heroIds.elementAt(0)).displayName + "的队伍");
                                nonGroupHeroIds.remove(heroId);
                                groupTable.save(group);
                            } else {
                                val gb = GroupBattle(group,null,nonGHero,heroTable,groupTable,msger,this)
                                if(gb.battle()) {
                                    battled = true;
                                }else{
                                    groups.remove(group)
                                    failed(group, groupTable, heroTable)
                                    continue@grouploop
                                }
                            }
                            msger.removeReceiver(heroTable.getMessager(heroId))
                        }
                        break
                    }
                }
                if (!battled) {
                    for (other in groups.toList()) {
                        if (other.heroIds.size > 1 && other != group) {
                            if (random.nextInt(other.id.hashCode()) < random.nextInt(group.id.hashCode())) {
                                for(id in other.heroIds){
                                    msger.addReceiver(heroTable.getMessager(id))
                                }
                                val gb = GroupBattle(group,other,null,heroTable,groupTable,msger,this)
                                if(gb.battle()) {
                                    groups.remove(other)
                                    failed(other, groupTable, heroTable)
                                    battled = true
                                }else{
                                    groups.remove(group)
                                    failed(group, groupTable, heroTable)
                                    continue@grouploop
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                val monster = monsterLoader.randomMonster(level, true);
                if (monster != null) {
                    battled = true;
                } else {
                    groupNextLevel(group, heroTable)
                    groups.remove(group)
                }
            }
            if (battled && random.nextInt(100) < random.nextInt(group.heroIds.size)) {
                groupNextLevel(group, heroTable)
                groups.remove(group)
            }
            groupTable.save(group)
        }
    }

    private fun groupNextLevel(group: Group, heroTable: HeroTable) {
        //Goto next level
        group.level++
        for (id in group.heroIds) {
            val maze = heroTable.getMaze(id);
            if (maze != null) {
                maze.level = group.level;
                if (maze.level > maze.maxLevel) {
                    maze.maxLevel = maze.level
                }
            }
        }
        if (group.level > heroTable.maxLevel) {
            heroTable.maxLevel = group.level;
        }
    }

    private fun nonGroupAction(groupTable: GroupTable, heroTable: HeroTable, level: Long, monsterLoader: MonsterLoader, nonGroupHeroIds: MutableSet<String>, random: Random) {
        for (id in nonGroupHeroIds.toList()) {
            if (id in nonGroupHeroIds) {
                val msger = Messager()
                msger.addReceiver(heroTable.getMessager(id))
                var nexLevel = false
                val hero = heroTable.getHero(id);
                val maze = heroTable.getMaze(hero.id)
                if (random.nextInt(100) < 40 + random.nextInt(nonGroupHeroIds.size)) {
                    val monster = monsterLoader.randomMonster(level, true)
                    val bs = BattleService(hero, monster, random, null);
                    bs.setBattleMessage(msger);
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
                    for (otherId in nonGroupHeroIds.toList()) {
                        val h = heroTable.getHero(otherId);
                        msger.addReceiver(heroTable.getMessager(otherId));
                        if (random.nextInt(h.name.hashCode()) < random.nextInt(hero.name.hashCode()) && random.nextLong(h.birthDay) <= random.nextLong(hero.birthDay)) {//New Group
                            val group = Group()
                            group.heroIds.add(otherId)
                            group.heroIds.add(hero.id)
                            group.level = level
                            nonGroupHeroIds.removeAll(group.heroIds)
                            msger.createGroup(hero, h)
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
                        val bs = BattleService(hero, other, random, null)
                        bs.setBattleMessage(msger);
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

    private fun failed(group: Group, groupTable: GroupTable, heroTable: HeroTable){

    }

}

*/
