package com.urbano.urbano;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class Home extends Fragment {
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    Activity activity;
    @BindView(R.id.bt_login)
    TextView bt_login;
    ItemPagerAdapter_ mCustomPagerAdapter;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.sp_city)Spinner sp_city;
    @BindView(R.id.sp_item)Spinner sp_item;
    @BindView(R.id.et_duration)EditText et_duration;
    List<StorageReference> list = new ArrayList<>();
    int currentPage = 0;
    ArrayList<String> _city_list = new ArrayList<>();
    ArrayList<String> _item_list = new ArrayList<>();
    ArrayAdapter<String> _city_adapter, _item_adapter;
    Timer timer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        activity = getActivity();
        ButterKnife.bind(this, view);
        final ProgressDialog progress = ProgressDialog.show(activity, "", "Loading...", true);
        if (!Hive.isAuthenticated()) {
            bt_login.setVisibility(View.VISIBLE);
        }

        mCustomPagerAdapter = new ItemPagerAdapter_(activity, list);
        mViewPager.setAdapter(mCustomPagerAdapter);

        StorageReference _ref = FirebaseStorage.getInstance().getReference().child("Home");
        for (int i = 1; i <= 5; i++) {
            list.add(_ref.child(i + ".jpg"));
        }
        mCustomPagerAdapter.notifyDataSetChanged();

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 6) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage, true);
                ++currentPage;
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

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
                    ++i;
                }
                _city_adapter.notifyDataSetChanged();
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

    @OnClick(R.id.bt_login)
    public void bt_login() {
        getFragmentManager().beginTransaction().replace(R.id.content, new Login()).addToBackStack(null).commit();
    }

    @OnClick(R.id.bt_contact)
    public void bt_contact() {
        getFragmentManager().beginTransaction().replace(R.id.content, new Contact()).addToBackStack(null).commit();
    }

    @OnClick(R.id.bt_search)
    public void bt_search(){
        if(sp_city.getSelectedItemPosition() == 0 || sp_item.getSelectedItemPosition() == 0 || et_duration.getText().toString().isEmpty()){
            Toast.makeText(activity, "Kindly Fill the Form", Toast.LENGTH_SHORT).show();
            return;
        }

        Delivery.setFilter_from_home(true);
        Delivery.setFilter_duration_number(Integer.parseInt(et_duration.getText().toString()));
        Delivery.setSetCity((String) sp_city.getSelectedItem());
        Delivery.setSetItem(Integer.parseInt(sp_city.getSelectedItemPosition()+""));
        getFragmentManager().beginTransaction().replace(R.id.content, new Catlog()).addToBackStack(null).commit();
    }
}
