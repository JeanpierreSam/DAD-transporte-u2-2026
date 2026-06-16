package pe.universidadperuanaunion.reserva.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cliente")
public interface ClienteFeignClient {

    @GetMapping("/api/clientes/{codCli}")
    Object getCliente(@PathVariable("codCli") String codCli);
}
