package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.listener.BattleEndListener;
import cn.luo.yuan.maze.listener.Listener;
import cn.luo.yuan.maze.listener.LostListener;
import cn.luo.yuan.maze.listener.PetCatchListener;
import cn.luo.yuan.maze.listener.WinListener;

import java.util.HashMap;

/**
 * Created by gluo on 5/8/2017.
 */
public class ListenerService {
    public static HashMap<String, BattleEndListener> battleEndListeners = new HashMap<>(2);
    public static HashMap<String, PetCatchListener> petCatchListeners = new HashMap<>(2);
    public static HashMap<String, LostListener> lostListeners = new HashMap<>(2);
    public static HashMap<String, WinListener> winListeners = new HashMap<>(2);

    public static void registerListener(Listener listener){
        if(listener instanceof BattleEndListener){
            battleEndListeners.put(listener.getKey(), (BattleEndListener) listener);
        }else if(listener instanceof PetCatchListener){
            petCatchListeners.put(listener.getKey(), (PetCatchListener) listener);
        } else if(listener instanceof WinListener){
            winListeners.put(listener.getKey(), (WinListener) listener);
        } else if(listener instanceof LostListener){
            lostListeners.put(listener.getKey(), (LostListener) listener);
        }
    }

    public static void unregister(Listener listener){
        if(listener instanceof BattleEndListener){
            battleEndListeners.remove(listener.getKey());
        }else if(listener instanceof PetCatchListener){
            petCatchListeners.remove(listener.getKey());
        }else if(listener instanceof LostListener){
            lostListeners.remove(listener.getKey());
        }else if(listener instanceof WinListener){
            winListeners.remove(listener.getKey());
        }
    }
}
