package com.aps.qrcode.view.secretqrgen;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.aps.qrcode.R;
import com.aps.qrcode.adapter.GeneratedGeneralQRCodeAdapter;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.model.GeneralQrGen;
import com.aps.qrcode.util.RecyclerDividerItemDecoration;
import com.aps.qrcode.util.RecyclerItemTouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


public class SecretQRGenHistory extends AppCompatActivity {

    private DBHelper db;

    private GeneratedGeneralQRCodeAdapter mAdapter;
    private List<GeneralQrGen> generalQrGenList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noQRGenTxtView;

    //close buttons on popup windows
    TextView closePopupTxtView, QrImgNameTxtView;
    ImageView QrImgView;
    //QR Operations popup window
    private PopupWindow QrActionPopUp;
    Button editBtn, deleteBtn, addFavoriteBtn, detailsBtn;
    View layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_qr_codes_list);


        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);
        generalQrGenList.addAll(db.getAllGeneratedGeneralSecretQrCodes());

        recyclerView = findViewById(R.id.recycler_vw_qr_codes_holder);
        noQRGenTxtView = findViewById(R.id.txt_vw_empty_qr_list);

        mAdapter = new GeneratedGeneralQRCodeAdapter(this, generalQrGenList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyPaymentGeneratedQRCode();

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
            }
        }));
    }

    //close button for about us popup window
    private View.OnClickListener cancel_button_click_listenerA = new View.OnClickListener() {
        public void onClick(View v) {
            QrActionPopUp.dismiss();

        }
    };

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyPaymentGeneratedQRCode() {
        // you can check notesList.size() > 0

        if (db.getGeneratedGeneralSecretQrCodesCount() > 0) {
            noQRGenTxtView.setVisibility(View.GONE);
        } else {
            noQRGenTxtView.setText(getString(R.string.empty_generated_qr_codes));
            noQRGenTxtView.setVisibility(View.VISIBLE);
        }
    }

    // updating employee request
    public void updateGeneratedQRCode(int qrGenId, int favorite){
        QrActionPopUp.dismiss();
        Intent intent = new Intent(SecretQRGenHistory.this, SecretQRContentDisplay.class);
        intent.putExtra("qr_gen_id",qrGenId);
        intent.putExtra("qr_update",true);
        startActivity(intent);

    }

    // show the details of QR Code
    public void showQRCodeDetails(int qrGenId){
        QrActionPopUp.dismiss();
        Intent intent = new Intent(SecretQRGenHistory.this, SecretQRCodeDetails.class);
        intent.putExtra("secret_qr_details_by_id",qrGenId);
        startActivity(intent);
    }

    //delete and employee
    private void showDialogDelete(int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(SecretQRGenHistory.this);
        dialogDelete.setTitle("Warning!!!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("Yes", (dialog, which) -> {
            try {
                QrActionPopUp.dismiss();
                db.deleteGeneratedGeneralSecretQrCodeById(idRecord);
                Toast.makeText(SecretQRGenHistory.this, "Deleted successfully!", Toast.LENGTH_LONG).show();
                updateRecordListner();
            } catch (Exception e) {
                Log.e("Delete Error", e.getMessage());
            }
        });

        dialogDelete.setNegativeButton("Cancel", (dialog, which) -> {
            QrActionPopUp.dismiss();
            dialog.dismiss();
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
            LayoutInflater inflater = (LayoutInflater) SecretQRGenHistory.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.recycler_item_on_click_popup, findViewById(R.id.popup_element));
            QrActionPopUp = new PopupWindow(layout, (int) (width * .8), (int) (height * .7), true);
            QrActionPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
            closePopupTxtView = layout.findViewById(R.id.txt_vw_close_qr_action_popup);
            QrImgNameTxtView = layout.findViewById(R.id.txt_vw_qr_code_img_name);
            QrImgView = layout.findViewById(R.id.img_vw_qr_img);
            GeneralQrGen non_payment_qr_gen_index = generalQrGenList.get(position);
            String qr_gen_img_name = non_payment_qr_gen_index.getQrImgName();
            QrImgNameTxtView.setText(qr_gen_img_name);
            QrImgView.setImageBitmap(BitmapFactory.decodeByteArray(non_payment_qr_gen_index.getQrImg(), 0, non_payment_qr_gen_index.getQrImg().length));

            closePopupTxtView.setOnClickListener(cancel_button_click_listenerA);

            editBtn = layout.findViewById(R.id.edit_qr_code);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateGeneratedQRCode(non_payment_qr_gen_index.getQrId(), non_payment_qr_gen_index.getFavoriteQr());
                }
            });

            deleteBtn = layout.findViewById(R.id.btn_qr_delete);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDelete(non_payment_qr_gen_index.getQrId());
                }
            });

            addFavoriteBtn = layout.findViewById(R.id.btn_add_qr_to_fav);
            if(non_payment_qr_gen_index.getFavoriteQr() == 0) {
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QrActionPopUp.dismiss();
                        db.addGeneratedGeneralSecretQrCodeToFavList(non_payment_qr_gen_index.getQrId());
                        Toast.makeText(SecretQRGenHistory.this, "QR Code added to favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }else{
                addFavoriteBtn.setText("Remove");
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QrActionPopUp.dismiss();
                        db.removeGeneratedGeneralSecretQrCodeFromFavList(non_payment_qr_gen_index.getQrId());
                        Toast.makeText(SecretQRGenHistory.this, "QR Code removed from favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }

            detailsBtn = layout.findViewById(R.id.btn_qr_details);
            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQRCodeDetails(non_payment_qr_gen_index.getQrId());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecordListner(){
        //get all data from sqlite
        generalQrGenList.clear();
        generalQrGenList.addAll(db.getAllGeneratedGeneralSecretQrCodes());
        mAdapter = new GeneratedGeneralQRCodeAdapter(this, generalQrGenList);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);

    }
}
