package cn.luo.yuan.maze.display.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.StringAdapter;
import cn.luo.yuan.maze.display.dialog.GiftDialog;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.persistence.IndexManager;
import cn.luo.yuan.maze.utils.LogHelper;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class SelectedActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{
    private IndexManager indexManager;
    private StringAdapter<HeroIndex> adapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_index);
        Resource.init(this);
        LogHelper.initLogSystem(this);
        indexManager = new IndexManager(this);
        final List<HeroIndex>  indexList = indexManager.getIndex();
        adapter = new StringAdapter<>(indexList);
        adapter.setOnClickListener(this);
        adapter.setOnLongClickListener(this);
        ((ListView)findViewById(R.id.selected_hero_index)).setAdapter(adapter);
        findViewById(R.id.new_index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化Hero
                final int index = indexList.size() + indexList.hashCode();
                final DataManager dataManager = new DataManager(index, SelectedActivity.this);
                final Hero hero = dataManager.loadHero();
                final Maze maze = dataManager.loadMaze();
                final AlertDialog dialog = new AlertDialog.Builder(SelectedActivity.this).create();
                dialog.setTitle(Resource.getString(R.string.name_input));
                dialog.setView(View.inflate(SelectedActivity.this,R.layout.init_hero, null));
                dialog.show();
                dialog.setCancelable(false);
                Spinner element = (Spinner) dialog.findViewById(R.id.select_element);
                ArrayAdapter<Element> fa = new ArrayAdapter<>(SelectedActivity.this, android.R.layout.simple_spinner_item, Arrays.asList(Element.values()));
                element.setAdapter(fa);
                Spinner race = (Spinner) dialog.findViewById(R.id.select_race);
                ArrayAdapter<Race> ra = new ArrayAdapter<>(SelectedActivity.this, android.R.layout.simple_spinner_item, Arrays.asList(Race.values()));
                race.setAdapter(ra);
                DatePicker birthday = (DatePicker)dialog.findViewById(R.id.select_birthday);
                GregorianCalendar maxDate = new GregorianCalendar(2010, 1, 1);
                birthday.setMaxDate(maxDate.getTimeInMillis());
                final Button gift = (Button) dialog.findViewById(R.id.select_gift);
                gift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new GiftDialog(SelectedActivity.this, hero, new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                gift.setText(hero.getGift().getName());
                            }
                        }).show();
                    }
                });
                dialog.findViewById(R.id.init_hero).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hero.setName(((EditText)dialog.findViewById(R.id.hero_name)).getText().toString());
                        DatePicker picker = (DatePicker) dialog.findViewById(R.id.select_birthday);
                        GregorianCalendar calendar = new GregorianCalendar(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                        hero.setBirthDay(calendar.getTimeInMillis());
                        Spinner element = (Spinner) dialog.findViewById(R.id.select_element);
                        hero.setElement((Element) element.getSelectedItem());
                        Spinner race = (Spinner) dialog.findViewById(R.id.select_race);
                        hero.setRace(((Race) race.getSelectedItem()).ordinal());
                        if(!StringUtils.isNotEmpty(hero.getName()) || hero.getGift() == null){
                            dialog.show();
                        }else{
                            dataManager.saveHero(hero);
                            dataManager.saveMaze(maze);
                            dialog.dismiss();
                            openGameView(index);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag(R.string.item);
        if(o instanceof HeroIndex){
            openGameView(((HeroIndex) o).getIndex());
        }
    }

    private void openGameView(int index){
        Intent gameIntent = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        gameIntent.putExtras(bundle);
        startActivity(gameIntent);
        finish();
    }

    @Override
    public boolean onLongClick(View v) {
        final Object index = v.getTag(R.string.item);
        if(index instanceof HeroIndex) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(Resource.getString(R.string.delete_index) + ((HeroIndex) index).getName());
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataManager dataManager = new DataManager(((HeroIndex) index).getIndex(), SelectedActivity.this);
                    dataManager.clean();
                    reload();
                }
            });
            dialog.show();
        }
        return false;
    }

    private void reload() {
        adapter.setData(indexManager.getIndex());
        adapter.notifyDataSetChanged();
    }
}
