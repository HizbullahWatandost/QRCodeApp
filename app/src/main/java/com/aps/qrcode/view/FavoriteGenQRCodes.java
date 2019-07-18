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
import com.aps.qrcode.adapter.GeneratedPaymentQRCodeAdapter;
import com.aps.qrcode.helper.DBHelper;
import com.aps.qrcode.model.PaymentQrGen;
import com.aps.qrcode.util.RecyclerDividerItemDecoration;
import com.aps.qrcode.util.RecyclerItemTouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.aps.qrcode.database.DBManager.DATABASE_NAME;
import static com.aps.qrcode.database.DBManager.DATABASE_VERSION;


/**
 * The FavoriteGenQRCodes activity, displays the list of generated payment QR Code images for the merchant
 * after clicking on the favorite in navigation under payment QR code section
 */
public class FavoriteGenQRCodes extends AppCompatActivity {

    private DBHelper db;

    private GeneratedPaymentQRCodeAdapter mAdapter;
    private List<PaymentQrGen> favoriteQRLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noFavGeneratedQRTxtView;

    //QR Operations popup window
    private PopupWindow QRActionPopUp;
    //close buttons on popup windows
    TextView btnClosePopupA, QRGenImgName;
    ImageView QRGenImgView;
    Button editBtn, deleteBtn, addFavoriteBtn, detailsBtn;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_qr_codes_list);

        db = new DBHelper(this, DATABASE_NAME, null,DATABASE_VERSION);
        favoriteQRLists.addAll(db.getAllFavGeneratedPaymentQrCodes());

        recyclerView = findViewById(R.id.recycler_vw_qr_codes_holder);
        noFavGeneratedQRTxtView = findViewById(R.id.txt_vw_empty_qr_list);

        mAdapter = new GeneratedPaymentQRCodeAdapter(this, favoriteQRLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyFavoriteGeneratedQRCode();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete Add to / Remove from favorite and Details
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
     * Toggling list and empty generated QR Code images view
     */
    private void toggleEmptyFavoriteGeneratedQRCode() {
        // you can check notesList.size() > 0

        if (db.getFavGeneratedPaymentQrCodesCount() > 0) {
            noFavGeneratedQRTxtView.setVisibility(View.GONE);
        } else {
            noFavGeneratedQRTxtView.setText("No Favorite Generate QR Code!");
            noFavGeneratedQRTxtView.setVisibility(View.VISIBLE);
        }
    }
    //close button for about us popup window
    private View.OnClickListener cancel_button_click_listenerA = new View.OnClickListener() {
        public void onClick(View v) {
            QRActionPopUp.dismiss();

        }
    };

    //updating the payment generated QR Code request
    public void updateGeneratedQRCode(int qrGenId, int favorite){
        QRActionPopUp.dismiss();
        Intent intent = new Intent(FavoriteGenQRCodes.this, QRGenUpdateActivity.class);
        intent.putExtra("qr_gen_id",qrGenId);
        intent.putExtra("qr_update",true);
        intent.putExtra("favorite",favorite);
        startActivity(intent);
    }

    // show the details of generated payment QR Code
    public void showQRCodeDetails(int qrGenId, int favorite){
        QRActionPopUp.dismiss();
        Intent intent = new Intent(FavoriteGenQRCodes.this, QRGenUpdateActivity.class);
        intent.putExtra("qr_gen_id",qrGenId);
        intent.putExtra("qr_update",false);
        intent.putExtra("favorite",favorite);
        startActivity(intent);
    }

    //delete a generated payment QR Code image
    private void showDialogDelete(int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(FavoriteGenQRCodes.this);
        dialogDelete.setTitle("Warning!!!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    QRActionPopUp.dismiss();
                    db.deleteGeneratedPaymentQrCodeById(idRecord);
                    Toast.makeText(FavoriteGenQRCodes.this, "Deleted successfully!",Toast.LENGTH_LONG).show();
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
            LayoutInflater inflater = (LayoutInflater) FavoriteGenQRCodes.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.recycler_item_on_click_popup, (ViewGroup) findViewById(R.id.popup_element));
            QRActionPopUp = new PopupWindow(layout, (int) (width * .8), (int) (height * .7), true);
            QRActionPopUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
            btnClosePopupA = (TextView) layout.findViewById(R.id.txt_vw_close_qr_action_popup);
            QRGenImgName = (TextView) layout.findViewById(R.id.txt_vw_qr_code_img_name);
            QRGenImgView = (ImageView) layout.findViewById(R.id.img_vw_qr_img);
            PaymentQrGen qr_gen_index = favoriteQRLists.get(position);
            String qr_gen_img_name = qr_gen_index.getQrImgName();
            QRGenImgName.setText(qr_gen_img_name);
            QRGenImgView.setImageBitmap(BitmapFactory.decodeByteArray(qr_gen_index.getQrImg(), 0, qr_gen_index.getQrImg().length));

            btnClosePopupA.setOnClickListener(cancel_button_click_listenerA);

            editBtn = (Button) layout.findViewById(R.id.edit_qr_code);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateGeneratedQRCode(qr_gen_index.getQrId(), qr_gen_index.getFavQrGen());


                }
            });

            deleteBtn = (Button) layout.findViewById(R.id.btn_qr_delete);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDelete(qr_gen_index.getQrId());
                }
            });

            addFavoriteBtn = (Button) layout.findViewById(R.id.btn_add_qr_to_fav);
            if(qr_gen_index.getFavQrGen() == 0) {
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QRActionPopUp.dismiss();
                        db.addGeneratedPaymentQrCodeToFavList(qr_gen_index.getQrId());
                        Toast.makeText(FavoriteGenQRCodes.this, "QR Code added to favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }else{
                addFavoriteBtn.setText("Remove");
                addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QRActionPopUp.dismiss();
                        db.rermoveGeneratedPaymentQrCodeFromFavList(qr_gen_index.getQrId());
                        Toast.makeText(FavoriteGenQRCodes.this, "QR Code removed from favorite list successfully!", Toast.LENGTH_SHORT).show();
                        updateRecordListner();
                    }
                });
            }

            detailsBtn = (Button) layout.findViewById(R.id.btn_qr_details);
            detailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQRCodeDetails(qr_gen_index.getQrId(),qr_gen_index.getFavQrGen());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecordListner(){
        // get all data from sqlite
        favoriteQRLists.clear();
        favoriteQRLists.addAll(db.getAllFavGeneratedPaymentQrCodes());
        mAdapter = new GeneratedPaymentQRCodeAdapter(this, favoriteQRLists);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);

    }
}
