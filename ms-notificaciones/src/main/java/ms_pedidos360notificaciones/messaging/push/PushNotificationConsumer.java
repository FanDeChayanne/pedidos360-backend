package ms_pedidos360notificaciones.messaging.push;

import org.springframework.stereotype.Component;
import ms_pedidos360notificaciones.messaging.dto.NotificationCommand;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
public class PushNotificationConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(PushNotificationConsumer.class);

    private final JsonMapper objectMapper;

    public PushNotificationConsumer(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification-push}")
    public void receive(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            NotificationCommand command = objectMapper.readValue(
                    message.getBody(),
                    NotificationCommand.class
            );

            if (command.eventId() == null || command.eventId().isBlank()) {
                throw new IllegalArgumentException("El mensaje no incluye eventId");
            }

            log.info(
                    "Notificación push simulada: eventId={}, pedido={}, cliente={}, estado={}",
                    command.eventId(),
                    command.orderId(),
                    command.clienteId(),
                    command.estado()
            );
        } catch (Exception ex) {
            log.error("No se pudo procesar el mensaje push; se enviará a la DLQ", ex);
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        channel.basicAck(deliveryTag, false);
    }
}
