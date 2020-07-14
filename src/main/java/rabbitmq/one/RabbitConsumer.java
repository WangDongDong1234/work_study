package rabbitmq.one;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitConsumer {
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root");
        //这里的连接方式与生产者的demo 略有不同，注意辨别区别
        Connection connection = factory.newConnection(addresses); //创建连接
        final Channel channel = connection.createChannel(); //创建信道
        channel.basicQos(64); //设置客户端最多接收未被ack的消息的个数
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("recv message : " + new String(body)+"second");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }

            @Override
            public void handleConsumeOk(String consumerTag) {
                super.handleConsumeOk(consumerTag);
                System.out.println("first");
            }

            @Override
            public void handleCancelOk(String consumerTag) {
                super.handleCancelOk(consumerTag);
                System.out.println("third");
            }
        };
        //1.推消息
        //channel.basicConsume(QUEUE_NAME,true, consumer);
        //2。拉去消息
        GetResponse response=channel.basicGet(QUEUE_NAME,false);
        System.out.println(new String(response.getBody()));
        channel.basicAck(response.getEnvelope().getDeliveryTag(),false);
        //等待回调函数执行完毕之后， 关闭资源
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();

    }
}
