package org.trustnote.wallet.settings;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SettingGroup extends ExpandableGroup<SettingItem> {

  private int iconResId;

  public SettingGroup(String title, List<SettingItem> items, int iconResId) {
    super(title, items);
    this.iconResId = iconResId;
  }

  public SettingGroup(String title, List<SettingItem> items) {
    super(title, items);
  }

  public int getIconResId() {
    return iconResId;
  }

}

