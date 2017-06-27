package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.utils.Random

import java.io.File
import java.io.IOException

/**
 * Created by gluo on 6/27/2017.
 */
class NPCTable @Throws(IOException::class, ClassNotFoundException::class)
constructor(root: File) : HeroTable(root) {
    val random = Random(System.currentTimeMillis())
    @Throws(IOException::class, ClassNotFoundException::class)
    override fun getHero(id: String, level: Long): Hero {
        val hero = Hero()
        hero.name = randomName()
        hero.element = random.randomItem(Element.values())
        hero.setRace(random.randomItem(Race.values()).ordinal)
        hero.atkGrow = 10;
        hero.defGrow = 10;
        hero.hpGrow = 10;
        hero.agi = 10 * level
        hero.str = 10 * level
        return hero
    }

    private fun randomName(): String? {
        return "测试人员"
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    override fun getMaze(id: String, level: Long): Maze {
        val maze = Maze()
        maze.maxLevel = level
        maze.level = level
        maze.id = id
        return maze
    }

    override fun save() {
        //Do nothing
    }

    override fun saveMaze(maze: Maze?, id: String?) {
        //Do nothing
    }

    override fun saveHero(hero: Hero?) {
        //Do nothing
    }

    override fun getRecord(id: String?): ServerRecord {
        return ServerRecord()
    }


}
