package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.index.HeroIndex;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectDB;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by gluo on 5/22/2017.
 */
public class HeroTable {
    private ObjectDB<Hero> heroDb;
    private ObjectDB<Maze> mazeDb;
    private HashMap<HeroIndex, SoftReference<Hero>> cache;

    public HeroTable() {
        heroDb = new ObjectDB<>(Hero.class);
        mazeDb = new ObjectDB<>(Maze.class);
        List<Hero> heros = heroDb.loadAll();
        cache = new HashMap<>(heros.size());
        for (Hero hero : heros) {
            HeroIndex index = new HeroIndex();
            index.setId(hero.getId());
            index.setElement(hero.getElement());
            Maze maze = mazeDb.loadObject(hero.getId());
            index.setLevel(maze.getLevel());
            index.setMaxLevel(maze.getMaxLevel());
            cache.put(index, new SoftReference<Hero>(hero));
        }
    }

    public List<Hero> queryHero(HeroIndex index) {
        List<Hero> heros = new ArrayList<>();
        for (HashMap.Entry<HeroIndex, SoftReference<Hero>> entry : cache.entrySet()) {
            HeroIndex key = entry.getKey();
            boolean match = index.getElement() == null || index.getElement() == key.getElement();
            match &= index.getLevel() == null || Objects.equals(index.getLevel(), key.getLevel());
            match &= index.getMaxLevel() == null || Objects.equals(index.getMaxLevel(), key.getMaxLevel());
            if (match) {
                Hero hero = entry.getValue().get();
                if (hero == null) {
                    hero = loadHero(key.getId());
                }
                if (hero != null) {
                    heros.add(hero);
                }
            }
        }
        return heros;
    }

    private Hero loadHero(String id) {
        return heroDb.loadObject(id);
    }
    private Maze loadMaze(String id){
        return mazeDb.loadObject(id);
    }


}
