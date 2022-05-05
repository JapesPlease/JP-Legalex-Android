package lax.lega.rv.com.legalax.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.sinch.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_proposal_bidding.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.MySharedPreference
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.BidList
import lax.lega.rv.com.legalax.pojos.BidResponse
import lax.lega.rv.com.legalax.utils.doubleToStringNoDecimal

class ProposalBiddingFragment(var bidList: BidList) : Fragment() {

    lateinit var sharedPreference: MySharedPreference
    var price = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proposal_bidding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listeners()
        setData()
    }

    private fun init() {
        sharedPreference = MySharedPreference(activity)
//      val text="10000"
//      etPrice.setText(doubleToStringNoDecimal(text.toDouble()))
    }

    private fun setData() {
        tvName.text = bidList.userInfo?.name
        tvTitle.text = bidList.title
        tvDescription.text = bidList.desc
        Picasso.with(requireActivity()).load(Utils.IMAGESURL + bidList.userInfo?.profileImage.toString()).fit().placeholder(R.drawable.icn_user_large).into(ivUserImage)
    }

    private fun listeners() {
        tvCancel.setOnClickListener { activity?.onBackPressed() }
        ivBack.setOnClickListener { activity?.onBackPressed() }

        tvBid.setOnClickListener {
            if (etPrice.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter offer amount", Toast.LENGTH_LONG).show()
            } else if (etPrice.text.toString().replace(",","").toInt() in 1..99) {
                Toast.makeText(requireContext(),
                        "Please enter a valid offer. Offer should be 0 or greater than 100", Toast.LENGTH_LONG).show()
            } else {
                createBidApi(bidList.id.toString(), etPrice.text.toString().replace(",", ""))
            }
        }

        etPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    if (price != etPrice.text.toString()) {

                        // replace comma
                        val typedText = etPrice.text.toString().replace(", $", "");
                        price = doubleToStringNoDecimal(typedText.toDouble()).toString()
                        etPrice.setText(price)
                        val pos: Int = etPrice.text.length
                        etPrice.setSelection(pos)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }


    // create bid api
    private fun createBidApi(problemId: String, amount: String) {
        Log.i("CreateBid","id:: "+problemId)
        Utils.instance.showProgressBar(requireActivity())
        AndroidNetworking.post(Utils.BASE_URL + Utils.CREATE_BID)
                .addHeaders("Authorization", "Bearer " + sharedPreference.getString("access_token"))
                .addHeaders("Accept", "application/json")
                .addBodyParameter("problem_id", problemId)
                .addBodyParameter("bid_amount", amount)
                .addBodyParameter("notes", etNotes.text.toString().trim())
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(BidResponse::class.java, object : ParsedRequestListener<BidResponse> {
                    override fun onResponse(user: BidResponse) {
                        Log.i("CreateBid","Response:: "+Gson().toJson(user))
                        Utils.instance.dismissProgressDialog()
                        if (user.success == true) {
                            Toast.makeText(requireContext(), user.message, Toast.LENGTH_LONG).show()
                            activity?.onBackPressed()
                        } else {
                            Toast.makeText(requireContext(), user.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        Utils.instance.dismissProgressDialog()
                        Log.i("CreateBid","Error:: "+Gson().toJson(anError))
                        Toast.makeText(requireContext(), "Unable to connect server", Toast.LENGTH_LONG).show()
                    }
                })
    }
}