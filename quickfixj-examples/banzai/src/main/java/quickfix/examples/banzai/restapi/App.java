package quickfix.examples.banzai.restapi;

import quickfix.SessionID;
import quickfix.examples.banzai.*;

import static spark.Spark.get;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void start( final BanzaiApplication application)
    {
        System.out.println( "Hello World ing!" );
       get("/posts", (req, res) -> {

            return "Hello Sparkinglys World!";
        });

        get("/uurs", (req, res) -> {

            Order order = getOrder();

            System.out.println(" get uurs ");
            //application.send(order);
            application.sendQuoteReq(new SessionID("FIX.4.3", "BANZAI", "",
                    "", "EXEC", "",
                    "", ""));

            return "Hello uurs World!";
        });

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


        order.setSessionID(new SessionID("FIX.4.3", "BANZAI", "",
                "", "EXEC", "",
                "", ""));
        return order;
    }
}
