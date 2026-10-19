package ms_pedidos360notificaciones.messaging.admin;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RabbitAdminService {
    public enum ExchangeType {
        DIRECT,
        TOPIC
    }

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public void crearCola(String nombre) {
        validarNombre(nombre, "cola");
        amqpAdmin.declareQueue(QueueBuilder.durable(nombre).build());
    }

    public void eliminarCola(String nombre) {
        validarNombre(nombre, "cola");
        amqpAdmin.deleteQueue(nombre);
    }

    public void crearExchange(String nombre, ExchangeType tipo) {
        validarNombre(nombre, "exchange");

        if (tipo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El tipo de exchange es obligatorio");
        }

        switch (tipo) {
            case DIRECT -> amqpAdmin.declareExchange(
                    ExchangeBuilder.directExchange(nombre).durable(true).build());
            case TOPIC -> amqpAdmin.declareExchange(
                    ExchangeBuilder.topicExchange(nombre).durable(true).build());
        }
    }

    public void eliminarExchange(String nombre) {
        validarNombre(nombre, "exchange");
        amqpAdmin.deleteExchange(nombre);
    }

    public void crearBinding(String cola, String exchange, String routingKey) {
        validarNombre(cola, "cola");
        validarNombre(exchange, "exchange");

        if (routingKey == null || routingKey.isBlank()
                || routingKey.length() > 255
                || routingKey.chars().anyMatch(Character::isWhitespace)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La routing key está vacía o no es válida");
        }

        Binding binding = new Binding(
                cola,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                null
        );

        amqpAdmin.declareBinding(binding);
    }

    public void eliminarBinding(String cola, String exchange, String routingKey) {
        validarNombre(cola, "cola");
        validarNombre(exchange, "exchange");

        if (routingKey == null || routingKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La routing key es obligatoria");
        }

        Binding binding = new Binding(
                cola,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                null
        );

        amqpAdmin.removeBinding(binding);
    }

    private void validarNombre(String nombre, String tipo) {
        if (nombre == null || !nombre.matches("[A-Za-z0-9._-]{1,255}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre de " + tipo + " está vacío o no es válido"
            );
        }
    }
}
