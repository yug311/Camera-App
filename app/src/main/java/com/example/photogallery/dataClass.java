package com.example.photogallery;

import android.graphics.Bitmap;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class dataClass
{
    String addresss;
    String citys;
    String countrys;
    String dates;
    String ids;
    String locations;


    public dataClass()
    {

    }

    public dataClass(String address, String city, String country,String date, String id, String location)
    {
        addresss = address;
        citys = city;
        countrys = country;
        dates = date;
        ids = id;
        locations = location;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getAddresss() {
        return addresss;
    }

    public String getCitys() {
        return citys;
    }

    public String getCountrys() {
        return countrys;
    }

    public String getDates() {
        return dates;
    }

    public void setAddresss(String addresss) {
        this.addresss = addresss;
    }

    public void setCitys(String citys) {
        this.citys = citys;
    }

    public void setCountrys(String countrys) {
        this.countrys = countrys;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }


    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
