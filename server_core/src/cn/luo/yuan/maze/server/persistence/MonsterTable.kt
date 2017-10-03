package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Monster
import cn.luo.yuan.serialize.FileObjectTable

import java.io.File
import java.util.*

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/3/2017.
 */
class MonsterTable(root: File) {
    val monsterTable: FileObjectTable<Monster> = FileObjectTable(Monster::class.java, root)

    fun listMonster(start:Int, count:Int):List<Monster>{
        return monsterTable.loadLimit(start, count, null, Comparator { o1, o2 -> Integer.compare(o1.index, o2.index) })
    }
}
