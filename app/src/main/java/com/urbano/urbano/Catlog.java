package com.urbano.urbano;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class Catlog extends Fragment {
    Activity activity;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    List<CatlogItem_> list = new ArrayList<>();
    CatlogItem_ item;
    @BindView(R.id.sp_item)
    Spinner sp_item;
    @BindView(R.id.sp_city)
    Spinner sp_city;
    @BindView(R.id.tv_t_title)
    TextView tv_t_title;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.bt_search)
    ImageView bt_search;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.bt_sort)
    LinearLayout bt_sort;
    @BindView(R.id.bt_filter)
    LinearLayout bt_filter;
    ArrayList<String> _city_list = new ArrayList<>();
    ArrayList<String> _item_list = new ArrayList<>();
    ArrayAdapter<String> _city_adapter, _item_adapter;
    String __search_text = "";
    boolean __search = false, ___dort = false, ___duration_filter = false;
    int ___duration_number;
    Map<String, Integer> _city_numbers = new HashMap<>();
    AdapterView.OnItemSelectedListener catlog_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (sp_city.getSelectedItemPosition() == 0 || sp_item.getSelectedItemPosition() == 0) {
                ll_search.setVisibility(View.GONE);
                bt_search.setVisibility(View.GONE);
                tv_t_title.setVisibility(View.VISIBLE);
                return;
            }
            paintCatlog();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.catlog, container, false);
        activity = getActivity();
        final ProgressDialog progress = ProgressDialog.show(activity, "", "Loading...", true);
        ButterKnife.bind(this, view);

        layoutManager = new LinearLayoutManager(activity);
        adapter = new CatlogAdapter_(list, activity);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        _item_list.add("-Select Item-");
        _item_list.add("Apartment or PG");
        _item_list.add("Furniture");
        _item_list.add("Car or Bike");

        _city_list.add("-Select City-");
        _city_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, _city_list);
        _item_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, _item_list);
        sp_city.setAdapter(_city_adapter);
        sp_item.setAdapter(_item_adapter);

        Hive.getDb_ref().child("listing").child("cities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || !isAdded()) {
                    return;
                }
                int i = 1;
                for (DataSnapshot city : dataSnapshot.getChildren()) {
                    _city_list.add(city.getKey());
                    _city_numbers.put(city.getKey(), i);
                    ++i;
                }
                _city_adapter.notifyDataSetChanged();
                sp_city.setOnItemSelectedListener(catlog_listener);
                sp_item.setOnItemSelectedListener(catlog_listener);
                progress.dismiss();
                if (!Delivery.getSetCity().isEmpty()) {
                    sp_city.setSelection(_city_numbers.get(Delivery.getSetCity()));
                    Delivery.setSetCity("");
                }
                if (Delivery.getSetItem() != 0) {
                    sp_item.setSelection(Delivery.getSetItem(), true);
                    Delivery.setSetItem(0);
                }

                if(Delivery.isFilter_from_home()){
                    ___duration_number = Delivery.getFilter_duration_number();
                    ___duration_filter = true;
                    paintCatlog();
                    Delivery.setSetCity("");
                    Delivery.setSetItem(0);
                }
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

        adapter.notifyDataSetChanged();
        return view;
    }

    @OnClick(R.id.bt_search)
    public void bt_search() {
        if (et_search.getText().toString().isEmpty()) {
            __search = false;
            paintCatlog();
            return;
        }
        __search = true;
        __search_text = et_search.getText().toString();
        paintCatlog();
    }

    public void paintCatlog() {
        final ProgressDialog progress2 = ProgressDialog.show(activity, "", "Fetching Item...", true);
        ll_search.setVisibility(View.VISIBLE);
        bt_search.setVisibility(View.VISIBLE);
        tv_t_title.setVisibility(View.GONE);

        Query db_ref = Hive.getDb_ref().child("listing").child(sp_item.getSelectedItem().toString()).child(sp_city.getSelectedItem().toString()).orderByChild("Price");

        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || !isAdded()) {
                    return;
                }
                int _count = adapter.getItemCount();
                list.clear();
                adapter.notifyItemRangeRemoved(0, _count);

                Map<String, String> _item_map;
                StorageReference _storage = FirebaseStorage.getInstance().getReference().child("Items");
                StorageReference _reference;
                for (DataSnapshot _item : dataSnapshot.getChildren()) {
                    if (__search) {
                        if (!_item.child("Title").getValue(String.class).toLowerCase().contains(__search_text.toLowerCase())) {
                            continue;
                        }
                    }

                    if(___duration_filter){
                        if(!(Integer.parseInt(_item.child("Duration").getValue(String.class)) == ___duration_number)){
                            continue;
                        }
                    }
                    _item_map = new HashMap<String, String>();
                    _item_map.put("title", _item.child("Title").getValue(String.class));
                    _item_map.put("duration", _item.child("Duration").getValue(String.class) + " DAYS");
                    _item_map.put("images", _item.child("Images").getValue(String.class));
                    _item_map.put("price", getString(R.string.Rs) + " " + _item.child("Price").getValue(int.class));
                    _item_map.put("item", sp_item.getSelectedItem().toString());
                    _item_map.put("city", sp_city.getSelectedItem().toString());
                    _item_map.put("seller", _item.child("Seller").getValue(String.class));
                    _item_map.put("code", _item.getKey());
                    _reference = _storage.child(_item.getKey()).child("1");

                    item = new CatlogItem_(_item_map, _reference);
                    list.add(item);
                }
                __search = false;
                adapter.notifyDataSetChanged();
                progress2.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (activity == null || !isAdded()) {
                    return;
                }
                Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progress2.dismiss();
            }
        });
    }

    @OnClick(R.id.bt_sort)
    public void bt_sort(){
        if(___dort){
            ___dort = false;
            bt_sort.setBackgroundColor(getResources().getColor(R.color.White));
            paintCatlog();
            Toast.makeText(activity, "Sort Removed", Toast.LENGTH_SHORT).show();
        }else {
            ___dort = true;
            bt_sort.setBackgroundColor(getResources().getColor(R.color.Tone5));
            paintCatlog();
        }
    }

    @OnClick(R.id.bt_filter)
    public void bt_filter(){
        if(___duration_filter){
            ___duration_filter = false;
            bt_filter.setBackgroundColor(getResources().getColor(R.color.White));
            paintCatlog();
            Toast.makeText(activity, "Filter Removed", Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog cut_dialog;
            AlertDialog.Builder cut_builder = new AlertDialog.Builder(activity);

            LayoutInflater inflater = activity.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout._forgot, null);
            final EditText cut_pass = (EditText) dialogView.findViewById(R.id.et_email_forgot);
            cut_builder.setView(dialogView);
            cut_builder.setPositiveButton("FILTER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ___duration_number = Integer.parseInt(cut_pass.getText().toString());
                    bt_filter.setBackgroundColor(getResources().getColor(R.color.Tone5));
                    ___duration_filter = true;
                    paintCatlog();
                }
            });
            cut_builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            cut_dialog = cut_builder.create();
            cut_dialog.show();
        }
    }
}
