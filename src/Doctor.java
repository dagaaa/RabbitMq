import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


public class Doctor {

    public static void main(String[] args) throws IOException, TimeoutException {
        // info
        System.out.println("Doctor");

        String uuid = UUID.randomUUID().toString();
        AMQP.BasicProperties p = new AMQP.BasicProperties.Builder().replyTo(uuid).build();

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

        String queueName = channel.queueDeclare().getQueue();
        String queueName3=channel.queueDeclare().getQueue();

    //    channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME2, uuid);

//        channel.queueDeclare(queueName3, false, false, false, null);
        channel.queueBind(queueName3, EXCHANGE_NAME3, "");



        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

            }
        };


        while (true) {

            channel.basicConsume(queueName, true, consumer);
            channel.basicConsume(queueName3, true, consumer);

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
            channel.basicPublish(EXCHANGE_NAME, routingKey+"."+patient, p, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);



        }
    }
}
