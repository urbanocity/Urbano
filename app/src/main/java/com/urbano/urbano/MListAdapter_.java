package com.urbano.urbano;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class MListAdapter_ extends RecyclerView.Adapter<MListAdapter_.mViewHolder> {
    private List<MListItem_> list;
    private Context context;

    public MListAdapter_(List<MListItem_> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.__list, parent, false);
        return new mViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        MListItem_ item = list.get(position);
        Glide.with(context).using(new FirebaseImageLoader()).load(item.getRef()).into(holder.iv_image);
        holder.tv_title.setText(item.getTitle());
        holder.tv_duration.setText(item.getDuration());
        holder.tv_price.setText(item.getAmount());
        if (!item.getCity().isEmpty()) {
            holder.tv_city.setVisibility(View.VISIBLE);
            holder.tv_city.setText(item.getCity());
        }
        if (!item.getItem().isEmpty()) {
            holder.tv_item.setVisibility(View.VISIBLE);
            holder.tv_item.setText(item.getItem());
        }
        if (!item.getDate().isEmpty()) {
            holder.tv_date.setVisibility(View.VISIBLE);
            holder.tv_date.setText(item.getDate());
        }

        if (item.is_sold()) {
            holder.bt_delete.setVisibility(View.VISIBLE);
            holder.bt_delete.setTag(item.getItem() + "_" + item.getCity() + "_" + item.getCode());
            holder.bt_edit.setVisibility(View.VISIBLE);
            holder.bt_edit.setTag(item.getEdit_map());
        } else if (item.is_catlog()) {
            holder.bt_resell.setVisibility(View.VISIBLE);
            holder.bt_resell.setTag(item.getSold_map());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_duration)
        TextView tv_duration;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_city)
        TextView tv_city;
        @BindView(R.id.tv_item)
        TextView tv_item;
        @BindView(R.id.tv_date)
        TextView tv_date;
        @BindView(R.id.iv_image)
        ImageView iv_image;
        @BindView(R.id.bt_resell)
        TextView bt_resell;
        @BindView(R.id.bt_edit)
        TextView bt_edit;
        @BindView(R.id.bt_delete)
        TextView bt_delete;

        mViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
