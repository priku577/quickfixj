package quickfix.examples.banzai.restapi;

import quickfix.SessionID;
import quickfix.examples.banzai.*;
import quickfix.examples.banzai.restapi.message.MarketOrder;
import quickfix.examples.banzai.restapi.message.Message;
import spark.Request;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Hello world!
 *
 */
public class App implements Runnable
{
    private BlockingQueue<Message> restMsgQueue;
    private BlockingQueue<quickfix.Message> fixMsgQueue;

    private PrintWriter printWriter;

    public App(BlockingQueue<Message> restMsgQueue, BlockingQueue<quickfix.Message> fixMsgQueue,
               PrintWriter printWriter) {
        this.restMsgQueue = restMsgQueue;
        this.fixMsgQueue = fixMsgQueue;
        this.printWriter = printWriter;
    }

    @Override
    public void run()
    {
        post("/api/v1/accounts/marketorder", "application/json",(request, response) -> {

           String orderId = request.queryMap("orderId").value();
           printWriter.println("RESTServer -> BanzaiApplication (Outgoing) restMsg MarketOrder \n\n");
           restMsgQueue.put(getMarketOrder(request));
           quickfix.fix43.Quote quote = (quickfix.fix43.Quote )fixMsgQueue.take();
           printWriter.println("BanzaiApplication -> RESTServer (Incoming) quickfix Quote \n" + quote + "\n\n");
           printWriter.println("RESTServer -> RESTClient (Outgoing)  quickfix Quote \n" + quote+ "\n\n");

            printWriter.close();
           return quote;
        },new JsonTransformer());

        get("/posts", (req, res) -> {

            return "Hello Sparkinglys World!";
        });


        get("/uurs", (req, res) -> {
            Order order = getOrder();
            printWriter.println(" get uurs ");
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
