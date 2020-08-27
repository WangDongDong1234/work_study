package rabbitmq.four.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

//优先级队列
public class QueuePriorityTest {

    private static final String EXCHANGE_NAME = "exchange_queue_priority";
    private static final String ROUTING_KEY = "routingkey_queue_priority";
    private static final String QUEUE_NAME = "queue_queue_priority";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ服务端默认端口号为5672

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("root");
        factory.setPassword("root");
        Connection connection = factory.newConnection();//创建连接
        Channel channel = connection.createChannel();//创建信道
        //创建一个type ＝ ” direct ” 、持久化的、非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true, false, null);
        //使用参数x-max-priority指定当前的队列为优先级队列
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-max-priority", 10);
        //创建一个持久化、非排他的、非自动删除的队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        //将交换器与队列通过路由键绑定
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        //关闭资源
        channel.close();
        connection.close();
    }
}
