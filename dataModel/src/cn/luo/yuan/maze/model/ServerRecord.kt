package cn.luo.yuan.maze.model

import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by gluo on 6/26/2017.
 */
class ServerRecord : IDModel, Serializable {
    private var id: String? = null
    var winCount = 0
    var lostCount = 0
    var range = -1
    val messages = ConcurrentLinkedQueue<String>()
    val data = ServerData()
    override fun getId(): String? {
        return id
    }

    override fun setId(id: String) {
        this.id = id
    }
}
