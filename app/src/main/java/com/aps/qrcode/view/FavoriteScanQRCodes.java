package com.aps.qrcode.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.adapter.ScannedPaymentQRCodeAdapter;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.PaymentQrScan;
import com.aps.qrcode.util.RecyclerDividerItemDecoration;
import com.aps.qrcode.util.RecyclerItemTouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class FavoriteScanQRCodes extends AppCompatActivity {

    private DBHelper db;
    private ZXingHelper zXingHelper;

    private ScannedPaymentQRCodeAdapter mAdapter;
    private List<PaymentQrScan> scannedQRList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noFavScannedQRTxtView;

    //QR Operations popup window
    private PopupWindow QRActionPopUp;
    //close buttons on popup windows
    TextView btnClosePopupA, QRScanImgName;
    ImageView QRScannedImgView;
    Button editBtn, deleteBtn, addFavoriteBtn, detailsBtn;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_qr_codes_list);

        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);
        zXingHelper = new ZXingHelper();
        scannedQRList.addAll(db.getAllFavScannedPaymentQrCodes());

        recyclerView = findViewById(R.id.recycler_vw_qr_codes_holder);
        noFavScannedQRTxtView = findViewById(R.id.txt_vw_empty_qr_list);

        mAdapter = new ScannedPaymentQRCodeAdapter(this, scannedQRList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyPaymentScannedQRCode();
        updateRecordListner();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerItemTouchListener(this,
                recyclerView, new RecyclerItemTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                qrActionPopup(position);
                //showActionsDialog(position);
            }
        }));

    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyPaymentScannedQRCode() {
        // you can check notesList.size() > 0

        if (db.getFavScannedPaymentQrCodesCount() > 0) {
            noFavScannedQRTxtView.setVisibility(View.GONE);
        } else {
            noFavScannedQRTxtView.setText("No Favorite Scanned QR Code!");
            noFavScannedQRTxtView.setVisibility(View.VISIBLE);
        }
    }

    //close button for about us popup window
    private View.OnClickListener cancel_button_click_listenerA = new View.OnClickListener() {
        public void onClick(View v) {
            QRActionPopUp.dismiss();

        }
    };

    //updating Scanned QR Code request
    public void updateScannedQRCode(int qrScanId, String scannedQRCodeImgName,byte[] qrImg ){
        QRActionPopUp.dismiss();
        Intent intent = new Intent(FavoriteScanQRCodes.this, QRScannedUpdateActivity.class);
        intent.putExtra("qr_scanned_id",qrScanId);
        intent.putExtra("qr_name",scannedQRCodeImgName);
        intent.putExtra("QrImgView",qrImg);
        startActivity(intent);

    }

    // show the details of QR Code
    public void showQRCodeDetails(int qrScanId, String qrUpdatedName, String qrContent){
        QRActionPopUp.dismiss();
        Intent intent = new Intent(FavoriteScanQRCodes.this, QRScannedUpdateActivity.class);
        intent.putExtra("qr_gen_id",qrScanId);
        intent.putExtra("qr_update",qrUpdatedName);
        intent.putExtra("favorite",qrContent);
        startActivity(intent);
    }

    //delete a scanned QR Code
    private void showDialogDelete(int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(FavoriteScanQRCodes.this);
        dialogDelete.setTitle("Warning!!!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    QRActionPopUp.dismiss();
                    db.deleteScannedPaymentQrCodeById(idRecord);
                    Toast.makeText(FavoriteScanQRCodes.this, "Deleted successfully!",Toast.LENGTH_LONG).show();
                    updateRecordListner();
                }catch (Exception e){
                    Log.e("Delete Error",e.getMessage());
                }
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QRActionPopUp.dismiss();
                dialog.dismiss();
            }
        });
        dialogDelete.show();

    }

    /**
     * the method which popup the QR Operations window
     */
    private void qrActionPopup(final int position) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            //getWindow().setLayout((int)(width*.8),(int)(height*.6));

            //we need to take the instance of layoutinflator
            LayoutInflater inflater = (LayoutInflater) FavoriteScanQRCodes.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.recycler_item_on_click_popup, (ViewGroup) findViewById(R.id.popup_element));
            QRActionPopUp = new PopupWindow(layout, (int) (width * .8), (int) (height * .7), true);
            QRActionPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
            btnClosePopupA = (TextView) layout.findViewById(R.id.txt_vw_close_qr_action_popup);
            QRScanImgName = (TextView) layout.findViewById(R.id.txt_vw_qr_code_img_name);
            QRScannedImgView = (ImageView) layout.findViewById(R.id.img_vw_qr_img);
            PaymentQrScan qr_scan_index = scannedQRList.get(position);
            String qr_gen_img_name = qr_scan_index.getQrImgName();
            QRScanImgName.setText(qr_gen_img_name);
            QRScannedImgView.setImageBitmap(BitmapFactory.decodeByteArray(qr_scan_index.getQrImg(), 0, qr_scan_index.getQrImg().length));

            btnClosePopupA.setOnClickListener(cancel_button_click_listenerA);

            editBtn = (Button) layout.findViewById(R.id.edit_qr_code);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateScannedQRCode(qr_scan_index.getQrId(), qr_scan_index.getQrImgName(),qr_scan_index.getQrImg());
                }
            });

            deleteBtn = (Button) layout.findViewById(R.id.btn_qr_delete);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDelete(qr_scan_index.getQrId());
                }
            });

            addFavoriteBtn = (Button) layout.findViewById(R.id.btn_add_qr_to_fav);
            if(qr_scan_index.getFavQrScan() == 0) {
                addFavoriteBtn.setText("Favorite");
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QRActionPopUp.dismiss();
                        db.addScannedPaymentQrCodeToFavList(qr_scan_index.getQrId());
                        Toast.makeText(FavoriteScanQRCodes.this, "QR Code added to favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }else{
                addFavoriteBtn.setText("Remove");
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QRActionPopUp.dismiss();
                        db.removeScannedPaymentQrCodeFromFavList(qr_scan_index.getQrId());
                        Toast.makeText(FavoriteScanQRCodes.this, "QR Code removed from favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }

            detailsBtn = (Button) layout.findViewById(R.id.btn_qr_details);
            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FavoriteScanQRCodes.this, QRScanResultDisplayActivity.class);
                    intent.putExtra("scanned_qr_code_details",zXingHelper.qr2Txt(QRScannedImgView));
                    startActivity(intent);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecordListner(){
        //get all data from sqlite
        scannedQRList.clear();
        scannedQRList.addAll(db.getAllFavScannedPaymentQrCodes());
        mAdapter = new ScannedPaymentQRCodeAdapter(this, scannedQRList);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);

    }
}