package cn.luo.yuan.maze.client.display.handler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.GameActivity;
import cn.luo.yuan.maze.client.display.activity.ImageActivity;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.dialog.GiftDialog;
import cn.luo.yuan.maze.client.display.dialog.MessageDialog;
import cn.luo.yuan.maze.client.display.dialog.RealBattleDialog;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.LocalRealTimeManager;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ClientPetMonsterHelper;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.SDFileUtils;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.LevelRecord;
import cn.luo.yuan.maze.model.NPCLevelRecord;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.UpgradeAble;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.model.task.Task;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class GameActivityViewHandler extends Handler {
    private GameActivity context;
    private NeverEnd neverEnd;
    private Runnable refreshSkillTask = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            clickSkill(hero);
            heroSkill(hero);

        }
    };

    public void disableInvincible() {
        post(new Runnable() {
            @Override
            public void run() {
                context.findViewById(R.id.show_invicible).setVisibility(View.INVISIBLE);
            }
        });
    }
    public void disableWeakling() {
        disableInvincible();
    }

    public void showInvincible() {
        post(new Runnable() {
            @Override
            public void run() {
                View vicible = context.findViewById(R.id.show_invicible);
                ((ImageView)vicible).setImageResource(android.R.drawable.btn_star_big_on);
                vicible.setVisibility(View.VISIBLE);
            }
        });

    }

    public void showWeakling() {
        post(new Runnable() {
            @Override
            public void run() {
                View invicible = context.findViewById(R.id.show_invicible);
                ((ImageView)invicible).setImageResource(android.R.drawable.btn_star_big_off);
                invicible.setVisibility(View.VISIBLE);
            }
        });

    }

    public void showZoarium(final int index) {
        post(new Runnable() {
            @Override
            public void run() {
                ImageView zoarium = (ImageView) context.findViewById(R.id.show_zoaroum);
                zoarium.setVisibility(View.VISIBLE);
                zoarium.setImageDrawable(Resource.loadMonsterImage(index));
            }
        });
    }

    public void hideZoarium(){
        post(new Runnable() {
            @Override
            public void run() {
                ImageView zoarium = (ImageView) context.findViewById(R.id.show_zoaroum);
                zoarium.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void updateSkillButton(Button button, Skill skill){
        Skill skillE = (Skill)button.getTag(R.string.item);
        if(skillE == null || !skillE.getId().equals(skill.getId())){
            button.setBackground(Resource.getSkillDrawable(skill));
            button.setTag(R.string.item, skill);
        }
        if(skill instanceof UpgradeAble){
            button.setText(String.format("X%d", ((UpgradeAble)skill).getLevel()));
        }else{
            button.setText(Resource.getString(R.string.empty));
        }
    }

    private void resetSkillButton(Button button){
        button.setTag(R.string.item, null);
        button.setBackgroundResource(0);
        button.setText(Resource.getString(R.string.not_mount));
    }

    private void heroSkill(Hero hero) {
        Skill[] heroSkills = hero.getSkills();
        Button first = (Button) context.findViewById(R.id.first_skill);
        if (heroSkills.length > 0 && heroSkills[0] != null && !(heroSkills[0] instanceof EmptySkill)) {
            updateSkillButton(first,heroSkills[0]);
        } else {
            resetSkillButton(first);
        }
        Button second = (Button) context.findViewById(R.id.secondary_skill);
        if (heroSkills.length > 1 && heroSkills[1] != null && !(heroSkills[1] instanceof EmptySkill)) {
            updateSkillButton(second, heroSkills[1]);
        } else {
            resetSkillButton(second);
        }
        Button third = (Button) context.findViewById(R.id.third_skill);
        if (heroSkills.length > 2 && heroSkills[2] != null && !(heroSkills[2] instanceof EmptySkill)) {
            updateSkillButton(third,heroSkills[2]);
        } else {
            resetSkillButton(third);
        }
        Button  fourth = (Button) context.findViewById(R.id.fourth_skill);
        if (heroSkills.length > 3 && heroSkills[3] != null && !(heroSkills[3] instanceof EmptySkill)) {
            updateSkillButton(fourth, heroSkills[3]);
        } else {
            resetSkillButton(fourth);
            if (hero.getReincarnate() >= 2 && heroSkills.length > 3) {
                fourth.setText(R.string.not_mount);
                fourth.setEnabled(true);
            } else {
                fourth.setText(R.string.fourth_skill_enable);
               fourth.setEnabled(false);
            }
        }
        Button fifth = (Button) context.findViewById(R.id.fifit_skill);
        if (heroSkills.length > 4 && heroSkills[4] != null && !(heroSkills[4] instanceof EmptySkill)) {
            updateSkillButton(fifth, heroSkills[4]);
        } else {
            resetSkillButton(fifth);
            if (hero.getReincarnate() >= 4  && heroSkills.length > 4) {
                fifth.setText(R.string.not_mount);
                fifth.setEnabled(true);
            } else {
                fifth.setText(R.string.fifth_skill_enable);
                fifth.setEnabled(false);
            }
        }
        Button sixth = (Button) context.findViewById(R.id.sixth_skill);
        if (heroSkills.length > 5 && heroSkills[5] != null && !(heroSkills[5] instanceof EmptySkill)) {
            updateSkillButton(sixth, heroSkills[5]);
        } else {
            resetSkillButton(sixth);
            if (hero.getReincarnate() >= 8  && heroSkills.length > 5) {
                sixth.setText(R.string.not_mount);
                sixth.setEnabled(true);
            } else {
                sixth.setText(R.string.sixth_skill_enable);
                sixth.setEnabled(false);
            }
        }
    }

    private void clickSkill(Hero hero) {
        ArrayList<ClickSkill> skills = hero.getClickSkills();
        Button first = (Button) context.findViewById(R.id.first_click_skill);
        first.setEnabled(false);
        Button second = (Button) context.findViewById(R.id.second_click_skill);
        second.setEnabled(false);
        Button third = (Button) context.findViewById(R.id.third_click_skill);
        third.setEnabled(false);
        if (skills.size() > 0) {
            first.setBackgroundResource(skills.get(0).getImageResource());
            first.setText(skills.get(0).getName());
            if (skills.get(0).isUsable()) {
                first.setEnabled(true);
            }
        }else{
            first.setEnabled(false);
            first.setBackgroundResource(0);
        }
        if (skills.size() > 1) {
            second.setBackgroundResource(skills.get(1).getImageResource());
            second.setText(skills.get(1).getName());
            if (skills.get(1).isUsable()) {
                second.setEnabled(true);
            }
        }else{
            second.setEnabled(false);
            second.setBackgroundResource(0);
        }
        if (skills.size() > 2) {
            third.setBackgroundResource(skills.get(2).getImageResource());
            third.setText(skills.get(2).getName());
            if (skills.get(2).isUsable()) {
                third.setEnabled(true);
            }
        }else{
            third.setEnabled(false);
            third.setBackgroundResource(0);
        }
    }

    private Runnable refreshFreqPrepertiesTask = new Runnable() {
        @Override
        public void run() {
            //Hero properties
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_level), StringUtils.formatNumber(context.control.getMaze().getLevel(), true));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_level_max), StringUtils.formatNumber(context.control.getMaze().getMaxLevel(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_mate),StringUtils.formatNumber(context.control.getHero().getMaterial(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_point),StringUtils.formatNumber(context.control.getHero().getPoint(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_click),StringUtils.formatNumber(context.control.getHero().getClick(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_streaking),StringUtils.formatNumber(context.control.getMaze().getStreaking(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_die_count),StringUtils.formatNumber(context.control.getMaze().getDie(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_hp),StringUtils.formatNumber(context.control.getHero().getCurrentHp(), false));
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_max_hp),StringUtils.formatNumber(context.control.getHero().getMaxHp(), false));
            TextView additionHp = (TextView) context.findViewById(R.id.hero_addition_hp);
            long additionHpValue = context.control.getHero().getAdditionHp();
            setNumberText(additionHp, additionHpValue);
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_atk),StringUtils.formatNumber(context.control.getHero().getAtk(), false));
            setNumberText((TextView) context.findViewById(R.id.hero_atk_addition), context.control.getHero().getAdditionAtk());
            ViewHandler.setText((TextView) context.findViewById(R.id.hero_def),StringUtils.formatNumber(context.control.getHero().getDef(), false));
            setNumberText((TextView) context.findViewById(R.id.hero_def_addition),context.control.getHero().getAdditionDef());
            View view = context.findViewById(R.id.monster_view);
            if(view.getVisibility() == View.VISIBLE){
                ViewHandler.setText((TextView) view.findViewById(R.id.local_monster_percent), neverEnd.getPetMonsterHelper().getLocalCatchPercent());
                ViewHandler.setText((TextView) view.findViewById(R.id.global_monster_percent), neverEnd.getPetMonsterHelper().getGlobalCatchPercent());
            }
            if(neverEnd.getMaze().getDie() > 2000){
                context.findViewById(R.id.into_invicible).setVisibility(View.VISIBLE);
            }
        }
    };

    private void setNumberText(TextView textView, long value) {
        if(value >= 0) {
            ViewHandler.setText(textView, " + " + StringUtils.formatNumber(value, false));
            textView.setTextColor(Color.BLUE);
        }else{
            ViewHandler.setText(textView, StringUtils.formatNumber(value, false));
            textView.setTextColor(context.getResources().getColor(R.color.mobvista_reward_green));
        }
    }

    private Runnable refreshPetTask = new Runnable() {
        @Override
        public void run() {
            Hero hero = neverEnd.getHero();
            ArrayList<Pet> pets = new ArrayList<>(hero.getPets());
            ImageView first = (ImageView)context.findViewById(R.id.pet_1);
            ImageView second = (ImageView)context.findViewById(R.id.pet_2);
            ImageView third = (ImageView)context.findViewById(R.id.pet_3);
            if(pets.size() > 0){
                first.setVisibility(View.VISIBLE);
                Pet pet = pets.get(0);
                updatePetView(pet, first);
            }else{
                resetPetButton(first);
            }
            if(pets.size() > 1){
                second.setVisibility(View.VISIBLE);
                Pet pet = pets.get(1);
                updatePetView(pet, second);
            }else{
               resetPetButton(second);
            }
            if(pets.size() > 2){
                third.setVisibility(View.VISIBLE);
                Pet pet = pets.get(2);
                updatePetView(pet, third);
            }else{
                resetPetButton(third);
            }
            ((Button)context.findViewById(R.id.more_pet)).setText(String.format("%d+",pets.size()));
        }
    };

    private void resetPetButton(ImageView first) {
        first.setTag(R.string.item, (Pet)null);
        first.setImageResource(0);
        first.setVisibility(View.INVISIBLE);
    }

    private void updatePetView(Pet pet, ImageView petView) {
        if(pet!=null){
            boolean notChange = false;
            Pet vp = (Pet)petView.getTag(R.string.item);
            Drawable drawable;
            if(vp == null || !vp.getId().equals(pet.getId())) {
                drawable = ClientPetMonsterHelper.loadMonsterImage(pet.getIndex());
            }else{
                drawable = petView.getDrawable();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if((pet.getHp() > 0 && drawable.getColorFilter() == null) || (pet.getHp() <= 0 && drawable.getColorFilter()!=null)){
                        //宠物状态没有变化
                        notChange = true;
                    }
                }
            }
            if(!notChange) {
                petView.setImageDrawable(drawable);
                if (pet.getHp() > 0) {
                    drawable.clearColorFilter();
                } else {
                    drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        petView.setTag(R.string.item, pet);
    }

    private Runnable refreshPropertiesTask = new Runnable() {
        @Override
        public void run() {
            ((TextView) context.findViewById(R.id.hero_name)).setText(Html.fromHtml(context.control.getHero().getDisplayName()));
            if(context.control.getHero().getGift()!=null) {
                ((TextView) context.findViewById(R.id.hero_gift)).setText(Html.fromHtml(context.control.getHero().getGift().getName()));
            }else{
                ((TextView) context.findViewById(R.id.hero_gift)).setText(StringUtils.EMPTY_STRING);
            }
        }
    };;

    public GameActivityViewHandler(GameActivity activity, NeverEnd context) {
        this.context = activity;
        this.neverEnd = context;
    }

    public void showStartTip(){
        List<String> msg = Arrays.asList(Resource.readStringFromAssets("help", "start_tip").split("<br>"));
        MessageDialog dialog = new MessageDialog(context, msg);
        dialog.show();
    }

    public void refreshHeadImage(final NeverEndConfig config) {
        post(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isNotEmpty(config.getHead())) {
                    Drawable bitmap = Resource.loadImageFromAssets(config.getHead(), false);
                    if (bitmap != null) {
                        ((ImageView) context.findViewById(R.id.hero_pic)).setImageDrawable(bitmap);
                    }
                }
            }
        });
    }

    public void refreshSkill() {
        post(refreshSkillTask);
    }

    public void refreshProperties(final Hero hero) {
        post(refreshPropertiesTask);
    }

    //刷新变化频繁的属性
    public void refreshFreqProperties() {
        post(refreshFreqPrepertiesTask);

    }

    public void refreshPets(final Hero hero) {

        post(refreshPetTask);

    }

    public void reCreate() {
        post(new Runnable() {
            @Override
            public void run() {
                context.recreate();
            }
        });
    }

    public void showGiftChoose() {
        post(new Runnable() {
            @Override
            public void run() {
                new GiftDialog(context, neverEnd.getHero(), new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (neverEnd.getHero().getGift() != null) {
                            try {
                                neverEnd.getHero().getGift().handler(neverEnd);
                            } catch (Exception e) {
                                LogHelper.logException(e, "GameActivityViewHandler -> chooseGift -> ");
                            }
                        }
                        refreshProperties(neverEnd.getHero());
                    }
                }).show();
            }
        });
    }

    private void refreshDieMessage(){
        if(!SDFileUtils.getFilesListFromSD(String.valueOf(neverEnd.getIndex())).isEmpty()){
            post(new Runnable() {
                @Override
                public void run() {
                    context.findViewById(R.id.die_msg_button).setVisibility(View.VISIBLE);
                }
            });
        }else{
            post(new Runnable() {
                @Override
                public void run() {
                    context.findViewById(R.id.die_msg_button).setVisibility(View.GONE);
                }
            });
        }
    }

    public void addDieMessage(List<String> msgs){
        if(msgs == null || msgs.size() == 0){
            return ;
        }
        try {
            String folder = String.valueOf(neverEnd.getIndex());
            List<String> list = SDFileUtils.getFilesListFromSDWithOrder(folder);
            if (list.size() > 5) {
                SDFileUtils.deleteFile(folder, list.get(list.size() - 1));
            }
            if(list.size() > 10){
                return ;
            }
            String title = StringUtils.getCurrentTime().replaceAll(":", "-");
            StringBuilder builder = new StringBuilder(title).append(": <br>");
            for (String s : msgs) {
                builder.append(s).append("<br>");
            }
            SDFileUtils.saveStringIntoSD(folder, title, builder.toString());
            refreshDieMessage();
        }catch (Exception e){
            LogHelper.logException(e, "LogDie");
        }
    }

    public void showDieList(){
        post(new Runnable() {
            @Override
            public void run() {
                ListView listView = new ListView(context);
                final StringAdapter<String> adapter = new StringAdapter<>(SDFileUtils.getFilesListFromSDWithOrder(String.valueOf(neverEnd.getIndex())));
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        neverEnd.getExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                Object o = v.getTag(R.string.item);
                                final String s = SDFileUtils.readStringFromSD(String.valueOf(neverEnd.getIndex()),o.toString());
                                SDFileUtils.deleteFile(String.valueOf(neverEnd.getIndex()), o.toString());
                                adapter.getData().remove(o.toString());
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        SimplerDialogBuilder.build(s, Resource.getString(R.string.close), context, null);
                                    }
                                });
                                refreshDieMessage();
                            }
                        });

                    }
                });
                listView.setAdapter(adapter);
                SimplerDialogBuilder.build(listView, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, Resource.getString(R.string.clear), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!SDFileUtils.deleteFolder(String.valueOf(neverEnd.getIndex()))){
                            neverEnd.showPopup("删除文件失败，您可以手动删除SD卡neverend目录下的" + neverEnd.getIndex() + "文件夹！");
                        }
                        refreshDieMessage();
                        dialog.dismiss();
                        if(adapter.getCount() > 0 && neverEnd.getRandom().nextBoolean()) {
                            neverEnd.getHero().setPoint(neverEnd.getHero().getPoint() + adapter.getCount());
                            neverEnd.showToast("获得了%d点能力点", adapter.getCount());
                        }
                    }
                },context);
            }
        });

    }

    public void showNPCIcon(final NPCLevelRecord npcLevelRecord){
        post(new Runnable() {
            @Override
            public void run(){
                View npc = context.findViewById(R.id.npc_button);
                npc.setVisibility(View.VISIBLE);
                npc.setTag(npcLevelRecord);
            }
        });
    }

    public void hidNPCIcon(){
        post(new Runnable() {
            @Override
            public void run(){
                View npc = context.findViewById(R.id.npc_button);
                npc.setVisibility(View.INVISIBLE);
                npc.setTag(null);
            }
        });
    }

    public void showNPCDialog(){
        final NPCLevelRecord record = (NPCLevelRecord) context.findViewById(R.id.npc_button).getTag();
        if(record!=null) {
            AlertDialog dialog = SimplerDialogBuilder.build(Resource.getString(
                    R.string.npc_tip, record.getSex() == 0 ? Resource.getString(R.string.npc_man) : Resource.getString(R.string.npc_gril)),
                    Resource.getString(R.string.npc_accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            hidNPCIcon();
                            neverEnd.getHero().setMaterial(neverEnd.getHero().getMaterial() - 1000);
                            if(record.getMessage().isEmpty()){
                                npcbattle(record);
                            }else {
                                MessageDialog msgD = new MessageDialog(context, record.getMessage());
                                msgD.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        npcbattle(record);
                                    }
                                });
                                msgD.setPic(Resource.loadImageFromAssets(record.getHead(), true));
                                msgD.setPosition(Gravity.CENTER_VERTICAL);
                                msgD.show();
                            }
                        }
                    }, Resource.getString(R.string.npc_refuse), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            hidNPCIcon();
                            dialog.dismiss();
                        }
                    }, context);
            Button pb = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if(pb!=null && neverEnd.getHero().getMaterial() < 1000){
                pb.setEnabled(false);
            }
        }
    }

    private void npcbattle(NPCLevelRecord record) {
        Hero hero = neverEnd.getHero().clone();
        hero.setHp(hero.getMaxHp());
        LevelRecord lr = new LevelRecord(hero);
        final LocalRealTimeManager manager = new LocalRealTimeManager(neverEnd, record.getHero());
        manager.setTargetRecord(record);
        post(new Runnable() {
            @Override
            public void run() {
                new RealBattleDialog(manager, neverEnd, "local");
            }
        });
    }

    public void showFirstInTip(){
        post(new Runnable() {
            @Override
            public void run() {
                Intent gameIntent = new Intent(context, ImageActivity.class);
                context.startActivity(gameIntent);
            }
        });
    }

    public void showTasks(){
        LinearLayout linearLayout = new LinearLayout(context);
        Button notStartTask= new Button(context);
        linearLayout.addView(notStartTask);
        Button startingTask = new Button(context);
        linearLayout.addView(startingTask);
        Button canStartTask = new Button(context);
        linearLayout.addView(canStartTask);
        Button finshedTask = new Button(context);
        linearLayout.addView(finshedTask);
        final Dialog dialog = SimplerDialogBuilder.build(linearLayout, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, context, false);
        notStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Task> tasks = neverEnd.getTaskManager().queryCanStartTask(0, 50);
                final LoadMoreListView list = new LoadMoreListView(context);
                final StringAdapter<Task> adapter = new StringAdapter<Task>(tasks);
                list.setAdapter(adapter);
                list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreListView loadMoreListView) {
                        List<Task> tasks = neverEnd.getTaskManager().queryCanStartTask(adapter.getCount(), adapter.getCount());
                        if(!tasks.isEmpty()){
                            adapter.addAll(tasks);
                        }
                    }
                });
            }
        });
        startingTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Task> tasks = neverEnd.getTaskManager().queryProgressTask(0, 50);
                final LoadMoreListView list = new LoadMoreListView(context);
                final StringAdapter<Task> adapter = new StringAdapter<Task>(tasks);
                list.setAdapter(adapter);
                list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreListView loadMoreListView) {
                        List<Task> tasks = neverEnd.getTaskManager().queryProgressTask(adapter.getCount(), adapter.getCount());
                        if(!tasks.isEmpty()){
                            adapter.addAll(tasks);
                        }
                    }
                });
            }
        });
        finshedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Task> tasks = neverEnd.getTaskManager().queryFinishedTask(0, 50);
                final LoadMoreListView list = new LoadMoreListView(context);
                final StringAdapter<Task> adapter = new StringAdapter<Task>(tasks);
                list.setAdapter(adapter);
                list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                    @Override
                    public void onLoadMore(LoadMoreListView loadMoreListView) {
                        List<Task> tasks = neverEnd.getTaskManager().queryFinishedTask(adapter.getCount(), adapter.getCount());
                        if(!tasks.isEmpty()){
                            adapter.addAll(tasks);
                        }
                    }
                });
            }
        });
    }
}

