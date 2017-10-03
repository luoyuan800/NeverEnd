package cn.luo.yuan.maze.persistence

import java.sql.Connection

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/13/2017.
 */
interface DatabaseConnection {
    @Throws(Exception::class)
    fun getConnection(): Connection
    fun close()
}