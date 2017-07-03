package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.ExchangeAdapter;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.ExchangeManager;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.utils.Field;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class ExchangeDialog implements LoadMoreListView.OnItemClickListener {
    private NeverEnd context;
    private ExchangeManager manager;
    private AlertDialog progress;
    private Dialog currentShowingDialog;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(progress.isShowing()){
                progress.dismiss();
            }
            switch (msg.what) {
                case 7:

                    Object[] objs = (Object[]) msg.obj;
                    refreshList((List<ExchangeObject>)objs[0], (LoadMoreListView) objs[1]);
                    break;
                case 6:

                    ExchangeObject eo = (ExchangeObject) msg.obj;
                    showSelectExchangeDialog(eo);
                    break;
                case 5:

                    SimplerDialogBuilder.build("网络异常，请稍后再试", Resource.getString(R.string.close), (DialogInterface.OnClickListener) null, context.getContext());
                case 4:

                    Object item = msg.obj;
                    Toast.makeText(context.getContext(), Html.fromHtml(item.toString()), Toast.LENGTH_SHORT).show();
                    break;
                case 3:

                    showSelectSubmitDialog();
                    break;
                case 1:
                    if (msg.obj instanceof List) {
                        List<ExchangeObject> objects = (List<ExchangeObject>) msg.obj;
                        showSubmitedDialog(objects);
                    }
                    break;
                case 2:
                    showOtherSubmitedDialog();
            }
        }
    };

    public ExchangeDialog(NeverEnd context) {
        this.context = context;
        manager = new ExchangeManager(context);
        progress = SimplerDialogBuilder.build(Resource.getString(R.string.syn_server), context.getContext(), true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getAdapter().getItem(position);
        if (item instanceof Serializable) {
            if (currentShowingDialog != null) {
                currentShowingDialog.dismiss();
                currentShowingDialog = null;
            }
            LinearLayout root = new LinearLayout(context.getContext());
            root.setOrientation(LinearLayout.VERTICAL);
            RadioButton petType = new RadioButton(context.getContext());
            petType.setText(R.string.pet_type);
            RadioButton accType = new RadioButton(context.getContext());
            accType.setText(R.string.acc_type);
            RadioButton goodesType = new RadioButton(context.getContext());
            goodesType.setText(R.string.goods_type);
            LinearLayout rbg = new LinearLayout(context.getContext());
            rbg.setOrientation(LinearLayout.HORIZONTAL);
            rbg.addView(petType);
            rbg.addView(accType);
            rbg.addView(goodesType);
            root.addView(rbg);
            EditText limit = new EditText(context.getContext());
            limit.setHint("输入你的限制条件");
            limit.setText("测试");
            root.addView(limit);
            petType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        accType.setChecked(false);
                        goodesType.setChecked(false);
                    }
                }
            });
            accType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        petType.setChecked(false);
                        goodesType.setChecked(false);
                    }
                }
            });
            goodesType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        petType.setChecked(false);
                        accType.setChecked(false);
                    }
                }
            });
            petType.setChecked(true);
            Dialog dialog = SimplerDialogBuilder.build(root, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progress.show();
                    context.getExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            if (manager.submitExchange((Serializable) item, limit.getText().toString(), petType.isChecked() ? Field.PET_TYPE : accType.isChecked() ? Field.ACCESSORY_TYPE : Field.GOODS_TYPE)) {
                                Message message = new Message();
                                message.what = 4;
                                message.obj = "成功上传" + (item instanceof NameObject ? ((NameObject) item).getName() : "");
                                handler.sendMessage(message);
                            } else {
                                Toast.makeText(context.getContext(), "上传失败，请检测网络后重试。", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }, context.getContext());

        }
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

    private void showSelectExchangeDialog(final ExchangeObject eo) {
        ListAdapter adapter;
        LoadMoreListView list = new LoadMoreListView(context.getContext());
        switch (eo.getExpectedType()) {
            case Field.PET_TYPE:
                adapter = new PetAdapter(context.getContext(), context.getDataManager(), eo.getExpectedKeyWord());
                list.setAdapter(adapter);
                list.setOnLoadListener((LoadMoreListView.OnRefreshLoadingMoreListener) adapter);
                break;
            case Field.ACCESSORY_TYPE:
                adapter = new StringAdapter<Accessory>(context.getDataManager().loadAccessories(0, 50, eo.getExpectedKeyWord(), null));
                list.setAdapter(adapter);
                list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreListView loadMoreListView) {
                        List<Accessory> accessories = context.getDataManager().loadAccessories(adapter.getCount(), 50, eo.getExpectedKeyWord(), null);
                        if (accessories.size() > 0) {
                            ((StringAdapter<Accessory>) adapter).addAll(accessories);
                            loadMoreListView.onLoadMoreComplete(false);
                        } else {
                            loadMoreListView.onLoadMoreComplete(true);
                        }
                    }
                });
                break;
            case Field.GOODS_TYPE:
                adapter = new StringAdapter<Goods>(context.getDataManager().loadAllGoods());
                list.setAdapter(adapter);
                list.onLoadMoreComplete(true);
                break;
        }
        Dialog selectDialog = SimplerDialogBuilder.build(list, Resource.getString(R.string.close), null, context.getContext());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object myItem = parent.getAdapter().getItem(position);
                selectDialog.dismiss();
                progress.show();
                context.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (manager.requestExchange((Serializable) myItem, eo)) {
                            context.getDataManager().save(eo.getExchange());
                            if (currentShowingDialog != null && currentShowingDialog.isShowing()) {
                                currentShowingDialog.dismiss();
                            }
                            Message message = new Message();
                            message.what = 4;
                            message.obj = "交换成功！获得了" + (eo.getExchange() instanceof NameObject ? ((NameObject) eo.getExchange()).getDisplayName() : "");
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        });
    }

    private void showOtherSubmitedDialog() {
        View view = View.inflate(context.getContext(), R.layout.select_submit, null);
        RadioButton petR = (RadioButton) view.findViewById(R.id.pet_type);
        RadioButton accessoryR = (RadioButton) view.findViewById(R.id.accessory_type);
        RadioButton goodsR = (RadioButton) view.findViewById(R.id.goods_type);
        EditText key = (EditText) view.findViewById(R.id.key_text);
        LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
        list.onLoadMoreComplete(true);
        currentShowingDialog = SimplerDialogBuilder.build(view, Resource.getString(R.string.close), null, context.getContext());
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                progress.show();
                List<ExchangeObject> exchangeObjects =
                        manager.queryAvailableExchanges(petR.isChecked() ? Field.PET_TYPE :
                                accessoryR.isChecked() ? Field.ACCESSORY_TYPE : Field.GOODS_TYPE, key.getText().toString());
                Message message = new Message();
                message.obj = new Object[]{exchangeObjects, list};
                message.what = 7;
                handler.sendMessage(message);
            }
        };
        key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                context.getExecutor().submit(updateTask);
            }
        });
        petR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
                if (isChecked) {
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
                if (isChecked) {
                    accessoryR.setChecked(false);
                    petR.setChecked(false);
                    progress.show();
                    context.getExecutor().submit(updateTask);
                }
            }
        });
        context.getExecutor().submit(updateTask);

    }

    private void refreshList(List<ExchangeObject> exchangeObjects, LoadMoreListView list) {
        if (progress.isShowing()) {
            progress.dismiss();
        }
        ExchangeAdapter adapter = buildExchangeAdapter(exchangeObjects);
        adapter.setButtonString(Resource.getString(R.string.exchange_label));
        list.setAdapter(adapter);
    }

    @NotNull
    private ExchangeAdapter buildExchangeAdapter(List<ExchangeObject> objects) {
        return new ExchangeAdapter(objects, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExchangeObject eo = (ExchangeObject) v.getTag(R.string.item);
                Message message = new Message();
                message.what = 6;
                message.obj = eo;
                handler.sendMessage(message);

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
                        if (eo.getChanged() == null) {
                            Object result = manager.getBackMyExchange(eo.getId());
                            if (result instanceof ExchangeObject) {
                                model = manager.acknowledge(eo);

                            } else {
                                model = manager.unBox(eo);
                            }
                        } else {
                            model = manager.acknowledge(eo);
                        }
                        if (model != null) {
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

    private void showSelectSubmitDialog() {
        View view = View.inflate(context.getContext(), R.layout.select_submit, null);
        RadioButton petR = (RadioButton) view.findViewById(R.id.pet_type);
        RadioButton accessoryR = (RadioButton) view.findViewById(R.id.accessory_type);
        RadioButton goodsR = (RadioButton) view.findViewById(R.id.goods_type);
        EditText key = (EditText) view.findViewById(R.id.key_text);
        LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
        list.setOnItemClickListener(ExchangeDialog.this);
        petR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    accessoryR.setChecked(false);
                    goodsR.setChecked(false);
                    PetAdapter adapter = new PetAdapter(context.getContext(), context.getDataManager(), key.getText().toString());
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
                if (isChecked) {
                    petR.setChecked(false);
                    goodsR.setChecked(false);
                    StringAdapter<Accessory> adapter = new StringAdapter<>(context.getDataManager().loadAccessories(0, 100, key.getText().toString(), null));
                    list.setAdapter(adapter);
                    list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                        @Override
                        public void onLoadMore(LoadMoreListView loadMoreListView) {
                            List<Accessory> collection = context.getDataManager().loadAccessories(adapter.getCount(), 100, key.getText().toString(), null);
                            if (collection.size() > 0) {
                                adapter.getData().addAll(collection);
                            } else {
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
                if (isChecked) {
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
        currentShowingDialog = SimplerDialogBuilder.build(view, Resource.getString(R.string.close), null, context.getContext());
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
                handler.sendEmptyMessage(2);
            }
        });
    }
}
