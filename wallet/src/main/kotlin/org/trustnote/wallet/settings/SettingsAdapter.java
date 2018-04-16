package org.trustnote.wallet.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;

import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import org.trustnote.wallet.R;
import org.trustnote.wallet.js.JSApi;
import org.trustnote.wallet.walletadmin.NewSeedActivity;

import java.util.List;

import static android.view.LayoutInflater.from;

public class SettingsAdapter
        extends MultiTypeExpandableRecyclerViewAdapter<SettingGroupViewHolder, ChildViewHolder> {

    public static final int FAVORITE_VIEW_TYPE = 3;
    public static final int ARTIST_VIEW_TYPE = 4;
    private final Context context;

    public SettingsAdapter(Context context, List<SettingGroup> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public SettingGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
        return new SettingGroupViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ARTIST_VIEW_TYPE:
                View artist = from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new SettingItemViewHolder(artist);
            case FAVORITE_VIEW_TYPE:
                View favorite =
                        from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new SettingItemViewHolder(favorite);
            default:
                throw new IllegalArgumentException("Invalid viewType");
        }
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {
        int viewType = getItemViewType(flatPosition);
        final SettingItem artist = ((SettingGroup) group).getItems().get(childIndex);
        switch (viewType) {
            case ARTIST_VIEW_TYPE:
                ((SettingItemViewHolder) holder).setTitle(artist.getName());
                break;
            case FAVORITE_VIEW_TYPE:
                ((SettingItemViewHolder) holder).setTitle(artist.getName());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   if (artist != null && artist.action != null) {
                                                       artist.action.run();
                                                   }
                                               }
                                           }
        );
    }

    @Override
    public void onBindGroupViewHolder(SettingGroupViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setGenreTitle(group);
    }

    @Override
    public int getChildViewType(int position, ExpandableGroup group, int childIndex) {
        if (((SettingGroup) group).getItems().get(childIndex).isFavorite()) {
            return FAVORITE_VIEW_TYPE;
        } else {
            return ARTIST_VIEW_TYPE;
        }
    }

    @Override
    public boolean isGroup(int viewType) {
        return viewType == ExpandableListPosition.GROUP;
    }

    @Override
    public boolean isChild(int viewType) {
        return viewType == FAVORITE_VIEW_TYPE || viewType == ARTIST_VIEW_TYPE;
    }
}
