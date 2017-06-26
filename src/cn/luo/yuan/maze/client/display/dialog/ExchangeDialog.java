package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.ExchangeAdapter;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.client.service.ExchangeManager;
import cn.luo.yuan.maze.client.service.GameContext;
import cn.luo.yuan.maze.client.utils.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class ExchangeDialog {
    private GameContext context;
    private ExchangeManager manager;
    private AlertDialog progress;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    if(msg.obj instanceof List){
                        List<ExchangeObject> objects = (List<ExchangeObject>) msg.obj;
                        ExchangeAdapter es = new ExchangeAdapter(objects, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ExchangeObject eo = (ExchangeObject) v.getTag(R.string.item);
                                context.getDataManager().save(eo.getExchange());
                                context.getExecutor().submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        manager.getBackMyExchange(eo.getId());
                                    }
                                });
                                ExchangeAdapter adapter = (ExchangeAdapter) v.getTag(R.string.adapter);
                                adapter.getExchanges().remove(eo);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        ListView list = new ListView(context.getContext());
                        list.setAdapter(es);
                        progress.dismiss();
                        SimplerDialogBuilder.build(list, Resource.getString(R.string.close), null, context.getContext());
                    }
                    break;
                case 2:
                    if(msg.obj instanceof List){
                        List<ExchangeObject> objects = (List<ExchangeObject>) msg.obj;
                        progress.dismiss();
                        ExchangeAdapter ea = new ExchangeAdapter(objects, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progress.show();
                                context.getExecutor().submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        ExchangeObject eo = (ExchangeObject) v.getTag(R.string.item);
                                        IDModel myItem = manager.queryMyAvaiableItemForExchange(eo.getExpectedType(), eo.getExpectedKeyWord());
                                        if (manager.requestExchange((Serializable) myItem, eo)) {
                                            context.getDataManager().save(eo.getExchange());
                                            ExchangeAdapter adapter = (ExchangeAdapter) v.getTag(R.string.adapter);
                                            List<ExchangeObject> exchangeObjects = manager.queryAvailableExchanges(-1);
                                            adapter.setExchanges(exchangeObjects);
                                            adapter.notifyDataSetChanged();
                                            SimplerDialogBuilder.build("交换成功！", Resource.getString(R.string.conform), null, context.getContext());
                                            progress.dismiss();
                                        }
                                    }
                                });

                            }
                        });
                        LinearLayout linearLayout = new LinearLayout(context.getContext());
                        CheckBox cb = new CheckBox(context.getContext());
                        linearLayout.addView(cb);
                        ListView list = new ListView(context.getContext());
                        list.setAdapter(ea);
                        linearLayout.addView(list);
                        SimplerDialogBuilder.build(linearLayout, Resource.getString(R.string.close), null, context.getContext());
                    }
            }
        }
    };

    public ExchangeDialog(GameContext context) {
        this.context = context;
        manager = new ExchangeManager(context);
        progress = SimplerDialogBuilder.build(Resource.getString(R.string.syn_server), context.getContext(), true);
    }

    public void show() {
        Button queryItem = new Button(context.getContext());
        queryItem.setText(R.string.query_exchange_items);
        queryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExchanges();
            }
        });
        Button myItem = new Button(context.getContext());
        myItem.setText(R.string.my_exchange_items);
        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyExchange();
            }
        });
        LinearLayout linearLayout = new LinearLayout(context.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(queryItem);
        linearLayout.addView(myItem);
        Button upload = new Button(context.getContext());
        upload.setText(R.string.upload);
        //TODO upload
        linearLayout.addView(upload);
        SimplerDialogBuilder.build(linearLayout, Resource.getString(R.string.close), null, context.getContext());
    }

    private void showMyExchange() {
        progress.show();
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                List<ExchangeObject> eos = manager.querySubmittedExchangeOfMine();
                Message msg = new Message();
                msg.what = 1;
                msg.obj = eos;
                handler.sendMessage(msg);
            }
        });
    }

    private void showExchanges() {
        progress.show();
        context.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                List<ExchangeObject> exchangeObjects = manager.queryAvailableExchanges(-1);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = exchangeObjects;
                handler.sendMessage(msg);
            }
        });
    }
}
