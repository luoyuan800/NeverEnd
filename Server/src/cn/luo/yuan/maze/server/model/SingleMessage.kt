package cn.luo.yuan.maze.server.model

import cn.luo.yuan.maze.model.IDModel
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by gluo on 6/20/2017.
 */
class SingleMessage : Serializable, IDModel {
    private var delete: Boolean = false
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }
    private var mid = ""
    private val message = ConcurrentLinkedQueue<String>()

    override fun getId(): String {
        return mid
    }

    override fun setId(id: String) {
        this.mid = id
    }

    fun pollMessage(): String {
        return message.poll()
    }

    fun addMessage(msg: String) {
       message.add(msg)
    }
}
