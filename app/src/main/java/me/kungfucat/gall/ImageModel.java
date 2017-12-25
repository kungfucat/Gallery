package me.kungfucat.gall;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by harsh on 12/13/17.
 */

public class ImageModel implements Parcelable {
    String title;
    String url;
    String date;

    //video attributes
    Boolean isAVideo = false;

    public ImageModel() {
    }

    public ImageModel(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.date = in.readString();
        this.isAVideo = in.readByte() != 0;
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public Boolean getAVideo() {
        return isAVideo;
    }

    public void setAVideo(Boolean AVideo) {
        isAVideo = AVideo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
        parcel.writeString(date);
        parcel.writeByte((byte) (isAVideo ? 1 : 0));
    }
}
