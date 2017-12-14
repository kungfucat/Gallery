package me.kungfucat.gall;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by harsh on 12/14/17.
 */

public class FoldersModel implements Parcelable {
    String foldersName;
    ArrayList<ImageModel> imageModelsList=new ArrayList<>();

    public FoldersModel() {

    }

    public FoldersModel(Parcel in) {
        this.foldersName = in.readString();
        this.imageModelsList = in.readArrayList(null);
    }

    public static final Parcelable.Creator<ImageModel> CREATOR = new Parcelable.Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public void setFoldersName(String foldersName) {
        this.foldersName = foldersName;
    }

    public String getFoldersName() {
        return foldersName;
    }

    public void setImageModelsList(ArrayList<ImageModel> imageModelsList) {
        this.imageModelsList = imageModelsList;
    }

    public ArrayList<ImageModel> getImageModelsList() {
        return imageModelsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(foldersName);
        parcel.writeList(imageModelsList);
    }
}
