package com.jainantas.expenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private MessageAdapter mMessageAdapter;

    private ProgressBar mProgressBar;
    private EditText mPrice, mReason;
    private FloatingActionButton mSendButton;
    private ListView mMessageListView;
    String key;

    private String mUsername;
    private String date;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseRefrence;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    int sum;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    AdView adView;

    // private static LinkedHashSet<String> deets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() == null || !manager.getActiveNetworkInfo().isConnected()) {
            FancyToast.makeText(getApplicationContext(), "Internet Connection Not Found", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        }
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });
//        adView = findViewById(R.id.adView);
//        // adView.setAdUnitId("ca-app-pub-8823916091635142/8269645797");
//        // adView.setAdSize(AdSize.BANNER);
//        final AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

        mUsername = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        //  deets=new LinkedHashSet<>();
        mDatabaseRefrence = mFirebaseDatabase.getReference().child("message");

        mProgressBar = findViewById(R.id.progressBar);
        mMessageListView = findViewById(R.id.messageListView);
        //  mMessageRecyclerView=findViewById(R.id.messageListView);
        mPrice = findViewById(R.id.amt);
        mReason = findViewById(R.id.reason);
        mSendButton = findViewById(R.id.sendButton);

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        final Date todayDate = new Date();
        date = currentDate.format(todayDate);
        Log.e("date", date);
        //mProgressBar.setVisibility(View.VISIBLE);
        final List<MoneyDetails> moneyDetail = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_display, moneyDetail);
        mMessageListView.setAdapter(mMessageAdapter);
//
        mMessageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.expens);
                builder.setTitle("Delete Item?");
                builder.setMessage("Are you sure you want to delete Expenses of ₹" + moneyDetail.get(position).getPrice() + " paid for " + moneyDetail.get(position).getDetail() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FancyToast.makeText(getApplicationContext(), "Deleted Payment For: " + moneyDetail.get(position).getDetail(), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        mDatabaseRefrence.child(moneyDetail.get(position).getKey()).removeValue();
                        moneyDetail.remove(moneyDetail.get(position));
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FancyToast.makeText(getApplicationContext(), "Data Not Deleted!", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  MoneyDetails moneyDetails=new MoneyDetails(mUsername,date,mPrice.getText().toString(),mReason.getText().toString(),mDatabaseRefrence.push().getKey());
                MoneyDetails moneyDetails = new MoneyDetails();
                moneyDetails.setName(mUsername);
                moneyDetails.setDate(date);
                key = mDatabaseRefrence.push().getKey();
                moneyDetails.setKey(key);
                // stringList.add(key);
                moneyDetails.setDetail(mReason.getText().toString());
                moneyDetails.setPrice(mPrice.getText().toString());
                mDatabaseRefrence.child(key).setValue(moneyDetails);
                //String key=mDatabaseRefrence.getKey();
                // moneyDetails.setKey(key);
                //Log.e("Keys",key);
                // Log.e("keys",stringList.toString());
                mPrice.setText(null);
                mReason.setText(null);

                FancyToast.makeText(getApplicationContext(), "Data Added Successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        });

        //mDatabaseRefrence.addChildEventListener(mChildEventListener);

        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() != 0)
                    mSendButton.setEnabled(true);
                else
                    mSendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInIntialize(user.getDisplayName());
                } else {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))

                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    private void onSignedOutCleanUp() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseRefrence.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void onSignedInIntialize(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();


    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    MoneyDetails moneyDetails = snapshot.getValue(MoneyDetails.class);
                    mMessageAdapter.add(moneyDetails);
                    mProgressBar.setVisibility(View.GONE);
                    mMessageAdapter.notifyDataSetChanged();
                    int s_sum = Integer.parseInt(moneyDetails.getPrice());
                    sum += s_sum;

                    //data=moneyDetails.getKeyList();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // mMessageAdapter.clear();

                    sum = 0;
                    // mMessageAdapter.remove(details);
                    //onPause();

                    mMessageAdapter.notifyDataSetChanged();


                    //onResume();
                    // mMessageAdapter.notifyAll();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            mDatabaseRefrence.addChildEventListener(mChildEventListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.get_report:
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


                return true;
            case R.id.how_to_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.expens);
                builder.setCancelable(false);
                builder.setTitle("How to Delete Data?");
                builder.setMessage("To Delete a data entry, long press on the data you want to delete and click YES !\n\nWARNING!!:\t Please Double check details before deleting data, once deleted data cannot be bought back.");
                builder.setPositiveButton("OKAY!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            case R.id.credits:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setIcon(R.drawable.expens);
                builder1.setCancelable(false);
                builder1.setTitle("About the App");
                builder1.setMessage("Developed by:\tAntas Jain\nUses of App:\tTo Manage Personal or Family expenses\nCopyright:\tAntas Jain\nContact Developer:\t jainantas.99@gmail.com");
                builder1.setPositiveButton("Antas OP!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog2 = builder1.create();
                alertDialog2.show();
                return true;
            case R.id.showTotal:
                // FancyToast.makeText(getApplicationContext(),Integer.toString(sum)+" Rupees",FancyToast.LENGTH_LONG,FancyToast.DEFAULT,false).show();
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                mBuilder.setIcon(R.drawable.expens);
                mBuilder.setTitle("Total Expenses");
                mBuilder.setMessage("You have spent a total of ₹" + Integer.toString(sum) + " till now.");
                mBuilder.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog1 = mBuilder.create();
                alertDialog1.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mMessageAdapter.clear();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sign In Cancelled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                     // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}