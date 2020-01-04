package com.example.budget;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class today_page extends Fragment {

    public static final String CURRENCY = "currency";

    public today_page() {
        // Required empty public constructor
    }

    ArrayList<PaymentDataOfTodayPage> paymentListOfTodayPage;
    ArrayList<PaymentDataOfMonthPage> monthlypaymentList;

    public String selectedPaymentName;

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

        //when keyboard is opened, linearlayout that consist edittext and floatactionbutton goes to up and be visible.
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.tToolbar);
        toolbar.setTitle("bud-get");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //Database
        new asyncTask().execute();
        BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
        paymentListOfTodayPage = (ArrayList<PaymentDataOfTodayPage>) budgetDatabaseHelper.getAllTodayPagePaymentData();
        monthlypaymentList = (ArrayList<PaymentDataOfMonthPage>) budgetDatabaseHelper.getAllMonthPagePaymentData();

        //Listview custom layout
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

        //Calculating of difference of dates
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

                paymentListOfTodayPage.clear();
                budgetDatabaseHelper.clearTodayPagePaymentTable();
            }
        } catch (IndexOutOfBoundsException e) {
            paymentListOfTodayPage.remove(null);
        }

        //if paymentlist is empty, textviews that called "harcama" and "tarih" are invisible and paymentlist backgroundcolor is same with main screen backgrouncolor.
        if(paymentListOfTodayPage.isEmpty()) {
            LinearLayout listViewNamelinearLayout = view.findViewById(R.id.listViewNamelinearLayoutInTodayPage);
            paymentListView.setBackgroundColor(212121);
        }

        FloatingActionButton addPaymentFAB = view.findViewById(R.id.addPaymentFAB);
        addPaymentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment();
                // This hides the android keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
        final CustomAdapter customAdapter = new CustomAdapter(paymentListOfTodayPage);

        Date todaysDate = new Date();
        int differenceOfDates;
        if(paymentListOfTodayPage.isEmpty()) {
            //we get last payment date to calculate differenceofdates from monthlypaymentlist
           try {
                differenceOfDates = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(0,2)) - Integer.valueOf(monthlypaymentList.get(0).getMpaymentDate().substring(0,2));
            } catch (NullPointerException e) {
                differenceOfDates = 0;
            } catch (IndexOutOfBoundsException e) {
                differenceOfDates = 0;
            }
        } else {
            /*
            String oldDateInButton;
            oldDateInButton = paymentListOfTodayPage.get(0).getPaymentDate();
            differenceOfDates = Integer.valueOf(todaysDateFormatWithHour.format(todaysDate).substring(0,2)) - Integer.valueOf(oldDateInButton.substring(0,2));
             */
            differenceOfDates = 0;
        }

        String enteredPayment = paymentEditText.getText().toString();
        String forWhatStr = getString(R.string.Spent_For_What);
        BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
        //listview will be cleared when new payment is added in new day
        if (differenceOfDates > 0) {
            try {
                paymentListOfTodayPage.clear();
                budgetDatabaseHelper.clearTodayPagePaymentTable();
            } catch (NullPointerException e) {
                System.out.println("null in time controlling");
            } catch (IndexOutOfBoundsException e) {
                paymentListOfTodayPage.clear();
            }
            //Database of today_page is updated
            paymentListOfTodayPage.add(0, new PaymentDataOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate)));
            budgetDatabaseHelper.insertPaymentOfTodayPage(enteredPayment, forWhatStr, todaysDateFormatWithHour.format(todaysDate));
            budgetDatabaseHelper.insertPaymentOfProfilePage(enteredPayment, forWhatStr);

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

                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Added), Toast.LENGTH_SHORT).show();
            }

        }
        //ArrayList will be cleared in the new day
        //but if we do not set to payment listview, listview shows old data.
        //so we must set to payment listview when a new day is came.
        paymentListView.setAdapter(customAdapter);

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

        if(differenceOfDates == 0) {
            //Database of month_page is updated
            monthlypaymentList.add(0, new PaymentDataOfMonthPage(String.valueOf(totalPaymentOnDay),paymentListOfTodayPage.get(0).getPaymentDate().substring(0,10)));
            budgetDatabaseHelper.insertPaymentOfMonthPage(String.valueOf(totalPaymentOnDay), paymentListOfTodayPage.get(0).getPaymentDate().substring(0,10));
            budgetDatabaseHelper.deleteRowOfMonthPagePaymentTable(monthlypaymentList.get(1).getMpayment(), monthlypaymentList.get(1).getMpaymentDate());
            monthlypaymentList.remove(1);
            monthlypaymentList.remove(null);
        } else {
            //Database of month_page is updated
            monthlypaymentList.add(0, new PaymentDataOfMonthPage(String.valueOf(totalPaymentOnDay),todaysDateFormatWithHour.format(todaysDate).substring(0,10)));
            budgetDatabaseHelper.insertPaymentOfMonthPage(String.valueOf(totalPaymentOnDay),todaysDateFormatWithHour.format(todaysDate).substring(0,10));
        }

        final SharedPreferences sharedPref = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();
        //totalPayment is sent to json
        String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
        editor.putString("jsonTotalPayment", jsonTotalPayment);
        editor.commit();

        /*
        //totalPayment date is sent to json to share with month_page
        try {
            String lastPaymentDateToJson = gson.toJson(paymentListOfTodayPage.get(0).getPaymentDate());
            editor.putString("lastPaymentDateToJson", lastPaymentDateToJson);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("lastPaymentDateToJson is 0");
        }
         */

        //app is recreated when the first element is added for access to wanted display in today_page, listViewNamelinearLayout will visible
        //if we do not use if statement, the app is recreated when each element adding and this causes bad using.
        if(paymentListOfTodayPage.size() < 2) {

            /**try it**/
            customAdapter.notifyDataSetChanged();
            //list is rebuilt
            new asyncTask().execute();
            paymentListOfTodayPage = (ArrayList<PaymentDataOfTodayPage>) budgetDatabaseHelper.getAllTodayPagePaymentData();


        }
    }

    public void deleteTotalPaymentOnDay() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setMessage(todaysTotalPaymentTextView.getText().toString());
        deleteDialog.setPositiveButton(getString(R.string.Cancel), null);
        deleteDialog.setNegativeButton(getString(R.string.Delete), new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
                //if in same day, when daily total payment is deleted, first monthly payment list element is deleted too
                if(Integer.valueOf(monthlypaymentList.get(0).getMpayment()) == Integer.valueOf(todaysTotalPaymentTextView.getText().toString())
                        && Integer.valueOf(monthlypaymentList.get(0).getMpaymentDate().substring(0,2)) == Integer.valueOf(paymentListOfTodayPage.get(0).paymentDate.substring(0,2))) {
                    budgetDatabaseHelper.deleteRowOfMonthPagePaymentTable(monthlypaymentList.get(0).getMpayment(), monthlypaymentList.get(0).getMpaymentDate());
                }

                for(int indexOfPaymentListOfTodayPage = 0; indexOfPaymentListOfTodayPage<paymentListOfTodayPage.size(); indexOfPaymentListOfTodayPage++) {
                    if(budgetDatabaseHelper.getAllProfilePagePaymentData().get(indexOfPaymentListOfTodayPage).getPpayment()
                            == paymentListOfTodayPage.get(indexOfPaymentListOfTodayPage).getPayment() && budgetDatabaseHelper.getAllProfilePagePaymentData().get(indexOfPaymentListOfTodayPage).getPpaymentName()
                            == paymentListOfTodayPage.get(indexOfPaymentListOfTodayPage).getPaymentName()){
                        budgetDatabaseHelper.deleteRowOfProfilePagePaymentTable(paymentListOfTodayPage.get(indexOfPaymentListOfTodayPage).getPayment(), paymentListOfTodayPage.get(indexOfPaymentListOfTodayPage).getPaymentName());
                    }
                }

                budgetDatabaseHelper.clearTodayPagePaymentTable();
                paymentListOfTodayPage.clear();
                todaysTotalPaymentTextView.setText(null);

                Gson gson = new Gson();
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String jsonTotalPayment = gson.toJson(todaysTotalPaymentTextView.getText().toString());
                editor.putString("jsonTotalPayment", jsonTotalPayment);
                editor.commit();

                CustomAdapter customAdapter = new CustomAdapter(paymentListOfTodayPage);
                paymentListView.setAdapter(customAdapter);

                dialog.dismiss();

                //list is rebuilt
                new asyncTask().execute();
                paymentListOfTodayPage = (ArrayList<PaymentDataOfTodayPage>) budgetDatabaseHelper.getAllTodayPagePaymentData();

                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.Deleted), Toast.LENGTH_SHORT).show();
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


            final TextView currencyTextView = convertView.findViewById(R.id.currencyTextView);


            String currencyFromJson = sharedPref.getString("currencyToJson", null);
            Type typeCurrency = new TypeToken<String>() {}.getType();
            String currency;
            try {
                currency = gson.fromJson(currencyFromJson, typeCurrency);
            } catch (NullPointerException e) {
                currency = getActivity().getIntent().getStringExtra(CURRENCY);
            }

            if(getActivity().getIntent().getStringExtra(CURRENCY) != null) {
                currency = getActivity().getIntent().getStringExtra(CURRENCY);
            }

            String currencyToJson = gson.toJson(currency);
            editor.putString("currencyToJson",currencyToJson);
            editor.commit();

            //dailyTotalPaymentTextview is in the bottom of screen.
            currencyOfDTP.setText(currency);

            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            TextView dateTextView2 = convertView.findViewById(R.id.dateTextView2);
            try {
                currencyTextView.setText(currency);
                paymentTextView.setText(paymentListOfTodayPage.get(position).getPayment());
                dateTextView.setText(paymentListOfTodayPage.get(position).getPaymentDate().substring(0,10));
                dateTextView2.setText(paymentListOfTodayPage.get(position).getPaymentDate().substring(10,19));
            } catch (IndexOutOfBoundsException e) {
                paymentTextView.clearComposingText();
                dateTextView.clearComposingText();
                dateTextView2.clearComposingText();
            }

            final ImageButton moreVertButton = convertView.findViewById(R.id.moreVertImageButton);

            moreVertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final View convertToView = getLayoutInflater().inflate(R.layout.custom_alertdialog, null);

                    final TextView textViewInAlertDialog = convertToView.findViewById(R.id.textViewinAlertDialog);
                    try {
                        textViewInAlertDialog.setText(String.valueOf(paymentListOfTodayPage.get(position).getPaymentName()));
                    } catch (NullPointerException e) {
                        textViewInAlertDialog.setText("");
                    } catch (IndexOutOfBoundsException e) {
                        textViewInAlertDialog.setText("");
                    }

                    TextView paymentInAlertDialogTextView = convertToView.findViewById(R.id.paymentInAlertDialogTextView);
                    paymentInAlertDialogTextView.setText(paymentListOfTodayPage.get(position).getPayment() + currencyTextView.getText());
                    TextView enterPaymentTypeText = convertToView.findViewById(R.id.enterPaymentTypeText);
                    enterPaymentTypeText.setText(getString(R.string.Enter_Payment_Name));

                    Spinner paymentNameSpinner = convertToView.findViewById(R.id.addPaymentNameSpinner);
                    ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.payment_types_in_spinner, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    paymentNameSpinner.setAdapter(spinnerAdapter);
                    paymentNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedPaymentName = parent.getItemAtPosition(position).toString();
                            System.out.println(selectedPaymentName);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    AlertDialog.Builder pencilDialog = new AlertDialog.Builder(getActivity());
                    pencilDialog.setView(convertToView);
                    pencilDialog.setNegativeButton(getString(R.string.Close), null);
                    pencilDialog.setPositiveButton(getString(R.string.Save), new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editTextInAlertDialog = convertToView.findViewById(R.id.editTextinAlertDialog);
                            if (!TextUtils.isEmpty(editTextInAlertDialog.getText().toString())){
                                selectedPaymentName = editTextInAlertDialog.getText().toString();
                            }

                            SQLiteOpenHelper sqLiteOpenHelper = new BudgetDatabaseHelper(getActivity());
                            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();


                            String whereInTodayList="payment = ? AND paymentname = ? AND paymentdate = ?";
                            String paymentTodayList = paymentListOfTodayPage.get(position).getPayment();
                            String paymentnameTodayList = paymentListOfTodayPage.get(position).getPaymentName();
                            String paymentdateTodayList = paymentListOfTodayPage.get(position).getPaymentDate();
                            ContentValues newNameValuesToTodayList = new ContentValues();
                            newNameValuesToTodayList.put("paymentname", selectedPaymentName);
                            db.update("paymenttable", newNameValuesToTodayList, whereInTodayList, new String[]{paymentTodayList, paymentnameTodayList, paymentdateTodayList});

                            BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
                            ArrayList<PaymentDataOfProfilePage> profilePaymentList = (ArrayList<PaymentDataOfProfilePage>) budgetDatabaseHelper.getAllProfilePagePaymentData();
                            String whereInProfileList="ppayment = ? AND ppaymentname = ?";
                            String paymentProfileList = profilePaymentList.get(position).getPpayment();
                            String paymentnameProfileList = profilePaymentList.get(position).getPpaymentName();
                            ContentValues newNameValuesToProfileList = new ContentValues();
                            newNameValuesToProfileList.put("ppaymentname", selectedPaymentName);
                            db.update("profilepaymenttable", newNameValuesToProfileList, whereInProfileList, new String[]{paymentProfileList, paymentnameProfileList});
                            db.close();

                            //list is rebuilt
                            new asyncTask().execute();
                            paymentListOfTodayPage = budgetDatabaseHelper.getAllTodayPagePaymentData();

                            Toast.makeText(getActivity().getApplicationContext(), paymentListOfTodayPage.get(position).getPayment() + currencyTextView.getText().toString() + " " + paymentListOfTodayPage.get(position).getPaymentName() + " " + getString(R.string.Saved), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    pencilDialog.create();
                    pencilDialog.show();
                }
            });



            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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

                            //Database of month_page is updated
                            budgetDatabaseHelper.deleteRowOfMonthPagePaymentTable(monthlypaymentList.get(0).getMpayment(), monthlypaymentList.get(0).getMpaymentDate());
                            monthlypaymentList.remove(0);
                            monthlypaymentList.add(0, new PaymentDataOfMonthPage(todaysTotalPaymentTextView.getText().toString(),paymentListOfTodayPage.get(0).getPaymentDate().substring(0,10)));
                            budgetDatabaseHelper.insertPaymentOfMonthPage(todaysTotalPaymentTextView.getText().toString(), paymentListOfTodayPage.get(0).getPaymentDate().substring(0,10));

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
                    return false;
                }
            });

            /*

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

                            //Database of month_page is updated
                            budgetDatabaseHelper.deleteRowOfMonthPagePaymentTable(monthlypaymentList.get(0).getMpayment(), monthlypaymentList.get(0).getMpaymentDate());
                            monthlypaymentList.remove(0);
                            monthlypaymentList.add(0, new PaymentDataOfMonthPage(todaysTotalPaymentTextView.getText().toString(),paymentListOfTodayPage.get(0).getPaymentDate().substring(0,10)));
                            budgetDatabaseHelper.insertPaymentOfMonthPage(todaysTotalPaymentTextView.getText().toString(), paymentListOfTodayPage.get(0).getPaymentDate().substring(0,10));

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

             */
            return convertView;
        }
    }

    private class asyncTask extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog pDialog;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Downloading...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Boolean doInBackground(Void... voids) {
            SQLiteOpenHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity());
            SQLiteDatabase db = budgetDatabaseHelper.getWritableDatabase();
            try {
                db.rawQuery( "select * from paymenttable", null);
                db.close();
                return true;
            } catch (SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(getActivity(),
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                pDialog.dismiss();
            }
        }
    }
}
