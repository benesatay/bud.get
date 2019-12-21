package com.example.budget;

public class PaymentDataOfMonthPage {

    int mid;
    String mpayment;
    String mpaymentDate;

    public PaymentDataOfMonthPage() {

    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMpayment() {
        return mpayment;
    }

    public void setMpayment(String mpayment) {
        this.mpayment = mpayment;
    }

    public String getMpaymentDate() {
        return mpaymentDate;
    }

    public void setMpaymentDate(String mpaymentDate) {
        this.mpaymentDate = mpaymentDate;
    }

    public PaymentDataOfMonthPage(String mpayment, String mpaymentDate) {
        this.mpayment = mpayment;
        this.mpaymentDate = mpaymentDate;
    }

}
