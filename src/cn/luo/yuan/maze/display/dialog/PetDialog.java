package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.PetAdapter;
import cn.luo.yuan.maze.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.service.PetMonsterLoder;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/15/2017.
 */
public class PetDialog implements View.OnClickListener{
    Handler handler = new Handler();
    private GameContext control;
    private AlertDialog.Builder builder;
    private PetAdapter adapter;
    private Pet currentPet;
    private LoadMoreListView loadMoreListView;
    private AlertDialog dialog;

    public PetDialog(GameContext control, PetAdapter adapter) {
        setAdapter(adapter);
        this.control = control;
        builder = new AlertDialog.Builder(control.getContext()).setTitle(Resource.getString(R.string.pet_dialog_title)).setView(R.layout.pet_view);
    }

    public PetDialog setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public PetDialog setAdapter(PetAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void show() {
        dialog = builder.create();
        dialog.show();
        EditText tag = (EditText) dialog.findViewById(R.id.pet_tag);
        loadMoreListView = (LoadMoreListView) dialog.findViewById(R.id.pet_simple_list);
        loadMoreListView.setAdapter(adapter);
        loadMoreListView.setOnLoadListener(adapter);
        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentPet = adapter.getItem(position);
                        ((TextView) dialog.findViewById(R.id.pet_name)).setText(Html.fromHtml(currentPet.getDisplayName()));
                        ((TextView) dialog.findViewById(R.id.pet_level)).setText(StringUtils.formatStar(currentPet.getLevel()));
                        ((TextView) dialog.findViewById(R.id.pet_atk)).setText(StringUtils.formatNumber(currentPet.getAtk()));
                        ((TextView) dialog.findViewById(R.id.pet_def)).setText(StringUtils.formatNumber(currentPet.getDef()));
                        ((TextView) dialog.findViewById(R.id.pet_hp)).setText(StringUtils.formatNumber(currentPet.getHp()) + "/" + StringUtils.formatNumber(currentPet.getMaxHp()));
                        Skill skill = currentPet.getSkills()[0];
                        ((TextView) dialog.findViewById(R.id.pet_skill)).setText(skill != null ? skill.getName() : StringUtils.EMPTY_STRING);
                        ((TextView) dialog.findViewById(R.id.pet_owner)).setText(currentPet.getOwnerName());
                        ((TextView) dialog.findViewById(R.id.pet_mother)).setText(Html.fromHtml(currentPet.getMother()));
                        ((TextView) dialog.findViewById(R.id.pet_farther)).setText(Html.fromHtml(currentPet.getFarther()));
                        tag.setText(Html.fromHtml(currentPet.getTag()));
                        dialog.findViewById(R.id.pet_drop).setOnClickListener(PetDialog.this);
                        dialog.findViewById(R.id.pet_upgrade).setOnClickListener(PetDialog.this);
                    }
                });
            }
        });
        tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentPet.setTag(s.toString());
                control.getDataManager().savePet(currentPet);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pet_drop:
                if(currentPet.isMounted()){
                   new AlertDialog.Builder(control.getContext()).setMessage(R.string.mount_not_drop).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   }).show();
                }else {
                    new AlertDialog.Builder(control.getContext()).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.conform_drop),currentPet.getDisplayName()))).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            control.getDataManager().deletePet(currentPet);
                        }
                    }).setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

                }
                break;
            case R.id.pet_upgrade:
                PetMonsterHelper helper = control.getPetMonsterHelper();
                PetAdapter petAdapter = new PetAdapter(control.getContext(),control.getDataManager(),"");
                LoadMoreListView listView = new LoadMoreListView(control.getContext());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Pet minor = petAdapter.getItem(position);
                        if(minor!=currentPet){
                            if(helper.upgrade(currentPet, minor)){
                                new AlertDialog.Builder(control.getContext()).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        control.getDataManager().deletePet(minor);
                                        control.getDataManager().savePet(currentPet);
                                    }
                                }).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.upgrade_success), currentPet.getDisplayName()))).show();
                            }else{
                                new AlertDialog.Builder(control.getContext()).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        control.getDataManager().deletePet(minor);
                                    }
                                }).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.upgrade_failed), currentPet.getDisplayName()))).show();
                            }
                        }
                    }
                });
                listView.setOnLoadListener(petAdapter);
                new AlertDialog.Builder(control.getContext()).setTitle(R.string.select_pet).setView(listView).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
    }

}
