package pedidos360.ms_orders.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteId;

    private Long productoId;

    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;
}