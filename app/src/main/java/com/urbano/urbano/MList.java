package com.urbano.urbano;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MList extends Fragment {
    Activity activity;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    List<MListItem_> list = new ArrayList<>();
    MListItem_ item;
    @BindView(R.id.tv_list_title)
    TextView tv_list_title;
    ProgressDialog progress;
    String _type = "";
    DatabaseReference _ref;
    boolean __sold = false, __catlog = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, container, false);
        activity = getActivity();
        ButterKnife.bind(this, view);
        progress = ProgressDialog.show(getActivity(), "", "Loading...", true);

        layoutManager = new LinearLayoutManager(activity);
        adapter = new MListAdapter_(list, activity);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        _type = Delivery.getList_type();
        if (_type.contentEquals("catlog")) {
            __catlog = true;
            tv_list_title.setText("ACTIVE ITEMS");
            _ref = Hive.getDb_ref().child("seller").child(Hive.getUID());
        } else if (_type.contentEquals("sold")) {
            __sold = true;
            tv_list_title.setText("SOLD ITEMS");
            _ref = Hive.getDb_ref().child("sold").child(Hive.getUID());
        } else {
            tv_list_title.setText("MY ORDERS");
            _ref = Hive.getDb_ref().child("orders").child(Hive.getUID());
        }

        _ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || !isAdded()) {
                    return;
                }
                String _title, _duration, _price, _city = "", _item = "", _date = "", _code, _images;
                StorageReference storage = FirebaseStorage.getInstance().getReference().child("Items");
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    _code = snap.getKey();
                    _title = snap.child("title").getValue(String.class);
                    _duration = snap.child("duration").getValue(String.class);
                    _price = snap.child("price").getValue(String.class);
                    _images = snap.child("images").getValue(String.class);
                    if (snap.child("city").exists()) {
                        _city = snap.child("city").getValue(String.class);
                    }
                    if (snap.child("item").exists()) {
                        _item = snap.child("item").getValue(String.class);
                    }
                    if (snap.child("date").exists()) {
                        _date = snap.child("date").getValue(String.class);
                    }

                    String _pre_list = "listing/" + _item + "/" + _city + "/" + _code + "/";
                    String _pre_seller = "seller/" + Hive.getUID() + "/" + _code + "/";

                    Map<String, Object> _sold_map = new HashMap<String, Object>();

                    _sold_map.put(_pre_list + "Title", _title);
                    _sold_map.put(_pre_list + "Duration", _duration);
                    _sold_map.put(_pre_list + "Price", Integer.parseInt(_price));
                    _sold_map.put(_pre_list + "Date", _date);
                    _sold_map.put(_pre_list + "Seller", Hive.getUID());
                    _sold_map.put(_pre_list + "Images", _images);

                    _sold_map.put(_pre_seller + "title", _title);
                    _sold_map.put(_pre_seller + "duration", _duration);
                    _sold_map.put(_pre_seller + "price", _price + "");
                    _sold_map.put(_pre_seller + "images", _images);
                    _sold_map.put(_pre_seller + "date", _date);
                    _sold_map.put(_pre_seller + "city", _city);
                    _sold_map.put(_pre_seller + "item", _item);

                   Map<String, Object> _edit_map = new HashMap<String, Object>();
                    _edit_map.put("title", _title);
                    _edit_map.put("duration", _duration);
                    _edit_map.put("price", _price + "");
                    _edit_map.put("images", _images);
                    _edit_map.put("date", _date);
                    _edit_map.put("city", _city);
                    _edit_map.put("item", _item);
                    _edit_map.put("code", _code);

                    item = new MListItem_(_code, _title, _duration, _price, _city, _item, _date.replace("_", " "), storage.child(_code).child("1"), __sold, __catlog, _sold_map, _edit_map);
                    list.add(item);
                }
                adapter.notifyDataSetChanged();
                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (activity == null || !isAdded()) {
                    return;
                }
                Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });

        return view;
    }

}
