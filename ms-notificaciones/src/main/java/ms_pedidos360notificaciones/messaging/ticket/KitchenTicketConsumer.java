package ms_pedidos360notificaciones.messaging.ticket;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
public class KitchenTicketConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(KitchenTicketConsumer.class);

    private static final String RETRY_COUNT_HEADER = "x-retry-count";

    private final JsonMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final String retryExchange;
    private final int maxRetries;

    public KitchenTicketConsumer(
            JsonMapper objectMapper,
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.retry}") String retryExchange,
            @Value("${rabbitmq.retry.max-retries}") int maxRetries) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.retryExchange = retryExchange;
        this.maxRetries = maxRetries;
    }

    @RabbitListener(queues = "${rabbitmq.queue.kitchen-ticket}")
    public void receive(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        NotificationCommand command;

        // Mensaje inválido: se envía directamente a la DLQ.
        try {
            command = objectMapper.readValue(
                    message.getBody(),
                    NotificationCommand.class
            );

            if (command.eventId() == null || command.eventId().isBlank()) {
                throw new IllegalArgumentException(
                        "El mensaje no incluye eventId"
                );
            }
        } catch (Exception ex) {
            log.error("Mensaje de ticket inválido; se enviará a la DLQ", ex);
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        try {
            log.info(
                    "Ticket de cocina simulado: eventId={}, pedido={}, cliente={}, estado={}",
                    command.eventId(),
                    command.orderId(),
                    command.clienteId(),
                    command.estado()
            );

            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            Object headerValue = message.getMessageProperties()
                    .getHeaders()
                    .get(RETRY_COUNT_HEADER);

            int retries = headerValue instanceof Number
                    ? ((Number) headerValue).intValue()
                    : 0;

            if (retries >= maxRetries) {
                log.error(
                        "Se agotaron los reintentos del ticket; se enviará a la DLQ",
                        ex
                );
                channel.basicNack(deliveryTag, false, false);
                return;
            }

            String routingKey = message.getMessageProperties()
                    .getReceivedRoutingKey();

            try {
                Message retryMessage = MessageBuilder
                        .fromMessage(message)
                        .setHeader(RETRY_COUNT_HEADER, retries + 1)
                        .build();

                rabbitTemplate.send(retryExchange, routingKey, retryMessage);

                log.warn(
                        "Ticket enviado a reintento {}/{}: eventId={}",
                        retries + 1,
                        maxRetries,
                        command.eventId(),
                        ex
                );

                channel.basicAck(deliveryTag, false);
            } catch (Exception retryException) {
                log.error(
                        "No se pudo publicar el reintento del ticket; se reencolará el mensaje original",
                        retryException
                );
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }
}
