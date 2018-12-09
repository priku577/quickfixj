package quickfix.examples.banzai.restapi.message;

public class MarketOrder implements Message {

    public MarketOrder(String fromCurrency, String toCurrency, Double amount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    public String fromCurrency;
    public String toCurrency;
    public Double amount;


    @Override
    public Type getType() {
        return Type.QuoteRequest;
    }
}
