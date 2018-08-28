package org.trustnote.superwallet.widget;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

//TODO: to kotlin code.
public class WordAdapter extends ArrayAdapter<String> {

    List<String> items, tempItems, suggestions;

    public WordAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        tempItems = new ArrayList(this.items);
        suggestions = new ArrayList();
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((String) resultValue);
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String word : tempItems) {
                    if (word.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(word);
                        if (suggestions.size() >= 3) {
                            break;
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (String String : filterList) {
                    add(String);
                    notifyDataSetChanged();
                }
            }
        }
    };
}