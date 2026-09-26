package pedidos360.ms_bff.model;

public class Pedido {

    private Long id;
    private String clienteId;
    private Long productoId;
    private Integer cantidad;
    private String estado;

    public Pedido() {
    }

    public Pedido(
            Long id,
            String clienteId,
            Long productoId,
            Integer cantidad,
            String estado) {

        this.id = id;
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}