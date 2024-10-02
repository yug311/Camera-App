package com.example.photogallery;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Images implements Parcelable
{
    String city;
    String country;
    String address;
    int image;
    Bitmap bitmap;
    LatLng location;
    String date;

    public Images()
    {

    }

    public Images(String city, String country, String address, int image, Bitmap bitmap, LatLng location, String date)
    {
        this.city = city;
        this.address = address;
        this.country = country;
        this.image = image;
        this.bitmap = bitmap;
        this.location = location;
        this.date = date;
    }

    protected Images(Parcel in) {
        city = in.readString();
        country = in.readString();
        address = in.readString();
        image = in.readInt();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        location = in.readParcelable(LatLng.class.getClassLoader());
        date = in.readString();
    }

    public static final Creator<Images> CREATOR = new Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    public int getImage() {
        return image;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getAddress() { return address; }

    public LatLng getLocation() { return location; }

    public String getDate() { return date; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(address);
        dest.writeInt(image);
        dest.writeParcelable(bitmap, flags);
        dest.writeParcelable(location, flags);
        dest.writeString(date);
    }
}
