package jairo.com.practica5.servicio

import jairo.com.practica5.adaptadores.entrada.CrearPedidoRequest
import jairo.com.practica5.adaptadores.entrada.PedidoCreadoResponse
import jairo.com.practica5.adaptadores.entrada.PedidoResponse
import jairo.com.practica5.modelo.EstadoPedido
import jairo.com.practica5.modelo.Pedido
import jairo.com.practica5.puertos.PedidoRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PedidoService(
    private val pedidoRepository: PedidoRepositoryPort
) {
    // Stock simulado en memoria (Cuaderno: 10, Lapiz: 5 según la Parte A)
    private val stockProductos = mutableMapOf("Cuaderno" to 10, "Lapiz" to 5)

    fun crear(request: CrearPedidoRequest): PedidoCreadoResponse {
        // Regla R1: Un cliente no puede hacer un pedido nuevo si ya tiene otro PENDIENTE
        if (pedidoRepository.existsByClienteAndEstado(request.cliente, EstadoPedido.PENDIENTE)) {
            throw RuntimeException("El cliente ya tiene un pedido pendiente (409 Conflict)")
        }

        val codigoPedido = "PED-" + UUID.randomUUID().toString().take(8).uppercase()
        val codigoVerificacion = (100000..999999).random().toString() // Regla R7

        request.items.forEach { item ->
            val stockActual = stockProductos[item.producto] ?: throw RuntimeException("El producto no existe (404 Not Found)")

            // Regla R2: No se puede pedir más cantidad de la que hay en stock
            if (stockActual < item.cantidad) {
                throw RuntimeException("Stock insuficiente para ${item.producto} (409 Conflict)")
            }

            // Regla R6: Al crear el pedido, el stock baja
            stockProductos[item.producto] = stockActual - item.cantidad

            pedidoRepository.save(
                Pedido(
                    codigoPedido = codigoPedido,
                    cliente = request.cliente,
                    producto = item.producto,
                    cantidad = item.cantidad,
                    estado = EstadoPedido.PENDIENTE,
                    codigoVerificacion = codigoVerificacion
                )
            )
        }

        return PedidoCreadoResponse(codigoPedido, codigoVerificacion, "Pedido creado con éxito")
    }

    fun enviar(codigoPedido: String): PedidoResponse {
        val filas = pedidoRepository.buscar(codigoPedido)
        if (filas.isEmpty()) throw RuntimeException("Pedido no encontrado")

        // Regla R4: El pedido solo avanza de PENDIENTE a ENVIADO
        if (filas.first().estado != EstadoPedido.PENDIENTE) {
            throw RuntimeException("Solo un pedido PENDIENTE se puede enviar (409 Conflict)")
        }
        filas.forEach { it.estado = EstadoPedido.ENVIADO }
        return PedidoResponse(codigoPedido, "ENVIADO", filas)
    }

    fun recibir(codigoPedido: String, codigoVerificacion: String): PedidoResponse {
        val filas = pedidoRepository.buscar(codigoPedido)
        if (filas.isEmpty()) throw RuntimeException("Pedido no encontrado")

        // Regla R4: El pedido avanza de ENVIADO a RECIBIDO
        if (filas.first().estado != EstadoPedido.ENVIADO) {
            throw RuntimeException("Solo un pedido ENVIADO se puede recibir (409 Conflict)")
        }
        // Regla R5: Se necesita el código de verificación correcto
        if (filas.first().codigoVerificacion != codigoVerificacion) {
            throw RuntimeException("Código de verificación incorrecto (400 Bad Request)")
        }
        filas.forEach { it.estado = EstadoPedido.RECIBIDO }
        return PedidoResponse(codigoPedido, "RECIBIDO", filas)
    }

    fun buscar(codigoPedido: String): List<Pedido> {
        return pedidoRepository.buscar(codigoPedido)
    }
}