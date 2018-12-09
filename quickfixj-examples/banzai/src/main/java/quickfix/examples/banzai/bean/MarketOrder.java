package quickfix.examples.banzai.bean;

public class MarketOrder {

    public MarketOrder(String fromCurrency, String toCurrency, Double amount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    public String fromCurrency;
    public String toCurrency;
    public Double amount;


}
