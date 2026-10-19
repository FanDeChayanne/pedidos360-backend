package pedidos360.ms_orders.messaging;

import pedidos360.ms_orders.messaging.dto.NotificationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Locale;

@Component
public class NotificationPublisher {
    private static final Logger log =
            LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final JsonMapper jsonMapper;
    private final String directExchange;
    private final String topicExchange;
    private final String emailRoutingKey;
    private final String pushRoutingPrefix;
    private final String ticketRoutingKey;

    public NotificationPublisher(
            RabbitTemplate rabbitTemplate,
            JsonMapper jsonMapper,
            @Value("${rabbitmq.exchange.commands-direct}") String directExchange,
            @Value("${rabbitmq.exchange.commands-topic}") String topicExchange,
            @Value("${rabbitmq.routing.email}") String emailRoutingKey,
            @Value("${rabbitmq.routing.push}") String pushRoutingPrefix,
            @Value("${rabbitmq.routing.ticket}") String ticketRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.jsonMapper = jsonMapper;
        this.directExchange = directExchange;
        this.topicExchange = topicExchange;
        this.emailRoutingKey = emailRoutingKey;
        this.pushRoutingPrefix = pushRoutingPrefix;
        this.ticketRoutingKey = ticketRoutingKey;
    }

    public void publicarCorreo(NotificationCommand command) {
        publicar(directExchange, emailRoutingKey, command);
    }

    public void publicarPush(NotificationCommand command) {
        String estado = command.estado() == null
                ? "desconocido"
                : command.estado().toLowerCase(Locale.ROOT);

        publicar(topicExchange, pushRoutingPrefix + "." + estado, command);
    }

    public void publicarTicket(NotificationCommand command) {
        publicar(directExchange, ticketRoutingKey, command);
    }

    private void publicar(
            String exchange,
            String routingKey,
            NotificationCommand command) {
        try {
            byte[] body = jsonMapper.writeValueAsBytes(command);

            MessageProperties properties = new MessageProperties();
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

            rabbitTemplate.send(
                    exchange,
                    routingKey,
                    new Message(body, properties)
            );
        } catch (Exception ex) {
            log.error(
                    "No se pudo publicar el comando {} para el pedido {}",
                    command.type(),
                    command.orderId(),
                    ex
            );
        }
    }
}
