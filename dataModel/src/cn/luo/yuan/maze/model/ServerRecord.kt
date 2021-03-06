package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.LimitConcurrentLinkedQueue
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Created by gluo on 6/26/2017.
 */
class ServerRecord : IDModel, Serializable, MessageReceiver {
    override fun receive(msg: String?) {
        if (msg != null)
            if(messages.isNotEmpty() && (msg.contains("重生次数已经达到上限") || msg.contains("下一场战斗")) && (messages.last().contains("重生次数已经达到上限") || messages.last().contains("下一场战斗"))){
                return
            }else {
                messages.add(msg)
            }
    }

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
    var mac: String? = StringUtils.EMPTY_STRING
    var submitDate = 0L
    var winCount = 0
    var currentWin = 0
    var lostCount = 0
    var range = Int.MAX_VALUE
    val messages = LimitConcurrentLinkedQueue<String>()
    var data: ServerData? = ServerData()
    var dieTime = 0L
    var dieCount = 0L
    var restoreLimit = Data.RESTORE_LIMIT
    var debris = 0
        set(value) {
            if (value >= 0) {
                field = value
            } else {
                field = 0;
            }
        }
    var gift = 0
    val myKeys = mutableListOf<String>()
        get(){
            if(field == null){
                field = mutableListOf<String>()
            }
            return field
        }
    val dlcs:Set<String>? = mutableSetOf<String>()
    get(){
        if(field == null){
            field = mutableSetOf<String>()
        }
        return field
    }

    val cdkdys:Set<String>? = mutableSetOf<String>()
    get(){
        if(field == null){
            field = mutableSetOf<String>()
        }
        return field
    }

    var updateTime = 0L

    var award = false

    override fun getId(): String? {
        return id
    }

    override fun setId(id: String) {
        this.id = id
    }

    fun winRate(): String {
        var t = winCount + lostCount
        if (t <= 0) t++
        return StringUtils.formatPercentage((winCount * 100 / t).toFloat())
    }
}
