package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.PetAdapter;
import cn.luo.yuan.maze.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/15/2017.
 */
public class PetDialog implements View.OnClickListener {
    Handler handler = new Handler();
    private InfoControl control;
    private AlertDialog.Builder builder;
    private PetAdapter adapter;
    private Pet currentPet;
    private LoadMoreListView loadMoreListView;

    public PetDialog(InfoControl control) {
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
        final AlertDialog dialog = builder.create();
        dialog.show();
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
                        ((TextView) dialog.findViewById(R.id.pet_hp)).setText(StringUtils.formatNumber(currentPet.getHp()) + "/" + StringUtils.formatNumber(currentPet.getMaxHP()));
                        Skill skill = currentPet.getSkills()[0];
                        ((TextView) dialog.findViewById(R.id.pet_skill)).setText(skill != null ? skill.getName() : StringUtils.EMPTY_STRING);
                        ((TextView) dialog.findViewById(R.id.pet_owner)).setText(currentPet.getOwnerName());
                        ((TextView) dialog.findViewById(R.id.pet_mother)).setText(Html.fromHtml(currentPet.getMother()));
                        ((TextView) dialog.findViewById(R.id.pet_farther)).setText(Html.fromHtml(currentPet.getFarther()));
                        ((TextView) dialog.findViewById(R.id.pet_tag)).setText(Html.fromHtml(currentPet.getTag()));
                        dialog.findViewById(R.id.pet_drop).setOnClickListener(PetDialog.this);
                        dialog.findViewById(R.id.pet_upgrade).setOnClickListener(PetDialog.this);
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pet_drop:
                control.getDataManager().deletePet(currentPet);
                break;
            case R.id.pet_upgrade:
                PetMonsterHelper helper = new PetMonsterHelper(control);

                break;
        }
    }
}
