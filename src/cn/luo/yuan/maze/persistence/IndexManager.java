package cn.luo.yuan.maze.persistence;

import android.content.Context;
import android.database.Cursor;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.SDFileUtils;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.HeroIndex;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.persistence.database.Sqlite;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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

    public List<HeroIndex> getIndex() {
        List<HeroIndex> indexs = new ArrayList<>();
        try (Cursor cursor = database.excuseSOL("select h.id, h.race, h.element, h.hero_index,h.name,h.last_update, h.created, m.level,m.max_level from hero h left join maze m on h.hero_index = m.hero_index order by h.last_update DESC")) {
            while (!cursor.isAfterLast()) {
                HeroIndex index = new HeroIndex();
                index.setCreated(cursor.getLong(cursor.getColumnIndex("created")));
                index.setLastUpdated(cursor.getLong(cursor.getColumnIndex("last_update")));
                index.setName(cursor.getString(cursor.getColumnIndex("name")));
                index.setLevel(cursor.getLong(cursor.getColumnIndex("level")));
                index.setMaxLevel(cursor.getLong(cursor.getColumnIndex("max_level")));
                index.setIndex(cursor.getInt(cursor.getColumnIndex("hero_index")));
                index.setId(cursor.getString(cursor.getColumnIndex("id")));
                index.setElement(Element.valueOf(cursor.getString(cursor.getColumnIndex("element"))));
                index.setRace(Race.valueOf(cursor.getString(cursor.getColumnIndex("race"))));
                indexs.add(index);
                cursor.moveToNext();
            }
        }
        return indexs;
    }

    public boolean restore(File file) {
        try {
                List<Serializable> seris = SDFileUtils.unzipObjects(file, context);
                int index = -1;
                Hero hero = null;
                Maze maze = null;
                for (Serializable s : seris) {
                    if (s instanceof Hero) {
                        hero = (Hero) s;
                        index = hero.getIndex();
                    }
                    if (s instanceof Maze) {
                        maze = (Maze) s;
                    }
                    if (hero != null && maze != null) {
                        break;
                    }
                }
                DataManager manager = new DataManager(index, context);
                manager.overrideCurrentSaveFile(seris);
                manager.saveHero(hero);
                manager.saveMaze(maze);
                manager.close();
                return true;
        } catch (IOException e) {
            LogHelper.logException(e, "restore save");
        }
        return false;
    }

    public String backup(HeroIndex index) {
        DataManager manager = new DataManager(index.getIndex(), context);
        String file = SDFileUtils.zipFiles(index.getId() + "_" + index.getIndex(), manager.retrieveAllSaveFile());
        manager.close();
        return file;
    }

    public void create() {

    }
}
