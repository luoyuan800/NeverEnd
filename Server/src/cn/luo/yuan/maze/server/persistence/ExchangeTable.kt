package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable
import java.io.File
import java.lang.ref.SoftReference

/**
 * Created by gluo on 6/1/2017.
 */
class ExchangeTable(root: File) {
    class key(val id: String, val type: Int, val requestType: Int) {
        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

    val exchangeDb = ObjectTable(ExchangeObject::class.java, root)
    val cache = mutableMapOf<key, SoftReference<ExchangeObject>>()
    fun addExchange(ex: Any?, ownerId: String, limit: String, expect: Int): Boolean {
        val exchange = ExchangeObject(ex as IDModel, ownerId)
        if (ex is Pet) {
            exchange.type = 1
        } else if (ex is Accessory) {
            exchange.type = 2
        } else if (ex is Goods) {
            exchange.type = 3
        }
        exchange.expectedType = expect;
        exchange.expectedKeyWord = limit;
        exchange.submitTime = System.currentTimeMillis()
        return addExchange(exchange)
    }

    fun addExchange(exchange: ExchangeObject): Boolean {
        val ref = exchangeDb.loadObject(exchange.id);
        if (ref != null) {
            return false
        }

        cache.put(key(exchange.id, exchange.type, exchange.expectedType), SoftReference(exchange))
        exchangeDb.save(exchange)
        return true
    }

    fun loadAll(type: Int, limit: String): List<ExchangeObject> {
        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                val exchangeObject = value.get()
                if (exchangeObject != null) {
                    if(exchangeObject.type == type && exchangeObject.changed == null) {
                        if(exchangeObject.exchange is NameObject){
                            if((exchangeObject.exchange as NameObject).name.contains(limit)){
                                result.add(value.get()!!)
                            }
                        }else{
                            result.add(value.get()!!)
                        }
                    }
                } else {
                    val eo = exchangeDb.loadObject(key.id)
                    if (eo != null) {
                        if (eo.type == type && eo.changed == null) {
                            if(eo.exchange is NameObject){
                                if((eo.exchange as NameObject).name.contains(limit)){
                                    result.add(value.get()!!)
                                }
                            }else{
                                result.add(value.get()!!)
                            }
                        }
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (id in exchangeDb.loadIds()) {
                val eo = exchangeDb.loadObject(id);
                if (eo != null) {
                    cache.put(key(eo.id, eo.type, eo.expectedType), SoftReference(eo))
                    if (eo.type == type && eo.changed == null) {
                        result.add(eo)
                    }
                }
            }
        }
        return result
    }

    fun loadAll(id: String): List<ExchangeObject> {

        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                if (value.get() != null && value.get()?.ownerId == id) {
                    result.add(value.get()!!)
                } else {
                    val eo = exchangeDb.loadObject(key.id)
                    if (eo != null) {
                        if (eo.ownerId == id) {
                            result.add(eo);
                        }
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (exchangeId in exchangeDb.loadIds()) {
                val eo = exchangeDb.loadObject(exchangeId);
                if (eo != null) {
                    cache.put(key(eo.id, eo.type, eo.expectedType), SoftReference(eo))
                    if (eo.ownerId == id) {
                        result.add(eo)
                    }
                }
            }
        }
        return result

    }

    fun loadObject(id: String): ExchangeObject? {
        return exchangeDb.loadObject(id);
    }

    fun removeObject(eo: ExchangeObject) {
        cache.remove(key(eo.id, eo.type, eo.expectedType));
        exchangeDb.delete(eo.id);
    }
}