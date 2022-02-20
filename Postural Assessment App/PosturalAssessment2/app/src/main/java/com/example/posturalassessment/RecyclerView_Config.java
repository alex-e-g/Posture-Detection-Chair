package com.example.posturalassessment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerView_Config {
    private Context mContext;
    private LoadCellAdapter mLoadCellAdapter;
    public void SetConfig(RecyclerView recyclerView, Context context, List<LoadCell> loadCells, List<String> keys){
        mContext = context;
        mLoadCellAdapter = new LoadCellAdapter(loadCells, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mLoadCellAdapter);
    }

    class LoadCellItemView extends RecyclerView.ViewHolder{
        private TextView mTitle;

        private String key;

        public LoadCellItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).
                    inflate(R.layout.load_cell_list_item, parent, false));

            mTitle = (TextView) itemView.findViewById(R.id.textView);
        }

        public void bind(LoadCell loadCell, String key){
            mTitle.setText(String.valueOf((loadCell.getCell_F())));
            this.key = key;
        }
    }

    class LoadCellAdapter extends RecyclerView.Adapter<LoadCellItemView>{
        private List<LoadCell> mLoadCell;
        private List<String> mkeys;

        public LoadCellAdapter(List<LoadCell> mLoadCell, List<String> mkeys) {
            this.mLoadCell = mLoadCell;
            this.mkeys = mkeys;
        }

        @NonNull

        @Override
        public LoadCellItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LoadCellItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView_Config.LoadCellItemView holder, int position) {
            holder.bind(mLoadCell.get(position), mkeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mLoadCell.size();
        }
    }
}
