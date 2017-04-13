package com.urbano.urbano;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class Item extends Fragment {
    Activity activity;
    ProgressDialog progress;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_duration)
    TextView tv_duration;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_description)
    TextView tv_description;
    Map<String, String> _selected_item;
    ItemPagerAdapter_ mCustomPagerAdapter;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    List<StorageReference> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item, container, false);
        activity = getActivity();
        progress = ProgressDialog.show(activity, "", "Getting your Item", true);
        ButterKnife.bind(this, view);
        _selected_item = Delivery.getSelected_item();
        tv_title.setText(_selected_item.get("title"));
        tv_price.setText(_selected_item.get("price"));
        tv_duration.setText(_selected_item.get("duration"));

        mCustomPagerAdapter = new ItemPagerAdapter_(activity, list);
        mViewPager.setAdapter(mCustomPagerAdapter);

        int _images_count = Integer.parseInt(_selected_item.get("images"));
        StorageReference _ref = FirebaseStorage.getInstance().getReference().child("Items").child(_selected_item.get("code"));
        for (int i = 1; i <= _images_count; i++) {
            list.add(_ref.child(i + ""));
        }
        mCustomPagerAdapter.notifyDataSetChanged();


        Hive.getDb_ref().child("description").child(_selected_item.get("code")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || !isAdded()) {
                    return;
                }
                for (DataSnapshot desc : dataSnapshot.getChildren()) {
                    tv_description.append(desc.getKey() + " : " + desc.getValue(String.class) + "\n\n");
                }
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

    @OnClick(R.id.bt_register)
    public void bt_register() {
        new AlertDialog.Builder(activity)
                .setTitle("Buying Confirmation")
                .setMessage("Are you sure you want to Buy?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progress = ProgressDialog.show(getActivity(), "", "Processing...", true);
                        String _date = new SimpleDateFormat("dd/MM/yy_HH:mm").format(new Date());
                        _selected_item.put("date", _date);
                        _selected_item.put("price", _selected_item.get("price").replace(getString(R.string.Rs), "")).trim();

                        Map<String, Object> _sold = new HashMap<>();
                        _sold.put("orders/" + Hive.getUID() + "/" + _selected_item.get("code") + "/", _selected_item);
                        _sold.put("sold/" + _selected_item.get("seller") + "/" + _selected_item.get("code") + "/", _selected_item);
                        _sold.put("listing/" + _selected_item.get("item") + "/" + _selected_item.get("city") + "/" + _selected_item.get("code"), null);
                        _sold.put("seller/" + Hive.getUID() + "/" + _selected_item.get("code"), null);
                        Hive.getDb_ref().updateChildren(_sold, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                getFragmentManager().popBackStack();
                                getFragmentManager().popBackStack();
                                progress.dismiss();
                                Toast.makeText(activity, "Buy Order Successful!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
