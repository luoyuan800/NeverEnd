package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.SellItem
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable
import java.io.File
import java.sql.Statement

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/13/2017.
 */
class ShopTable(private val database: DatabaseConnection, fileRoot: File) {
    private var accessoryDb: ObjectTable<Accessory>? = null

    init {
        initIfNeed()
        accessoryDb = ObjectTable(Accessory::class.java, fileRoot)
    }

    private fun initIfNeed() {
        var statement: Statement? = null
        try {
            val connection = database.getConnection()
            statement = connection.createStatement()
            statement.execute("create table IF NOT EXISTS shop(id varchar(100) NOT NULL, " +
                    "type varchar(100) NOT NULL, cost INT default 0, count INT default 0, " +
                    "sold INT default 0 ), on_sell TINYINT default 0, ref varchar(255), special TINYINT default 0" +
                    "primary key (id)")
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            statement?.close()
        }

    }

    fun getAllSelling(): List<SellItem> {
        val con = database.getConnection()
        val stat = con.createStatement()
        val rs = stat.executeQuery("select * from shop where on_sell = 1 and count > 0");
        val list = mutableListOf<SellItem>()
        while (rs.next()) {
            val type = rs.getString("type")
            val sellItem = SellItem()
            when (type) {
                "goods" -> {
                    val ins = Class.forName(rs.getString("ref")).newInstance()
                    (ins as Goods).count = 1
                    sellItem.instance = ins
                    sellItem.desc = ins.desc
                    sellItem.type = "物品"
                }
                "accessory" -> {
                    val acc = accessoryDb!!.loadObject(rs.getString("ref"))
                    if (acc != null) {
                        sellItem.instance = acc
                        sellItem.color = acc.color
                        sellItem.author = acc.author
                        sellItem.desc = acc.desc
                        sellItem.effects = acc.effects
                        sellItem.type = acc.type
                    }
                }
            }
            sellItem.id = rs.getString("id")
            sellItem.count = rs.getInt("count")
            sellItem.price = rs.getLong("cost")
            sellItem.special = rs.getBoolean("special")
            list.add(sellItem)

        }
        stat?.close()
        return list
    }

    fun sell(item: SellItem) {
        val conn = database.getConnection()
        val stat = conn.createStatement()
        stat.execute("update shop set count = count - " + item.count + ", sold = sold + " + item.count + " where id = '" + item.id)
    }
}
