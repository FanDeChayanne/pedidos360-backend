package pedidos360.ms_bff.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import pedidos360.ms_bff.model.Pedido;

import java.util.List;

@Service
public class PedidoService {

    private final RestClient restClient;

    public PedidoService(
            RestClient.Builder restClientBuilder,
            @Value("${orders.service.url}") String ordersUrl) {

        this.restClient = restClientBuilder
                .baseUrl(ordersUrl)
                .build();
    }


    // ==========================
    // LISTAR PEDIDOS
    // ==========================

    public List<Pedido> listarPedidos() {

        return restClient
                .get()
                .uri("/api/pedidos")
                .retrieve()
                .body(List.class);
    }


    // ==========================
    // CREAR PEDIDO
    // ==========================

    public Pedido crearPedido(Pedido pedido) {

        return restClient
                .post()
                .uri("/api/pedidos")
                .body(pedido)
                .retrieve()
                .body(Pedido.class);
    }


    // ==========================
    // CAMBIAR ESTADO
    // ==========================

    public Pedido cambiarEstado(
            Long id,
            String estado) {

        return restClient
                .put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/api/pedidos/{id}/estado")
                                .queryParam("estado", estado)
                                .build(id)
                )
                .retrieve()
                .body(Pedido.class);
    }

}