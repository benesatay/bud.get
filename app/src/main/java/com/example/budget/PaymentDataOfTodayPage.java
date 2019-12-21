package com.example.budget;

import java.io.Serializable;

public class PaymentDataOfTodayPage implements Serializable {

    int id;
    String payment;
    String paymentName;
    String paymentDate;

    public PaymentDataOfTodayPage() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentDataOfTodayPage(String payment, String paymentName, String paymentDate) {
        this.payment = payment;
        this.paymentName = paymentName;
        this.paymentDate = paymentDate;
    }
}
