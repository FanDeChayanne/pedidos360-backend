package ms_pedidos360catalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ms_pedidos360catalog.model.Producto;
import ms_pedidos360catalog.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @GetMapping("/{id}")
    public Producto obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerProducto(id);
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.crearProducto(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        return productoService.actualizarProducto(id, producto);
    }

    @PutMapping("/{id}/stock/aumentar")
    public Producto aumentarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {

        return productoService.aumentarStock(id, cantidad);
    }

    @PutMapping("/{id}/stock/disminuir")
    public ResponseEntity<?> disminuirStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {

        try {
            return ResponseEntity.ok(
                    productoService.disminuirStock(id, cantidad));

        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }
}
