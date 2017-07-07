package cn.luo.yuan.maze.model

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/7/2017.
 */
class GroupHolder{
    val heroIds = mutableSetOf<String>()

    fun isInGroup(id:String):Boolean{
        return heroIds.contains(id)
    }
}
