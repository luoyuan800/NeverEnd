package cn.luo.yuan.maze.serialize;

import cn.luo.yuan.maze.persistence.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.testng.internal.ClassHelper;

import java.sql.Connection;
import java.sql.DriverManager;

public class LocalMySqlDataBase implements DatabaseConnection {
    @NotNull
    @Override
    public Connection getConnection() throws Exception {
        ClassHelper.forName("com.mysql.jdbc.driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/test");
    }

    @Override
    public void close() {

    }
}
