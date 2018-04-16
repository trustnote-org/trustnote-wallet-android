package org.trustnote.wallet.settings;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import org.trustnote.wallet.R;

public class SettingItemViewHolder extends ChildViewHolder {

  private TextView titleTextView;

  public SettingItemViewHolder(View itemView) {
    super(itemView);

    titleTextView = (TextView) itemView.findViewById(R.id.title);

  }

  public void setTitle(String name) {
    titleTextView.setText(name);
  }
}
