package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.Accessory
import cn.luo.yuan.maze.model.ServerData
import cn.luo.yuan.maze.serialize.ObjectTable
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection
import java.sql.Connection
import java.sql.Statement

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/7/2017.
 */
class CribberTable(private val database: DatabaseConnection) {
    init {
        var statement: Statement? = null
        var connection: Connection? = null
        try {
            connection = database.getConnection()
            statement = connection.createStatement()
            statement.execute("CREATE TABLE IF NOT EXISTS `cribber` (`mac` VARCHAR(100),`uuid` VARCHAR(100) NOT NULL,`name` VARCHAR(45), PRIMARY KEY  USING BTREE(`uuid`) ) ENGINE = InnoDB;")
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            statement?.close()
            connection?.close()
        }
    }

    fun isCribber(data: ServerData): Boolean {
        val connection = database.getConnection();
        try {
            val stat = connection.createStatement()
            val rs = stat.executeQuery("select count(*) from cribber where uuid = '${data.getId()}' or mac = '${data.mac}'")
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    LogHelper.info("Cribber: " + data.hero?.displayName)
                    return true;
                }
            }
            rs.close()
            stat.close()
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            connection.close()
        }
        return false
    }

    fun isCribber(id: String): Boolean {
        val connection = database.getConnection();
        try {
            val stat = connection.createStatement()
            val rs = stat.executeQuery("select count(*) from cribber where uuid = '$id'")
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    LogHelper.info("Cribber: " + id)
                    return true;
                }
            }
            rs.close()
            stat.close()
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            connection.close()
        }
        return false
    }

    fun addToCribber(uuid: String, mac: String, name: String) {
        val connection = database.getConnection();
        try {
            val stat = connection.createStatement()
            stat.execute("insert into cribber(uuid, mac, name) values('$uuid' , '$mac' , '$name' )")
            stat.close()
        } catch (e: Exception){
            LogHelper.error(e)
        }finally {
            connection.close()
        }
    }
}
