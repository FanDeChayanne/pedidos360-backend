package pedidos360.ms_bff.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
public class CatalogController {

    private final RestClient restClient;

    public CatalogController(
            RestClient.Builder restClientBuilder,
            @Value("${catalog.service.url}") String catalogUrl) {

        this.restClient = restClientBuilder
                .baseUrl(catalogUrl)
                .build();
    }

    @GetMapping("/api/catalog/productos")
    public List<Object> listarProductos() {
        return restClient.get()
                .uri("/api/catalog/productos")
                .retrieve()
                .body(List.class);
    }

    @PostMapping("/api/catalog/productos")
    public Object crearProducto(@RequestBody Object producto) {

        return restClient
                .post()
                .uri("/api/catalog/productos")
                .body(producto)
                .retrieve()
                .body(Object.class);
    }

    @PutMapping("/api/catalog/productos/{id}/stock/aumentar")
    public Object aumentarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {

        return restClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/catalog/productos/{id}/stock/aumentar")
                        .queryParam("cantidad", cantidad)
                        .build(id))
                .retrieve()
                .body(Object.class);
    }
}