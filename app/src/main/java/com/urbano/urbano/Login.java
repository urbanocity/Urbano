package com.urbano.urbano;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class Login extends Fragment {
    @BindView(R.id.l_et_pass)
    EditText et_password;
    @BindView(R.id.l_et_uname)
    EditText et_username;
    View view;
    Activity activity;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login, container, false);
        activity = getActivity();
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.bt_signup_buyer)
    public void bt_signup_buyer() {
        Delivery.setSignup_type("buyer");
        getFragmentManager().beginTransaction().replace(R.id.content, new Signup()).commit();
    }

    @OnClick(R.id.bt_signup_seller)
    public void bt_signup_seller() {
        Delivery.setSignup_type("seller");
        getFragmentManager().beginTransaction().replace(R.id.content, new Signup()).commit();
    }

    @OnClick(R.id.l_bt_login)
    public void bt_login() {
        progress = ProgressDialog.show(getActivity(), "", "Logging In", true);
        String _username = et_username.getText().toString();
        String _password = et_password.getText().toString();

        if (_username.length() > 1 && _password.length() > 1) {
            Hive.getAuth().signInWithEmailAndPassword(_username, _password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w("Urbano", "signInWithEmail:failed", task.getException());
                        Toast.makeText(getActivity(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        et_username.setTextColor(getResources().getColor(R.color.Red));
                        et_password.setTextColor(getResources().getColor(R.color.Red));
                    }
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getActivity(), Master.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    progress.dismiss();
                }
            });
        } else {
            progress.dismiss();
            Toast.makeText(getActivity(), "Enter Credentials!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick(R.id.bt_forgot)
    public void bt_forgot() {
        AlertDialog cut_dialog;
        AlertDialog.Builder cut_builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout._forgot, null);
        final EditText cut_pass = (EditText) dialogView.findViewById(R.id.et_email_forgot);
        cut_builder.setView(dialogView);
        cut_builder.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailAddress = cut_pass.getText().toString();

                Hive.getAuth().sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(activity, "Email Sent!", Toast.LENGTH_SHORT).show();
                                }
                                if (!task.isSuccessful()) {
                                    try {
                                        if (null != task.getException()) {
                                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
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
