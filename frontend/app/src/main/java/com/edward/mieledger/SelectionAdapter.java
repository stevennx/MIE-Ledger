package com.edward.mieledger;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class SelectionAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList<String> nameArray;
    public ArrayList<String> checkedBoxes = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public SelectionAdapter(Activity context, ArrayList<String> nameArray) {
        super(context, R.layout.listview_selection, nameArray);

        this.context = context;
        this.nameArray = nameArray;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_selection, null);

        CheckBox nameCheckBox = rowView.findViewById(R.id.checkBox);
        nameCheckBox.setText(nameArray.get(position));
        if (checkedBoxes.contains(nameArray.get(position))) {
            nameCheckBox.setChecked(true);
        }
        nameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!checkedBoxes.contains(nameArray.get(position))) {
                        checkedBoxes.add(nameArray.get(position));
                    }
                } else {
                    if (checkedBoxes.contains(nameArray.get(position))) {
                        checkedBoxes.remove(nameArray.get(position));
                    }
                }
            }
        });

        return rowView;
    }

}
