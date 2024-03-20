package com.zeco.notificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(/*OrderPlacedEvent orderPlacedEvent*/String msg) {
		//log.info("received notification for order - {} ",orderPlacedEvent.getOrderNumber());
		log.info("received notification for order - {} ",msg);
		/*Observation.createNotStarted("on-message", this.observationRegistry).observe(() -> {
			log.info("Got message <{}>", orderPlacedEvent);
			log.info("TraceId- {}, Received Notification for Order - {}", this.tracer.currentSpan().context().traceId(),
					orderPlacedEvent.getOrderNumber());
		});*/
		// send out an email notification
	}

}
