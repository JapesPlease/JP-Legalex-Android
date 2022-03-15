package lax.lega.rv.com.legalax.ipay;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import lax.lega.rv.com.legalax.R;


/**
 * Created by tonyb on 3/22/2016.
 */
public class HisResultsListAdapt extends ArrayAdapter<ResultPaymentObject> {
    Context adapterContext;
    ArrayList<ResultPaymentObject> datas;

    public HisResultsListAdapt(Context context, ArrayList<ResultPaymentObject> objects) {
        super(context, R.layout.item_payment_result_history, objects);
        adapterContext = context;
        datas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HisResultsListHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_payment_result_history, parent, false);
            holder = new HisResultsListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HisResultsListHolder) convertView.getTag();
        }
        holder.populateData(datas.get(position));
        return convertView;
    }

    private void notifyDataChanged() {
        this.notifyDataSetChanged();
    }


    /**
     *
     */
    private class HisResultsListHolder {
        TextView tvTransID = null;
        TextView tvTime = null;
        ImageView paymentStateImg;

        ImageView imgVDelete;
        ImageView imgVMoreDetail;

        public HisResultsListHolder(View v) {
            tvTransID = (TextView) v.findViewById(R.id.info_transid_tv);
            tvTime = (TextView) v.findViewById(R.id.info_time_tv);
            paymentStateImg = (ImageView) v.findViewById(R.id.history_item_payment_imgstate);
            imgVDelete = (ImageView) v.findViewById(R.id.history_item_delete);
            imgVMoreDetail = (ImageView) v.findViewById(R.id.history_item_moredetail);
        }

        public void populateData(final ResultPaymentObject dataObject) {
            tvTransID.setText(dataObject.getStrTransID());

            Date date = new Date(dataObject.getlTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm   dd-MM-yyyy", Locale.US);
            tvTime.setText(dateFormat.format(date));

            if (dataObject.getStrTit().toUpperCase().equals("FAILURE")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    paymentStateImg.setImageDrawable(adapterContext.getDrawable(R.drawable.notice));
                } else {
                    paymentStateImg.setImageDrawable(adapterContext.getResources().getDrawable(R.drawable.notice));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    paymentStateImg.setImageDrawable(adapterContext.getDrawable(R.drawable.checked));
                } else {
                    paymentStateImg.setImageDrawable(adapterContext.getResources().getDrawable(R.drawable.checked));
                }
            }


            imgVMoreDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }
    }
}
