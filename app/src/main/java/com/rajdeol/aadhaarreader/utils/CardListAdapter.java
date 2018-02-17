package com.rajdeol.aadhaarreader.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rajdeol.aadhaarreader.R;
import com.rajdeol.aadhaarreader.SavedAadhaarCardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardListAdapter extends ArrayAdapter<ArrayList> {

    private final SavedAadhaarCardActivity context;
    private final ArrayList<JSONObject> values;

    public CardListAdapter(SavedAadhaarCardActivity context, ArrayList values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate the row with layout
        View rowView = inflater != null ? inflater.inflate(R.layout.aadharcard_list, parent, false) : null;

        // initiate UI elements
        assert rowView != null;
        TextView tv_sd_uid = rowView.findViewById(R.id.tv_sd_uid);
        TextView tv_sd_name = rowView.findViewById(R.id.tv_sd_name);
        TextView tv_sd_gender = rowView.findViewById(R.id.tv_sd_gender);
        TextView tv_sd_yob = rowView.findViewById(R.id.tv_sd_yob);
        TextView tv_sd_co = rowView.findViewById(R.id.tv_sd_co);
        TextView tv_sd_vtc = rowView.findViewById(R.id.tv_sd_vtc);
        TextView tv_sd_po = rowView.findViewById(R.id.tv_sd_po);
        TextView tv_sd_dist = rowView.findViewById(R.id.tv_sd_dist);
        TextView tv_sd_state = rowView.findViewById(R.id.tv_sd_state);
        TextView tv_sd_pc = rowView.findViewById(R.id.tv_sd_pc);
        TextView tv_delete_card = rowView.findViewById(R.id.tv_delete_card);
        TextView tv_sd_dob = rowView.findViewById(R.id.tv_sd_dob);


        // populate UI elements
        try {
            JSONObject jObj = values.get(position);
            String uid = jObj.getString(DataAttributes.AADHAR_UID_ATTR);
            String name = jObj.getString(DataAttributes.AADHAR_NAME_ATTR);
            String gender = jObj.getString(DataAttributes.AADHAR_GENDER_ATTR);
            String yearOfBirth = jObj.getString(DataAttributes.AADHAR_YOB_ATTR);
            String careOf = jObj.getString(DataAttributes.AADHAR_CO_ATTR);
            String villageTehsil = jObj.getString(DataAttributes.AADHAR_VTC_ATTR);
            String postOffice = jObj.getString(DataAttributes.AADHAR_PO_ATTR);
            String district = jObj.getString(DataAttributes.AADHAR_DIST_ATTR);
            String state = jObj.getString(DataAttributes.AADHAR_STATE_ATTR);
            String postCode = jObj.getString(DataAttributes.AADHAR_PC_ATTR);
            String dob = jObj.getString(DataAttributes.AADHAR_DOB_ATTR);
            // update UI Elements
            tv_sd_uid.setText(uid);
            tv_sd_name.setText(name);
            tv_sd_gender.setText(gender);
            tv_sd_yob.setText(yearOfBirth);
            tv_sd_co.setText(careOf);
            tv_sd_vtc.setText(villageTehsil);
            tv_sd_po.setText(postOffice);
            tv_sd_dist.setText(district);
            tv_sd_state.setText(state);
            tv_sd_pc.setText(postCode);
            tv_sd_dob.setText(dob);
            tv_delete_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jObj = values.get(position);
                    try {
                        String cardUid = jObj.getString(DataAttributes.AADHAR_UID_ATTR);
                        context.deleteCard(cardUid);
                        values.remove(position);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }
}
