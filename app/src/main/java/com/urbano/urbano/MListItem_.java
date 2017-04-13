package com.urbano.urbano;

import com.google.firebase.storage.StorageReference;

import java.util.Map;


public class MListItem_ {
    private String title, duration, amount, city, item, date, code;
    private StorageReference ref;
    private boolean _sold, _catlog;
    private Map<String, Object> sold_map, edit_map;

    MListItem_(String code, String title, String duration, String amount, String city, String item, String date, StorageReference ref, boolean catlog, boolean sold, Map<String, Object> sold_map, Map<String, Object> edit_map) {
        this.sold_map = sold_map;
        this.edit_map = edit_map;
        this.code = code;
        this.title = title;
        this.duration = duration;
        this.amount = amount;
        this.city = city;
        this.item = item;
        this.date = date;
        this.ref = ref;
        this._sold = sold;
        this._catlog = catlog;
    }

    public Map<String, Object> getSold_map() {
        return sold_map;
    }

    public Map<String, Object> getEdit_map() {
        return edit_map;
    }

    public boolean is_sold() {
        return _sold;
    }

    public String getCode() {
        return code;
    }

    public boolean is_catlog() {
        return _catlog;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public String getAmount() {
        return amount;
    }

    public String getCity() {
        return city;
    }

    public String getItem() {
        return item;
    }

    public String getDate() {
        return date;
    }

    public StorageReference getRef() {
        return ref;
    }
}
