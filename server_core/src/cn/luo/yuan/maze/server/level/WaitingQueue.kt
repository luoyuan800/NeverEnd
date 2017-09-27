package cn.luo.yuan.maze.server.level

import cn.luo.yuan.maze.model.LevelRecord
import cn.luo.yuan.maze.model.real.level.ElyosrRealLevel
import cn.luo.yuan.utils.Random

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/11/2017.
 */
class WaitingQueue {
    private val queues = mutableMapOf<Int, MutableList<LevelRecord>>()
    private val random = Random(System.currentTimeMillis())
    fun addQueue(record: LevelRecord): Boolean {
        if (record.hero != null) {
            val level: Int = ElyosrRealLevel.getCurrentLevel(record.point).ordinal
            synchronized(queues) {
                var list: MutableList<LevelRecord>? = queues[level]
                if (list == null) {
                    list = mutableListOf<LevelRecord>()
                    queues[level] = list
                }
                if (list.find { it.id == record.id } == null) {
                    list.add(record)
                }
                return true
            }
        }
        return false
    }

    fun removeQueue(record: LevelRecord): Boolean {
        if (record.hero != null) {
            val level: Int = ElyosrRealLevel.getCurrentLevel(record.point).ordinal
            synchronized(queues) {
                val list: MutableList<LevelRecord>? = queues[level]
                if (list != null && list.find { it.id == record.id } != null) {
                    list.removeAll { it.id == record.id }
                }
                return true
            }
        }
        return false
    }

    fun findTarget(record: LevelRecord): LevelRecord? {
        if (record.hero != null) {
            val level: Int = calculateLevel(record)
            synchronized(queues) {
                val list: MutableList<LevelRecord>? = queues[level]
                if (list != null) {
                    val item = random.randomItem(list)
                    if (item != null) {
                        list.remove(item)
                    }
                    return item
                }
            }
        }
        return null
    }

    private fun calculateLevel(record: LevelRecord): Int {
        val level: Int = ElyosrRealLevel.getCurrentLevel(record.point).ordinal
        return level
    }

    fun poolFirst(level: Int): LevelRecord? {
        synchronized(queues) {
            var list: MutableList<LevelRecord>? = queues[level]
            if (list != null && list.size > 0) {
                val item = list[0]
                list.remove(item)
                return item
            } else {
                list = mutableListOf<LevelRecord>()
                queues[level] = list
            }
        }
        return null
    }

    fun isQueue(record: LevelRecord): Boolean {
        var list: MutableList<LevelRecord>? = queues[calculateLevel(record)]
        if (list != null) {
            synchronized(list) {
                return list.find { it.id == record.id } != null
            }
        }
        return false
    }
}