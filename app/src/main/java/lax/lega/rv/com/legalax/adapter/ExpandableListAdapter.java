package lax.lega.rv.com.legalax.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lax.lega.rv.com.legalax.R;
import lax.lega.rv.com.legalax.common.DateUtils;
import lax.lega.rv.com.legalax.common.MySharedPreference;
import lax.lega.rv.com.legalax.pojos.ExpandableListData;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    Activity activity;
    List<ExpandableListData> navigationItemDetails;
    MySharedPreference mySharedPreference;
    ClickHandler handler;

    public ExpandableListAdapter(Activity activity, List<ExpandableListData> navigationItemDetails) {
        this.activity = activity;
        this.navigationItemDetails=navigationItemDetails;
        mySharedPreference = new MySharedPreference(activity);
    }

    @Override
    public int getGroupCount() {
        return navigationItemDetails.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  navigationItemDetails.get(groupPosition).getData().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.adapter_navigation, null);
        }

        TextView textViewItemName=convertView.findViewById(R.id.textViewItemName);
        ImageView imageViewIndicator=convertView.findViewById(R.id.imageViewIndicator);
        textViewItemName.setText(navigationItemDetails.get(groupPosition).getItemName());


        if(isExpanded){
            imageViewIndicator.setImageResource(R.drawable.ic_drop_up);
        }else{
            imageViewIndicator.setImageResource(R.drawable.ic_drop_down);
        }

        return convertView;
    }


    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.booked_appointment_item, null);
        }

        TextView txt_name=convertView.findViewById(R.id.txt_name);
        TextView txt_concern=convertView.findViewById(R.id.txt_concern);
        TextView txt_time=convertView.findViewById(R.id.txt_time);
        TextView txt_place=convertView.findViewById(R.id.txt_place);
        RelativeLayout rl_appointment=convertView.findViewById(R.id.rl_appointment);


        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(navigationItemDetails.get(groupPosition).getData().get(childPosition).getBookingDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*Date current = new Date();
        if (newDate.before(current)) {
            rl_appointment.setVisibility(View.VISIBLE);
        } else {
            rl_appointment.setVisibility(View.GONE);
        }*/


        rl_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             handler.removeClick(groupPosition,childPosition);
            }
        });



        if(mySharedPreference.getString("role").equals("1")) {
            txt_name.setText(navigationItemDetails.get(groupPosition).getData().get(childPosition).getReguserFullname()+ " booked "+navigationItemDetails.get(groupPosition).getData().get(childPosition).getLawyerFullname());
        }else{
            txt_name.setText(navigationItemDetails.get(groupPosition).getData().get(childPosition).getUser().getName()+ " "+navigationItemDetails.get(groupPosition).getData().get(childPosition).getUser().getLastName());
        }

        txt_concern.setText(navigationItemDetails.get(groupPosition).getData().get(childPosition).getConcern());

        txt_time.setText(navigationItemDetails.get(groupPosition).getData().get(childPosition).getBookingDate()+" at "+ DateUtils.Convert24to12(navigationItemDetails.get(groupPosition).getData().get(childPosition).getTime()));
        txt_place.setText(navigationItemDetails.get(groupPosition).getData().get(childPosition).getUser().getLocation());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);


    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);

    }

    public  interface  ClickHandler{
        void removeClick(int groupPosition, int childPosition);
    }

   public void clickListener(ClickHandler handler){
        this.handler=handler;

    }

}
