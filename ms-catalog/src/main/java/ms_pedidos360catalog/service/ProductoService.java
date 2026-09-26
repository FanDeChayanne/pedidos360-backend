package ms_pedidos360catalog.service;

import ms_pedidos360catalog.model.Producto;
import ms_pedidos360catalog.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto datos) {

        Producto producto = obtenerProducto(id);

        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());

        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        Producto producto = obtenerProducto(id);
        productoRepository.delete(producto);
    }

    public Producto aumentarStock(Long id, Integer cantidad) {

        Producto producto = obtenerProducto(id);

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        producto.setStock(producto.getStock() + cantidad);

        return productoRepository.save(producto);
    }

    public Producto disminuirStock(Long id, Integer cantidad) {

        Producto producto = obtenerProducto(id);

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (producto.getStock() < cantidad) {
            throw new IllegalStateException(
                    "Stock insuficiente"
            );
        }

        producto.setStock(producto.getStock() - cantidad);

        return productoRepository.save(producto);
    }
}
