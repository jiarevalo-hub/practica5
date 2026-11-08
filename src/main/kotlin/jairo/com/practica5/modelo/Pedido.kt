package jairo.com.practica5.modelo

data class Pedido(
    val codigoPedido: String,
    val cliente: String,
    val producto: String,
    val cantidad: Int,
    var estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val codigoVerificacion: String
)