import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;


public class Doctor {

    public static void main(String[] args) throws IOException, TimeoutException {
        // info
        System.out.println("Doctor");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "exchange1";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);


        while (true) {

            // read msg
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter type or exit: ");
            String routingKey = br.readLine();
            System.out.println("Enter patient name: ");
            String patient = br.readLine();
            String message=routingKey+" "+patient;
            // break condition
            if ("exit".equals(routingKey)) {
                break;
            }

            // publish
            channel.basicPublish(EXCHANGE_NAME, routingKey+"."+patient, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }
    }
}
