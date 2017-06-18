package cn.luo.yuan.maze.model;

/**
 * Created by gluo on 6/15/2017.
 */
public interface OwnedAble {
    String getOwnerId();
    String getKeeperId();
    void setKeeperId(String id);
    void setOwnerId(String id);

    void setOwnerName(String name);
    void setKeeperName(String name);

    void setHeroIndex(int index);
    int getHeroIndex();
}
