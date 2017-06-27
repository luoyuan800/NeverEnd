package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.ExchangeAdapter;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.client.service.ExchangeManager;
import cn.luo.yuan.maze.client.service.GameContext;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.utils.Field;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class ExchangeDialog implements LoadMoreListView.OnItemClickListener {
    private GameContext context;
    private ExchangeManager manager;
    private AlertDialog progress;
    private Dialog currentShowingDialog;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 3:
                    showSelectDialog();
                    break;
                case 1:
                    if(msg.obj instanceof List){
                        List<ExchangeObject> objects = (List<ExchangeObject>) msg.obj;
                        progress.dismiss();
                        showSubmitedDialog(objects);
                    }
                    break;
                case 2:
                    showOtherSubmitedDialog();
            }
        }
    };

    @Override
    public  void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getAdapter().getItem(position);
        if(item instanceof Serializable) {
            if(currentShowingDialog!=null){
                currentShowingDialog.dismiss();
                currentShowingDialog = null;
            }
            progress.show();
            context.getExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    if(manager.submitExchange((Serializable) item)){
                        Toast.makeText(context.getContext(), "成功上传" + (item instanceof NameObject ? ((NameObject) item).getName() : ""), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context.getContext(), "上传失败，请检测网络后重试。", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void showOtherSubmitedDialog() {
        View view = View.inflate(context.getContext(), R.layout.select_submit, null);
        RadioButton petR = (RadioButton) view.findViewById(R.id.pet_type);
        RadioButton accessoryR = (RadioButton) view.findViewById(R.id.accessory_type);
        RadioButton goodsR = (RadioButton) view.findViewById(R.id.goods_type);
        EditText key = (EditText)view.findViewById(R.id.key_text);
        LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
        SimplerDialogBuilder.build(view, Resource.getString(R.string.close), null, context.getContext());

        progress.show();
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                List<ExchangeObject> exchangeObjects =
                        manager.queryAvailableExchanges(petR.isChecked() ? Field.PET_TYPE :
                                accessoryR.isChecked() ? Field.ACCESSORY_TYPE : Field.GOODS_TYPE);
                if(progress.isShowing()) {
                    progress.dismiss();
                }
                if(list.getAdapter() == null){
                    list.setAdapter(buildExchangeAdapter(exchangeObjects));
                }else{
                    ExchangeAdapter adapter = (ExchangeAdapter) list.getAdapter();
                    adapter.setExchanges(exchangeObjects);
                    adapter.notifyDataSetChanged();
                }

            }
        };
        petR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    accessoryR.setChecked(false);
                    goodsR.setChecked(false);
                    progress.show();
                    context.getExecutor().submit(updateTask);
                }
            }
        });
        petR.setChecked(true);

        accessoryR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    petR.setChecked(false);
                    goodsR.setChecked(false);
                    progress.show();
                    context.getExecutor().submit(updateTask);
                }
            }
        });


        goodsR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    accessoryR.setChecked(false);
                    petR.setChecked(false);
                    progress.show();
                    context.getExecutor().submit(updateTask);
                }
            }
        });
        context.getExecutor().submit(updateTask);

    }

    @NotNull
    private ExchangeAdapter buildExchangeAdapter(List<ExchangeObject> objects) {
        return new ExchangeAdapter(objects, new View.OnClickListener() {
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
    }

    private void showSubmitedDialog(List<ExchangeObject> objects) {
        ExchangeAdapter es = new ExchangeAdapter(objects, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExchangeObject eo = (ExchangeObject) v.getTag(R.string.item);
                context.getDataManager().save(eo.getExchange());
                context.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        IDModel model;
                        if(eo.getChanged() == null) {
                            Object result = manager.getBackMyExchange(eo.getId());
                            if (result instanceof ExchangeObject) {
                                model = manager.acknowledge(eo);

                            }else{
                                model = manager.unBox(eo);
                            }
                        }else{
                            model = manager.acknowledge(eo);
                        }
                        if(model!=null){
                            context.getDataManager().save(model);
                        }
                        if (model instanceof NameObject) {
                            Toast.makeText(context.getContext(), "取回" + ((NameObject) model).getName(), Toast.LENGTH_SHORT).show();
                        }
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

    private void showSelectDialog() {
        View view = View.inflate(context.getContext(),R.layout.select_submit, null);
        RadioButton petR = (RadioButton) view.findViewById(R.id.pet_type);
        RadioButton accessoryR = (RadioButton) view.findViewById(R.id.accessory_type);
        RadioButton goodsR = (RadioButton) view.findViewById(R.id.goods_type);
        EditText key = (EditText)view.findViewById(R.id.key_text);
        LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
        list.setOnItemClickListener(ExchangeDialog.this);
        petR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    accessoryR.setChecked(false);
                    goodsR.setChecked(false);
                    PetAdapter adapter = new PetAdapter(context.getContext(),context.getDataManager(),key.getText().toString());
                    list.setAdapter(adapter);
                    list.setOnLoadListener(adapter);
                    adapter.notifyDataSetChanged();
                    key.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            adapter.setLimitKeyWord(s.toString());
                        }
                    });
                }
            }
        });
        accessoryR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    petR.setChecked(false);
                    goodsR.setChecked(false);
                    StringAdapter<Accessory> adapter = new StringAdapter<>(context.getDataManager().loadAccessories(0, 100,key.getText().toString(),null));
                    list.setAdapter(adapter);
                    list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                        @Override
                        public void onLoadMore(LoadMoreListView loadMoreListView) {
                            List<Accessory> collection = context.getDataManager().loadAccessories(adapter.getCount(), 100, key.getText().toString(), null);
                            if(collection.size() > 0) {
                                adapter.getData().addAll(collection);
                            }else {
                                list.onLoadMoreComplete(true);
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }
        });
        goodsR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    accessoryR.setChecked(false);
                    petR.setChecked(false);
                    StringAdapter<Goods> adapter = new StringAdapter<>(context.getDataManager().loadAllGoods());
                    list.onLoadMoreComplete(true);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        petR.setChecked(true);
        currentShowingDialog = SimplerDialogBuilder.build(view,Resource.getString(R.string.close),null,context.getContext());
    }

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
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(3);
            }
        });
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

    }
}
