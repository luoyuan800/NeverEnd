package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.ExchangeObject
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable
import java.io.File
import java.lang.ref.SoftReference

/**
 * Created by gluo on 6/1/2017.
 */
class ExchangeTable(root: File) {
    val exchangeDb = ObjectTable(ExchangeObject::class.java, root)
    val cache = mutableMapOf<String, SoftReference<ExchangeObject>>()
    fun addExchange(exchange: ExchangeObject): Boolean {
        val ref = cache[exchange.id];
        if (ref != null && ref.get() != null) {
            return false
        }
        cache.put(exchange.id, SoftReference(exchange))
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
                    val eo = exchangeDb.loadObject(key)
                    if (eo != null) {
                        if (eo.type == type && eo.changed==null) {
                            result.add(eo);
                        }
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (id in exchangeDb.loadAllId()) {
                if (!cache.containsKey(id) || cache[id]?.get() == null) {
                    val eo = exchangeDb.loadObject(id);
                    if (eo != null) {
                        cache.put(eo.id, SoftReference(eo))
                        if (eo.type == type && eo.changed==null) {
                            result.add(eo)
                        }
                    }
                } else {
                    if (cache[id]?.get()?.type == type && cache[id]?.get()?.changed == null)
                        result.add(cache[id]!!.get()!!)
                }
            }
        }
        return result
    }

    fun loadAll(id:String):List<ExchangeObject>{
        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                if (value.get() != null && value.get()?.ownerId == id && value.get()?.acknowledge!=true) {
                    result.add(value.get()!!)
                } else {
                    val eo = exchangeDb.loadObject(key)
                    if (eo != null) {
                        if (eo.ownerId == id && !eo.acknowledge) {
                            result.add(eo);
                        }
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (eid in exchangeDb.loadAllId()) {
                if (!cache.containsKey(eid) || cache[eid]?.get() == null) {
                    val eo = exchangeDb.loadObject(eid);
                    if (eo != null) {
                        cache.put(eo.id, SoftReference(eo))
                        if (eo.ownerId == id && !eo.acknowledge) {
                            result.add(eo)
                        }
                    }
                } else {
                    if (cache[eid]?.get()?.ownerId == id && cache[eid]?.get()?.acknowledge!=true)
                        result.add(cache[eid]!!.get()!!)
                }
            }
        }
        return result
    }
}