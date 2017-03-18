package cn.luo.yuan.maze.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.persistence.serialize.SerializeLoader;

/**
 * Created by luoyuan on 2017/3/19.
 */

/**
 * 多存档机制设计：
 * 存档用index区分，将存档存入数据库中，存放的是Hero对象
 * 需要额外增加保存的是创建时间，最后更新时间，在开始页面的时候查询这些数据（名字，层数，时间相关）然后显示
 * 用户选择了对应的存档编号后，使用对应的编号创建DataManager
 * 所以的相关数据在存入数据库的时候需要带上存档编号信息
 */
public class DataManager {
    //多存档，所以需要一个index来区分当前使用的是哪一个存档
    private int index;
    private SerializeLoader<Accessory> accessoryLoader = new SerializeLoader<>(Accessory.class);
    private Sqlite database;

    public DataManager(int index, Context context){
        this.index = index;
        this.database = Sqlite.getSqlite(context);
    }


}
