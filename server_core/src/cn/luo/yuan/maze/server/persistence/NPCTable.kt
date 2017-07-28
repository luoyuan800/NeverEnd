package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.serialize.ObjectTable
import cn.luo.yuan.maze.utils.Random
import java.io.File
import java.io.IOException

/**
 * Created by gluo on 6/27/2017.
 */
class NPCTable @Throws(IOException::class, ClassNotFoundException::class)
constructor(root: File) : HeroTable(root) {
    val heroDb = ObjectTable<Hero>(Hero::class.java,root)
    val name = arrayOf("袁酥兄","龙剑森","小梅","雷二蛋", "某鸟")
    val major = arrayOf("伯爵","贱客","诗人","骑士", "流氓")
    val random = Random(System.currentTimeMillis())
    @Throws(IOException::class, ClassNotFoundException::class)
    override fun getHero(id: String, level: Long): Hero {
        val hero = randomBoss(level) ?: randomNPC(level)
        return hero
    }

    private fun randomBoss(level: Long):Hero?{
        if(random.nextInt(100) < 5) {
            val id = random.randomItem(heroDb.loadIds())
            val hero = heroDb.loadObject(id);
            if (hero != null) {
                hero.name = random.randomItem(major) + "☆" + hero.name
                hero.agi = random.randomRange(100, 1000) * level
                hero.def += hero.defGrow * hero.agi
                hero.str = random.randomRange(100, 1000) * level
                hero.maxHp += hero.hpGrow * hero.str
                hero.hp = hero.maxHp
                hero.atk += hero.atkGrow * hero.str
                return hero;
            }
        }
        return null;
    }

    private fun randomNPC(level: Long): Hero {
        val hero = Hero()
        hero.name = randomName()
        hero.element = random.randomItem(Element.values())
        hero.setRace(random.randomItem(Race.values()).ordinal)
        hero.atkGrow = 10;
        hero.defGrow = 10;
        hero.hpGrow = 10;
        hero.agi = 100 * level
        hero.def += hero.defGrow * hero.agi
        hero.str = 100 * level
        hero.maxHp += hero.hpGrow * hero.str
        hero.hp = hero.maxHp
        hero.atk += hero.atkGrow * hero.str
        return hero
    }

    private fun randomName(): String? {
        return random.randomItem(major) + "☆" + random.randomItem(name)
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

    override fun getRecord(id: String?): ServerRecord {
        return ServerRecord()
    }

    override fun save(obj: Any?) {
        if(obj is Hero){
            heroDb.save(obj)
        }
    }


}
