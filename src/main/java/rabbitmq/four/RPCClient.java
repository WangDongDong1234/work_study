package rabbitmq.four;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class RPCClient {
    private Connection connection;
    private Channel channel;
    private String requestQueueName="rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws IOException, TimeoutException {
        connection=new ConnectionFactory().newConnection();
        channel=connection.createChannel();
        //回调队列
        replyQueueName=channel.queueDeclare().getQueue();//??
        consumer=new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName,true,consumer);
    }

    public String call(String message) throws IOException, InterruptedException {
        String response=null;
        String corrId= UUID.randomUUID().toString();
        //将回调队列风轧辊到props
        AMQP.BasicProperties props=new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();
        //发送带回调对列的消息
        channel.basicPublish("",requestQueueName,props,message.getBytes());
        while (true){
            QueueingConsumer.Delivery delivery=consumer.nextDelivery();
            if(delivery.getProperties().getCorrelationId().equals(corrId)){
                response=new String(delivery.getBody());
                break;
            }
        }
        return response;
    }

    public void close() throws Exception{
        connection.close();
    }

    public static void main(String args[]) throws Exception {
        RPCClient fibRpc=new RPCClient();
        System.out.println("[X] Requesting fib(30)");
        String response=fibRpc.call("30");
        System.out.println("[.] Got'"+response+"'");
        fibRpc.close();
    }

}
