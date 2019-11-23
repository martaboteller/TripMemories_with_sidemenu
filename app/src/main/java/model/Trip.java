package model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import cat.cifo.hospitalet.tripmemoriessidemenu.R;

@Entity(tableName = "table_trips")
public class Trip {

    //Definition of variables for HTTP
    @SerializedName("uuid")
    private UUID mUUID;
    @SerializedName("name")
    private String mName;
    @SerializedName("country")
    private String mCountry;
    @SerializedName("date")
    private Date mDate;
    @SerializedName("comp")
    private String mComp;
    @SerializedName ("photo")
    private String mPhoto;


    //Constructor
    public Trip(String name, String country){
        mUUID = UUID.randomUUID();
        this.mName = name;
        this.mCountry = country;
        this.mDate = new Date();
    }

    //Constructor2
    public Trip(String name, String country,String comp){
        mUUID = UUID.randomUUID();
        this.mName = name;
        this.mCountry = country;
        this.mDate = new Date();
        this.mComp = comp;
    }

    //Getters and Setters
    public UUID getUUID() {
        return mUUID;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getCountry() {
        return mCountry;
    }
    public void setCountry(String country) {
        this.mCountry = country;
    }
    public Date getDate() {return mDate; }
    public void setDate(@NonNull Date date) {mDate = date;}
    public String getComp() {return mComp;}
    public void setComp(String comp) {mComp = comp;}
    public String getPhoto() { return "IMG_" + mUUID + ".jpg";}
    public void setPhoto(String photo) {mPhoto = photo;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return getUUID().equals(trip.getUUID()) &&
                getName().equals(trip.getName()) &&
                Objects.equals(getCountry(), trip.getCountry()) &&
                getDate().equals(trip.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID(), getName(), getCountry(), getDate());
    }

    @Override
    public String toString() {
        return "Trip{" +
                "mUUID=" + mUUID +
                ", mName='" + mName + '\'' +
                ", mCountry='" + mCountry + '\'' +
                ", mDate=" + mDate +
                '}';
    }







}

