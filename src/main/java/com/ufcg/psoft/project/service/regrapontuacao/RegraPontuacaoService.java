package com.ufcg.psoft.project.service.regrapontuacao;

import com.ufcg.psoft.project.dto.RegraPontuacaoRequestDTO;
import com.ufcg.psoft.project.model.RegraPontuacao;

public interface RegraPontuacaoService {
    RegraPontuacao configurarRegraPontuacao(Long grupoId, Long usuarioId, RegraPontuacaoRequestDTO dto);
}
