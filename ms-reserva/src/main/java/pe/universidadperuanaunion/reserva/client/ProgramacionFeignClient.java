package pe.universidadperuanaunion.reserva.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-programacion")
public interface ProgramacionFeignClient {

    @GetMapping("/api/programaciones/{idProg}")
    Object getProgramacion(@PathVariable("idProg") Integer idProg);
}
