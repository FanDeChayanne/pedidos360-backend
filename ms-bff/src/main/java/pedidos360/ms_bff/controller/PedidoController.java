package pedidos360.ms_bff.controller;

import org.springframework.web.bind.annotation.*;

import pedidos360.ms_bff.model.Pedido;
import pedidos360.ms_bff.service.PedidoService;

import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(
            PedidoService pedidoService) {

        this.pedidoService = pedidoService;
    }


    // ==========================
    // LISTAR PEDIDOS
    // ==========================

    @GetMapping("/api/pedidos")
    public List<Pedido> listarPedidos() {

        return pedidoService.listarPedidos();
    }


    // ==========================
    // CREAR PEDIDO
    // ==========================

    @PostMapping("/api/pedidos")
    public Pedido crearPedido(
            @RequestBody Pedido pedido) {

        return pedidoService.crearPedido(pedido);
    }


    // ==========================
    // CAMBIAR ESTADO
    // ==========================

    @PutMapping("/api/pedidos/{id}/estado")
    public Pedido cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {

        return pedidoService.cambiarEstado(
                id,
                estado
        );
    }

}