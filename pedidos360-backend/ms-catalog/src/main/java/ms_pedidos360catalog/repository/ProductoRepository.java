package ms_pedidos360catalog.repository;

import ms_pedidos360catalog.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
