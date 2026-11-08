package jairo.com.practica5.adaptadores.salida

import jairo.com.practica5.modelo.EstadoPedido
import jairo.com.practica5.modelo.Pedido
import jairo.com.practica5.puertos.PedidoRepositoryPort
import org.springframework.stereotype.Repository

@Repository
class PedidosEnMemoria : PedidoRepositoryPort {
    private val filas = mutableListOf<Pedido>()

    override fun existsByClienteAndEstado(cliente: String, estado: EstadoPedido): Boolean {
        return filas.any { it.cliente == cliente && it.estado == estado }
    }

    override fun save(pedido: Pedido): Pedido {
        filas.add(pedido)
        return pedido
    }

    override fun buscar(codigoPedido: String): List<Pedido> {
        return filas.filter { it.codigoPedido == codigoPedido }
    }
}