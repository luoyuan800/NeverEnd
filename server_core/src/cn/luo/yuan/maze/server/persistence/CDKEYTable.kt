package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.KeyResult
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection
import cn.luo.yuan.maze.utils.Random
import java.sql.Connection
import java.sql.Statement

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/15/2017.
 */
class CDKEYTable(private val database:DatabaseConnection) {
    init {
        var statement: Statement? = null
        var connection: Connection? = null
        try {
            connection = database.getConnection()
            statement = connection.createStatement()
            statement.execute("create table IF NOT EXISTS cdkey(id varchar(100) NOT NULL, " +
                    "gift integer, debris integer, used integer, single integer default 0, " +
                    "primary key (id))")
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            statement?.close()
            connection?.close()
        }
    }

    fun use(id:String):KeyResult{
        val ur = KeyResult()
        val con = database.getConnection()
        var verify = false
        try{
            val stat = con.createStatement();
            val rs = stat.executeQuery("select used, single, gift, debris from cdkey where id = '$id'")
            var gift = 0
            var debris = 0
            if(rs.next()){
                gift = rs.getInt("gift")
                debris = rs.getInt("debris")
                if(rs.getBoolean("single")){
                    verify = rs.getInt("used") <= 0
                }else{
                    verify = true
                }
            }
            rs.close()
            if(verify){
                verify = stat.execute("update cdkey set used = used + 1 where id ='$id'")
                if(verify){
                    val r = Random(System.currentTimeMillis())
                    if(gift > 0){
                        ur.gift = r.nextInt(gift) + 1
                    }
                    if(debris > 0){
                        ur.debris = r.nextInt(debris) + 1
                    }
                }
            }
        }catch (e:Exception){
            LogHelper.error(e)
        }
        con.close()
        ur.verify = verify
        return ur
    }

}