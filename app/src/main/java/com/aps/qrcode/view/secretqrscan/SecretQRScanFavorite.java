package com.aps.qrcode.view.secretqrscan;

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
import com.aps.qrcode.adapter.ScannedGeneralQRCodeAdapter;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.helper.ZXingHelper;
import com.aps.qrcode.model.GeneralQrScan;
import com.aps.qrcode.util.RecyclerDividerItemDecoration;
import com.aps.qrcode.util.RecyclerItemTouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class SecretQRScanFavorite extends AppCompatActivity {

    private DBHelper db;

    private ScannedGeneralQRCodeAdapter mAdapter;
    private List<GeneralQrScan> generalQrScanList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noQRGenTxtView;

    //QR Operations popup window
    private PopupWindow QRActionPopUp;
    //close buttons on popup windows
    TextView btnClosePopupA, QRGenImgName;
    ImageView QRGenImgView;
    Button editBtn, deleteBtn, addFavoriteBtn, detailsBtn;
    View layout;

    private ZXingHelper zXingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_qr_codes_list);

        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);
        generalQrScanList.addAll(db.getAllFavScannedGeneralSecretQrCodes());

        recyclerView = findViewById(R.id.recycler_vw_qr_codes_holder);
        noQRGenTxtView = findViewById(R.id.txt_vw_empty_qr_list);

        mAdapter = new ScannedGeneralQRCodeAdapter(this, generalQrScanList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyPaymentGeneratedQRCode();

        zXingHelper = new ZXingHelper();

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
    private void toggleEmptyPaymentGeneratedQRCode() {
        // you can check notesList.size() > 0

        if (db.getScannedFavGeneratedGeneralSecretQrCodesCount() > 0) {
            noQRGenTxtView.setVisibility(View.GONE);
        } else {
            noQRGenTxtView.setText("Empty Secret Favorite Scanned QR Code List!");
            noQRGenTxtView.setVisibility(View.VISIBLE);
        }
    }
    //close button for about us popup window
    private View.OnClickListener cancel_button_click_listenerA = new View.OnClickListener() {
        public void onClick(View v) {
            QRActionPopUp.dismiss();

        }
    };

    // updating employee request
    public void updateScannededQRCode(int qrGenId){
        QRActionPopUp.dismiss();
        Intent intent = new Intent(SecretQRScanFavorite.this, ScannedQRCodeContentDisplay.class);
        intent.putExtra("scan_qr_id",qrGenId);
        intent.putExtra("qr_update",true);
        startActivity(intent);

    }

    // show the details of QR Code
    public void showQRCodeDetails(int qrGenId){
        QRActionPopUp.dismiss();
        Intent intent = new Intent(SecretQRScanFavorite.this, ScannedQRCodeContentDisplay.class);
        intent.putExtra("scan_qr_id",qrGenId);
        intent.putExtra("qr_details", true);
        startActivity(intent);
    }

    //delete and employee
    private void showDialogDelete(int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(SecretQRScanFavorite.this);
        dialogDelete.setTitle("Warning!!!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    QRActionPopUp.dismiss();
                    db.deleteScannedGeneralSecretQrCodeById(idRecord);
                    Toast.makeText(SecretQRScanFavorite.this, "Deleted successfully!", Toast.LENGTH_LONG).show();
                    updateRecordListner();
                } catch (Exception e) {
                    Log.e("Delete Error", e.getMessage());
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
            LayoutInflater inflater = (LayoutInflater) SecretQRScanFavorite.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.recycler_item_on_click_popup, (ViewGroup) findViewById(R.id.popup_element));
            QRActionPopUp = new PopupWindow(layout, (int) (width * .8), (int) (height * .7), true);
            QRActionPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
            btnClosePopupA = (TextView) layout.findViewById(R.id.txt_vw_close_qr_action_popup);
            QRGenImgName = (TextView) layout.findViewById(R.id.txt_vw_qr_code_img_name);
            QRGenImgView = (ImageView) layout.findViewById(R.id.img_vw_qr_img);
            GeneralQrScan non_payment_qr_scan_index = generalQrScanList.get(position);
            String qr_gen_img_name = non_payment_qr_scan_index.getQrImgName();
            QRGenImgName.setText(qr_gen_img_name);
            QRGenImgView.setImageBitmap(BitmapFactory.decodeByteArray(non_payment_qr_scan_index.getQrImg(), 0, non_payment_qr_scan_index.getQrImg().length));

            btnClosePopupA.setOnClickListener(cancel_button_click_listenerA);

            editBtn = (Button) layout.findViewById(R.id.edit_qr_code);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateScannededQRCode(non_payment_qr_scan_index.getQrId());
                }
            });

            deleteBtn = (Button) layout.findViewById(R.id.btn_qr_delete);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDelete(non_payment_qr_scan_index.getQrId());
                }
            });

            addFavoriteBtn = (Button) layout.findViewById(R.id.btn_add_qr_to_fav);
            if(non_payment_qr_scan_index.getFavoriteQr() == 0) {
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QRActionPopUp.dismiss();
                        db.addScannedGeneralSecretQrCodeToFavList(non_payment_qr_scan_index.getQrId());
                        Toast.makeText(SecretQRScanFavorite.this, "QR Code added to favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }else{
                addFavoriteBtn.setText("Remove");
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QRActionPopUp.dismiss();
                        db.removeScannedGeneralSecretQrCodeFromFavList(non_payment_qr_scan_index.getQrId());
                        Toast.makeText(SecretQRScanFavorite.this, "QR Code removed from favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }

            detailsBtn = (Button) layout.findViewById(R.id.btn_qr_details);
            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQRCodeDetails(non_payment_qr_scan_index.getQrId());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecordListner(){
        //get all data from sqlite
        generalQrScanList.clear();
        generalQrScanList.addAll(db.getAllFavScannedGeneralSecretQrCodes());
        mAdapter = new ScannedGeneralQRCodeAdapter(this, generalQrScanList);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);

    }
}
