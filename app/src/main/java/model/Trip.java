package model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;



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
    @SerializedName ("deleted")
    private int mDeleted;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("latitude")
    private Double mLatitude;
    @SerializedName("longitude")
    private Double mLongitude;
    @SerializedName("location")
    private String mLocation;
    @SerializedName("photobase")
    private String mPhotobase;

    //Constructor1
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

    //Constructor3
    public Trip(String name, String country,Date date,String comp,String photo,int deleted,String phone, Double latitude, Double longitude,String location,String photobase){
        mUUID = UUID.randomUUID();
        this.mName = name;
        this.mCountry = country;
        this.mDate = date;
        this.mComp = comp;
        this.mPhoto = photo;
        this.mDeleted = deleted;
        this.mPhone = phone;
        this.mLatitude =latitude;
        this.mLongitude = longitude;
        this.mLocation = location;
        this.mPhotobase = photobase;
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
    public int getDeleted() {return mDeleted;}
    public void setDeleted(int deleted) {mDeleted = deleted;}
    public String getPhone() {return mPhone;}
    public void setPhone(String phone) {mPhone = phone;}
    public Double getLatitude() {return mLatitude;}
    public void setLatitude(Double latitude) {mLatitude = latitude;}
    public Double getLongitude() {return mLongitude;}
    public void setLongitude(Double longitude) {mLongitude = longitude;}
    public String getLocation() {return mLocation;}
    public void setLocation(String location) {mLocation = location;}
    public String getPhotobase() {return mPhotobase;}
    public void setPhotobase(String photobase) {mPhotobase = photobase;}

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
        return Objects.hash(getUUID(), getName(), getCountry(), getDate(),
                getComp(),getPhoto(),getDeleted(),getPhone(),getLatitude(),getLongitude());
    }

    @Override
    public String toString() {
        return "Trip{" +
                "mUUID=" + mUUID +
                ", mName='" + mName + '\'' +
                ", mCountry='" + mCountry + '\'' +
                ", mDate=" + mDate + '\'' +
                ", mComp='" + mComp + '\'' +
                ", mPhoto='" + mPhoto + '\'' +
                ", mDeleted=" + mDeleted + '\'' +
                ", mPhone='" + mPhone + '\'' +
                ", mLatitude=" + mLatitude + '\'' +
                ", mLongitude=" + mLongitude + '\'' +
                ", mLocation='" + mLocation + '\'' +
                ", mPhotobase='" + mPhotobase + '\'' +
                '}';
    }

}

