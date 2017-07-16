package cn.luo.yuan.maze.client.display.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.PetMonsterLoder;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luoyuan on 2017/5/11.
 */
public class PetAdapter extends BaseAdapter implements LoadMoreListView.OnRefreshLoadingMoreListener {
    public static final int SORT_COLOR = 2, SORT_INDEX = 0, SORT_NAME = 1, SORT_INTIMACY = -1;
    private List<Pet> pets;
    private DataManager dataManager;
    private Context context;
    private boolean sortOderRevert;
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
    private final Comparator<Pet> colorComparator = new Comparator<Pet>() {
        @Override
        public int compare(Pet lhs, Pet rhs) {
            if (lhs.getColor().equalsIgnoreCase(rhs.getColor()))
                return 0;
            if (lhs.getColor().equalsIgnoreCase(Data.DEFAULT_QUALITY_COLOR)) {
                return sortOderRevert ? 1 : -1;
            }
            if (lhs.getColor().equalsIgnoreCase(Data.BLUE_COLOR) && !rhs.getColor().equalsIgnoreCase(Data.DEFAULT_QUALITY_COLOR)) {
                return sortOderRevert ? 1 : -1;
            }
            if (lhs.getColor().equalsIgnoreCase(Data.RED_COLOR) && !rhs.getColor().equalsIgnoreCase(Data.DEFAULT_QUALITY_COLOR) && !rhs.getColor().equalsIgnoreCase(Data.BLUE_COLOR)) {
                return sortOderRevert ? 1 : -1;
            }
            if (lhs.getColor().equalsIgnoreCase(Data.ORANGE_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.DEFAULT_QUALITY_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.BLUE_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.RED_COLOR)) {
                return sortOderRevert ? 1 : -1;
            }
            if (lhs.getColor().equalsIgnoreCase(Data.DARKGOLD_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.DEFAULT_QUALITY_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.BLUE_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.ORANGE_COLOR) &&
                    !rhs.getColor().equalsIgnoreCase(Data.RED_COLOR)) {
                return sortOderRevert ? 1 : -1;
            }
            return sortOderRevert ? -1 : 1;
        }
    };
    private int sortType;
    private String limitKeyWord;
    public PetAdapter(Context context, DataManager dataManager, String limitKeyWord) {
        this.context = context;
        this.dataManager = dataManager;
        this.limitKeyWord = limitKeyWord;
        loadPetsData();
    }

    public void addPets(List<Pet> pets) {
        for (Pet pet : pets) {
            if (!this.pets.contains(pet)) {
                this.pets.add(pet);
            }
        }
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

        }
        Pet pet = getItem(position);
        ((TextView) convertView.findViewById(R.id.pet_name)).setText(Html.fromHtml(pet.getDisplayNameWithLevel() + (pet.isMounted() ? "√" : "")));
        ((ImageView) convertView.findViewById(R.id.pet_image)).setImageDrawable(PetMonsterLoder.loadMonsterImage(pet.getIndex()));
        ((TextView) convertView.findViewById(R.id.pet_tag)).setText(pet.getTag());
        convertView.setTag(R.string.adapter, this);
        convertView.setTag(R.string.item, pet);
        convertView.setTag(R.string.position, position);
        return convertView;
    }

    @Override
    public void onLoadMore(LoadMoreListView loadMoreListView) {
        List<Pet> loadPets = dataManager.loadPets(pets.size(), 20, limitKeyWord, getSort());
        if (loadPets.size() == 0) {
            loadMoreListView.onLoadMoreComplete(true);
        } else {
            addPets(loadPets);
            loadMoreListView.onLoadMoreComplete(false);
        }
        notifyDataSetChanged();
    }

    public Comparator<Pet> getSort() {
        switch (sortType) {
            case 0: //index
                return indexCompare;
            case 1: //name
                return nameComparator;
            case 2://color
                return colorComparator;
            default://亲密度
                return intimacyComparator;
        }
    }

    public String getLimitKeyWord() {
        return limitKeyWord;
    }

    public void setLimitKeyWord(String limitKeyWord) {
        this.limitKeyWord = limitKeyWord;
        ArrayList<Pet> pets = new ArrayList<>(this.pets);
        for(Pet pet : pets){
            if(!pet.getName().contains(limitKeyWord) && !(StringUtils.isNotEmpty(pet.getTag()) && pet.getTag().contains(limitKeyWord))){
                this.pets.remove(pet);
            }
        }
        if(pets.size() == 0){
            loadPetsData();
        }
        notifyDataSetChanged();
    }

    public void removePet(Pet pet) {
        pets.remove(pet);
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int type) {
        if (type == sortType) {
            sortOderRevert = !sortOderRevert;
        }
        this.sortType = type;
        loadPetsData();
    }

    public boolean getSortOrderRevert() {
        return sortOderRevert;
    }

    private void loadPetsData() {
        pets = dataManager.loadPets(0, 20, limitKeyWord, getSort());
        notifyDataSetChanged();
    }
}
