package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.ServerData
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/7/2017.
 */
class CribberTable(private val database: DatabaseConnection) {
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
