package com.example.budget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class profile_page extends Fragment {

    public profile_page() {
        // Required empty public constructor
    }

    ArrayList<PaymentDataOfProfilePage> profilePaymentList;

    TextView nameTextViewInProfileToolbar;
    TextView creationDateTextView;

    PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        pieChart = view.findViewById(R.id.piechart);

        Toolbar profileToolbar = view.findViewById(R.id.profileToolbar);
        profileToolbar.setTitle("username");
        ((AppCompatActivity)getActivity()).setSupportActionBar(profileToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        creationDateTextView = view.findViewById(R.id.creationDateTextView);

        TextView totalPaymentAllTimeTextView = view.findViewById(R.id.totalPaymentAllTimeTextView);
        TextView totalSavingAllTimeTextView = view.findViewById(R.id.totalSavingAllTimeTextView);

        ImageButton allTimeTotalPaymentDeleteImageButton = view.findViewById(R.id.allTimeTotalPaymentImageButton);
        ImageButton allTimeTotalSavingImageButton = view.findViewById(R.id.allTimeTotalSavingImageButton);

        BudgetDatabaseHelper budgetDatabaseHelper = new BudgetDatabaseHelper(getActivity().getApplicationContext());
        profilePaymentList = (ArrayList<PaymentDataOfProfilePage>) budgetDatabaseHelper.getAllProfilePagePaymentData();

        generatePaymentListAndPaymentNameList(profilePaymentList);

        /*
        //INCELE YENİDEN YAZ
        //total payment for all time

        int allTimeTotalPayment = 0;

        for(int indexOfPaymentArray = 0; indexOfPaymentArray<allPaymentArrayList.size(); indexOfPaymentArray++) {
            if(allPaymentArrayList.size() <= 1) {
                try {
                    allTimeTotalPayment = Integer.valueOf(instantPaymentList.get(0));
                } catch (NumberFormatException e) {
                    allPaymentArrayList.clear();
                } catch (IndexOutOfBoundsException e) {
                    allTimeTotalPayment = 0;
                }
            } else {
                try {
                    allTimeTotalPayment = allTimeTotalPayment + Integer.valueOf(allPaymentArrayList.get(indexOfPaymentArray));
                } catch (NumberFormatException e) {
                    allPaymentArrayList.remove(null);
                }
            }
        }
        if (allTimeTotalPayment == 0) {
            totalPaymentAllTimeTextView.setText("");
        } else {
            totalPaymentAllTimeTextView.setText(String.valueOf(allTimeTotalPayment));
        }




     allSavingArrayList.add(0, getIntent().getStringExtra(SAVING_TO_PROFILE));
        int allTimeTotalSaving = 0;

        for (int indexOfSavingArray = 0; indexOfSavingArray<allSavingArrayList.size(); indexOfSavingArray++) {
            if (allSavingArrayList.size() <= 1) {
                try {
                    allTimeTotalSaving = Integer.valueOf(getIntent().getStringExtra(SAVING_TO_PROFILE));
                } catch (NumberFormatException e) {
                    allSavingArrayList.clear();
                }
            } else {
                try {
                    allTimeTotalSaving = allTimeTotalSaving + Integer.valueOf(allSavingArrayList.get(indexOfSavingArray));
                } catch (NumberFormatException e) {
                    allSavingArrayList.remove(null);
                }
            }
        }
        if (allTimeTotalSaving == 0) {
            totalSavingAllTimeTextView.setText("");
        } else {
            totalSavingAllTimeTextView.setText(String.valueOf(allTimeTotalSaving));
        }

        //toJSON
        String allTimeTotalPaymentToJson = gson.toJson(String.valueOf(allTimeTotalPayment));
        String allTimeTotalSavingToJson = gson.toJson(String.valueOf(allTimeTotalSaving));

        editor.putString("allTimeTotalPayment", allTimeTotalPaymentToJson);
        editor.putString("allTimeTotalSaving", allTimeTotalSavingToJson);
        editor.commit(); */

        allTimeTotalPaymentDeleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        allTimeTotalSavingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupPieChart();

        return view;
    }

    public void generatePaymentListAndPaymentNameList(ArrayList<PaymentDataOfProfilePage> profilePaymentList) {

        this.profilePaymentList = profilePaymentList;

        if (profilePaymentList.size() > 1) {
            int sumOfSameNamelyPayment;
            String renamedPayment;
            for (int indexOfProfilePaymentList = 0; indexOfProfilePaymentList < profilePaymentList.size(); indexOfProfilePaymentList++) {
                for(int j = 0; j <=indexOfProfilePaymentList; j++) {
                    try {
                        while (indexOfProfilePaymentList != j && profilePaymentList.get(j).getPpaymentName().equals(profilePaymentList.get(indexOfProfilePaymentList).getPpaymentName())) {
                            sumOfSameNamelyPayment = Integer.valueOf(profilePaymentList.get(j).getPpayment()) + Integer.valueOf(profilePaymentList.get(indexOfProfilePaymentList).getPpayment());
                            renamedPayment = profilePaymentList.get(j).getPpaymentName();

                            profilePaymentList.remove(j);
                            profilePaymentList.remove(indexOfProfilePaymentList-1);

                            profilePaymentList.add(new PaymentDataOfProfilePage(String.valueOf(sumOfSameNamelyPayment), renamedPayment));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("fsdsdfafdsasdfafafaf");
                    }
                }
            }

            int sumOfSameNamelyPayment01 = 0;
            String renamedPayment01 = "";
            try {
                sumOfSameNamelyPayment01 = Integer.valueOf(profilePaymentList.get(0).getPpayment()) + Integer.valueOf(profilePaymentList.get(1).getPpayment());
                renamedPayment01 = profilePaymentList.get(0).getPpaymentName();

                if(profilePaymentList.get(0).getPpaymentName().equals(profilePaymentList.get(1).getPpaymentName())) {
                    profilePaymentList.remove(0);
                    profilePaymentList.remove(0);
                    profilePaymentList.remove(null);
                    profilePaymentList.add(new PaymentDataOfProfilePage(String.valueOf(sumOfSameNamelyPayment01), renamedPayment01));
                }
            } catch (IndexOutOfBoundsException e) {
                profilePaymentList.clear();
                profilePaymentList.add(new PaymentDataOfProfilePage(String.valueOf(sumOfSameNamelyPayment01), renamedPayment01));
            }
        }

    }

    public void setupPieChart() {
        ArrayList piePaymentList = new ArrayList();
        ArrayList piePaymentNames = new ArrayList();

        for(int indexOfPaymentArray = 0; indexOfPaymentArray<profilePaymentList.size(); indexOfPaymentArray++) {
            try {
                piePaymentList.add(new Entry(Float.valueOf(profilePaymentList.get(indexOfPaymentArray).getPpayment()), indexOfPaymentArray));
            } catch (NullPointerException e) {
                piePaymentList.add(new Entry(0f,indexOfPaymentArray));
            }
            try {
                piePaymentNames.add(indexOfPaymentArray, profilePaymentList.get(indexOfPaymentArray).getPpaymentName());
            } catch (IndexOutOfBoundsException e) {
                piePaymentNames.remove(null);
                piePaymentNames.add("");
            }
        }

        PieDataSet dataSet = new PieDataSet(piePaymentList, "Harcama Türleri");

        try {
            PieData data = new PieData(piePaymentNames,dataSet);
            data.setValueTextSize(10);
            pieChart.setData(data);
        } catch (NullPointerException e) {
            piePaymentList.add(new BarEntry(0f,0));
            piePaymentNames.add(0,0);
        }
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setDescription("Harcama Türü Grafiği");
        pieChart.animateXY(3000, 3000);
    }

    public void userInfo() {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final Gson gson = new Gson();

        String username = "...";
        String creationDate = "...";
        nameTextViewInProfileToolbar.setText(username);


        String creationDateText = creationDate;
        String creationDateFromJson = sharedPreferences.getString("creationDate", null);
        Type typeCreationDate = new TypeToken<String>() {}.getType();
        try {
            if (creationDateText == null) {
                creationDateTextView.setText(gson.fromJson(creationDateFromJson,typeCreationDate) + " Tarihinden İtibaren");
            } else {
                creationDateTextView.setText(creationDateText + " Tarihinden İtibaren");
                String creationDateToJson = gson.toJson(creationDateText);
                editor.putString("creationDate",creationDateToJson);
                editor.apply();
            }
        } catch (NullPointerException e) {
            System.out.println("creationDate is null in pp");
        }

    }

}