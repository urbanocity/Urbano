package com.urbano.urbano;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Signup extends Fragment {
    Activity activity;
    String _type;
    @BindView(R.id.ll_seller)
    LinearLayout ll_seller;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.et_aadhar)
    EditText et_aadhar;
    @BindView(R.id.et_bank)
    EditText et_bank;
    @BindView(R.id.et_banknumber)
    EditText et_banknumber;
    @BindView(R.id.et_ifsc)
    EditText et_ifsc;
    @BindView(R.id.et_pan)
    EditText et_pan;
    @BindView(R.id.iv_aadhar)
    ImageView iv_aadhar;
    @BindView(R.id.iv_pan)
    ImageView iv_pan;
    @BindView(R.id.iv_check)
    ImageView iv_check;
    Map<String, Object> _new;
    ProgressDialog progress;
    int _uploaded = 0;
    int _last_uploaded = 0;
    String _email = "";
    Map<Integer, Object> image_uris = new HashMap<>();
    Map<Integer, Object> image_views = new HashMap<>();
    View.OnClickListener onclick_iv = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, Integer.parseInt(v.getTag().toString()));
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup, container, false);
        activity = getActivity();
        ButterKnife.bind(this, view);
        _type = Delivery.getSignup_type();

        if (_type.contentEquals("seller")) {
            ll_seller.setVisibility(View.VISIBLE);
        }

        iv_aadhar.setOnClickListener(onclick_iv);
        iv_pan.setOnClickListener(onclick_iv);
        iv_check.setOnClickListener(onclick_iv);

        image_views.put(1, iv_aadhar);
        image_views.put(2, iv_pan);
        image_views.put(3, iv_check);

        return view;
    }

    @OnClick(R.id.bt_back)
    public void bt_back() {
        getFragmentManager().beginTransaction().replace(R.id.content, new Login()).commit();
    }

    @OnClick(R.id.bt_signup)
    public void bt_signup() {
        progress = ProgressDialog.show(activity, "", "Creating Account...", true);
        String _name, _password, _mobile, _address, _aadhar = "", _bank = "", _banknumber = "", _ifsc = "", _pan = "";
        _name = et_name.getText().toString();
        _email = et_email.getText().toString();
        _password = et_password.getText().toString();
        _mobile = et_mobile.getText().toString();
        _address = et_address.getText().toString();
        if (_name.isEmpty() || _email.isEmpty() || _password.isEmpty() || _mobile.isEmpty() || _address.isEmpty()) {
            Toast.makeText(activity, "All Details are Necessary", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }
        if (_type.contentEquals("seller")) {
            _aadhar = et_aadhar.getText().toString();
            _bank = et_bank.getText().toString();
            _banknumber = et_banknumber.getText().toString();
            _ifsc = et_ifsc.getText().toString();
            _pan = et_pan.getText().toString();
            if (_aadhar.isEmpty() || _bank.isEmpty() || _banknumber.isEmpty() || _ifsc.isEmpty() || _pan.isEmpty()) {
                Toast.makeText(activity, "All Details are Necessary", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                return;
            }
        }

        if (image_uris.size() != 3) {
            Toast.makeText(activity, "Kindly Upload All the Required Images", Toast.LENGTH_LONG).show();
            progress.dismiss();
            return;
        }

        _new = new HashMap<>();
        _new.put("name", _name);
        _new.put("email", _email);
        _new.put("mobile", _mobile);
        _new.put("address", _address);
        _new.put("type", "buyer");
        if (_type.contentEquals("seller")) {
            _new.put("type", "seller");
            _new.put("aadhar", _aadhar);
            _new.put("bank", _bank);
            _new.put("banknumber", _banknumber);
            _new.put("ifsc", _ifsc);
            _new.put("pan", _pan);
            _new.put("confirmation", false);
        }

        Hive.getAuth().createUserWithEmailAndPassword(_email, _password).addOnSuccessListener(activity, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startUploading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    public void imageUploader(Uri file) {
        StorageReference iamgeRef = FirebaseStorage.getInstance().getReference().child(_email).child(file.getLastPathSegment());
        UploadTask uploadTask = iamgeRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ++_uploaded;
                startUploading();
            }
        });
    }

    public void dataUploader() {
        Hive.getDb_ref().child("users").child(Hive.getUID()).setValue(_new, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progress.dismiss();
                if (databaseError != null) {
                    Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), Master.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}

