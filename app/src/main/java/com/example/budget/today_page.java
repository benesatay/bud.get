package com.example.budget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class today_page extends Fragment {

    public static final String CURRENCY = "currency";


    /*
    // This hides the android keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(
                                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
     */

    public today_page() {
        // Required empty public constructor
    }

    ArrayList<PaymentDataOfTodayPage> paymentListOfTodayPage;

    public TextView todaysTotalPaymentTextView;
    public ListView paymentListView;
    public EditText paymentEditText;

    public TextView currencyOfDTP;

    SimpleDateFormat todaysDateFormatWithHour = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_today_page, container, false);

        //when keyboard is opened, linrarlayout that consist edittext and floatactionbutton goes to up and be visible.
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Toolbar toolbar = view.findViewById(R.id.tToolbar);
        toolbar.setTitle("bud-get");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
        paymentListOfTodayPage = (ArrayList<PaymentDataOfTodayPage>) budgetDatabaseHelper.getAllTodayPagePaymentData();
        final CustomAdapter customAdapter = new CustomAdapter(paymentListOfTodayPage);

        paymentEditText = view.findViewById(R.id.totalPaymentEditText);
        paymentListView = view.findViewById(R.id.paymentList);
        todaysTotalPaymentTextView = view.findViewById(R.id.todaysTotalPaymentTextView);

        currencyOfDTP = view.findViewById(R.id.currencyOfDTP);

        ImageButton totalPaymentOnDayDeleteImageButton = view.findViewById(R.id.totalPaymentOnDayDeleteImageButton);

        //data got from json
        getDataFromJson();

        paymentListView.setAdapter(customAdapter);

        final Date todaysDate = new Date();
        String oldDate;

        int differenceOfDates;
        int differenceOfMonths;
        int differenceOfYears;
        if(paymentListOfTodayPage.isEmpty()) {
            differenceOfDates = 0;
            differenceOfMonths = 0;
            differenceOfYears = 0;
        } else {
            oldDate = paymentListOfTodayPage.get(0).getPaymentDate();
            differenceOfDates = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(0,2)) - Integer.valueOf(oldDate.substring(0,2));
            differenceOfMonths = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(3,5)) - Integer.valueOf(oldDate.substring(3,5));
            differenceOfYears = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(6,10)) - Integer.valueOf(oldDate.substring(6,10));
        }

        //listview will be cleared when new day start
        try {
            if (differenceOfDates > 0 || differenceOfMonths > 0 || differenceOfYears > 0) {

                //silinmeden önce tekrar gönderilerek oradaki veriler korunur
                String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
                editor.putString("jsonTotalPayment", jsonTotalPayment);
                
                String lastPaymentDateToJson = gson.toJson(paymentListOfTodayPage.get(0).getPaymentDate());
                editor.putString("lastPaymentDateToJson", lastPaymentDateToJson);

                editor.commit();

                paymentListOfTodayPage.clear();
                budgetDatabaseHelper.clearTodayPagePaymentTable();
            }
        } catch (IndexOutOfBoundsException e) {
            paymentListOfTodayPage.remove(null);
        }

        //if paymentlist is empty, textviews that called "harcama" and "tarih" are invisible and paymentlist backgroundcolor is same with main screen backgrouncolor.
        if(paymentListOfTodayPage.isEmpty()) {
            LinearLayout listViewNamelinearLayout = view.findViewById(R.id.listViewNamelinearLayout);
            listViewNamelinearLayout.setVisibility(View.INVISIBLE);
            paymentListView.setBackgroundColor(212121);
        }

        FloatingActionButton addPaymentFAB = view.findViewById(R.id.addPaymentFAB);
        addPaymentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment();
            }
        });

        totalPaymentOnDayDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTotalPaymentOnDay();
            }
        });

        //done button in keyboard
        paymentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addPayment();
                }
                return false;
            }});

        return view;
    }

    public void addPayment() {
        BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
        SharedPreferences sqlSharedPreferences = getActivity().getSharedPreferences("SQL", 0);
        SharedPreferences.Editor dbEditor = sqlSharedPreferences.edit();

        final SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final Gson gson = new Gson();

        final CustomAdapter customAdapter = new CustomAdapter(paymentListOfTodayPage);

        Date todaysDate = new Date();
        int differenceOfDates;
        if(paymentListOfTodayPage.isEmpty()) {
            //we get last payment date to calculate differenceofdates from monthlypaymentlist
            BudgetDatabaseHelper mBudgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
            ArrayList<PaymentDataOfMonthPage> monthlypaymentList = (ArrayList<PaymentDataOfMonthPage>) mBudgetDatabaseHelper.getAllMonthPagePaymentData();
            try {
                differenceOfDates = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(0,2)) - Integer.valueOf(monthlypaymentList.get(0).getMpaymentDate().substring(0,2));
            } catch (NullPointerException e) {
                differenceOfDates = 0;
            }
        } else {
            String oldDateInButton;
            oldDateInButton = paymentListOfTodayPage.get(0).getPaymentDate();
            differenceOfDates = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(0,2)) - Integer.valueOf(oldDateInButton.substring(0,2));
        }

        String enteredPayment = paymentEditText.getText().toString();
        String forWhatStr = getString(R.string.Spent_For_What);

        //listview will be cleared when new payment is added in new day
        if (differenceOfDates > 0) {

            try {
                    //silinmeden önce tekrar gönderilerek oradaki veriler korunur
                    String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
                    editor.putString("jsonTotalPayment", jsonTotalPayment);

                    String lastPaymentDateToJson = gson.toJson(paymentListOfTodayPage.get(0).getPaymentDate());
                    editor.putString("lastPaymentDateToJson", lastPaymentDateToJson);

                    editor.commit();

                    paymentListOfTodayPage.clear();
                    budgetDatabaseHelper.clearTodayPagePaymentTable();
                } catch (NullPointerException e) {
                    System.out.println("null in time controlling");
                } catch (IndexOutOfBoundsException e) {
                    paymentListOfTodayPage.clear();

                    budgetDatabaseHelper.insertPaymentOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate));
                    budgetDatabaseHelper.insertPaymentOfProfilePage(enteredPayment, forWhatStr);
                    dbEditor.commit();

                }

            paymentListOfTodayPage.add(0, new PaymentDataOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate)));
            budgetDatabaseHelper.insertPaymentOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate));
            budgetDatabaseHelper.insertPaymentOfProfilePage(enteredPayment, forWhatStr);
            dbEditor.commit();

            paymentListView.setAdapter(customAdapter);
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Added), Toast.LENGTH_SHORT).show();
        } else {
            if(enteredPayment.equals("")) {
                AlertDialog.Builder exceptionDialog = new AlertDialog.Builder(getActivity());
                exceptionDialog.setMessage(getString(R.string.Please_Enter_Payment));
                exceptionDialog.setPositiveButton(getString(R.string.Close), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                exceptionDialog.show();
            } else {
                paymentListOfTodayPage.add(0, new PaymentDataOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate)));
                budgetDatabaseHelper.insertPaymentOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate));
                budgetDatabaseHelper.insertPaymentOfProfilePage(enteredPayment, forWhatStr);
                dbEditor.commit();

                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Added), Toast.LENGTH_SHORT).show();
            }
        }
        //ArrayList will cleared in the new day
        //but if we do not set to payment listview, listview shows old data.
        //so we must set to payment listview when a new day is came.
        paymentListView.setAdapter(customAdapter);

        //app is recreated when the first element is added for access to wanted display in today_page, listViewNamelinearLayout will visible
        //if we do not use if statement, the app is recreated when each element adding and this causes bad using.
        if(paymentListOfTodayPage.size() < 2) {
            getActivity().recreate();
        }

        //total payment is calculated
        int totalPaymentOnDay = 0;
        for(int indexofPaymentList = 0; indexofPaymentList < paymentListOfTodayPage.size(); indexofPaymentList++) {
            totalPaymentOnDay = totalPaymentOnDay + Integer.valueOf(paymentListOfTodayPage.get(indexofPaymentList).getPayment());
        }

        //totalPayment is displayed
        if (totalPaymentOnDay == 0) {
            todaysTotalPaymentTextView.setText("");
        } else {
            todaysTotalPaymentTextView.setText(String.valueOf(totalPaymentOnDay));
        }

        //totalPayment is sent to json to share with month_page
        String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
        editor.putString("jsonTotalPayment", jsonTotalPayment);

        //totalPayment date is sent to json to share with month_page
        try {
            String lastPaymentDateToJson = gson.toJson(paymentListOfTodayPage.get(0).getPaymentDate());
            editor.putString("lastPaymentDateToJson", lastPaymentDateToJson);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("lastPaymentDateToJson is 0");
        }
        editor.commit();
    }

    public void deleteTotalPaymentOnDay() {

        final BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());

        final ArrayList<PaymentDataOfTodayPage> paymentListOfTodayPage = (ArrayList<PaymentDataOfTodayPage>) budgetDatabaseHelper.getAllTodayPagePaymentData();

        final CustomAdapter customAdapter = new CustomAdapter(paymentListOfTodayPage);

        final SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setMessage(todaysTotalPaymentTextView.getText().toString());
        deleteDialog.setPositiveButton(getString(R.string.Cancel), null);
        deleteDialog.setNegativeButton(getString(R.string.Delete), new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                todaysTotalPaymentTextView.setText(null);

                paymentListOfTodayPage.clear();

                budgetDatabaseHelper.clearTodayPagePaymentTable();
                budgetDatabaseHelper.clearProfilePagePaymentTable();

                String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
                editor.putString("jsonTotalPayment", jsonTotalPayment);

                editor.commit();

                paymentListView.setAdapter(customAdapter);

                dialog.dismiss();

                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Deleted), Toast.LENGTH_SHORT).show();

                getActivity().recreate();
            }
        });
        deleteDialog.show();
    }

    public void getDataFromJson()  {
        final SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);

        final Gson gson = new Gson();

        String jsonTotalPayment = sharedPref.getString("jsonTotalPayment", null);
        Type typeTotalPayment = new TypeToken<String>() {}.getType();
        try {
            todaysTotalPaymentTextView.setText(String.valueOf(gson.fromJson(jsonTotalPayment,typeTotalPayment)));
        } catch (NullPointerException e) {
            System.out.println("arr is null");
        }


    }

    final class CustomAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<PaymentDataOfTodayPage> paymentListOfTodayPage;

        public CustomAdapter(ArrayList<PaymentDataOfTodayPage> paymentDataOfTodayPageList) {
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.paymentListOfTodayPage = paymentDataOfTodayPageList;
        }
        @Override
        public int getCount() {
            return paymentListOfTodayPage.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.custom_listview, null);

            final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            final Gson gson = new Gson();

            TextView paymentTextView = convertView.findViewById(R.id.paymentTextView);
            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            TextView currencyTextView = convertView.findViewById(R.id.currencyTextView);

            String currency = getActivity().getIntent().getStringExtra(CURRENCY);
            String currencyToJson = gson.toJson(currency);
            editor.putString("currencyToJson",currencyToJson);
            editor.commit();

            currencyOfDTP.setText(currency);


            try {
                currencyTextView.setText(currency);
                paymentTextView.setText(paymentListOfTodayPage.get(position).getPayment());
                dateTextView.setText(paymentListOfTodayPage.get(position).getPaymentDate());
            } catch (IndexOutOfBoundsException e) {
                paymentTextView.clearComposingText();
                dateTextView.clearComposingText();
            }

            final ImageButton pencilImageButton = convertView.findViewById(R.id.pencilImageButton);

            pencilImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder pencilDialog = new AlertDialog.Builder(getActivity());
                    final View customAlertdialog = getLayoutInflater().inflate(R.layout.custom_alertdialog, null);
                    pencilDialog.setView(customAlertdialog);

                    final EditText editTextInAlertDialog = customAlertdialog.findViewById(R.id.editTextinAlertDialog);
                    final TextView textViewInAlertDialog = customAlertdialog.findViewById(R.id.textViewinAlertDialog);

                    try {
                        textViewInAlertDialog.setText(String.valueOf(paymentListOfTodayPage.get(position).getPaymentName()));
                    } catch (NullPointerException e) {
                        System.out.println("paymentname array is null");
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("IndexOutOfBoundsException in alertdialog");
                    }

                    pencilDialog.setMessage(getString(R.string.Enter_Payment_Name));
                    pencilDialog.setNegativeButton(getString(R.string.Close), null);
                    pencilDialog.setPositiveButton(getString(R.string.Save), new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //if paymentName was not created first time
                            //paymentNameList's 0 index comes from addPaymentImageButton as an empty string.
                            // so empty string moves with elements of paymentNameList
                            //if the empty string does not removed from list
                            //whole list elements become empty string after a point.
                            //so I removed the data in clicked position.
                            textViewInAlertDialog.setText(editTextInAlertDialog.getText().toString());

                            String paymentTransfering = paymentListOfTodayPage.get(position).getPayment();
                            String paymentDateTransfering = paymentListOfTodayPage.get(position).getPaymentDate();
                            String paymentNameTransfering = paymentListOfTodayPage.get(position).getPaymentName();
                            BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());

                            paymentListOfTodayPage.remove(position);
                            budgetDatabaseHelper.deleteRowOfTodayPagePaymentTable(paymentTransfering, paymentNameTransfering, paymentDateTransfering);
                            budgetDatabaseHelper.deleteRowOfProfilePagePaymentTable(paymentTransfering, paymentNameTransfering);

                            paymentListOfTodayPage.add(position, new PaymentDataOfTodayPage(paymentTransfering, textViewInAlertDialog.getText().toString(), paymentDateTransfering));

                            budgetDatabaseHelper.insertPaymentOfTodayPage(paymentTransfering, textViewInAlertDialog.getText().toString(), paymentDateTransfering);
                            budgetDatabaseHelper.insertPaymentOfProfilePage(paymentTransfering, textViewInAlertDialog.getText().toString());

                            dialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Saved), Toast.LENGTH_SHORT).show();
                        }
                    });
                    pencilDialog.create();
                    pencilDialog.show();

                    /**
                     pencilDialog.setNegativeButton("Sil", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    paymentNameList.remove(position);
                    paymentNameList.remove(null);
                    String paymentNameListToJson = gson.toJson(paymentNameList);
                    editor.putString("paymentNameList",paymentNameListToJson);
                    editor.commit();
                    dialog.dismiss();

                    Toast.makeText(getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
                    }
                    });
                     pencilDialog.show();
                     **/
                }
            });


            ImageButton deletePaymentImageButton = convertView.findViewById(R.id.deletePaymentImageButton);
            deletePaymentImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                    deleteDialog.setMessage(String.valueOf(paymentListOfTodayPage.get(position).getPayment()));

                    deleteDialog.setNegativeButton(getString(R.string.Cancel), null);
                    deleteDialog.setPositiveButton(getString(R.string.Delete), new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String totalPaymentAfterDeletingAnyPayment = todaysTotalPaymentTextView.getText().toString();

                            try {
                                if ((Integer.valueOf(totalPaymentAfterDeletingAnyPayment) - Integer.valueOf(paymentListOfTodayPage.get(position).getPayment())) == 0) {
                                    todaysTotalPaymentTextView.setText(null);
                                } else {
                                    todaysTotalPaymentTextView.setText(String.valueOf(Integer.valueOf(totalPaymentAfterDeletingAnyPayment) - Integer.valueOf(paymentListOfTodayPage.get(position).getPayment())));
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Deleted), Toast.LENGTH_SHORT).show();
                            }

                            BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
                            budgetDatabaseHelper.deleteRowOfTodayPagePaymentTable(paymentListOfTodayPage.get(position).getPayment(), paymentListOfTodayPage.get(position).getPaymentName(), paymentListOfTodayPage.get(position).getPaymentDate());

                            paymentListOfTodayPage.remove(position);
                            paymentListOfTodayPage.remove(null);

                            // Notifies the attached observers that the underlying data has been changed
                            // and any View reflecting the data set should refresh itself.
                            notifyDataSetChanged();

                            //toJSON, changed values are notified
                            String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
                            editor.putString("jsonTotalPayment", jsonTotalPayment);

                            editor.commit();

                            dialog.dismiss();

                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Deleted), Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleteDialog.show();
                }
            });
            return convertView;
        }
    }

}
