package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.exception.AlreadyAcknowledge
import cn.luo.yuan.maze.utils.Field.Companion.SERVER_VERSION
import java.io.Serializable
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by gluo on 6/1/2017.
 */
class ExchangeObject(val exchange: IDModel, val ownerId: String) : Serializable, IDModel {
    companion object {
        private const val serialVersionUID: Long = SERVER_VERSION
    }

    private var delete: Boolean = false
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }

    var expectedKeyWord = ""
    var expectedType: Int = 0
    var submitTime = 0L;
    var changedTime = 0L;
    var type = 0;
    var changed: ExchangeObject? = null
    var lock: ReentrantLock = ReentrantLock()
    var acknowledge = false
        set(value) {
            lock.tryLock()
            try {
                //use field to access this file if you want to do some different in the set or get
                if (field != value) {
                    field = value
                } else {
                    throw AlreadyAcknowledge(exchange.toString() + " has been acknowledged!")
                }
            } finally {
                lock.unlock()
            }
        }

    override fun getId(): String {
        return exchange.id
    }

    override fun setId(id: String) {
        exchange.id = id
    }

    fun change(changed: ExchangeObject): Boolean {
        if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
            try {
                if (this.changed == null) {
                    changedTime = System.currentTimeMillis()
                    this.changed = changed
                    return true
                }
            } finally {
                lock.unlock()
            }
        }
        return false
    }
}
