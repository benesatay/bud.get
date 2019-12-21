package com.example.budget;

public class PaymentDataOfProfilePage {

    int pid;
    String ppayment;
    String ppaymentName;

    public PaymentDataOfProfilePage() {

    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPpayment() {
        return ppayment;
    }

    public void setPpayment(String ppayment) {
        this.ppayment = ppayment;
    }

    public String getPpaymentName() {
        return ppaymentName;
    }

    public void setPpaymentName(String ppaymentName) {
        this.ppaymentName = ppaymentName;
    }

    public PaymentDataOfProfilePage(String ppayment, String ppaymentName) {
        this.ppayment = ppayment;
        this.ppaymentName = ppaymentName;
    }
}
