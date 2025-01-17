package com.aps.qrcode.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aps.qrcode.R;
import com.aps.qrcode.model.PaymentQrScan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ScannedPaymentQRCodeAdapter extends RecyclerView.Adapter<ScannedPaymentQRCodeAdapter.MyViewHolder> {

    private Context mContext;
    // Retrieving all the generated payment QR codes and putting them in merchanGenList
    private List<PaymentQrScan> mPaymentQrScannedList;

    public ScannedPaymentQRCodeAdapter(Context mContext, List<PaymentQrScan> paymentQrScans) {
        this.mContext = mContext;
        this.mPaymentQrScannedList = paymentQrScans;
    }

    /**
     * MyViewHolder inflate the custom recycler item which is created for holding the QR Code image
     * details which are displayed in history
     *
     * @param parent
     * @param viewType
     * @return the qr cod item
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_recycler_item_holder, parent, false);
        return new MyViewHolder(itemView);
    }

    /**
     * onBindViewHolder binds the  QR Code image in recycler view position wise.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PaymentQrScan paymentQrScan = mPaymentQrScannedList.get(position);
        holder.QrImgView.setImageBitmap(BitmapFactory.decodeByteArray(paymentQrScan.getQrImg(), 0, paymentQrScan.getQrImg().length));
        holder.QrNameTxtView.setText(paymentQrScan.getQrImgName());

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(paymentQrScan.getTimestamp()));
    }

    /**
     * Get total number of generated QR code images
     *
     * @return: number of generated QR Codes
     */
    @Override
    public int getItemCount() {
        return mPaymentQrScannedList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    /**
     * Each item of recycler view holds 3 elements
     * 1. QR Image: QR Code image.
     * 2. Name of QR Code: the name which is assigned by the merchant after generating QR Code.
     * 3. The date of which the QR Code has been created at.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView QrImgView;
        public TextView QrNameTxtView;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            QrImgView = view.findViewById(R.id.img_vw_qr_img);
            QrNameTxtView = view.findViewById(R.id.txt_vw_qr_name);
            timestamp = view.findViewById(R.id.txt_vw_qr_timestamp);
        }
    }
}