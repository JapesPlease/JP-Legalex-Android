package lax.lega.rv.com.legalax.ipay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipay.obj.MCTokenizationRet;

import lax.lega.rv.com.legalax.R;


public class PaymentResultMessageDialog extends Dialog implements View.OnClickListener {

    ImageView imageViewExitButton;

    TextView tvPaymentResultExtra;
    TextView tvResultTitle;
    TextView tvResultInfo;

    TextView tvActionType;
    TextView tvTokenId;
    TextView tvCCname;
    TextView tvCCNo;
    TextView tvSBankName;
    TextView tvSCountry;
    TextView tvBindCardErrDesc;


    ResultPaymentMessage resultPaymentMessage;
    MCTokenizationRet mcTokenizationRet;
    Context dialogContext;

    public PaymentResultMessageDialog(Context context, ResultPaymentMessage resultMessage) {
        super(context);
        resultPaymentMessage = resultMessage;
        dialogContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        setContentView(R.layout.layout_payment_result_message);

        imageViewExitButton = findViewById(R.id.payment_result_close_button);
        imageViewExitButton.setOnClickListener(this);
        tvPaymentResultExtra = findViewById(R.id.payment_extra_result);
        tvPaymentResultExtra.setText(resultPaymentMessage.getStrResultExtra());

        tvResultTitle = findViewById(R.id.payment_title_result);
        tvResultTitle.setText(resultPaymentMessage.getStrResultTitle());
        if (resultPaymentMessage.getStrResultTitle().toLowerCase().equals("failure"))
        {
            tvResultTitle.setTextColor(dialogContext.getResources().getColor(R.color.red1));
        } else {
            tvResultTitle.setTextColor(dialogContext.getResources().getColor(R.color.green1));
        }

        tvResultInfo = findViewById(R.id.payment_info_result);
        tvResultInfo.setText(resultPaymentMessage.getStrResultInfo());

        mcTokenizationRet = resultPaymentMessage.getMcTokenizationRet();
        tvActionType = findViewById(R.id.tvActionType);
        tvTokenId = findViewById(R.id.tvTokenId);
        tvCCname = findViewById(R.id.tvStrCCName);
        tvCCNo = findViewById(R.id.tvStrCCNo);
        tvSBankName = findViewById(R.id.tvSBankName);
        tvSCountry = findViewById(R.id.tvCountry);
        tvBindCardErrDesc = findViewById(R.id.tvBindCardErrDesc);

        if (mcTokenizationRet == null) {
            tvActionType.setVisibility(View.GONE);
            tvTokenId.setVisibility(View.GONE);
            tvCCname.setVisibility(View.GONE);
            tvCCNo.setVisibility(View.GONE);
            tvSBankName.setVisibility(View.GONE);
            tvSCountry.setVisibility(View.GONE);
            tvBindCardErrDesc.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(mcTokenizationRet.getActionType())) {
                tvActionType.setVisibility(View.VISIBLE);
                tvActionType.setText(" ActionType: " + mcTokenizationRet.getActionType());
            }
            if (!TextUtils.isEmpty(mcTokenizationRet.getTokenId())) {
                tvTokenId.setVisibility(View.VISIBLE);
                tvTokenId.setText(" TokenID: " + mcTokenizationRet.getTokenId());
            }
            if (!TextUtils.isEmpty(mcTokenizationRet.getCcName())) {
                tvCCname.setVisibility(View.VISIBLE);
                tvCCname.setText("CCName: " + mcTokenizationRet.getCcName());
            }
            if (!TextUtils.isEmpty(mcTokenizationRet.getCcNo())) {
                tvCCNo.setVisibility(View.VISIBLE);
                tvCCNo.setText(" CCNo: " + mcTokenizationRet.getCcNo());
            }
            if (!TextUtils.isEmpty(mcTokenizationRet.getSBankName())) {
                tvSBankName.setVisibility(View.VISIBLE);
                tvSBankName.setText(" SBankName: " + mcTokenizationRet.getSBankName());
            }
            if (!TextUtils.isEmpty(mcTokenizationRet.getSCountry())) {
                tvSCountry.setVisibility(View.VISIBLE);
                tvSCountry.setText(" SCountry: " + mcTokenizationRet.getSCountry());
            }
            if (!TextUtils.isEmpty(mcTokenizationRet.getBindCardErrDesc())) {
                tvBindCardErrDesc.setVisibility(View.VISIBLE);
                tvBindCardErrDesc.setText(" BindCardErrDesc: " + mcTokenizationRet.getBindCardErrDesc());
            }
        }

        Log.v("testfairy-checkpoint", "Display Payment Result Message Dialog");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_result_close_button:
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
