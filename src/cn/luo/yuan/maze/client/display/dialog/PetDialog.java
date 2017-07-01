package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.client.service.PetMonsterLoder;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/15/2017.
 */
public class PetDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Handler handler = new Handler();
    private NeverEnd control;
    private AlertDialog.Builder builder;
    private PetAdapter adapter;
    private Pet currentPet;
    private LoadMoreListView loadMoreListView;
    private AlertDialog dialog;

    public PetDialog(NeverEnd control, PetAdapter adapter) {
        setAdapter(adapter);
        this.control = control;
        builder = new AlertDialog.Builder(control.getContext()).setTitle(Resource.getString(R.string.pet_dialog_title));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.pet_view);
        }else{
            builder.setView(View.inflate(control.getContext(),R.layout.pet_view, null));
        }
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
        final View detailView = dialog.findViewById(R.id.pet_detail_view);
        detailView.setVisibility(View.INVISIBLE);
        EditText tag = (EditText) detailView.findViewById(R.id.pet_tag);
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
                        refreshDetailView(detailView);

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
        dialog.findViewById(R.id.sort_intimacy).setOnClickListener(this);
        dialog.findViewById(R.id.sort_name).setOnClickListener(this);
        dialog.findViewById(R.id.sort_type).setOnClickListener(this);
        dialog.findViewById(R.id.sort_color).setOnClickListener(this);
        dialog.findViewById(R.id.close).setOnClickListener(this);
        dialog.findViewById(R.id.pet_evolution).setOnClickListener(this);
    }

    public void refreshDetailView(View detailView) {
        if (currentPet != null) {
            EditText tag = (EditText) detailView.findViewById(R.id.pet_tag);
            ((TextView) detailView.findViewById(R.id.pet_name)).setText(Html.fromHtml(currentPet.getDisplayNameWithLevel()));
            CheckBox isMounted = (CheckBox) dialog.findViewById(R.id.pet_mounted);
            isMounted.setOnCheckedChangeListener(null);
            isMounted.setChecked(currentPet.isMounted());
            isMounted.setOnCheckedChangeListener(PetDialog.this);
            ((TextView) detailView.findViewById(R.id.pet_atk)).setText(StringUtils.formatNumber(currentPet.getAtk()));
            ((TextView) detailView.findViewById(R.id.pet_def)).setText(StringUtils.formatNumber(currentPet.getDef()));
            ((TextView) detailView.findViewById(R.id.pet_hp)).setText(StringUtils.formatNumber(currentPet.getHp()) + "/" + StringUtils.formatNumber(currentPet.getMaxHp()));
            Skill skill = currentPet.getSkills()[0];
            ((TextView) detailView.findViewById(R.id.pet_skill)).setText(skill != null ? skill.getName() : StringUtils.EMPTY_STRING);
            ((TextView) detailView.findViewById(R.id.pet_owner)).setText(currentPet.getOwnerName());
            ((TextView) detailView.findViewById(R.id.pet_mother)).setText(Html.fromHtml(currentPet.getMother()));
            ((TextView) detailView.findViewById(R.id.pet_farther)).setText(Html.fromHtml(currentPet.getFarther()));
            ((TextView) detailView.findViewById(R.id.pet_intimacy)).setText(StringUtils.formatNumber(currentPet.getIntimacy()));
            tag.setText(Html.fromHtml(currentPet.getTag()));
            detailView.findViewById(R.id.pet_drop).setOnClickListener(PetDialog.this);
            detailView.findViewById(R.id.pet_upgrade).setOnClickListener(PetDialog.this);
            ((ImageView) detailView.findViewById(R.id.pet_image)).setImageDrawable(PetMonsterLoder.loadMonsterImage(currentPet.getIndex()));
            detailView.setVisibility(View.VISIBLE);
        } else {
            detailView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        PetMonsterHelper helper = control.getPetMonsterHelper();
        switch (v.getId()) {
            case R.id.sort_intimacy:
                adapter.setSortType(PetAdapter.SORT_INTIMACY);
                adapter.notifyDataSetChanged();
                refreshSortButton();
                break;
            case R.id.sort_type:
                adapter.setSortType(PetAdapter.SORT_INDEX);
                adapter.notifyDataSetChanged();
                refreshSortButton();
                break;
            case R.id.sort_name:
                adapter.setSortType(PetAdapter.SORT_NAME);
                adapter.notifyDataSetChanged();
                refreshSortButton();
                break;
            case R.id.sort_color:
                adapter.setSortType(PetAdapter.SORT_COLOR);
                adapter.notifyDataSetChanged();
                refreshSortButton();
                break;
            case R.id.pet_evolution:
                if (currentPet != null) {
                    if (helper.evolution(currentPet)) {
                        control.getDataManager().savePet(currentPet);
                        refreshDetailView(dialog.findViewById(R.id.pet_detail_view));
                        adapter.notifyDataSetChanged();
                        control.getViewHandler().refreshPets(control.getHero());
                        new AlertDialog.Builder(control.getContext()).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.evolution_successed), currentPet.getDisplayName()))).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        new AlertDialog.Builder(control.getContext()).setMessage(R.string.evolution_failed).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
                break;
            case R.id.close:
                dialog.dismiss();
                break;
            case R.id.pet_drop:
                if (currentPet.isMounted()) {
                    new AlertDialog.Builder(control.getContext()).setMessage(R.string.mount_not_drop).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    new AlertDialog.Builder(control.getContext()).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.conform_drop), currentPet.getDisplayName()))).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            control.getDataManager().deletePet(currentPet);
                            adapter.removePet(currentPet);
                            adapter.notifyDataSetChanged();
                            currentPet = null;
                            refreshDetailView(PetDialog.this.dialog.findViewById(R.id.pet_detail_view));
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
                if (currentPet != null && Data.FUSE_COST * currentPet.getLevel() <= control.getHero().getMaterial()) {
                    PetAdapter petAdapter = new PetAdapter(control.getContext(), control.getDataManager(), "");
                    LoadMoreListView listView = new LoadMoreListView(control.getContext());
                    listView.setAdapter(petAdapter);

                    listView.setOnLoadListener(petAdapter);
                    AlertDialog select = new AlertDialog.Builder(control.getContext()).setTitle(R.string.select_pet).setView(listView).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Pet minor = petAdapter.getItem(position);
                            if (minor != null && !minor.isMounted() && minor.getId() != currentPet.getId()) {
                                select.dismiss();
                                control.getDataManager().deletePet(minor);
                                if (helper.upgrade(currentPet, minor)) {
                                    new AlertDialog.Builder(control.getContext()).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dd, int which) {
                                            dd.dismiss();
                                            control.getDataManager().savePet(currentPet);
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    refreshDetailView(dialog.findViewById(R.id.pet_detail_view));
                                                }
                                            });
                                        }
                                    }).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.upgrade_success), currentPet.getDisplayName()))).show();
                                } else {
                                    new AlertDialog.Builder(control.getContext()).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setMessage(Html.fromHtml(String.format(Resource.getString(R.string.upgrade_failed), currentPet.getDisplayName()))).show();
                                }
                                adapter.removePet(minor);
                                adapter.notifyDataSetChanged();
                                control.getViewHandler().refreshPets(control.getHero());
                            }
                        }
                    });
                    control.getHero().setMaterial(control.getHero().getMaterial() - Data.FUSE_COST * currentPet.getLevel());
                    control.getViewHandler().refreshProperties(control.getHero());
                } else {
                    if (currentPet != null) {
                        new AlertDialog.Builder(control.getContext()).setMessage("需要" + currentPet.getLevel() * Data.FUSE_COST + "点锻造来进行升级").setPositiveButton(R.string.conform, null).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && !currentPet.isMounted()) {
            if(!control.getPetMonsterHelper().mountPet(currentPet, control.getHero())){
                buttonView.setChecked(false);
            }
        } else if (!isChecked && currentPet.isMounted()) {
            control.getHero().getPets().remove(currentPet);
            currentPet.setMounted(false);
        }
        control.getDataManager().savePet(currentPet);
        control.getViewHandler().refreshPets(control.getHero());
    }

    private void refreshSortButton() {
        ((Button) dialog.findViewById(R.id.sort_color)).setText(adapter.getSortOrderRevert() ? R.string.sort_color_down : R.string.sort_color_up);
        ((Button) dialog.findViewById(R.id.sort_name)).setText(adapter.getSortOrderRevert() ? R.string.sort_name_down : R.string.sort_name_up);
        ((Button) dialog.findViewById(R.id.sort_type)).setText(adapter.getSortOrderRevert() ? R.string.sort_type_down : R.string.sort_type_up);
        ((Button) dialog.findViewById(R.id.sort_intimacy)).setText(adapter.getSortOrderRevert() ? R.string.sort_intimacy_down : R.string.sort_intimacy_up);
    }
}
