package cn.luo.maze.web;

import cn.luo.yuan.maze.persistence.DatabaseConnection;
import cn.luo.yuan.maze.server.LogHelper;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlConnection implements DatabaseConnection {

    Context IC_ictx = null;


    String jndi = "java:comp/env/jdbc/mysql";


    DataSource dataSource = null;


    public MysqlConnection() {

    }


    /**
     * 取得数据库源;
     *
     * @return
     */

    public DataSource getDataSource() {

        return dataSource;

    }


    /**
     * 取得连接
     *
     * @return
     * @throws Exception
     */

    @Override
    public Connection getConnection() throws Exception {
        LogHelper.info("Try to get connection");
        Connection connection;

        String jndi = getJndi();

        try {

            DataSource dataSource = getDataSource();

            if (dataSource == null)

                dataSource = init();

            connection = dataSource.getConnection();
            return connection;

        } catch (SQLException PaEx_ex) {


            throw new Exception(String.valueOf(String

                    .valueOf((new StringBuffer("从数据源")).append(jndi).append(

                            "取连接失败: ").append(PaEx_ex.getMessage()))));

        } catch (Exception PaEx_ex) {

            throw PaEx_ex;

        }

    }


    /**
     * 初始化连接;
     *
     * @return
     * @throws Exception
     */

    public DataSource init() throws Exception {

        try {
            LogHelper.info("init mysql data source");
            Properties p = new Properties();

            IC_ictx = new InitialContext(p);
            dataSource = (DataSource) IC_ictx.lookup(jndi);

            return dataSource;

        } catch (NameNotFoundException PaEx_ex) {

            throw new Exception(String.valueOf(String

                    .valueOf((new StringBuffer("数据源 ")).append(jndi).append(

                            " 在应用服务器的JNDI环境上没有找到"))));

        }

    }

    public void close() {
        try {
            if (IC_ictx != null)
                IC_ictx.close();
        } catch (Exception e) {
            LogHelper.error(e);
        }
    }

    public String getJndi() {

        return jndi;

    }

}