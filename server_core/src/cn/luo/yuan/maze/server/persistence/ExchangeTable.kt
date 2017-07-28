package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.serialize.ObjectTable
import cn.luo.yuan.maze.utils.StringUtils
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
        val exchange = ex as? ExchangeObject ?: ExchangeObject(ex as IDModel, ownerId)
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



    fun loadAll(type: Int, limit: String?, keeper: String): List<ExchangeObject> {
        val limitWaord = limit ?: StringUtils.EMPTY_STRING

        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                val exchangeObject = value.get()
                if (exchangeObject != null) {
                    addToResult(exchangeObject, limitWaord, result, type, keeper)
                } else {
                    val eo = exchangeDb.loadObject(key.id)
                    if (eo != null) {
                        addToResult(eo, limitWaord, result, type, keeper)
                        cache.put(key, SoftReference(eo))
                    }
                }
            }
        } else {
            for (id in exchangeDb.loadIds()) {
                val eo = exchangeDb.loadObject(id);
                if (eo != null) {
                    cache.put(key(eo.id, eo.type, eo.expectedType), SoftReference(eo))
                    addToResult(eo, limitWaord, result, type, keeper)
                }
            }
        }
        return result
    }

    private fun addToResult(exchangeObject: ExchangeObject, limitWaord: String, result: MutableList<ExchangeObject>, type: Int, keeper: String) {
        if (exchangeObject.type == type && exchangeObject.changed == null) {
            val exchange = exchangeObject.exchange
            if(exchange is OwnedAble){
                if(exchange.keeperId == keeper){
                    return;
                }
            }
            if (exchange is NameObject) {
                if (exchange.name.contains(limitWaord)) {
                    result.add(exchangeObject)
                }
            } else {
                result.add(exchangeObject)
            }
        }
    }

    fun loadAll(id: String): List<ExchangeObject> {
        val result = mutableListOf<ExchangeObject>()
        if (cache.size == exchangeDb.size()) {
            for ((key, value) in cache.entries) {
                val exchangeObject = value.get()
                if (exchangeObject != null && exchangeObject.ownerId == id && !exchangeObject.acknowledge) {
                    result.add(exchangeObject)
                } else {
                    val eo = exchangeDb.loadObject(key.id)
                    if (eo != null) {
                        if (eo.ownerId == id && !eo.acknowledge) {
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
                    if (eo.ownerId == id && !eo.acknowledge) {
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