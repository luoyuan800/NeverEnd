package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.utils.Field
import java.io.Serializable
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by gluo on 6/26/2017.
 */
class ServerRecord : IDModel, Serializable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    private var delete: Boolean = false
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }
    private var id: String? = null
    var winCount = 0
    var currentWin = 0
    var lostCount = 0
    var currentLostCount = 0
    var range = Int.MAX_VALUE
    val messages = ConcurrentLinkedQueue<String>()
    var data:ServerData? = ServerData()
    var dieTime = 0L
    var dieCount = 0L
    var restoreLimit = Data.RESTORE_LIMIT

    override fun getId(): String? {
        return id
    }

    override fun setId(id: String) {
        this.id = id
    }
}
