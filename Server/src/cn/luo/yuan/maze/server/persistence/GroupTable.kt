package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.server.model.Group
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable
import java.io.File

class GroupTable(root:File) {
    val groupDb = ObjectTable<Group>(Group::class.java, root)
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