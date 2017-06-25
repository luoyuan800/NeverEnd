package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.ExchangeAdapter;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.service.ExchangeManager;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.utils.Resource;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class ExchangeDialog {
    private GameContext context;
    private ExchangeManager manager;
    private AlertDialog progress;

    public ExchangeDialog(GameContext context) {
        this.context = context;
        manager = new ExchangeManager(context);
        progress = SimplerDialogBuilder.build(Resource.getString(R.string.syn_server), context.getContext());
    }

    public void show() {
        Button queryItem = new Button(context.getContext(), null, android.R.style.Widget_Button_Inset);
        queryItem.setText(R.string.query_exchange_items);
        queryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                context.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        List<ExchangeObject> exchangeObjects = manager.queryAvailableExchanges(-1);
                        progress.dismiss();
                        ExchangeAdapter ea = new ExchangeAdapter(exchangeObjects, new View.OnClickListener() {
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
                });
            }
        });
        Button myItem = new Button(context.getContext(), null, android.R.style.Widget_Button_Inset);
        myItem.setText(R.string.my_exchange_items);
        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                context.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        List<ExchangeObject> eos = manager.querySubmittedExchangeOfMine();
                        progress.dismiss();
                        ExchangeAdapter es = new ExchangeAdapter(eos, new View.OnClickListener() {
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
                });
            }
        });
        LinearLayout linearLayout = new LinearLayout(context.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(queryItem);
        linearLayout.addView(myItem);
    }
}
