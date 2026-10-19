package ms_pedidos360notificaciones.messaging.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ms_pedidos360notificaciones.messaging.admin.RabbitAdminService.ExchangeType;

@RestController
@RequestMapping("/api/admin/rabbitmq")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping("/queues")
    public ResponseEntity<Void> crearCola(@RequestBody QueueRequest request) {
        rabbitAdminService.crearCola(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Void> eliminarCola(@PathVariable String name) {
        rabbitAdminService.eliminarCola(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Void> crearExchange(@RequestBody ExchangeRequest request) {
        rabbitAdminService.crearExchange(request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Void> eliminarExchange(@PathVariable String name) {
        rabbitAdminService.eliminarExchange(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bindings")
    public ResponseEntity<Void> crearBinding(@RequestBody BindingRequest request) {
        rabbitAdminService.crearBinding(
                request.queue(),
                request.exchange(),
                request.routingKey()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<Void> eliminarBinding(
            @RequestParam String queue,
            @RequestParam String exchange,
            @RequestParam String routingKey) {
        rabbitAdminService.eliminarBinding(queue, exchange, routingKey);
        return ResponseEntity.noContent().build();
    }

    public record QueueRequest(String name) {
    }

    public record ExchangeRequest(String name, ExchangeType type) {
    }

    public record BindingRequest(String queue, String exchange, String routingKey) {
    }
}
