package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.exception.AlreadyAcknowledge
import java.io.Serializable
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Created by gluo on 6/1/2017.
 */
class ExchangeObject(val exchange: IDModel, val ownerId:String) : Serializable, IDModel {
    var expectedType: Int = 0
    var submitTime = 0L;
    var changedTime = 0L;
    var type = 0;
    var changed : ExchangeObject? =null
    var lock: ReentrantLock = ReentrantLock()
    var acknowledge = false
    set(value){
        lock.tryLock()
        try {
            if (acknowledge != value) {
                acknowledge = value
            } else {
                throw AlreadyAcknowledge(exchange.toString() + " has been acknowledged!")
            }
        }finally {
            lock.unlock()
        }
    }

    override fun getId(): String {
        return exchange.id
    }

    override fun setId(id: String) {
        exchange.id = id
    }

    fun change(changed : ExchangeObject):Boolean{
        if(lock.tryLock(500, TimeUnit.MILLISECONDS)) {
            try {
                if (this.changed == null) {
                    changedTime = System.currentTimeMillis()
                    this.changed = changed
                    return true
                }
            }finally {
                lock.unlock()
            }
        }
        return false
    }
}
