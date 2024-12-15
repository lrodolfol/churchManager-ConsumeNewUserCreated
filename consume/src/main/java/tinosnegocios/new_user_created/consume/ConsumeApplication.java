package tinosnegocios.new_user_created.consume;

import com.google.gson.Gson;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import tinosnegocios.new_user_created.consume.Models.UserCreated;
import tinosnegocios.new_user_created.consume.Services.RabbitMqConsume;
import tinosnegocios.new_user_created.consume.Services.SNSService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class ConsumeApplication {

	public static void main(String[] args) {
		SNSService snsService = new SNSService();
		System.out.println("Process initialized, waiting messages...");

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		// Agendando uma tarefa para rodar a cada 5 segundos
		scheduler.scheduleAtFixedRate(() -> {
			snsService.searchAndSendEmailToConfirmedEmails();
		}, 0, 5, TimeUnit.SECONDS);

		RabbitMqConsume rabbitMqConsume = new RabbitMqConsume();
		rabbitMqConsume.ConsumeMessages();
	}
}
