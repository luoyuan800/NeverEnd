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
import cn.luo.yuan.maze.client.display.handler.ViewHandler;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Egg;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.gift.Epicure;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.types.Evolution;
import cn.luo.yuan.maze.model.goods.types.Grill;
import cn.luo.yuan.maze.model.goods.types.Omelet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.service.PetMonsterHelper;
import cn.luo.yuan.maze.client.service.ClientPetMonsterHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by gluo on 5/15/2017.
 */
public class PetDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {
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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                control.getViewHandler().refreshPets(control.getHero());
            }
        });
        dialog.show();
        final View detailView = dialog.findViewById(R.id.pet_detail_view);
        detailView.setVisibility(View.INVISIBLE);
        EditText tag = (EditText) detailView.findViewById(R.id.pet_tag);
        tag.addTextChangedListener(this);
        loadMoreListView = (LoadMoreListView) dialog.findViewById(R.id.pet_simple_list);
        loadMoreListView.setAdapter(adapter);
        loadMoreListView.setOnLoadListener(adapter);
        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentPet = (Pet) parent.getItemAtPosition(position);
                        refreshDetailView(detailView);

                    }
                });
            }
        });
        loadMoreListView.initQuery(new LoadMoreListView.OnQueryChange() {
            @Override
            public void onQueryChange(String query) {
                adapter.setLimitKeyWord(query);
            }
        });
        dialog.findViewById(R.id.sort_intimacy).setOnClickListener(this);
        dialog.findViewById(R.id.sort_name).setOnClickListener(this);
        dialog.findViewById(R.id.sort_type).setOnClickListener(this);
        dialog.findViewById(R.id.sort_color).setOnClickListener(this);
        dialog.findViewById(R.id.close).setOnClickListener(this);
        dialog.findViewById(R.id.pet_evolution).setOnClickListener(this);
        View batchDrop = dialog.findViewById(R.id.batch_drop);
        batchDrop.setOnClickListener(this);
        if(control.getHero().getGift() == Gift.Epicure){
            ((Button)batchDrop).setText(Resource.getString(R.string.eqicure_drop_pet));
        }
    }

    public void refreshDetailView(View detailView) {
        if (currentPet != null) {
            EditText tag = (EditText) detailView.findViewById(R.id.pet_tag);
            ((TextView) detailView.findViewById(R.id.pet_name)).setText(Html.fromHtml(currentPet.getDisplayNameWithLevel()));
            CheckBox isMounted = (CheckBox) dialog.findViewById(R.id.pet_mounted);
            isMounted.setOnCheckedChangeListener(null);
            isMounted.setChecked(currentPet.isMounted());
            isMounted.setOnCheckedChangeListener(PetDialog.this);
            ((TextView) detailView.findViewById(R.id.pet_atk)).setText(StringUtils.formatNumber(currentPet.getAtk(), false));
            ((TextView) detailView.findViewById(R.id.pet_def)).setText(StringUtils.formatNumber(currentPet.getDef(), false));
            ((TextView) detailView.findViewById(R.id.pet_hp)).setText(StringUtils.formatNumber(currentPet.getHp(), false) + "/" + StringUtils.formatNumber(currentPet.getMaxHp(), false));
            Skill skill = currentPet.getSkills()[0];
            if(skill!=null) {
                detailView.findViewById(R.id.pet_skill_layout).setVisibility(View.VISIBLE);
                ((TextView) detailView.findViewById(R.id.pet_effect)).setText(Html.fromHtml(StringUtils.formatEffectsAsHtml(currentPet.getContainsEffects())));
            }else{
                detailView.findViewById(R.id.pet_skill_layout).setVisibility(View.INVISIBLE);
            }
            if(currentPet.getContainsEffects()!=null && !currentPet.getContainsEffects().isEmpty()) {
                detailView.findViewById(R.id.pet_effect_layout).setVisibility(View.VISIBLE);
            }else{
                detailView.findViewById(R.id.pet_effect_layout).setVisibility(View.INVISIBLE);
            }
            ViewHandler.setText((TextView) detailView.findViewById(R.id.pet_owner), currentPet.getOwnerName());
            ((TextView) detailView.findViewById(R.id.pet_mother)).setText(Html.fromHtml(currentPet.getMother()));
            ((TextView) detailView.findViewById(R.id.pet_farther)).setText(Html.fromHtml(currentPet.getFarther()));
            ((TextView) detailView.findViewById(R.id.pet_intimacy)).setText(Html.fromHtml(StringUtils.formatIntimacyString(currentPet.getIntimacy())));
            tag.setText(Html.fromHtml(currentPet.getTag()));
            detailView.findViewById(R.id.pet_drop).setOnClickListener(PetDialog.this);
            detailView.findViewById(R.id.pet_upgrade).setOnClickListener(PetDialog.this);
            ((ImageView) detailView.findViewById(R.id.pet_image)).setImageDrawable(ClientPetMonsterHelper.loadMonsterImage(currentPet.getIndex()));
            detailView.setVisibility(View.VISIBLE);
        } else {
            detailView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            final PetMonsterHelper helper = control.getPetMonsterHelper();
            switch (v.getId()) {
                case R.id.batch_drop:
                    final EditText text = new EditText(control.getContext());
                    text.setHint("在此输入关键字，点击确定后名字中包含的该关键字的宠物会被丢弃。出战中或者有备注的宠物不会被丢弃。");
                    SimplerDialogBuilder.build(text, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int petCount = 0;
                            int eggCount = 0;
                            if (StringUtils.isNotEmpty(text.getText().toString())) {
                                for (Pet pet : control.getDataManager().loadPets(0, -1, text.getText().toString(), null)) {
                                    if (!pet.isMounted() && StringUtils.isEmpty(pet.getTag())) {
                                        control.getDataManager().deletePet(pet);
                                        adapter.removePet(pet);
                                        if (pet instanceof Egg && ((Egg) pet).step > 0) {
                                            eggCount++;
                                        } else {
                                            petCount++;
                                        }
                                    }
                                }
                            }
                            if (control.getHero().getGift() == Gift.Epicure) {
                                if(petCount>0) {
                                    Goods grill = new Grill();
                                    grill.setCount(petCount);
                                    control.getDataManager().addGoods(grill);
                                }
                                if(eggCount > 0) {
                                    Omelet omelet = new Omelet();
                                    omelet.setCount(eggCount);
                                    control.getDataManager().addGoods(omelet);
                                }
                                Toast.makeText(control.getContext(), "获得了" + petCount + "个烤肉和" + eggCount + "个煎蛋", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(control.getContext(), "丢弃了" + (petCount + eggCount) + "个宠物（蛋）", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }, Resource.getString(R.string.close), null, control.getContext());
                    break;
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
                        Goods evolution = control.getDataManager().loadGoods(Evolution.class.getSimpleName());
                        if (helper.evolution(currentPet, control.getHero(), evolution)) {
                            control.getDataManager().saveGoods(evolution);
                            control.getDataManager().savePet(currentPet);
                            for (Pet pet : new ArrayList<>(control.getHero().getPets())) {
                                if (pet.isDelete()) {
                                    helper.unMountPet(pet, control.getHero());
                                    control.getDataManager().save(pet);
                                }
                            }
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
                        final AlertDialog select = new AlertDialog.Builder(control.getContext()).setTitle(R.string.select_pet).setView(listView).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Pet minor = (Pet) parent.getItemAtPosition(position);
                                if (minor != null && !minor.isMounted() && !minor.getId().equals(currentPet.getId())) {
                                    control.getHero().setMaterial(control.getHero().getMaterial() - Data.FUSE_COST * currentPet.getLevel());
                                    select.dismiss();
                                    if (helper.upgrade(currentPet, minor)) {
                                        control.getDataManager().save(currentPet);
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
                                    if (currentPet.getUpperAtk() / 10 > control.getHero().getUpperAtk() || currentPet.getUpperHp() / 10 > control.getHero().getUpperHp()) {
                                        currentPet.setIntimacy(currentPet.getIntimacy() - 5);
                                    }
                                    control.getDataManager().deletePet(minor);
                                    adapter.removePet(minor);
                                    adapter.notifyDataSetChanged();
                                    control.getViewHandler().refreshPets(control.getHero());
                                }
                            }
                        });
                        control.getViewHandler().refreshProperties(control.getHero());
                    } else {
                        if (currentPet != null) {
                            new AlertDialog.Builder(control.getContext()).setMessage("需要" + currentPet.getLevel() * Data.FUSE_COST + "点锻造来进行升级").setPositiveButton(R.string.conform, null).show();
                        }
                    }
                    break;
            }
        }catch (Exception e){
            LogHelper.logException(e, "PetDialog click");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && !currentPet.isMounted()) {
            if(!control.getPetMonsterHelper().mountPet(currentPet, control.getHero())){
                buttonView.setChecked(false);
            }
        } else if (!isChecked && currentPet.isMounted()) {
            control.getPetMonsterHelper().unMountPet(currentPet, control.getHero());
        }
        adapter.notifyDataSetChanged();
        control.getDataManager().savePet(currentPet);
        control.getViewHandler().refreshPets(control.getHero());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(currentPet!=null){
            currentPet.setTag(s.toString());
            control.getDataManager().save(currentPet);
            adapter.notifyDataSetChanged();
        }
    }

    private void refreshSortButton() {
        ((Button) dialog.findViewById(R.id.sort_color)).setText(adapter.getSortOrderRevert() ? R.string.sort_color_down : R.string.sort_color_up);
        ((Button) dialog.findViewById(R.id.sort_name)).setText(adapter.getSortOrderRevert() ? R.string.sort_name_down : R.string.sort_name_up);
        ((Button) dialog.findViewById(R.id.sort_type)).setText(adapter.getSortOrderRevert() ? R.string.sort_type_down : R.string.sort_type_up);
        ((Button) dialog.findViewById(R.id.sort_intimacy)).setText(adapter.getSortOrderRevert() ? R.string.sort_intimacy_down : R.string.sort_intimacy_up);
    }
}
