package pe.universidadperuanaunion.reserva.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import pe.universidadperuanaunion.reserva.dto.ReservaRequestDto;
import pe.universidadperuanaunion.reserva.dto.ReservaResponseDto;
import pe.universidadperuanaunion.reserva.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ReservaResponseDto>> getAll() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservaResponseDto> create(@RequestBody ReservaRequestDto requestDto) {
        ReservaResponseDto saved = reservaService.save(requestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{nroReser}")
                .buildAndExpand(saved.getNroReser())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{nroReser}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String nroReser) {
        reservaService.deleteByNroReser(nroReser);
        return ResponseEntity.noContent().build();
    }
}
