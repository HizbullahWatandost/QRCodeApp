package com.aps.qrcode.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aps.qrcode.R;


/**
 * This ScannerFragment is used to scan payment QR Code.
 * NOTE: The QR Code image must be as per the APS rules and standard.
 * After scanning QR Code, it passes the result to another activity
 * to handle display its content to the user and proceed the payment,
 * and the user will be also able to save it in table.
 */
public class QRScannerFragment extends Fragment {
    public QRScannerFragment() {
        // Required empty public constructor
    }

    Button qr_scan_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrscanner, container, false);

        qr_scan_btn = view.findViewById(R.id.btn_qr_scan);
        qr_scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requesting the activity to handle the QR Code scanning
                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.putExtra("qr_scan_request","Scan_QRCodePlease");
                startActivity(intent);
            }
        });
        return  view;
        }

}