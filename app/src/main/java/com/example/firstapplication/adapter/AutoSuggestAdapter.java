package com.example.firstapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Assistant;

import java.util.ArrayList;
import java.util.List;

public class AutoSuggestAdapter extends ArrayAdapter<Assistant> implements Filterable {
    private static final String TAG = "AutoSuggestAdapter";

    private List<Assistant> mListData, suggestion;
    private LayoutInflater layoutInflater;

    public AutoSuggestAdapter(@NonNull Context context, int resource, List<Assistant> mListData) {
        super(context, resource);
        this.mListData = mListData;
        suggestion = new ArrayList<>();
        layoutInflater  = LayoutInflater.from(context);
    }

    public void setListData(List<Assistant> mListData) {
        this.mListData = mListData;
    }

    @Override
    public Assistant getItem(int position) {
        try {
            return suggestion.get(position);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Assistant entry = getItem(position);

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.layout_suggest_assistants, null);

            holder.txtInitial = convertView.findViewById(R.id.txt_initial);
            holder.txtEmail = convertView.findViewById(R.id.txt_email);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        assert entry != null;
        holder.txtInitial.setText(entry.getInitial());
        holder.txtEmail.setText(entry.getEmail());

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = mListData;
                    results.count = mListData.size();
                } else {
                    suggestion.clear();

                    String filterString = constraint.toString().toLowerCase();

                    for (Assistant data : mListData)
                        if (data.getInitial().contains(filterString))
                            suggestion.add(data);

                    Log.d("Filtering", "performFiltering: " + filterString + "#" + suggestion.size());
                    results.values = suggestion;
                    results.count = suggestion.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                List<Assistant> filterList = (ArrayList<Assistant>) results.values;
                if (results.count > 0) {
                    clear();
                    for (Assistant filter : filterList) {
                        Log.d(TAG, "publishResults: " + filter.getInitial());
                        add(filter);
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }

    private class ViewHolder {
        TextView txtEmail, txtInitial;
    }
}