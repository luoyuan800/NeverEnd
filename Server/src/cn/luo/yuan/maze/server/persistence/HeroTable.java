package cn.luo.yuan.maze.server.persistence;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.index.HeroIndex;
import cn.luo.yuan.maze.server.model.SingleMessage;
import cn.luo.yuan.maze.server.persistence.serialize.ObjectTable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
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
    private ObjectTable<Hero> heroDb;
    private ObjectTable<Maze> mazeDb;
    private ObjectTable<SingleMessage> msgerDb;
    private HashMap<HeroIndex, SoftReference<Hero>> cache;
    private long maxLevel = 1;
    private File root;

    public HeroTable(File root) throws IOException, ClassNotFoundException {
        this.root = root;
        heroDb = new ObjectTable<>(Hero.class, root);
        mazeDb = new ObjectTable<>(Maze.class, root);
        List<Hero> heros = heroDb.loadAll();
        cache = new HashMap<>(heros.size());
        for (Hero hero : heros) {
            HeroIndex index = new HeroIndex();
            index.setId(hero.getId());
            index.setElement(hero.getElement());
            Maze maze = getMaze(hero.getId());
            index.setLevel(maze.getLevel());
            index.setMaxLevel(maze.getMaxLevel());
            index.setRace(hero.getRace());
            cache.put(index, new SoftReference<Hero>(hero));
            if (index.getLevel() > maxLevel) {
                maxLevel = index.getLevel();
            }
        }
    }

    public List<Hero> queryHero(HeroIndex index) throws IOException, ClassNotFoundException {
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
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getAllHeroIds(long level) {
        Set<String> ids = new HashSet<>();
        for (HeroIndex index : cache.keySet()) {
            if (index.getLevel() == level)
                ids.add(index.getId());
        }
        return ids;
    }

    public Hero getHero(String id) throws IOException, ClassNotFoundException {
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

    public void clear() {
        heroDb.clear();
        mazeDb.clear();
    }

    @Nullable
    public SingleMessage getMessager(String id) {
        return msgerDb.loadObject(id);
    }

    public void saveMessager(SingleMessage singleMessage, String id) throws IOException {
        msgerDb.save(singleMessage, id);
    }

    private Hero loadHero(String id) throws IOException, ClassNotFoundException {
        return heroDb.loadObject(id);
    }

    public Maze getMaze(String id) throws IOException, ClassNotFoundException {
        return mazeDb.loadObject(id);
    }

    public void saveHero(Hero hero){
        try {
            heroDb.save(hero, hero.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMaze(Maze maze, String id){
        try {
            mazeDb.save(maze, id);
            if (maze.getLevel() > maxLevel) {
                maxLevel = maze.getLevel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
