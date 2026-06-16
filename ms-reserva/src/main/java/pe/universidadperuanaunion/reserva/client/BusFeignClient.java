package pe.universidadperuanaunion.reserva.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-bus")
public interface BusFeignClient {

    @GetMapping("/api/buses/{idBus}")
    Object getBus(@PathVariable("idBus") Integer idBus);
}
