package com.urbano.urbano;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddItem extends Fragment {
    Activity activity;
    @BindView(R.id.ll_common)
    LinearLayout ll_common;
    @BindView(R.id.ll_pg)
    LinearLayout ll_pg;
    @BindView(R.id.ll_description)
    LinearLayout ll_description;
    @BindView(R.id.bt_add)
    LinearLayout bt_add_item;
    @BindView(R.id.sp_item)
    Spinner sp_item;
    @BindView(R.id.sp_city)
    Spinner sp_city;
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_duration)
    EditText et_duration;
    @BindView(R.id.et_price)
    EditText et_price;
    @BindView(R.id.et_description)
    EditText et_description;

    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.iv3)
    ImageView iv3;
    @BindView(R.id.iv4)
    ImageView iv4;
    @BindView(R.id.iv5)
    ImageView iv5;

    @BindView(R.id.et_seater)
    EditText et_seater;
    @BindView(R.id.et_electricity)
    EditText et_electricity;
    @BindView(R.id.rb_girls)
    RadioButton rb_girls;
    @BindView(R.id.rb_boys)
    RadioButton rb_boys;
    @BindView(R.id.rb_family)
    RadioButton rb_family;
    @BindView(R.id.rb_both)
    RadioButton rb_both;
    @BindView(R.id.sw_food)
    Switch sw_food;
    @BindView(R.id.sw_air)
    Switch sw_air;
    @BindView(R.id.sw_furniture)
    Switch sw_furniture;
    @BindView(R.id.sw_water)
    Switch sw_water;
    @BindView(R.id.sw_wifi)
    Switch sw_wifi;
    @BindView(R.id.ll_confirmation)
    LinearLayout ll_confirmation;
    AdapterView.OnItemSelectedListener listener_item = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                ll_common.setVisibility(View.GONE);
                ll_pg.setVisibility(View.GONE);
                ll_description.setVisibility(View.GONE);
                bt_add_item.setVisibility(View.GONE);
            } else if (position == 1) {
                ll_common.setVisibility(View.VISIBLE);
                ll_pg.setVisibility(View.VISIBLE);
                ll_description.setVisibility(View.VISIBLE);
                bt_add_item.setVisibility(View.VISIBLE);
            } else {
                ll_common.setVisibility(View.VISIBLE);
                ll_pg.setVisibility(View.GONE);
                ll_description.setVisibility(View.VISIBLE);
                bt_add_item.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    ArrayList<String> _city_list = new ArrayList<>();
    ArrayList<String> _item_list = new ArrayList<>();
    ArrayAdapter<String> _city_adapter, _item_adapter;
    Map<Integer, Object> image_uris = new HashMap<>();
    Map<Integer, Object> image_views = new HashMap<>();
    Map<String, Object> save_item;
    String _item_code = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    ProgressDialog progress;
    View.OnClickListener onclick_iv = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, Integer.parseInt(v.getTag().toString()));
        }
    };
    int _uploaded = 0;
    int _last_uploaded = 0;
    boolean edit_mode = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_item, container, false);
        activity = getActivity();
        ButterKnife.bind(this, view);
        if (!Delivery.isApp_confirmation()) {
            ll_confirmation.setVisibility(View.VISIBLE);
            return view;
        }
        final ProgressDialog progress1 = ProgressDialog.show(activity, "", "Loading...", true);
        bt_add_item.setEnabled(true);
        sp_item.setOnItemSelectedListener(listener_item);

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
                for (DataSnapshot city : dataSnapshot.getChildren()) {
                    _city_list.add(city.getKey());
                }
                _city_adapter.notifyDataSetChanged();
                progress1.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (activity == null || !isAdded()) {
                    return;
                }
                Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progress1.dismiss();
            }
        });

        iv1.setOnClickListener(onclick_iv);
        iv2.setOnClickListener(onclick_iv);
        iv3.setOnClickListener(onclick_iv);
        iv4.setOnClickListener(onclick_iv);
        iv5.setOnClickListener(onclick_iv);
        image_views.put(1, iv1);
        image_views.put(2, iv2);
        image_views.put(3, iv3);
        image_views.put(4, iv4);
        image_views.put(5, iv5);

        if (Delivery.isEdit_item()) {
            edit_mode = true;
            final ProgressDialog progress2 = ProgressDialog.show(activity, "", "Loading Edit Data...", true);
            final Map<String, String> _existing = Delivery.getSelected_item();
            if (_existing.get("item").contentEquals("Apartment or PG")) {
                sp_item.setSelection(1);
            } else if (_existing.get("item").contentEquals("Furniture")) {
                sp_item.setSelection(2);
            } else if (_existing.get("item").contentEquals("Car or Bike")) {
                sp_item.setSelection(3);
            }
            _item_code = _existing.get("code");
            et_title.setText(_existing.get("title"));
            et_duration.setText(_existing.get("duration"));
            et_price.setText(_existing.get("price"));

            Hive.getDb_ref().child("description").child(_existing.get("code")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (activity == null || !isAdded()) {
                        return;
                    }
                    et_description.setText(dataSnapshot.child("Description").getValue(String.class));
                    if (_existing.get("item").contentEquals("Apartment or PG")) {
                        sw_air.setChecked(toBoolean(dataSnapshot.child("Air Conditioner").getValue(String.class)));
                        sw_water.setChecked(toBoolean(dataSnapshot.child("Water Facility").getValue(String.class)));
                        sw_furniture.setChecked(toBoolean(dataSnapshot.child("Furniture").getValue(String.class)));
                        sw_food.setChecked(toBoolean(dataSnapshot.child("Food").getValue(String.class)));
                        sw_wifi.setChecked(toBoolean(dataSnapshot.child("Wifi").getValue(String.class)));
                        et_electricity.setText(dataSnapshot.child("Free Electricity Units").getValue(String.class));
                        et_seater.setText(dataSnapshot.child("Seater").getValue(String.class));
                    }
                    progress2.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (activity == null || !isAdded()) {
                        return;
                    }
                    progress2.dismiss();
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            Delivery.setEdit_item(false);
        }
        return view;
    }

    @OnClick(R.id.bt_add)
    public void bt_add_item() {
        progress = ProgressDialog.show(activity, "", "Uploading Image(s) and Adding Item...", true);
        String _item, _city, _title, _duration, _description, _seater = "", _electricity = "", _preference = "";
        int _price;
        _item = sp_item.getSelectedItem().toString();
        _city = sp_city.getSelectedItem().toString();
        _title = et_title.getText().toString();
        _duration = et_duration.getText().toString();
        _description = et_description.getText().toString();

        if (sp_item.getSelectedItemPosition() == 0 || sp_city.getSelectedItemPosition() == 0 || _title.isEmpty() || _duration.isEmpty() || et_price.getText().toString().isEmpty() || _description.isEmpty()) {
            Toast.makeText(activity, "Please fill All Information", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }

        _price = Integer.parseInt(et_price.getText().toString().trim());

        if (sp_item.getSelectedItemPosition() == 1) {
            _seater = et_seater.getText().toString();
            _electricity = et_electricity.getText().toString();

            if (_seater.isEmpty() || _electricity.isEmpty()) {
                Toast.makeText(activity, "Please fill All Information", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                return;
            }
        }
        Log.d(Hive.getTAG(), Delivery.isEdit_item()+"");
        if (image_uris.isEmpty() && !edit_mode) {
            Toast.makeText(activity, "Select Atleast 1 Image by clicking on the Above Icons", Toast.LENGTH_LONG).show();
            progress.dismiss();
            return;
        }

        String _date = new SimpleDateFormat("dd/MM/yy_HH:mm").format(new Date());
        save_item = new HashMap<>();
        String _pre_list = "listing/" + _item + "/" + _city + "/" + _item_code + "/";
        String _pre_desc = "description/" + _item_code + "/";
        String _pre_seller = "seller/" + Hive.getUID() + "/" + _item_code + "/";

        save_item.put(_pre_list + "Title", _title);
        save_item.put(_pre_list + "Duration", _duration);
        save_item.put(_pre_list + "Price", _price);
        save_item.put(_pre_list + "Date", _date);
        save_item.put(_pre_list + "Seller", Hive.getUID());
        save_item.put(_pre_list + "Images", image_uris.size() + "");

        save_item.put(_pre_seller + "title", _title);
        save_item.put(_pre_seller + "duration", _duration);
        save_item.put(_pre_seller + "price", _price + "");
        save_item.put(_pre_seller + "images", image_uris.size() + "");
        save_item.put(_pre_seller + "date", _date);
        save_item.put(_pre_seller + "city", sp_city.getSelectedItem().toString());
        save_item.put(_pre_seller + "item", sp_item.getSelectedItem().toString());

        save_item.put(_pre_desc + "Description", _description);
        if (sp_item.getSelectedItemPosition() == 1) {
            if (rb_boys.isChecked()) {
                _preference = "Boy(s)";
            } else if (rb_girls.isChecked()) {
                _preference = "Girl(s)";
            } else if (rb_family.isChecked()) {
                _preference = "Family";
            } else {
                _preference = "Both Boy(s) and Girl(s)";
            }
            save_item.put(_pre_desc + "Seater", _seater);
            save_item.put(_pre_desc + "Preference", _preference);
            save_item.put(_pre_desc + "Free Electricity Units", _electricity);
            save_item.put(_pre_desc + "Food", switchParser(sw_food.isChecked()));
            save_item.put(_pre_desc + "Air Conditioner", switchParser(sw_air.isChecked()));
            save_item.put(_pre_desc + "Furniture", switchParser(sw_furniture.isChecked()));
            save_item.put(_pre_desc + "Water Facility", switchParser(sw_water.isChecked()));
            save_item.put(_pre_desc + "Wifi", switchParser(sw_wifi.isChecked()));
        }

        startUploading();
    }

    public String switchParser(boolean selection) {
        if (selection) {
            return "YES";
        } else {
            return "NO";
        }
    }

    public boolean toBoolean(String answer) {
        return answer.contentEquals("YES");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            Toast.makeText(activity, "Error! Please Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        image_uris.put(requestCode, data.getData());
        ((ImageView) image_views.get(requestCode)).setColorFilter(getResources().getColor(R.color.Blue));
    }

    public void startUploading() {
        if (_uploaded >= image_uris.size()) {
            dataUploader();
            return;
        }
        ++_last_uploaded;
        if (null == image_uris.get(_last_uploaded)) {
            while (_last_uploaded < 6 && null != image_uris.get(_last_uploaded)) {
                ++_last_uploaded;
            }
        }
        if (_last_uploaded < 6) {
            imageUploader((Uri) image_uris.get(_last_uploaded));
            Log.d(Hive.getTAG(), "Uploaded:" + _last_uploaded);
        }

    }

    public void imageUploader(Uri uri) {
        try {
            StorageReference iamgeRef = FirebaseStorage.getInstance().getReference().child("Items").child(_item_code).child((_uploaded + 1) + "");
            InputStream image_stream = activity.getContentResolver().openInputStream(uri);
            Bitmap photo = BitmapFactory.decodeStream(image_stream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageData = baos.toByteArray();
            UploadTask uploadTask = iamgeRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ++_uploaded;
                    startUploading();
                }
            });
        } catch (Exception e) {
        }
    }

    public void dataUploader() {
        Hive.getDb_ref().updateChildren(save_item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (activity == null || !isAdded()) {
                    return;
                }
                progress.dismiss();
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
    }
}
