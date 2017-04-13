package com.urbano.urbano;

import com.google.firebase.storage.StorageReference;

import java.util.Map;



public class CatlogItem_ {
    private String title, price, duration;
    private Map<String, String> item_map;
    private StorageReference reference;

    CatlogItem_(Map<String, String> item_map, StorageReference reference) {
        this.title = item_map.get("title");
        this.price = item_map.get("price");
        this.duration = item_map.get("duration");
        this.item_map = item_map;
        this.reference = reference;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public Map<String, String> getItem_map() {
        return item_map;
    }

    public String getDuration() {
        return duration;
    }

    public StorageReference getReference() {
        return reference;
    }
}
