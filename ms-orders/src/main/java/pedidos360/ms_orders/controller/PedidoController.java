package pedidos360.ms_orders.controller;

import org.springframework.web.bind.annotation.*;
import pedidos360.ms_orders.model.EstadoPedido;
import pedidos360.ms_orders.model.Pedido;
import pedidos360.ms_orders.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @GetMapping("/{id}")
    public Pedido obtenerPedido(@PathVariable Long id) {
        return pedidoService.obtenerPedido(id);
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        return pedidoService.crearPedido(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizarPedido(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return pedidoService.actualizarPedido(id, pedido);
    }

    @PutMapping("/{id}/estado")
    public Pedido cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {

        return pedidoService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public void eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
    }
}
