package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.index.HeroIndex;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectDB;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by gluo on 5/22/2017.
 */
public class HeroTable {
    private ObjectDB<Hero> heroDb;
    private ObjectDB<Maze> mazeDb;
    private HashMap<HeroIndex, SoftReference<Hero>> cache;
    private long maxLevel = 1;

    public HeroTable() {
        heroDb = new ObjectDB<>(Hero.class);
        mazeDb = new ObjectDB<>(Maze.class);
        List<Hero> heros = heroDb.loadAll();
        cache = new HashMap<>(heros.size());
        for (Hero hero : heros) {
            HeroIndex index = new HeroIndex();
            index.setId(hero.getId());
            index.setElement(hero.getElement());
            Maze maze = loadMaze(hero.getId());
            index.setLevel(maze.getLevel());
            index.setMaxLevel(maze.getMaxLevel());
            index.setRace(hero.getRace());
            cache.put(index, new SoftReference<Hero>(hero));
            if (index.getLevel() > maxLevel) {
                maxLevel = index.getLevel();
            }
        }
    }

    public List<Hero> queryHero(HeroIndex index) {
        List<Hero> heros = new ArrayList<>();
        for (HashMap.Entry<HeroIndex, SoftReference<Hero>> entry : cache.entrySet()) {
            HeroIndex key = entry.getKey();
            boolean match = index.getElement() == null || index.getElement() == key.getElement();
            match &= index.getRace() == null || index.getRace() == key.getRace();
            match &= index.getLevel() == null || Objects.equals(index.getLevel(), key.getLevel());
            match &= index.getMaxLevel() == null || Objects.equals(index.getMaxLevel(), key.getMaxLevel());

            if (match) {
                Hero hero = entry.getValue().get();
                if (hero == null) {
                    hero = loadHero(key.getId());
                    cache.put(key, new SoftReference<Hero>(hero));
                }
                if (hero != null) {
                    heros.add(hero);
                }
            }
        }
        return heros;
    }

    public void insertHero(Hero hero, Maze maze) {
        heroDb.save(hero, hero.getId());
        mazeDb.save(maze, hero.getId());
        HeroIndex index = new HeroIndex();
        index.setMaxLevel(maze.getMaxLevel());

        index.setName(hero.getName());
        index.setLevel(maze.getLevel());
        index.setElement(hero.getElement());
        if (index.getLevel() > maxLevel) {
            maxLevel = index.getLevel();
        }
        index.setRace(hero.getRace());
        index.setId(hero.getId());
        cache.put(index, new SoftReference<Hero>(hero));
    }

    public Set<String> getAllHeroIds(long level) {
        Set<String> ids = new HashSet<>();
        for (HeroIndex index : cache.keySet()) {
            if (index.getLevel() == level)
                ids.add(index.getId());
        }
        return ids;
    }

    public Hero getHero(String id) {
        for (HashMap.Entry<HeroIndex, SoftReference<Hero>> entry : cache.entrySet()) {
            HeroIndex index = entry.getKey();
            SoftReference<Hero> ref = entry.getValue();
            if (index.getId().equals(id)) {
                Hero hero = ref.get();
                if (hero == null) {
                    hero = loadHero(id);
                    cache.put(index, new SoftReference<>(hero));
                }
                return hero;
            }
        }
        return null;
    }

    public long getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(long maxLevel) {
        this.maxLevel = maxLevel;
    }

    private Hero loadHero(String id) {
        return heroDb.loadObject(id);
    }

    private Maze loadMaze(String id) {
        return mazeDb.loadObject(id);
    }

}
