package com.urbano.urbano;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class CatlogAdapter_ extends RecyclerView.Adapter<CatlogAdapter_.mViewHolder> {
    private List<CatlogItem_> list;
    private Context context;

    public CatlogAdapter_(List<CatlogItem_> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.__catlog, parent, false);
        return new mViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        CatlogItem_ item = list.get(position);
        holder.ll_holder.setTag(item.getItem_map());
        holder.tv_title.setText(item.getTitle());
        holder.tv_price.setText(item.getPrice());
        holder.tv_duration.setText(item.getDuration());
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(item.getReference())
                .into(holder.iv_image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_duration)
        TextView tv_duration;
        @BindView(R.id.ll1)
        LinearLayout ll_holder;
        @BindView(R.id.iv_image)
        ImageView iv_image;

        mViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
