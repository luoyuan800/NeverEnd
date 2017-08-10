package cn.luo.yuan.maze.server.persistence

import cn.luo.yuan.maze.server.persistence.db.DatabaseConnection
import cn.luo.yuan.maze.utils.StringUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/10/2017.
 */
class ReleaseManager(private val database: DatabaseConnection, private val apkFolder:File) {

    fun getReleaseNotes():String{
        val con = database.getConnection()
        try {
            var rn = StringUtils.EMPTY_STRING
            val sta = con.createStatement()
            val rs = sta.executeQuery("select releasenotes from release")
            if (rs.next()) {
                rn = rs.getString(1)
            }
            rs.close()
            sta.close()
            con.close()
            return rn
        }finally {
            con.close()
        }
    }

    fun getReleaseVersion():Int{
        val con = database.getConnection();
        try {
            var version = 0
            val sta = con.createStatement();
            val rs = sta.executeQuery("select version from release")
            if (rs.next()) {
                version = rs.getInt(1)
            }
            rs.close()
            sta.close()
            con.close()
            return version;
        }finally {
            con.close()
        }
    }

    fun getApk(version :Int):ByteArray{
        val os = ByteArrayOutputStream()
        val fis = FileInputStream(File(apkFolder, version.toString()))
        var b = fis.read()
        while (b != -1) {
            os.write(b)
            b = fis.read()
        }
        fis.close()
        val ba = os.toByteArray()
        os.close()
        return ba
    }
}