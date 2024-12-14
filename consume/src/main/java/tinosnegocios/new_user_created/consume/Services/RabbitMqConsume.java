package tinosnegocios.new_user_created.consume.Services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMqConsume {
    private static final String QUEUE_NAME = "user_created";
    private static final String QUEUE_NAME_DLQ = "user_created_dead_leatter";
    private static final String EXCHANGE_DLQ = "user_dead_leatter";
    private final ProcessReadMessage processReadMessage;

    public RabbitMqConsume(){
        processReadMessage = new ProcessReadMessage();
    }

    public void ConsumeMessages(){
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "127.0.0.1"));
            factory.setUsername(System.getenv().getOrDefault("RABBITMQ_USERNAME", "sinqia"));
            factory.setPassword(System.getenv().getOrDefault("RABBITMQ_PASSWORD", "sinqia123"));
            String rabbitMqPort = System.getenv().getOrDefault("RABBITMQ_PORT", "5674");
            factory.setPort(Integer.parseInt(rabbitMqPort));

            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();

            channel.queueDeclarePassive(QUEUE_NAME_DLQ);

            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-dead-letter-exchange", EXCHANGE_DLQ); // Usar o exchange padrão (direct)
            arguments.put("x-dead-letter-routing-key", "created_dead_leatter");

            channel.queueDeclare(QUEUE_NAME, false, false, false, arguments);

            // Callback para processar mensagens
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("Mensagem recebida: " + message);

                try {
                    if(processReadMessage.Process(message)) {
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        System.out.println("Mensagem processada com sucesso");
                    }else{
                        System.err.println("Erro no processamento da mensagem. Enviada para DQL");
                        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                    }
                } catch (Exception e) {
                    System.err.println("Erro no processamento da mensagem. Enviada para DQL");
                }
            };

            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (Exception ex){
            System.out.println("Falha " + ex.getMessage());
        }
    }
}
