package com.ufcg.psoft.project.service.palpite;

import com.ufcg.psoft.project.dto.palpite.PalpiteRequestDTO;
import com.ufcg.psoft.project.dto.palpite.PalpiteResponseDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.palpite.Palpite;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.PalpiteRepository;
import com.ufcg.psoft.project.repository.PartidaRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PalpiteServiceImpl implements PalpiteService {

    @Autowired
    private PalpiteRepository palpiteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PalpiteResponseDTO registrarPalpite(PalpiteRequestDTO palpiteRequestDTO) {
        Usuario usuario = usuarioRepository.findById(palpiteRequestDTO.getUsuarioId())
                .orElseThrow(() -> new ProjectException("Usuário não encontrado"));
        Partida partida = partidaRepository.findById(palpiteRequestDTO.getPartidaId())
                .orElseThrow(() -> new ProjectException("Partida não encontrada"));
        Grupo grupo = grupoRepository.findById(palpiteRequestDTO.getGrupoId())
                .orElseThrow(() -> new ProjectException("Grupo não encontrado"));
        if (!partida.getCampeonato().getId().equals(grupo.getCampeonato().getId())) {
            throw new ProjectException("Esta partida não pertence ao campeonato deste grupo.");
        } else if (!grupo.getParticipantes().contains(usuario)) {
            throw new ProjectException("O usuário não é participante deste grupo.");
        } else if (palpiteRepository.existsByUsuarioIdAndPartidaIdAndGrupoId(usuario.getId(), partida.getId(), grupo.getId())) {
            throw new ProjectException("Você já registrou um palpite para esta partida neste grupo.");
        } else if (!partida.periodoDePalpiteValido(grupo.getMinutosParaAbertura(), grupo.getMinutosParaFechamento())) {
            throw new ProjectException("Esta partida não está aberta para palpites.");
        }
        Palpite palpite = Palpite.builder()
                .usuario(usuario)
                .partida(partida)
                .grupo(grupo)
                .golsMandante(palpiteRequestDTO.getGolsMandante())
                .golsVisitante(palpiteRequestDTO.getGolsVisitante())
                .build();
        Palpite salvo = palpiteRepository.save(palpite);
        return modelMapper.map(salvo, PalpiteResponseDTO.class);
    }

    @Override
    @Transactional
    public PalpiteResponseDTO editarPalpite(Long id, PalpiteRequestDTO palpiteRequestDTO) {
        Palpite palpite = palpiteRepository.findById(id)
                .orElseThrow(() -> new ProjectException("Palpite não encontrado"));
        if (!palpite.getUsuario().getId().equals(palpiteRequestDTO.getUsuarioId())) {
            throw new ProjectException("Você não tem permissão para editar este palpite");
        }
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime horarioPartida = palpite.getPartida().getDataHora();
        LocalDateTime limiteFechamento = horarioPartida.minusMinutes(palpite.getGrupo().getMinutosParaFechamento());
        if (agora.isAfter(limiteFechamento)) {
            throw new ProjectException("O período de edição de palpites para esta partida já acabou.");
        }
        palpite.setGolsMandante(palpiteRequestDTO.getGolsMandante());
        palpite.setGolsVisitante(palpiteRequestDTO.getGolsVisitante());
        Palpite salvo = palpiteRepository.save(palpite);
        return modelMapper.map(salvo, PalpiteResponseDTO.class);
    }
}