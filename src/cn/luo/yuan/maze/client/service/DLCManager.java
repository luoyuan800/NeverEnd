package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/9/2017.
 */
public class DLCManager {
    private NeverEnd context;
    private ServerService service;

    public DLCManager(NeverEnd context) {
        this.context = context;
        service = new ServerService(context);
    }

    public void buyMonsterDlc(final String id, final BuyCallBack callBack) {
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                MonsterDLC dlc = service.getMonsterDlcDetail(id, context);
                saveMonsterDlc(dlc, callBack);
            }
        });
    }

    public void buyMonsterDlc(final MonsterDLC dlc, final BuyCallBack callBack) {
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                saveMonsterDlc(dlc, callBack);
            }
        });
    }

    public void queryMonsterDLCs(final QueryCallBack callBack){
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                List<DLCKey> keys =  service.getMonsterDlcKey(context);
                callBack.onQuerySuccess(keys);
            }
        });
    }

    public void queryMonsterDLC(final String id, final DetailCallBack callBack){
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                callBack.onDetailSuccess(service.getMonsterDlcDetail(id, context));
            }
        });
    }

    public interface BuyCallBack {
        void onBuySuccess(MonsterDLC dlc);

        void onBuyFailure(String failure);
    }

    public interface QueryCallBack{
        void onQuerySuccess(List<DLCKey> keys);
        void onQueryFailure();
    }

    public interface DetailCallBack{
        void onDetailSuccess(MonsterDLC dlc);
        void onDetailFailure();
    }

    private void saveMonsterDlc(MonsterDLC dlc, BuyCallBack callBack) {
        if (dlc != null) {
            if (service.buyMonsterDlc(dlc.getId(), context)) {
                for (int i = 0; i < dlc.getMonsters().size(); i++) {
                    Monster monster = dlc.getMonsters().get(i);
                    byte[] imageByte = dlc.getImage().get(i);
                    context.getPetMonsterHelper().addSpecialMonster(monster);
                    Resource.saveImageIntoAppFolder(String.valueOf(dlc.getId()),new ByteArrayInputStream(imageByte));
                }
                callBack.onBuySuccess(dlc);
            } else {
                callBack.onBuyFailure(StringUtils.EMPTY_STRING);
            }
        }
    }
}