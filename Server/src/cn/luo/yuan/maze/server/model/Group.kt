package cn.luo.yuan.maze.server.model
import java.io.Serializable
import java.util.*

class Group : Serializable {

    var id = UUID.randomUUID().toString()
    val heroIds = mutableSetOf<String>()
    var level = 1L
}