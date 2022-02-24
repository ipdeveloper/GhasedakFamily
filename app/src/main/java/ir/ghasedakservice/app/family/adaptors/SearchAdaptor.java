package ir.ghasedakservice.app.family.adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Vector;

import ir.ghasedakservice.app.family.GhasedakApplication;
import ir.ghasedakservice.app.family.R;
import ir.ghasedakservice.app.family.models.AddressLocation;
import ir.ghasedakservice.app.family.models.School;
import ir.ghasedakservice.app.family.models.SearchAddress;

public class SearchAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private final int SCHOOL=0;
    private final int ADDRESS=1;
    private Vector<School> schools;
    private Vector<SearchAddress> address;
    private int count=0;
    private int searchType=ADDRESS;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public SearchAdaptor(Context context){
        this.context=context;
    }

    public void setAddress(Vector<SearchAddress> address) {
        this.address = address;
        count=address.size();
        searchType=ADDRESS;
    }

    public void setSchools(Vector<School> schools) {
        this.schools = schools;
        count=schools.size();
        searchType=SCHOOL;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onClickListener(Object o);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_row, viewGroup, false);
        return new SearchRowHolder(view);
    }
    SearchRowHolder view;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder searchRowHolder, int i) {
        view = (SearchRowHolder) searchRowHolder;
        if(searchType==SCHOOL && schools!=null){
            view.icon.setImageResource(R.drawable.ic_search_location);
            view.name.setText(schools.get(i).name);
            view.details.setText(schools.get(i).address);
        }else if(searchType==ADDRESS && address!=null){
            view.icon.setImageResource(R.drawable.ic_search_location);
            view.name.setText(address.get(i).name);
            view.details.setText(" ");
        }
        view.mainLayout.setTag(i);
        view.mainLayout.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public void onClick(View v) {
        int i=(int)v.getTag();
        switch (searchType){
            case SCHOOL:
                onItemClickListener.onClickListener(schools.get(i));
                break;
            case ADDRESS:
                onItemClickListener.onClickListener((address.get(i)));
                break;
        }
    }


    /**
     * view holder
     */
    class SearchRowHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView name;
        public TextView details;
        public RelativeLayout mainLayout;

        SearchRowHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_of_search);
            name = itemView.findViewById(R.id.school_or_street_name);
            details = itemView.findViewById(R.id.school_or_street_details);
            mainLayout=itemView.findViewById(R.id.main_layout);

        }
    }
}
