package jairo.com.practica5.adaptadores.entrada

import jairo.com.practica5.modelo.Pedido

data class ItemRequest(
    val producto: String,
    val cantidad: Int
)

data class CrearPedidoRequest(
    val cliente: String,
    val items: List<ItemRequest>
)

data class PedidoCreadoResponse(
    val codigoPedido: String,
    val codigoVerificacion: String,
    val mensaje: String
)

data class PedidoResponse(
    val codigoPedido: String,
    val estado: String,
    val filas: List<Pedido>
)