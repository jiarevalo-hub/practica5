package jairo.com.practica5.puertos

import jairo.com.practica5.modelo.EstadoPedido
import jairo.com.practica5.modelo.Pedido

interface PedidoRepositoryPort {
    fun existsByClienteAndEstado(cliente: String, estado: EstadoPedido): Boolean
    fun save(pedido: Pedido): Pedido
    fun buscar(codigoPedido: String): List<Pedido>
}
