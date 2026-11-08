package jairo.com.practica5.adaptadores.entrada

import jairo.com.practica5.servicio.PedidoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pedidos")
class PedidoController(
    private val servicio: PedidoService
) {

    @PostMapping
    fun crear(@RequestBody request: CrearPedidoRequest): ResponseEntity<Any> {
        return try {
            val response = servicio.crear(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            val status = when {
                e.message!!.contains("404") -> HttpStatus.NOT_FOUND
                e.message!!.contains("409") -> HttpStatus.CONFLICT
                else -> HttpStatus.BAD_REQUEST
            }
            ResponseEntity.status(status).body(mapOf("error" to e.message))
        }
    }

    @PatchMapping("/{codigo}/enviar")
    fun enviar(@PathVariable codigo: String): ResponseEntity<Any> {
        return try {
            val response = servicio.enviar(codigo)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        }
    }

    @PatchMapping("/{codigo}/recibir")
    fun recibir(@PathVariable codigo: String, @RequestParam codigoVerificacion: String): ResponseEntity<Any> {
        return try {
            val response = servicio.recibir(codigo, codigoVerificacion)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            val status = if (e.message!!.contains("400")) HttpStatus.BAD_REQUEST else HttpStatus.CONFLICT
            ResponseEntity.status(status).body(mapOf("error" to e.message))
        }
    }
}