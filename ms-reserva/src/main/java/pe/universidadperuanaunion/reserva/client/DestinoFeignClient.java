package pe.universidadperuanaunion.reserva.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-destino")
public interface DestinoFeignClient {

    @GetMapping("/api/destinos/{codDest}")
    Object getDestino(@PathVariable("codDest") String codDest);
}
