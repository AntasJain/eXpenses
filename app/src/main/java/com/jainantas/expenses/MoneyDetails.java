package com.jainantas.expenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MoneyDetails {
    private String name;
    private String price;
    private String date;
    private String detail;
    private String key;
   // List<String> keyList=new LinkedList<>();
    public MoneyDetails()
    {

    }
    @Override
    public boolean equals(@NonNull Object obj) {
        if (obj instanceof MoneyDetails) {
            MoneyDetails temp = (MoneyDetails) obj;
            if (this.key.equals(temp.key))
                return true;
        }
        return false;
    }


    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public MoneyDetails(String name, String date, String price, String detail, String key)
    {
        this.name=name;
        this.date=date;
        this.price=price;
        this.detail=detail;
        this.key=key;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {

        this.detail = detail;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
      //  keyList.add(key);
    }
//    public String getKeyList(){
//        return keyList.get(keyList.size()-1);
//    }

}
