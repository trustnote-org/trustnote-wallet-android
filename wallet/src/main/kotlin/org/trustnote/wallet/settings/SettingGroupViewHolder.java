package org.trustnote.wallet.settings;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.trustnote.wallet.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class SettingGroupViewHolder extends GroupViewHolder {

  private TextView genreName;
  private ImageView arrow;
  private ImageView icon;

  public SettingGroupViewHolder(View itemView) {
    super(itemView);
    genreName = (TextView) itemView.findViewById(R.id.title);
    arrow = (ImageView) itemView.findViewById(R.id.list_item_genre_arrow);
  }

  public void setGenreTitle(ExpandableGroup genre) {
    if (genre instanceof SettingGroup) {
      genreName.setText(genre.getTitle());
      //icon.setBackgroundResource(((SettingGroup) genre).getIconResId());
    }
  }

  @Override
  public void expand() {
    animateExpand();
  }

  @Override
  public void collapse() {
    animateCollapse();
  }

  private void animateExpand() {
    RotateAnimation rotate =
        new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(300);
    rotate.setFillAfter(true);
    arrow.setAnimation(rotate);
  }

  private void animateCollapse() {
    RotateAnimation rotate =
        new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(300);
    rotate.setFillAfter(true);
    arrow.setAnimation(rotate);
  }
}
