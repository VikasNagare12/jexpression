package com.example.jexpression.model;

public class Transaction {
    private String country;
    private String channel;
    private Payment payment;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // Derived/Computed Getter: This will be serialized as "derivedStatus" in JSON
    public String getDerivedStatus() {
        if (country == null)
            return "Unknown";
        return "COMPUTED_" + country;
    }

    private String transactionDate;

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    // Helper for Rule Logic: Convert ISO Date to Numeric (YYYYMMDD) or Epoch
    // Simple approach: YYYYMMDD as long
    public long getTransactionDateNumeric() {
        if (transactionDate == null)
            return 0;
        return Long.parseLong(transactionDate.replace("-", ""));
    }

    // This method provides a dynamic value for logic comparison
    public String getExpectedCurrency() {
        return "SAR"; // In real life, this could apply complex logic
    }
}
