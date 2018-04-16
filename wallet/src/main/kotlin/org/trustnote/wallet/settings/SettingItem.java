package org.trustnote.wallet.settings;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingItem implements Parcelable {

  private String name;
  private boolean isFavorite;
  //TODO: private action.
  public Runnable action;

  public SettingItem(String name, boolean isFavorite) {
    this.name = name;
    this.isFavorite = isFavorite;
  }

  public SettingItem(String name) {
    this.name = name;
    this.isFavorite = false;
  }

  protected SettingItem(Parcel in) {
    name = in.readString();
  }

  public String getName() {
    return name;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SettingItem)) return false;

    SettingItem artist = (SettingItem) o;

    if (isFavorite() != artist.isFavorite()) return false;
    return getName() != null ? getName().equals(artist.getName()) : artist.getName() == null;

  }

  @Override
  public int hashCode() {
    int result = getName() != null ? getName().hashCode() : 0;
    result = 31 * result + (isFavorite() ? 1 : 0);
    return result;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<SettingItem> CREATOR = new Creator<SettingItem>() {
    @Override
    public SettingItem createFromParcel(Parcel in) {
      return new SettingItem(in);
    }

    @Override
    public SettingItem[] newArray(int size) {
      return new SettingItem[size];
    }
  };
}

