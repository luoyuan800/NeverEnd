package cn.luo.yuan.maze.display.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.Race;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.PetMonsterLoder;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luoyuan on 2017/5/11.
 */
public class PetAdapter extends BaseAdapter implements LoadMoreListView.OnRefreshLoadingMoreListener {
    private List<Pet> pets;
    private DataManager dataManager;
    private Context context;
    private boolean sortOderRevert;
    private String limitKeyWord;
    public PetAdapter(Context context, DataManager dataManager, String limitKeyWord){
        this.context = context;
        this.dataManager = dataManager;
        this.limitKeyWord = limitKeyWord;
        loadPetsData();
    }

    private  void loadPetsData(){
        pets = dataManager.loadPets(0,20, limitKeyWord);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return pets != null ? pets.size() : 0;
    }

    @Override
    public Pet getItem(int position) {
        if (position >= getCount()) position = 0;
        return pets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.pet_list_view, null);
            convertView.setTag(position);
        }
        if(!convertView.getTag().equals(position)){
            Pet pet = getItem(position);
            ((TextView)convertView.findViewById(R.id.pet_name)).setText(Html.fromHtml(pet.getDisplayName() + (pet.isMounted()? "√":"")));
            ((ImageView)convertView.findViewById(R.id.pet_image)).setImageDrawable(PetMonsterLoder.loadMonsterImage(pet.getIndex()));
            ((TextView)convertView.findViewById(R.id.pet_level)).setText(StringUtils.formatStar(pet.getLevel()));
            ((TextView)convertView.findViewById(R.id.pet_tag)).setText(pet.getTag());
        }

        return convertView;
    }

    @Override
    public void onLoadMore(LoadMoreListView loadMoreListView) {
        List<Pet> loadPets = dataManager.loadPets(pets.size(), 20, "");
        if(loadPets.size() == 0){
            loadMoreListView.onLoadMoreComplete(true);
        }else {
            pets.addAll(loadPets);
            loadMoreListView.onLoadMoreComplete(false);
        }
    }

    private final Comparator<Pet> indexCompare = new Comparator<Pet>() {
        @Override
        public int compare(Pet lhs, Pet rhs) {
            if (lhs.getIndex() == rhs.getIndex()) return 0;
            return sortOderRevert ? (lhs.getIndex() > rhs.getIndex() ? -1 : 1) : (lhs.getIndex() > rhs.getIndex() ? 1 : -1);
        }
    };
    private final Comparator<Pet> nameComparator = new Comparator<Pet>() {
        @Override
        public int compare(Pet lhs, Pet rhs) {
            return sortOderRevert ? lhs.getName().compareTo(rhs.getName()) : rhs.getName().compareTo(lhs.getName());
        }
    };
    private final Comparator<Pet> intimacyComparator = new Comparator<Pet>() {
        @Override
        public int compare(Pet lhs, Pet rhs) {
            if (lhs.getIntimacy() == rhs.getIntimacy())
                return 0;
            return sortOderRevert ? (lhs.getIntimacy() > rhs.getIntimacy() ? -1 : 1) : (lhs.getIntimacy() > rhs.getIntimacy() ? 1 : -1);
        }
    };

    public void sort(int type, List<Pet> adapterData){
        switch (type){
            case 0: //index

                Collections.sort(adapterData, indexCompare);
                break;
            case 1: //name
                Collections.sort(adapterData, nameComparator);
                break;
            default://亲密度
                Collections.sort(adapterData, intimacyComparator);
        }
        sortOderRevert = !sortOderRevert;
        notifyDataSetChanged();
    }

    public String getLimitKeyWord() {
        return limitKeyWord;
    }

    public void setLimitKeyWord(String limitKeyWord) {
        this.limitKeyWord = limitKeyWord;
        loadPetsData();
    }

    public void removePet(Pet pet){
        pets.remove(pet);
    }
}
