import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Admin {

    public static void main(String[] args) throws IOException, TimeoutException {
        // info
        System.out.println("Admin");


        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "exchange1";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String EXCHANGE_NAME2 = "exchange2";
        channel.exchangeDeclare(EXCHANGE_NAME2, BuiltinExchangeType.TOPIC);

        String EXCHANGE_NAME3 = "exchange3";
        channel.exchangeDeclare(EXCHANGE_NAME3, BuiltinExchangeType.FANOUT);

        String queueName = "queueAmin";
        String queueName1 = "queueAmin1";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, "#");

        channel.queueDeclare(queueName1, false, false, false, null);
        channel.queueBind(queueName1, EXCHANGE_NAME2, "#");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

            }
        };


        while (true) {

            channel.basicConsume(queueName, true, consumer);
            channel.basicConsume(queueName1, true, consumer);
            // read msg
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter info: ");
            String info = br.readLine();
            // break condition
            if ("exit".equals(info)) {
                break;
            }

            // publish
            channel.basicPublish(EXCHANGE_NAME3, "", null, info.getBytes("UTF-8"));
            System.out.println("Sent: " + info);


        }


    }
}
