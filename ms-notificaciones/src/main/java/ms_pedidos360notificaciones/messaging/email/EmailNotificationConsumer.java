package ms_pedidos360notificaciones.messaging.email;

import tools.jackson.databind.json.JsonMapper;
import com.rabbitmq.client.Channel;
import ms_pedidos360notificaciones.messaging.dto.NotificationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailNotificationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(EmailNotificationConsumer.class);

    private final JsonMapper objectMapper;

    public EmailNotificationConsumer(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification-email}")
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
                    "Notificación por correo simulada: eventId={}, pedido={}, cliente={}, estado={}",
                    command.eventId(),
                    command.orderId(),
                    command.clienteId(),
                    command.estado()
            );
        } catch (Exception ex) {
            log.error("No se pudo procesar el mensaje de correo; se enviará a la DLQ", ex);
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        channel.basicAck(deliveryTag, false);
    }
}
