package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.model.dlc.SingleItemDLC;
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
                DLC dlc = service.getDlcDetail(id, context);
                if(dlc instanceof MonsterDLC)
                saveMonsterDlc((MonsterDLC) dlc, callBack);
            }
        });
    }

    public void buySingleItemDlc(final SingleItemDLC dlc, final BuyCallBack callBack) {
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if (dlc != null) {
                    if (service.buyDlc(dlc.getId(), context)) {
                        context.getDataManager().add(dlc.getItem());
                        callBack.onBuySuccess(dlc);
                    } else {
                        callBack.onBuyFailure(StringUtils.EMPTY_STRING);
                    }
                }
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

    public void queryDLCKeys(final QueryCallBack callBack, final int offset, final int row){
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                List<DLCKey> keys =  service.getDlcKeys(context, offset, row);
                callBack.onQuerySuccess(keys);
            }
        });
    }

    public void queryDLC(final String id, final DetailCallBack callBack){
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                callBack.onDetailSuccess(service.getDlcDetail(id, context));
            }
        });
    }

    public interface BuyCallBack {
        void onBuySuccess(DLC dlc);

        void onBuyFailure(String failure);
    }

    public interface QueryCallBack{
        void onQuerySuccess(List<DLCKey> keys);
        void onQueryFailure();
    }

    public interface DetailCallBack{
        void onDetailSuccess(DLC dlc);
        void onDetailFailure();
    }

    private void saveMonsterDlc(MonsterDLC dlc, BuyCallBack callBack) {
        if (dlc != null) {
            if (service.buyDlc(dlc.getId(), context)) {
                for (int i = 0; i < dlc.getMonsters().size(); i++) {
                    Monster monster = dlc.getMonsters().get(i);
                    byte[] imageByte = dlc.getImage().get(i);
                    context.getPetMonsterHelper().addSpecialMonster(monster);
                    Resource.saveImageIntoAppFolder(String.valueOf(monster.getIndex()),new ByteArrayInputStream(imageByte));
                }
                callBack.onBuySuccess(dlc);
            } else {
                callBack.onBuyFailure(StringUtils.EMPTY_STRING);
            }
        }
    }
}
