package com.ufcg.psoft.project.service.aposta;

import com.ufcg.psoft.project.dto.aposta.ApostaRequestDTO;
import com.ufcg.psoft.project.dto.aposta.ApostaResponseDTO;
import com.ufcg.psoft.project.dto.aposta.ResultadoDTO;
import com.ufcg.psoft.project.dto.partida.PartidaResponseDTO;
import com.ufcg.psoft.project.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.project.exception.PermissaoNegadaException;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.exception.UsuarioNaoExisteException;
import com.ufcg.psoft.project.model.aposta.Aposta;
import com.ufcg.psoft.project.model.aposta.StatusAposta;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.partida.status.Finalizado;
import com.ufcg.psoft.project.model.partida.status.TipoStatus;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.ApostaRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApostaServiceImpl implements ApostaService {

    private final ApostaRepository apostaRepository;
    private final PartidaRepository partidaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<PartidaResponseDTO> listarPartidasAbertas() {
        return partidaRepository.findByDataHoraAfter(LocalDateTime.now()).stream()
                .filter(Partida::aceitaApostas)
                .sorted(Comparator.comparing(Partida::getDataHora))
                .map(PartidaResponseDTO::de)
                .toList();
    }

    @Override
    @Transactional
    public ApostaResponseDTO apostar(Long usuarioId, String codigoAcesso, ApostaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByIdForUpdate(usuarioId).orElseThrow(UsuarioNaoExisteException::new);
        validarCodigo(usuario, codigoAcesso);
        Partida partida = partidaRepository.findById(dto.getPartidaId())
                .orElseThrow(() -> new ProjectException("Partida nao encontrada."));
        if (!partida.aceitaApostas()) {
            throw new ProjectException("Mercado fechado para esta partida.");
        }
        if (usuario.getSaldo().compareTo(dto.getValor()) < 0) {
            throw new ProjectException("Saldo insuficiente.");
        }
        usuario.setSaldo(usuario.getSaldo().subtract(dto.getValor()));
        Aposta aposta = apostaRepository.save(Aposta.builder()
                .usuario(usuario)
                .partida(partida)
                .palpite(dto.getPalpite())
                .valor(dto.getValor())
                .odd(partida.odd(dto.getPalpite()))
                .build());
        return ApostaResponseDTO.de(aposta);
    }

    @Override
    public List<ApostaResponseDTO> listarApostas(Long usuarioId, String codigoAcesso) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(UsuarioNaoExisteException::new);
        validarCodigo(usuario, codigoAcesso);
        return apostaRepository.findByUsuarioIdOrderByCriadaEmDesc(usuarioId).stream()
                .map(ApostaResponseDTO::de).toList();
    }

    @Override
    @Transactional
    public PartidaResponseDTO registrarResultado(Long partidaId, Long adminId, String codigoAcesso, ResultadoDTO dto) {
        Usuario admin = usuarioRepository.findById(adminId).orElseThrow(UsuarioNaoExisteException::new);
        validarCodigo(admin, codigoAcesso);
        if (!admin.getPerfil().isAdmin()) {
            throw new PermissaoNegadaException();
        }
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new ProjectException("Partida nao encontrada."));
        partida.setGolsMandante(dto.golsMandante());
        partida.setGolsVisitante(dto.golsVisitante());
        partida.setStatus(new Finalizado());
        partidaRepository.save(partida);
        liquidarApostas();
        return PartidaResponseDTO.de(partida);
    }

    /** Paga apostas de partidas finalizadas e devolve as de partidas canceladas. Roda a cada minuto. */
    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public int liquidarApostas() {
        int liquidadas = 0;
        for (Aposta aposta : apostaRepository.findByStatus(StatusAposta.PENDENTE)) {
            Partida partida = aposta.getPartida();
            TipoStatus status = partida.getStatus().getTipoStatus();
            if (status == TipoStatus.CANCELADO) {
                aposta.setStatus(StatusAposta.ANULADA);
                creditar(aposta.getUsuario().getId(), aposta.getValor());
            } else if (status == TipoStatus.FINALIZADO && partida.getGolsMandante() != null && partida.getGolsVisitante() != null) {
                boolean ganhou = partida.resultado() == aposta.getPalpite();
                aposta.setStatus(ganhou ? StatusAposta.GANHA : StatusAposta.PERDIDA);
                if (ganhou) creditar(aposta.getUsuario().getId(), aposta.getRetornoPotencial());
            } else {
                continue;
            }
            apostaRepository.save(aposta);
            liquidadas++;
        }
        return liquidadas;
    }

    private void creditar(Long usuarioId, java.math.BigDecimal valor) {
        Usuario u = usuarioRepository.findByIdForUpdate(usuarioId).orElseThrow(UsuarioNaoExisteException::new);
        u.setSaldo(u.getSaldo().add(valor));
        usuarioRepository.save(u);
    }

    private void validarCodigo(Usuario usuario, String codigoAcesso) {
        if (!usuario.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }
}
