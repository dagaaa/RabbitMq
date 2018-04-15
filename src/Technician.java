import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Technician {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("Technician");

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

        // queue & bind
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter technician specializations: ");
        String specalizations = br.readLine();
        String[] specalizationsArray = specalizations.split(" ");

//        String queueName = channel.queueDeclare().getQueue();

        String queueName1 = specalizationsArray[0];
        String queueName2 = specalizationsArray[1];

        channel.queueDeclare(queueName1, false, false, false, null);
        channel.queueBind(queueName1, EXCHANGE_NAME, queueName1+".*");

        channel.queueDeclare(queueName2, false, false, false, null);
        channel.queueBind(queueName2, EXCHANGE_NAME, queueName2+".*");

        System.out.println("created queue: " + queueName1);
        System.out.println("created queue: " + queueName2);

        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

                try {
                    Thread.sleep( 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                message=message+" done";
                // publish
                channel.basicPublish(EXCHANGE_NAME2, properties.getReplyTo(), null, message.getBytes("UTF-8"));
                System.out.println("Sent: " + message);
            }
        };

        // start listening
        System.out.println("Waiting for messages...");

        channel.basicConsume(queueName1, true, consumer);
        channel.basicConsume(queueName2, true, consumer);




    }
}
