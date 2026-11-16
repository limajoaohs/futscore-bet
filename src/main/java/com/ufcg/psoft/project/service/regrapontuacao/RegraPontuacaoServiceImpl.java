package com.ufcg.psoft.project.service.regrapontuacao;

import com.ufcg.psoft.project.dto.RegraPontuacaoRequestDTO;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.RegraPontuacao;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.project.exception.AcessoNegadoException;

@Service
public class RegraPontuacaoServiceImpl implements RegraPontuacaoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public RegraPontuacao configurarRegraPontuacao(Long grupoId, Long usuarioId, RegraPontuacaoRequestDTO dto) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado!"));

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));

        if (!grupo.getOrganizador().getId().equals(usuarioId)) {
            throw new AcessoNegadoException("Apenas o organizador do grupo pode alterar as regras de pontuação!");
        }

        RegraPontuacao regra = grupo.getRegraPontuacao();
        if (regra == null) {
            regra = new RegraPontuacao();
        }

        regra.setPontosAcertoVencedor(dto.getPontosAcertoVencedor());
        regra.setPontosAcertoEmpate(dto.getPontosAcertoEmpate());
        regra.setPontosPlacarExato(dto.getPontosPlacarExato());
        regra.setPontosBonusRodada(dto.getPontosBonusRodada());
        regra.setPontosBonusMataMata(dto.getPontosBonusMataMata());

        grupo.setRegraPontuacao(regra);
        grupoRepository.save(grupo);

        return regra;
    }
}
