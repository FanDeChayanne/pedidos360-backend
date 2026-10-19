package pedidos360.ms_orders.service;

import pedidos360.ms_orders.model.Pedido;
import pedidos360.ms_orders.model.EstadoPedido;
import pedidos360.ms_orders.model.PedidoItem;
import pedidos360.ms_orders.messaging.NotificationPublisher;
import pedidos360.ms_orders.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import pedidos360.ms_orders.messaging.dto.NotificationCommand;
import java.time.Instant;
import java.util.UUID;

import java.util.List;

@Service
public class PedidoService{

    private final PedidoRepository pedidoRepository;
    private final RestClient restClient;
    private final NotificationPublisher notificationPublisher;


    public PedidoService(
            PedidoRepository pedidoRepository,
            NotificationPublisher notificationPublisher,
            RestClient.Builder restClientBuilder,
            @Value("${catalog.service.url}") String catalogUrl) {

        this.pedidoRepository = pedidoRepository;
        this.notificationPublisher = notificationPublisher;

        this.restClient = restClientBuilder
                .baseUrl(catalogUrl)
                .build();
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido obtenerPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    public Pedido crearPedido(Pedido pedido) {
        pedido.setEstado(EstadoPedido.CREADO);
        pedido.getItems().forEach(item -> item.setPedido(pedido));
        Pedido guardado = pedidoRepository.save(pedido);

        notificationPublisher.publicarCorreo(
                crearComando("NOTIFICACION_EMAIL", guardado)
        );
        notificationPublisher.publicarPush(
                crearComando("NOTIFICACION_PUSH", guardado)
        );

        return guardado;
    }

    public Pedido actualizarPedido(Long id, Pedido datos) {
        Pedido pedido = obtenerPedido(id);

        pedido.setClienteId(datos.getClienteId());

        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        Pedido pedido = obtenerPedido(id);
        pedidoRepository.delete(pedido);
    }

    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedido(id);
        EstadoPedido estadoActual = pedido.getEstado();

        if (estadoActual == EstadoPedido.CREADO
                && nuevoEstado == EstadoPedido.ACEPTADO) {

            try {
                for (PedidoItem item : pedido.getItems()) {
                    restClient.put()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/catalog/productos/{id}/stock/disminuir")
                                    .queryParam("cantidad", item.getCantidad())
                                    .build(item.getProductId()))
                            .retrieve()
                            .toBodilessEntity();
                }
            } catch (HttpClientErrorException.Conflict e) {
                throw new IllegalStateException("Stock insuficiente");
            }

            pedido.setEstado(nuevoEstado);
        } else if (estadoActual == EstadoPedido.ACEPTADO
                && nuevoEstado == EstadoPedido.EN_PREPARACION) {

            pedido.setEstado(nuevoEstado);

        } else if (estadoActual == EstadoPedido.EN_PREPARACION
                && nuevoEstado == EstadoPedido.DESPACHADO) {

            pedido.setEstado(nuevoEstado);

        } else if (estadoActual == EstadoPedido.DESPACHADO
                && nuevoEstado == EstadoPedido.ENTREGADO) {

            pedido.setEstado(nuevoEstado);

        } else if (nuevoEstado == EstadoPedido.CANCELADO
                && (estadoActual == EstadoPedido.CREADO
                        || estadoActual == EstadoPedido.ACEPTADO
                        || estadoActual == EstadoPedido.EN_PREPARACION)) {

            pedido.setEstado(nuevoEstado);

        } else {
            throw new IllegalStateException(
                    "Transición de estado no permitida: "
                            + estadoActual + " -> " + nuevoEstado);
        }

        Pedido guardado = pedidoRepository.save(pedido);

        notificationPublisher.publicarCorreo(
                crearComando("NOTIFICACION_EMAIL", guardado)
        );
        notificationPublisher.publicarPush(
                crearComando("NOTIFICACION_PUSH", guardado)
        );

        if (nuevoEstado == EstadoPedido.ACEPTADO) {
            notificationPublisher.publicarTicket(
                    crearComando("TICKET_COCINA", guardado)
            );
        }

        return guardado;
    }

    private NotificationCommand crearComando(String tipo, Pedido pedido) {
        return new NotificationCommand(
                tipo,
                UUID.randomUUID().toString(),
                Instant.now(),
                UUID.randomUUID().toString(),
                "pedido-" + pedido.getId(),
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getEstado().name()
        );
    }
}
