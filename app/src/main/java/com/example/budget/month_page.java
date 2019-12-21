package com.example.budget;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class month_page extends Fragment {

    public month_page() {
        // Required empty public constructor
    }

    ArrayList<PaymentDataOfMonthPage> monthlypaymentList;

    List<String> paymentArrayListAsDaily = new ArrayList<String>();
    List<String> dateArrayListasDaily = new ArrayList<String>();

    List<Integer> budgetArrList = new ArrayList<Integer>();

    public ArrayList barEntryDate = new ArrayList();
    public ArrayList barEntries = new ArrayList();

    public TextView totalPaymentInMonthTextView;
    public TextView savingTextView;
    public TextView balanceTextView;
    public TextView budgetTextView;

    public EditText budgetEditText;
    public EditText savingEditText;

    public ListView paymentListInDetailPage;

    public BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_month_page, container, false);

        BudgetDatabaseHelper mBudgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
        SharedPreferences settings = getActivity().getSharedPreferences("SQL", 0);
        monthlypaymentList = (ArrayList<PaymentDataOfMonthPage>) mBudgetDatabaseHelper.getAllMonthPagePaymentData();

        final CustomAdapter customAdapter = new CustomAdapter(monthlypaymentList);

        paymentListInDetailPage = view.findViewById(R.id.paymentListInDetailPage);

        barChart = view.findViewById(R.id.barchart);

        savingEditText = view.findViewById(R.id.savingEditText);
        budgetEditText = view.findViewById(R.id.budgetEditText);

        budgetTextView = view.findViewById(R.id.budgetTextView);
        savingTextView = view.findViewById(R.id.savingTextView);
        balanceTextView = view.findViewById(R.id.balanceTextView);
        totalPaymentInMonthTextView = view.findViewById(R.id.totalPaymentInMonthTextView);

        ImageButton addSavingImageButton = view.findViewById(R.id.addSavingImageButton);
        ImageButton budgetImageButton = view.findViewById(R.id.budgetImageButton);
        ImageButton depositDeleteImageButton = view.findViewById(R.id.depositDeleteImageButton);
        ImageButton balanceDeleteImageButton = view.findViewById(R.id.balanceDeleteImageButton);
        ImageButton totalPaymentOnMonthDeleteImageButton = view.findViewById(R.id.totalPaymentOnMonthDeleteImageButton);
        ImageButton budgetDeleteImageButton = view.findViewById(R.id.budgetDeleteImageButton);

        getDataFromJson();

        generatePaymentListAndPaymentDateListAndTotalPayment();

        //payment list is set for when creating the activity.
        paymentListInDetailPage.setAdapter(customAdapter);

        setupBarChart();

        addSavingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSaving();
            }
        });

        budgetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBudget();
            }
        });

        depositDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDeposit();
            }
        });

        totalPaymentOnMonthDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTotalPaymentInMonth();
            }
        });

        budgetDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBudget();
            }
        });

        balanceDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBalance();
            }
        });

        return view;
    }

    public void getDataFromJson() {

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final Gson gson = new Gson();

        String jsonTotalPaymentMonthly = sharedPref.getString("jsonTotalPaymentMonthly", null);
        Type typeTotalPaymentMonthly = new TypeToken<String>() {}.getType();
        try {
            totalPaymentInMonthTextView.setText(String.valueOf(gson.fromJson(jsonTotalPaymentMonthly,typeTotalPaymentMonthly)));
        } catch (NullPointerException e) {
            System.out.println("somethings are null in detailpage");
        }

        String jsonDeposit = sharedPref.getString("jsonDeposit", null);
        Type typeDeposit = new TypeToken<String>() {}.getType();
        try {
            savingTextView.setText(String.valueOf(gson.fromJson(jsonDeposit, typeDeposit)));
        } catch (NullPointerException e) {
            System.out.println("somethings are null in detailpage");
        }

        /*
        String jsonBalance = sharedPref.getString("jsonBalance", null);
        Type typeBalance = new TypeToken<String>() {}.getType();
        try {
            balanceTextView.setText(String.valueOf(gson.fromJson(jsonBalance, typeBalance)));
        } catch (NullPointerException e) {
            System.out.println("somethings are null in detailpage");
        }
       */

        String jsonBudget = sharedPref.getString("jsonBudget", null);
        Type typeBudget = new TypeToken<String>() {}.getType();
        try {
            budgetTextView.setText(String.valueOf(gson.fromJson(jsonBudget, typeBudget)));
        } catch (NullPointerException e) {
            System.out.println("somethings are null in detailpage");
        }
    }


    public void generatePaymentListAndPaymentDateListAndTotalPayment() {

        BudgetDatabaseHelper mBudgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());

        SharedPreferences settings = getActivity().getSharedPreferences("SQL", 0);
        SharedPreferences.Editor settingsEditor = settings.edit();

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final Gson gson = new Gson();

        int differenceOfDates;

        Date todaysDate = new Date();
        SimpleDateFormat todaysDateFormatWithHour = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //jsonTotalPayment from today_page
        String jsonTotalPaymentFromTodayPage = sharedPref.getString("jsonTotalPayment", null);
        Type typeTotalPaymentFromTodayPage = new TypeToken<String>() {}.getType();

        //total payment for 1 month
        String enteredTotalPayment;
        try {
            enteredTotalPayment = String.valueOf(gson.fromJson(jsonTotalPaymentFromTodayPage,typeTotalPaymentFromTodayPage));
        } catch (NullPointerException e) {
            enteredTotalPayment = "0";
            System.out.println("enteredTotalPayment is 0");
        }

        String jsonTotalPaymentDate = sharedPref.getString("lastPaymentDateToJson", null);
        Type typeTotalPaymentDate = new TypeToken<String>() {}.getType();
        String paymentDate;

        try {
            paymentDate = String.valueOf(gson.fromJson(jsonTotalPaymentDate, typeTotalPaymentDate));
        } catch (NullPointerException e) {
            paymentDate = "0";
        }

        if(monthlypaymentList.isEmpty()) {
            try {
                //dateArrayListasDaily.add(0,paymentDate);
                if(paymentDate.length() < 2) {
                    differenceOfDates = Integer.valueOf(paymentDate);
                } else {
                    differenceOfDates = Integer.valueOf(paymentDate.substring(0,2));
                }
            } catch (NumberFormatException e) {
                differenceOfDates = 1;
            }

        } else {
            try {
                if(paymentDate.length() < 2) {
                    differenceOfDates = Integer.valueOf(paymentDate) - Integer.valueOf(monthlypaymentList.get(0).getMpaymentDate().substring(0,2));
                } else {
                    differenceOfDates = Integer.valueOf(paymentDate.substring(0,2)) - Integer.valueOf(monthlypaymentList.get(0).getMpaymentDate().substring(0,2));
                }
            } catch (NumberFormatException e) {
                differenceOfDates = 1;
            }
        }

        if (differenceOfDates == 0) {

            //main activity'den gelen totalpayment ve paymentdate listeye tekrar eklenir böylece 0.indexteki eleman 1'e kayar
            //ilk 2 elemanın eklenme tarihleri aynı ise 0. indexteki eleman,
            //son eklenen ve aynı tarihte önceden eklenmiş (önceden 0. indexteki eleman) olan  2 payment'ın toplamı olur
            //bu yüzden 1. indexteki elemanın silinmesi gerekir
            monthlypaymentList.add(0, new PaymentDataOfMonthPage(enteredTotalPayment,paymentDate.substring(0,10)));
            monthlypaymentList.remove(1);
            monthlypaymentList.remove(null);

        } else {
            //eğer eklenme tarihleri farklı ise normal ekleme işlemi yapılarak günlük liste tutulur
            monthlypaymentList.add(0, new PaymentDataOfMonthPage(enteredTotalPayment,paymentDate.substring(0,10)));
            mBudgetDatabaseHelper.insertPaymentOfMonthPage(enteredTotalPayment, paymentDate.substring(0,10));
            settingsEditor.commit();
        }

        int totalPaymentInMonth = 0;
        for(int indexOfMPaymentList = 0; indexOfMPaymentList< monthlypaymentList.size(); indexOfMPaymentList++) {

            if(monthlypaymentList.size() <= 1) {
                try {
                    totalPaymentInMonth = Integer.valueOf(monthlypaymentList.get(0).getMpayment());
                } catch (NumberFormatException e) {

                    monthlypaymentList.clear();

                    /*
                    paymentArrayListAsDaily.clear();
                    dateArrayListasDaily.clear();
                     */
                }
            } else {
                try {
                    totalPaymentInMonth = totalPaymentInMonth + Integer.valueOf(monthlypaymentList.get(indexOfMPaymentList).getMpayment());
                } catch (NumberFormatException e) {
                    monthlypaymentList.remove(null);
                }
            }
        }

        if (totalPaymentInMonth == 0) {
            totalPaymentInMonthTextView.setText("");
        } else {
            //total payment in month is showed in this textView
            totalPaymentInMonthTextView.setText(String.valueOf(totalPaymentInMonth));
        }

        try {
            balanceTextView.setText(String.valueOf(Integer.valueOf(budgetTextView.getText().toString()) - Integer.valueOf(totalPaymentInMonthTextView.getText().toString())));
        } catch (NumberFormatException e) {
            System.out.println("balanceTextView.setText(String.valueOf(Integer.valueOf(balanceTextView.getText().toString()) totalPaymentInMonth))");
            balanceTextView.setText("");
        }
    }

    public void addSaving() {
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        String enteredDeposit = savingEditText.getText().toString();

        editor.putString("savedDeposit",enteredDeposit);
        editor.commit();

        try {
            savingTextView.setText(enteredDeposit);
            Toast.makeText(getActivity().getApplicationContext(), "Eklendi", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            AlertDialog.Builder exceptionDialog = new AlertDialog.Builder(getActivity());
            exceptionDialog.setMessage("Lütfen yapmak istediğiniz birikimi giriniz!");
            exceptionDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            exceptionDialog.show();
        }

        //toJSON, changed values are notified
        String jsonDeposit = gson.toJson(savingTextView.getText().toString());
        editor.putString("jsonDeposit",jsonDeposit);
        editor.commit();
    }

    public void addBudget() {

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        String enteredBudget = budgetEditText.getText().toString();

        try {
            budgetArrList.add(0, Integer.valueOf(enteredBudget));
            //balanceTextView.setText(enteredBudget);
        } catch (NumberFormatException e) {
            final AlertDialog.Builder exceptionDialog = new AlertDialog.Builder(getActivity());
            exceptionDialog.setMessage("Lütfen bütçenizi giriniz!");
            exceptionDialog.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            exceptionDialog.show();
        }

        //sum of entered budgets
        int balance = 0;
        for(int i = 0; i<budgetArrList.size(); i++) {
            if(budgetArrList.size() <= 1) {
                balance = Integer.valueOf(enteredBudget);
            } else {
                balance = balance + budgetArrList.get(i);
            }
        }

        balanceTextView.setText(String.valueOf(balance));
        budgetTextView.setText(String.valueOf(balance));

        Toast.makeText(getActivity().getApplicationContext(), "Eklendi", Toast.LENGTH_SHORT).show();

        String totalPaymentAfterAddingBudget = totalPaymentInMonthTextView.getText().toString();

        try {
            balance = balance - Integer.valueOf(totalPaymentAfterAddingBudget);
            balanceTextView.setText(String.valueOf(balance));
        } catch (NumberFormatException e) {
            System.out.println("balanceTextView.setText(String.valueOf(balance)) ");
        }

        //toJSON, changed values are notified
        String jsonBalance = gson.toJson(balanceTextView.getText().toString());
        editor.putString("jsonBalance", jsonBalance);

        String jsonBudget = gson.toJson(budgetTextView.getText().toString());
        editor.putString("jsonBudget",jsonBudget);

        editor.commit();
    }

    public void deleteDeposit() {

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setMessage(savingTextView.getText().toString());

        deleteDialog.setNegativeButton("İptal", null);
        deleteDialog.setPositiveButton("Sil", new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                savingTextView.setText(null);

                //toJSON, changed values are notified
                String jsonDeposit = gson.toJson(savingTextView.getText().toString());
                editor.putString("jsonDeposit",jsonDeposit);
                editor.commit();

                dialog.dismiss();

                Toast.makeText(getActivity().getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialog.show();
    }

    public void deleteTotalPaymentInMonth() {

        final CustomAdapter customAdapter = new CustomAdapter(monthlypaymentList);
        final BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setMessage(totalPaymentInMonthTextView.getText().toString());
        deleteDialog.setNegativeButton("İptal", null);
        deleteDialog.setPositiveButton("Sil", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    //balance will be rebuild when total payment is deleted.
                    balanceTextView.setText(String.valueOf(Integer.valueOf(balanceTextView.getText().toString()) + Integer.valueOf(totalPaymentInMonthTextView.getText().toString())));
                } catch (NumberFormatException e) {
                    budgetDatabaseHelper.clearMonthPagePaymentTable();
                    monthlypaymentList.clear();
                }
                //total payment comes from sum of payment array list items, so I cleared payment list and then date list is cleared to avoid exceptions.
                totalPaymentInMonthTextView.setText(String.valueOf(0));

                budgetDatabaseHelper.clearMonthPagePaymentTable();
                monthlypaymentList.clear();


                //toJSON, changed values are notified
                String jsonTotalPaymentMonthly = gson.toJson(totalPaymentInMonthTextView.getText().toString());
                editor.putString("jsonTotalPaymentMonthly", jsonTotalPaymentMonthly);

                editor.commit();

                paymentListInDetailPage.setAdapter(customAdapter);

                dialog.dismiss();

                Toast.makeText(getActivity().getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialog.show();
    }

    public void deleteBudget() {

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setMessage(budgetTextView.getText().toString());
        deleteDialog.setNegativeButton("İptal", null);
        deleteDialog.setPositiveButton("Sil", new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                budgetTextView.setText(null);
                balanceTextView.setText(null);

                String jsonBudget = gson.toJson(budgetTextView.getText().toString());
                editor.putString("jsonBudget",jsonBudget);

                String jsonBalance = gson.toJson(balanceTextView.getText().toString());
                editor.putString("jsonBalance", jsonBalance);

                editor.commit();

                budgetArrList.clear();

                dialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialog.show();
    }

    public void deleteBalance() {

        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final Gson gson = new Gson();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setMessage(balanceTextView.getText().toString());

        deleteDialog.setNegativeButton("İptal", null);
        deleteDialog.setPositiveButton("Sil", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //balance is deleted and balance text view is emptied
                balanceTextView.setText(null);
                budgetTextView.setText(null);

                //toJSON, changed values are notifed
                String jsonBalance = gson.toJson(balanceTextView.getText().toString());
                String jsonBudget = gson.toJson(budgetTextView.getText().toString());
                editor.putString("jsonBudget", jsonBudget);
                editor.putString("jsonBalance",jsonBalance);
                editor.commit();

                budgetArrList.clear();

                dialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialog.show();
    }

    public void setupBarChart() {

        Date todaysDate = new Date();
        for (int indexOfPaymentArray = 0; indexOfPaymentArray<monthlypaymentList.size(); indexOfPaymentArray++) {
            try {
                barEntries.add(new BarEntry(Float.valueOf(monthlypaymentList.get(indexOfPaymentArray).getMpayment()), indexOfPaymentArray));
            } catch (NumberFormatException e) {

                monthlypaymentList.remove(indexOfPaymentArray);
                monthlypaymentList.remove(null);

                /*
                paymentArrayListAsDaily.remove(indexOfPaymentArray);
                dateArrayListasDaily.remove(indexOfPaymentArray);
                paymentArrayListAsDaily.remove(null);
                dateArrayListasDaily.remove(null);
                 */

                barEntries.add(new BarEntry(Float.valueOf(monthlypaymentList.get(indexOfPaymentArray).getMpayment()), indexOfPaymentArray));
            }

            try {
                Date paymentDateInBarChart = new SimpleDateFormat("dd/MM").parse(monthlypaymentList.get(indexOfPaymentArray).getMpaymentDate());
                barEntryDate.add(indexOfPaymentArray, new SimpleDateFormat("dd/MM").format(paymentDateInBarChart));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                barEntryDate.add(indexOfPaymentArray, new SimpleDateFormat("dd/MM").format(todaysDate));
            } catch (NullPointerException e) {

            } catch (IndexOutOfBoundsException e) {

            }
        }

        try {
            BarDataSet bardataset = new BarDataSet(barEntries, "Harcamalar");
            BarData bardata = new BarData(barEntryDate, bardataset);
            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            bardataset.setBarSpacePercent(1f);
            barChart.setData(bardata);
        } catch (NullPointerException e) {
            barEntries.add(new BarEntry(0f,0));
            barEntryDate.add(0,0);
        } catch (IllegalArgumentException e) {

        }

        barChart.animateY(5000);
        barChart.setDescription("Aylık Harcama Grafiği");
    }

    final class CustomAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<PaymentDataOfMonthPage> monthlypaymentList;

        public CustomAdapter(ArrayList<PaymentDataOfMonthPage> paymentDataOfMonthPageList) {
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.monthlypaymentList = paymentDataOfMonthPageList;
        }

        @Override
        public int getCount() {
            return monthlypaymentList.size();
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

            //total payments on a day are showed in ListView
            TextView totalPaymentOnDayTextView = convertView.findViewById(R.id.paymentTextView);
            //dates of payments are showed in Listview
            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            //delete button in the list row
            ImageButton deletePaymentImageButton = convertView.findViewById(R.id.deletePaymentImageButton);
            ImageButton pencilImageButton = convertView.findViewById(R.id.pencilImageButton);

            //ı dont use payment name in detail page so ı made invisible it.
            pencilImageButton.setVisibility(View.INVISIBLE);

            totalPaymentOnDayTextView.setText(String.valueOf(monthlypaymentList.get(position).getMpayment()));
            dateTextView.setText(String.valueOf(monthlypaymentList.get(position).getMpaymentDate()));

            //delete button for deleting that selected row (payment-date)
            deletePaymentImageButton.setOnClickListener(new View.OnClickListener() {

                //total payment in month after deleting any total payment in any day
                String totalPaymentInMonth = totalPaymentInMonthTextView.getText().toString();

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                    deleteDialog.setMessage(String.valueOf(monthlypaymentList.get(position).getMpayment()));

                    deleteDialog.setNegativeButton("İptal", null);
                    deleteDialog.setPositiveButton("Sil", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                totalPaymentInMonthTextView.setText(String.valueOf(Integer.valueOf(totalPaymentInMonth) - Integer.valueOf(monthlypaymentList.get(position).getMpayment())));
                            } catch (NumberFormatException e) {
                                System.out.println("NumberFormatException in detail page's custom adapter");
                            }
                            BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());

                            budgetDatabaseHelper.deleteRowOfMonthPagePaymentTable(monthlypaymentList.get(position).getMpayment(), monthlypaymentList.get(position).getMpaymentDate());
                            //payment and date list element in the selected are removed position and null index is removed
                            monthlypaymentList.remove(position);
                            monthlypaymentList.remove(null);

                            // Notifies the attached observers that the underlying data has been changed
                            // and any View reflecting the data set should refresh itself.
                            notifyDataSetChanged();

                            //toJSON, changed values are notified
                            String jsonTotalPaymentMonthly = gson.toJson(totalPaymentInMonthTextView.getText().toString());
                            editor.putString("jsonTotalPaymentMonthly", jsonTotalPaymentMonthly);

                            editor.commit();

                            dialog.dismiss();

                            Toast.makeText(getActivity().getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleteDialog.show();
                }
            });
            return convertView;
        }
    }
}

