package ms_pedidos360notificaciones.messaging.dto;

import java.time.Instant;

public record NotificationCommand(
        String type,
        String eventId,
        Instant timestamp,
        String traceId,
        String correlationId,
        Long orderId,
        String clienteId,
        String estado
) {
}
