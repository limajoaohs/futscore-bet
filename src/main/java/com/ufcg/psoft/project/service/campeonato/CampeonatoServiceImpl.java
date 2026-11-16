package com.ufcg.psoft.project.service.campeonato;

import com.ufcg.psoft.project.dto.campeonato.CampeonatoRequestDTO;
import com.ufcg.psoft.project.dto.campeonato.CampeonatoResponseDTO;
import com.ufcg.psoft.project.dto.integration.FootballDataResponseDTO;
import com.ufcg.psoft.project.exception.*;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import com.ufcg.psoft.project.client.ClienteFootballData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class CampeonatoServiceImpl implements CampeonatoService {

    @Autowired
    private ClienteFootballData clienteFootballData;

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void sincronizarCampeonatos(Long usuarioId, String codigoAcesso) {
        verificarAdmin(usuarioId, codigoAcesso);
        try {
            FootballDataResponseDTO body = clienteFootballData.buscarCampeonatos();
            if (body != null && body.getCompetitions() != null) {
                body.getCompetitions().forEach(compDto -> {
                    Campeonato campeonato = campeonatoRepository.findByApiId(compDto.getId())
                            .orElseGet(() -> Campeonato.builder()
                                    .ano(String.valueOf(LocalDate.now().getYear()))
                                    .build());
                    campeonato.setApiId(compDto.getId());
                    campeonato.setNome(compDto.getName());
                    campeonato.setUrlApi("https://api.football-data.org/v4/competitions/" + compDto.getCode());
                    campeonatoRepository.save(campeonato);
                });
            }
        } catch (Exception e) {
            throw new ProjectException("Falha ao integrar com a API externa: " + e.getMessage());
        }
    }

    @Override
    public CampeonatoResponseDTO cadastrarCampeonato(CampeonatoRequestDTO campeonatoRequestDTO, Long usuarioId, String codigoAcesso) {
        verificarAdmin(usuarioId, codigoAcesso);
        Campeonato campeonato = modelMapper.map(campeonatoRequestDTO, Campeonato.class);
        Campeonato salvo = campeonatoRepository.save(campeonato);
        return modelMapper.map(salvo, CampeonatoResponseDTO.class);
    }

    @Override
    public void removerCampeonato(Long id, Long usuarioId, String codigoAcesso) {
        verificarAdmin(usuarioId, codigoAcesso);
        Campeonato campeonato = campeonatoRepository.findById(id)
                .orElseThrow(() -> new CampeonatoNaoExisteException(id));
        campeonatoRepository.delete(campeonato);
    }

    @Override
    public CampeonatoResponseDTO alterarStatus(Long id, boolean ativo, Long usuarioId, String codigoAcesso) {
        verificarAdmin(usuarioId, codigoAcesso);
        Campeonato campeonato = campeonatoRepository.findById(id)
                .orElseThrow(() -> new CampeonatoNaoExisteException(id));
        campeonato.setAtivo(ativo);
        Campeonato salvo = campeonatoRepository.save(campeonato);
        return modelMapper.map(salvo, CampeonatoResponseDTO.class);
    }

    private void verificarAdmin(Long usuarioId, String codigoAcesso) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(UsuarioNaoExisteException::new);
        if (!usuario.getPerfil().isAdmin()) {
            throw new PermissaoNegadaException();
        } else if (!usuario.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }
}
