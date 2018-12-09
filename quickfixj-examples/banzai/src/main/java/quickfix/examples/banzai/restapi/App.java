package quickfix.examples.banzai.restapi;

import quickfix.SessionID;
import quickfix.examples.banzai.*;
import quickfix.examples.banzai.bean.MarketOrder;
import spark.Request;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void start( final BanzaiApplication application)
    {
        System.out.println( "Hello World ing!" );
       post("/api/v1/accounts/marketorder", "application/json",(request, response) -> {

           String orderId = request.queryMap("orderId").value();
           application.startRFQ(getSessionId(), getMarketOrder(request));

           return "{\"OrderId\": \""+ orderId +"\"," +
           "\"Status\": \"P\", \"Text\": \"\"}";
       }

        );

        get("/posts", (req, res) -> {

            return "Hello Sparkinglys World!";
        });


        get("/uurs", (req, res) -> {

            Order order = getOrder();

            System.out.println(" get uurs ");
            //application.send(order);
            /*application.sendQuoteReq(new SessionID("FIX.4.3", "BANZAI", "",
                    "", "EXEC", "",
                    "", ""));*/

            return "Hello uurs World!";
        });


    }

    private static MarketOrder getMarketOrder(Request request) {
        return new MarketOrder(request.queryMap("FromCurrency").value(),
                       request.queryMap("ToCurrency").value(),
                       Double.parseDouble(request.queryMap("Amount").value()));
    }

    private static SessionID getSessionId() {
        return new SessionID("FIX.4.3", "BANZAI", "",
                "", "EXEC", "",
                "", "");
    }

    private static Order getOrder() {
        Order order = new Order();
        order.setSide(OrderSide.BUY);
        order.setType(OrderType.LIMIT);
        order.setTIF(OrderTIF.DAY);

        order.setSymbol("DAN");
        order.setQuantity(12);
        order.setOpen(12);

        OrderType type = order.getType();
        if (type == OrderType.LIMIT || type == OrderType.STOP_LIMIT)
            order.setLimit("12");
        if (type == OrderType.STOP || type == OrderType.STOP_LIMIT)
            order.setStop("");


        order.setSessionID(getSessionId());
        return order;
    }
}
