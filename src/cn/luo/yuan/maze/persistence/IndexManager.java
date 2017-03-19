package cn.luo.yuan.maze.persistence;

import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.HeroIndex;
import cn.luo.yuan.maze.persistence.database.Sqlite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class IndexManager {
    private Context context;
    private Sqlite database;

    public IndexManager(Context context) {
        this.context = context;
        database = Sqlite.getSqlite(context);
    }

    public List<HeroIndex> getIndex(){
        List<HeroIndex> indexs = new ArrayList<>();
        Cursor cursor = database.excuseSOL("select h.element, h.hero_index,h.name,h.last_update, h.created, m.level,m.max_level from hero h left join maze m on h.hero_index = m.hero_index order by h.last_update DESC");
        try {
            while (!cursor.isAfterLast()) {
                HeroIndex index = new HeroIndex();
                index.setCreated(cursor.getLong(cursor.getColumnIndex("created")));
                index.setLastUpdated(cursor.getLong(cursor.getColumnIndex("last_update")));
                index.setName(cursor.getString(cursor.getColumnIndex("name")));
                index.setLevel(cursor.getLong(cursor.getColumnIndex("level")));
                index.setMaxLevel(cursor.getLong(cursor.getColumnIndex("max_level")));
                index.setIndex(cursor.getInt(cursor.getColumnIndex("hero_index")));
                index.setElement(Element.valueOf(cursor.getString(cursor.getColumnIndex("element"))));
                indexs.add(index);
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return indexs;
    }

    public void create() {

    }
}
