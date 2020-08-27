package rabbitmq.four.two;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class RPCServer {
    private static final String RPC_QUEUE="rpc_queue";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ服务端默认端口号为5672

    public static void main(String args[]) throws Exception{
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("root");
        factory.setPassword("root");
        //这里的连接方式与生产者的demo 略有不同，注意辨别区别
        Connection connection = factory.newConnection(addresses); //创建连接
        final Channel channel = connection.createChannel(); //创建信道
        channel.queueDeclare(RPC_QUEUE,false,false,false,null);
        channel.basicQos(1);//设置客户端最多接收未被ack的消息的个数
        System.out.println("[ x] Awaiting RPC requests");

        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                AMQP.BasicProperties replyProps=new AMQP.BasicProperties.Builder().correlationId(properties.getCorrelationId())
                        .build();
                String response="";
                try{
                    String message=new String(body,"UTF-8");
                    int n=Integer.parseInt(message);
                    System.out.println("[.]fib("+message+")");
                    response+=fib(n);
                }catch (RuntimeException e){
                    System.out.println("[.]"+e.toString());
                }finally {
                    channel.basicPublish("",properties.getReplyTo(),replyProps,response.getBytes("UTF-8"));
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }

            private int fib(int n) {
                if(n==0) return 0;
                if(n==1) return 1;
                return fib(n-1)+fib(n-2);

            }
        };
        channel.basicConsume(RPC_QUEUE,false,consumer);
    }
}
