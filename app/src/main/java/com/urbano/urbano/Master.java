package com.urbano.urbano;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Master extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog progress;
    DrawerLayout drawer;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._master);
        getFragmentManager().beginTransaction().replace(R.id.content, new Splash()).commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Hive.isAuthenticated()) {
            Hive.getDb_ref().child("users").child(Hive.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String _type = dataSnapshot.child("type").getValue(String.class);
                    Delivery.setApp_mode(_type);
                    navigationView.getMenu().clear();
                    if (_type.contentEquals("seller")) {
                        Delivery.setApp_confirmation(dataSnapshot.child("confirmation").getValue(boolean.class));
                        navigationView.inflateMenu(R.menu.navigation_seller);
                    } else {
                        navigationView.inflateMenu(R.menu.navigation_buyer);
                    }
                    if (Delivery.getRedirect_to().isEmpty()) {
                        getFragmentManager().beginTransaction().replace(R.id.content, new Home()).commit();
                    } else {
                        getFragmentManager().beginTransaction().replace(R.id.content, new Home()).commit();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progress.dismiss();
                }
            });
        } else {
            getFragmentManager().beginTransaction().replace(R.id.content, new Home()).commit();
            navigationView.inflateMenu(R.menu.navigation_default);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_catlog) {
            getFragmentManager().beginTransaction().replace(R.id.content, new Catlog()).addToBackStack(null).commit();
        } else if (id == R.id.nav_login) {
            getFragmentManager().beginTransaction().replace(R.id.content, new Login()).addToBackStack(null).commit();
        } else if (id == R.id.nav_add) {
            getFragmentManager().beginTransaction().replace(R.id.content, new AddItem()).addToBackStack(null).commit();
        } else if (id == R.id.nav_contact) {
            getFragmentManager().beginTransaction().replace(R.id.content, new Contact()).addToBackStack(null).commit();
        } else if (id == R.id.nav_orders) {
            Delivery.setList_type("orders");
            getFragmentManager().beginTransaction().replace(R.id.content, new MList()).addToBackStack(null).commit();
        } else if (id == R.id.nav_mycatlog) {
            Delivery.setList_type("catlog");
            getFragmentManager().beginTransaction().replace(R.id.content, new MList()).addToBackStack(null).commit();
        } else if (id == R.id.nav_sold) {
            Delivery.setList_type("sold");
            getFragmentManager().beginTransaction().replace(R.id.content, new MList()).addToBackStack(null).commit();
        } else if (id == R.id.nav_logout) {
            Hive.getAuth().signOut();
            Intent intent = new Intent(Master.this, Master.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, download Urbano App!");
            startActivity(shareIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void bt_nav(View v) {
        drawer.openDrawer(Gravity.LEFT, true);
    }

    public void bt_back(View v) {
        getFragmentManager().popBackStack();
    }

    public void onclick_catlog(View v) {
        Delivery.setSelected_item((Map<String, String>) v.getTag());
        getFragmentManager().beginTransaction().add(R.id.content, new Item()).addToBackStack(null).commit();
    }

    public void bt_items(View v) {
        Delivery.setSetItem(Integer.parseInt(v.getTag().toString()));
        getFragmentManager().beginTransaction().add(R.id.content, new Catlog()).addToBackStack(null).commit();
    }

    public void bt_city(View v) {
        Delivery.setSetCity(v.getTag().toString());
        getFragmentManager().beginTransaction().add(R.id.content, new Catlog()).addToBackStack(null).commit();
    }

    public void bt_delete(View v) {
        String _tag[] = v.getTag().toString().split("_");

        Map<String, Object> _delete_map = new HashMap<>();
        _delete_map.put("/description/" + _tag[2], null);
        _delete_map.put("/listing/" + _tag[0] + "/" + _tag[1] + "/" + _tag[2], null);
        _delete_map.put("/seller/" + Hive.getUID() + "/" + _tag[2], null);

        Hive.getDb_ref().updateChildren(_delete_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(Master.this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(Master.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void bt_edit(View v) {
        Delivery.setEdit_item(true);
        Delivery.setSelected_item((Map<String, String>) v.getTag());
        getFragmentManager().beginTransaction().add(R.id.content, new AddItem()).addToBackStack(null).commit();
    }

    public void bt_resell(View v) {
        Map<String, String> _selected_item = (Map<String, String>) v.getTag();
        Map<String, Object> _sold = new HashMap<>();
        _sold.put("listing/" + _selected_item.get("item") + "/" + _selected_item.get("city") + "/" + _selected_item.get("code"), _selected_item);
        Hive.getDb_ref().updateChildren(_sold, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(Master.this, "Item put back on Listing", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });
    }
}
