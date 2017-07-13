package cn.luo.yuan.maze.server.persistence.db

import java.sql.Connection

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/13/2017.
 */
interface DatabaseConnection {
    @Throws(Exception::class)
    fun getConnection(): Connection
}