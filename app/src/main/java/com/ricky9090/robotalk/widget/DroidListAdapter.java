package com.ricky9090.robotalk.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ricky9090.robotalk.R;
import com.ricky9090.smallworld.obj.SmallObject;

import java.util.ArrayList;
import java.util.List;

public class DroidListAdapter extends RecyclerView.Adapter<DroidListAdapter.DroidItemViewHolder> {

    private Context mContext;
    private DroidListListener mListener;

    private final List<SmallObject> dataList = new ArrayList<>();

    public DroidListAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public DroidItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item, parent, false);
        DroidItemViewHolder holder = new DroidItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DroidListAdapter.DroidItemViewHolder holder, int position) {
        final SmallObject dataObject = dataList.get(position);
        holder.itemText.setText(dataObject.toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position, dataObject);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<SmallObject> list) {
        if (list != null) {
            dataList.clear();
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setListener(DroidListListener mListener) {
        this.mListener = mListener;
    }

    public interface DroidListListener {
        void onItemClick(int index, SmallObject data);
    }

    public static class DroidItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemText;

        public DroidItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemText = itemView.findViewById(R.id.list_item_text);
        }
    }
}
