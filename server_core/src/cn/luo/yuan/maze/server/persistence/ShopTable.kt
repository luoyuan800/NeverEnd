package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.Element
import cn.luo.yuan.maze.model.SellItem
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection
import cn.luo.yuan.maze.serialize.ObjectTable
import cn.luo.yuan.maze.utils.Random
import java.io.File
import java.sql.Connection
import java.sql.Statement

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/13/2017.
 */
class ShopTable(private val database: DatabaseConnection, fileRoot: File) {
    private var accessoryDb: ObjectTable<Accessory>? = null
    private val random = Random(System.currentTimeMillis())

    init {
        initIfNeed()
        accessoryDb = ObjectTable(Accessory::class.java, fileRoot)
    }

    private fun initIfNeed() {
        var statement: Statement? = null
        var connection: Connection? = null
        try {
            connection = database.getConnection()
            statement = connection.createStatement()
            statement.execute("create table IF NOT EXISTS shop(id varchar(100) NOT NULL, " +
                    "type varchar(100) NOT NULL, cost INT default 0, count INT default 0, " +
                    "sold INT default 0 , on_sell TINYINT default 0, ref varchar(255), special TINYINT default 0, " +
                    "primary key (id))")
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            statement?.close()
            connection?.close()
        }

    }

    fun getAllSelling(): List<SellItem> {
        var con:Connection? = null
        var stat:Statement? = null
        val list = mutableListOf<SellItem>()
        try {
            con = database.getConnection()
            stat = con.createStatement()
            val s = "select * from shop where on_sell = 1 and count > 0"
            LogHelper.debug("execute sql : " + s);
            val rs = stat.executeQuery(s);
            LogHelper.debug("sql return result: " + rs.row)
            while (rs.next()) {
                val type = rs.getString("type")
                val sellItem = SellItem()
                when (type) {
                    "goods" -> {
                        val ins = Class.forName(rs.getString("ref")).newInstance()
                        (ins as Goods).count = 1
                        sellItem.count = rs.getInt("count")
                        sellItem.instance = ins
                        sellItem.desc = ins.desc
                        sellItem.type = "物品"
                        sellItem.name = ins.name
                        list.add(sellItem)
                    }
                    "accessory" -> {
                        if(random.nextBoolean()) {
                            val acc = accessoryDb!!.loadObject(rs.getString("ref"))
                            if (acc != null) {
                                acc.element = random.randomItem(Element.values())
                                sellItem.count = 1;
                                sellItem.instance = acc
                                sellItem.color = acc.color
                                sellItem.author = acc.author
                                sellItem.desc = acc.desc
                                sellItem.effects = acc.effects
                                sellItem.type = acc.type
                                sellItem.name = acc.displayName
                                list.add(sellItem)
                            }
                        }
                    }
                }
                sellItem.id = rs.getString("id")
                sellItem.price = rs.getLong("cost")
                sellItem.special = rs.getBoolean("special")
                LogHelper.debug("return shop item result: " + list)
            }
        }finally {
            stat?.close()
            con?.close()
        }
        return list
    }

    fun sell(item: SellItem) {
        val conn = database.getConnection()
        val stat = conn.createStatement()
        stat.execute("update shop set count = count - " + item.count + ", sold = sold + " + item.count + " where id = '" + item.id + "'")
        stat.close()
        conn.close()
    }

    fun add(item:Any){
        val conn = database.getConnection()
        try{
        when(item){
            is Accessory ->{
                accessoryDb?.save(item)
                val state = conn.prepareStatement("insert into shop(id, type, cost, count,ref) values(?,?,?,?,?)")
                state.setString(1, item.id)
                state.setString(2, "accessory")
                state.setLong(3, 500000)
                state.setLong(4, 100)
                state.setString(5, item.id)
                state.execute()
                state.close()
            }
        }
        }catch (e:Exception) {
            LogHelper.error(e)
        }
        conn.close()
    }
}
