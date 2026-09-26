package pedidos360.ms_orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedidos360.ms_orders.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {
}
