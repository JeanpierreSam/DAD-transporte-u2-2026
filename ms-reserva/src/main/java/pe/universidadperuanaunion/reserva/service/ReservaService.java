package pe.universidadperuanaunion.reserva.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import feign.FeignException;
import pe.universidadperuanaunion.reserva.client.AsientoFeignClient;
import pe.universidadperuanaunion.reserva.client.BusFeignClient;
import pe.universidadperuanaunion.reserva.client.ClienteFeignClient;
import pe.universidadperuanaunion.reserva.client.DestinoFeignClient;
import pe.universidadperuanaunion.reserva.client.ProgramacionFeignClient;
import pe.universidadperuanaunion.reserva.dto.ReservaRequestDto;
import pe.universidadperuanaunion.reserva.dto.ReservaResponseDto;
import pe.universidadperuanaunion.reserva.model.Reserva;
import pe.universidadperuanaunion.reserva.repository.ReservaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteFeignClient clienteFeignClient;
    private final ProgramacionFeignClient programacionFeignClient;
    private final BusFeignClient busFeignClient;
    private final AsientoFeignClient asientoFeignClient;
    private final DestinoFeignClient destinoFeignClient;

    public ReservaService(ReservaRepository reservaRepository,
                          ClienteFeignClient clienteFeignClient,
                          ProgramacionFeignClient programacionFeignClient,
                          BusFeignClient busFeignClient,
                          AsientoFeignClient asientoFeignClient,
                          DestinoFeignClient destinoFeignClient) {
        this.reservaRepository = reservaRepository;
        this.clienteFeignClient = clienteFeignClient;
        this.programacionFeignClient = programacionFeignClient;
        this.busFeignClient = busFeignClient;
        this.asientoFeignClient = asientoFeignClient;
        this.destinoFeignClient = destinoFeignClient;
    }

    public List<ReservaResponseDto> findAll() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservas.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ReservaResponseDto findByNroReser(String nroReser) {
        Reserva reserva = reservaRepository.findById(nroReser).orElse(null);
        return reserva != null ? toResponseDto(reserva) : null;
    }

    public ReservaResponseDto save(ReservaRequestDto requestDto) {
        Reserva reserva = toEntity(requestDto);
        validateReferences(reserva);
        Reserva saved = reservaRepository.save(reserva);
        return toResponseDto(saved);
    }

    public void deleteByNroReser(String nroReser) {
        reservaRepository.deleteById(nroReser);
    }

    private Reserva toEntity(ReservaRequestDto dto) {
        Reserva reserva = new Reserva();
        reserva.setNroReser(dto.getNroReser());
        reserva.setFechaReser(dto.getFechaReser());
        reserva.setHoraReser(dto.getHoraReser());
        reserva.setCodCli(dto.getCodCli());
        reserva.setIdProg(dto.getIdProg());
        reserva.setCodDest(dto.getCodDest());
        return reserva;
    }

    private ReservaResponseDto toResponseDto(Reserva reserva) {
        ReservaResponseDto dto = new ReservaResponseDto();
        dto.setNroReser(reserva.getNroReser());
        dto.setFechaReser(reserva.getFechaReser());
        dto.setHoraReser(reserva.getHoraReser());
        dto.setCodCli(reserva.getCodCli());
        dto.setIdProg(reserva.getIdProg());
        dto.setCodDest(reserva.getCodDest());

        try {
            Object cliente = clienteFeignClient.getCliente(reserva.getCodCli());
            if (cliente instanceof Map) {
                dto.setNombreCliente((String) ((Map<?, ?>) cliente).get("nombre"));
            }
        } catch (FeignException e) {
            dto.setNombreCliente("Cliente no disponible");
        }

        try {
            Object programacion = programacionFeignClient.getProgramacion(reserva.getIdProg());
            if (programacion instanceof Map) {
                dto.setDescripcionProgramacion((String) ((Map<?, ?>) programacion).get("descripcion"));
            }
        } catch (FeignException e) {
            dto.setDescripcionProgramacion("Programación no disponible");
        }

        try {
            Object destino = destinoFeignClient.getDestino(reserva.getCodDest());
            if (destino instanceof Map) {
                dto.setNombreDestino((String) ((Map<?, ?>) destino).get("nombre"));
            }
        } catch (FeignException e) {
            dto.setNombreDestino("Destino no disponible");
        }

        return dto;
    }

    private void validateReferences(Reserva reserva) {
        try {
            clienteFeignClient.getCliente(reserva.getCodCli());
        } catch (FeignException e) {
            throw new IllegalArgumentException("Cliente no encontrado: " + reserva.getCodCli());
        }

        try {
            programacionFeignClient.getProgramacion(reserva.getIdProg());
        } catch (FeignException e) {
            throw new IllegalArgumentException("Programación no encontrada: " + reserva.getIdProg());
        }

        try {
            destinoFeignClient.getDestino(reserva.getCodDest());
        } catch (FeignException e) {
            throw new IllegalArgumentException("Destino no encontrado: " + reserva.getCodDest());
        }
    }
}
