package com.urbano.urbano;

import java.util.HashMap;
import java.util.Map;



public class Delivery {
    private static String signup_type = "";
    private static String redirect_to = "";
    private static String app_mode = "buyer";
    private static boolean app_confirmation = false;
    private static Map<String, String> selected_item;
    private static String setCity = "";
    private static int setItem = 0;
    private static String list_type = "";
    private static boolean edit_item = false;
    private static boolean filter_from_home = false;
    private static int filter_duration_number;

    public static int getFilter_duration_number() {
        return filter_duration_number;
    }

    public static void setFilter_duration_number(int filter_duration_number) {
        Delivery.filter_duration_number = filter_duration_number;
    }

    public static boolean isFilter_from_home() {
        return filter_from_home;
    }

    public static void setFilter_from_home(boolean filter_from_home) {
        Delivery.filter_from_home = filter_from_home;
    }

    public static boolean isEdit_item() {
        return edit_item;
    }

    public static void setEdit_item(boolean edit_item) {
        Delivery.edit_item = edit_item;
    }

    public static String getSignup_type() {
        return signup_type;
    }

    public static void setSignup_type(String signup_type) {
        Delivery.signup_type = signup_type;
    }

    public static String getRedirect_to() {
        return redirect_to;
    }

    public static void setRedirect_to(String redirect_to) {
        Delivery.redirect_to = redirect_to;
    }

    public static String getApp_mode() {
        return app_mode;
    }

    public static void setApp_mode(String app_mode) {
        Delivery.app_mode = app_mode;
    }

    public static Map<String, String> getSelected_item() {
        return selected_item;
    }

    public static void setSelected_item(Map<String, String> selected_item) {
        Delivery.selected_item = new HashMap<>();
        Delivery.selected_item = selected_item;
    }

    public static String getSetCity() {
        return setCity;
    }

    public static void setSetCity(String setCity) {
        Delivery.setCity = setCity;
    }

    public static int getSetItem() {
        return setItem;
    }

    public static void setSetItem(int setItem) {
        Delivery.setItem = setItem;
    }

    public static boolean isApp_confirmation() {
        return app_confirmation;
    }

    public static void setApp_confirmation(boolean app_confirmation) {
        Delivery.app_confirmation = app_confirmation;
    }

    public static String getList_type() {
        return list_type;
    }

    public static void setList_type(String list_type) {
        Delivery.list_type = list_type;
    }
}
