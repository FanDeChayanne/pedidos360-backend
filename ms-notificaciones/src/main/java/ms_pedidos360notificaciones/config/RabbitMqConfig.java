package ms_pedidos360notificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean("commandsDirectExchange")
    DirectExchange directExchange(
            @Value("${rabbitmq.exchange.commands-direct}")
            String name
    ) {
        return new DirectExchange(name);
    }

    @Bean("commandsTopicExchange")
    TopicExchange commandsExchange(
            @Value("${rabbitmq.exchange.commands-topic}")
            String name
    ) {
        return new TopicExchange( name);
    }

    @Bean
    DirectExchange deadLetterExchange(
            @Value("${rabbitmq.exchange.dead-letter}") String name) {
        return new DirectExchange(name);
    }

    @Bean
    Queue notificationEmailQueue(
            @Value("${rabbitmq.queue.notification-email}")
            String name,
            @Value("${rabbitmq.exchange.dead-letter}")
            String dlx,
            @Value("${rabbitmq.routing.dlq-email}")
            String dlqRoutingKey){
        return QueueBuilder.durable(name)
                .deadLetterExchange(dlx)
                .deadLetterRoutingKey(dlqRoutingKey).build();
    }

    @Bean
    Queue notificationPushQueue(
            @Value("${rabbitmq.queue.notification-push}") String name,
            @Value("${rabbitmq.exchange.dead-letter}") String dlx,
            @Value("${rabbitmq.routing.dlq-push}") String dlqRoutingKey) {
        return QueueBuilder.durable(name)
                .deadLetterExchange(dlx)
                .deadLetterRoutingKey(dlqRoutingKey)
                .build();
    }

    @Bean
    Queue kitchenTicketQueue(
            @Value("${rabbitmq.queue.kitchen-ticket}") String name,
            @Value("${rabbitmq.exchange.dead-letter}") String dlx,
            @Value("${rabbitmq.routing.dlq-ticket}") String dlqRoutingKey) {
        return QueueBuilder.durable(name)
                .deadLetterExchange(dlx)
                .deadLetterRoutingKey(dlqRoutingKey)
                .build();
    }

    @Bean
    Queue notificationEmailDlq(
            @Value("${rabbitmq.queue.notification-email-dlq}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Queue notificationPushDlq(
            @Value("${rabbitmq.queue.notification-push-dlq}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Queue kitchenTicketDlq(
            @Value("${rabbitmq.queue.kitchen-ticket-dlq}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Binding emailBinding(
            @Qualifier("notificationEmailQueue") Queue queue,
            @Qualifier("commandsDirectExchange") DirectExchange exchange,
            @Value("${rabbitmq.routing.email}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Binding pushBinding(
            @Qualifier("notificationPushQueue") Queue queue,
            @Qualifier("commandsTopicExchange") TopicExchange exchange,
            @Value("${rabbitmq.routing.push-binding}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Binding kitchenTicketBinding(
            @Qualifier("kitchenTicketQueue") Queue queue,
            @Qualifier("commandsDirectExchange") DirectExchange exchange,
            @Value("${rabbitmq.routing.ticket}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Binding emailDlqBinding(
            @Qualifier("notificationEmailDlq") Queue queue,
            @Qualifier("deadLetterExchange") DirectExchange exchange,
            @Value("${rabbitmq.routing.dlq-email}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Binding pushDlqBinding(
            @Qualifier("notificationPushDlq") Queue queue,
            @Qualifier("deadLetterExchange") DirectExchange exchange,
            @Value("${rabbitmq.routing.dlq-push}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    Binding ticketDlqBinding(
            @Qualifier("kitchenTicketDlq") Queue queue,
            @Qualifier("deadLetterExchange") DirectExchange exchange,
            @Value("${rabbitmq.routing.dlq-ticket}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}

