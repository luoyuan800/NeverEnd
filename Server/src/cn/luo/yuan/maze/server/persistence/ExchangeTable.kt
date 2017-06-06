package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.ExchangeObject
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable
import com.sun.jmx.snmp.SnmpPduRequestType
import java.io.File
import java.lang.ref.SoftReference

/**
 * Created by gluo on 6/1/2017.
 */
class ExchangeTable(root: File) {
    class key(val id:String,val type: Int,val requestType: Int){

    }
    val exchangeDb = ObjectTable(ExchangeObject::class.java, root)
    val cache = mutableMapOf<key, SoftReference<ExchangeObject>>()
    fun addExchange(exchange: ExchangeObject): Boolean {
        val ref = exchangeDb.loadObject(exchange.id);
        if (ref != null ) {
            return false
        }

        cache.put(key(exchange.id,exchange.type, exchange.expectedType), SoftReference(exchange))
        exchangeDb.save(exchange)
        return true
    }

    fun loadAll(type: Int): List<ExchangeObject> {
        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                if (value.get() != null && value.get()?.type == type && value.get()?.changed==null) {
                    result.add(value.get()!!)
                } else {
                    val eo = exchangeDb.loadObject(key.id)
                    if (eo != null) {
                        if (eo.type == type && eo.changed==null) {
                            result.add(eo);
                        }
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (id in exchangeDb.loadIds()) {
                    val eo = exchangeDb.loadObject(id);
                    if (eo != null) {
                        cache.put(key(eo.id,eo.type,eo.expectedType), SoftReference(eo))
                        if (eo.type == type && eo.changed==null) {
                            result.add(eo)
                        }
                    }
            }
        }
        return result
    }

    fun loadAll(id:String):List<ExchangeObject>{

        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                if (value.get() != null && value.get()?.ownerId == id && value.get()?.changed==null) {
                    result.add(value.get()!!)
                } else {
                    val eo = exchangeDb.loadObject(key.id)
                    if (eo != null) {
                        if (eo.ownerId == id && eo.changed==null) {
                            result.add(eo);
                        }
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (id in exchangeDb.loadIds()) {
                val eo = exchangeDb.loadObject(id);
                if (eo != null) {
                    cache.put(key(eo.id,eo.type,eo.expectedType), SoftReference(eo))
                    if (eo.ownerId == id && eo.changed==null) {
                        result.add(eo)
                    }
                }
            }
        }
        return result

    }

    fun loadObject(id: String): ExchangeObject {
        return exchangeDb.loadObject(id);
    }
}