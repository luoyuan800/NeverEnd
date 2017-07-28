package cn.luo.yuan.maze.model

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/7/2017.
 */
class GroupHolder:Comparable<GroupHolder>{
    override fun compareTo(other: GroupHolder): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val heroIds = mutableSetOf<String>()

    fun isInGroup(id:String):Boolean{
        return heroIds.contains(id)
    }
}
