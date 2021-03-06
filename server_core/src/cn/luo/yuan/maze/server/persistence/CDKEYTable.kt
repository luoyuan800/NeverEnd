package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.model.CDKey
import cn.luo.yuan.maze.model.KeyResult
import cn.luo.yuan.maze.server.LogHelper
import cn.luo.yuan.maze.persistence.DatabaseConnection
import cn.luo.yuan.maze.utils.Random
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/15/2017.
 */
class CDKEYTable(private val database: DatabaseConnection) {
    init {
        var statement: Statement? = null
        var connection: Connection? = null
        try {
            connection = database.getConnection()
            statement = connection.createStatement()
            statement.execute("CREATE TABLE IF NOT EXISTS `cdkey` (`id` VARCHAR(100) NOT NULL, `gift` INT(11), `debris` INT(11), `mate` INT(11), `used` INT(11), `single` INT(11), PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        } catch (e: Exception) {
            LogHelper.error(e)
        } finally {
            statement?.close()
            connection?.close()
        }
    }

    fun use(id: String): KeyResult {
        val ur = KeyResult()
        val con = database.getConnection()
        var verify = false
        try {
            val stat = con.createStatement();
            val rs = stat.executeQuery("select * from cdkey where id = '$id'")
            var gift = 0
            var debris = 0
            if (rs.next()) {
                gift = rs.getInt("gift")
                debris = rs.getInt("debris")
                ur.mate = rs.getLong("mate")
                if (rs.getBoolean("single")) {
                    verify = rs.getInt("used") <= 0
                } else {
                    verify = true
                }
            }
            rs.close()
            if (verify) {
                stat.execute("update cdkey set used = used + 1 where id ='$id'")
                if (verify) {
                    val r = Random(System.currentTimeMillis())
                    if (gift > 0) {
                        ur.gift = if (ur.mate > 0) r.nextInt(gift) + 1 else gift
                    }
                    if (debris > 0) {
                        ur.debris = if (ur.mate > 0) r.nextInt(debris) + 1 else debris
                    }
                }
            }
        } catch (e: Exception) {
            LogHelper.error(e)
        }
        con.close()
        ur.verify = verify
        return ur
    }

    fun newCdKey(): String {
        return newCdKey(1, 100000, 5)
    }

    fun newCdKey(deris: Int, mate: Int, gift: Int): String {

        val con = database.getConnection()
        try {
            val random = Random(System.currentTimeMillis())
            val stat = con.createStatement()
            val id = buildId(random)
            stat.execute("insert into cdkey(id, gift, debris, mate, used, single) values('$id',$gift, $deris,$mate,0,1)")
            stat.close();
            return id;
        } finally {
            con?.close()
        }
    }

    fun queryMyCdKey(ids:List<String>): List<CDKey>{
        val cks = mutableListOf<CDKey>()
        var con:Connection? = null
        try{
            con = database.getConnection()
            val builder = StringBuilder()
            ids.forEach {
                builder.append("'$it',")
            }
            val idss = builder.replaceFirst(Regex(",$"), "")
            val ps = con.prepareStatement("select * from `cdkey` where id in ($idss)")
            val rs = ps.executeQuery()
            while(rs.next()){
                if(rs.getLong("used") <= 0) {
                    val ck = CDKey()
                    ck.id = rs.getString("id")
                    ck.debris = rs.getLong("debris")
                    ck.mate = rs.getLong("mate")
                    ck.used = rs.getLong("used")
                    ck.isSingle = rs.getBoolean("single")
                    cks.add(ck)
                }
            }
            ps.close()
        }finally {
            con?.close()
        }
        return cks;
    }

    private fun buildId(random: Random): String {
        val sb = StringBuilder()
        while (sb.length < 5) {
            sb.append((random.nextInt(25) + 97).toChar())
        }
        return sb.toString()
    }

}