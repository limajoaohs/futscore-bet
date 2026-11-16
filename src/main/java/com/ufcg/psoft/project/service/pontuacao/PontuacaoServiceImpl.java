package com.ufcg.psoft.project.service.pontuacao;

import com.ufcg.psoft.project.model.RegraPontuacao;
import com.ufcg.psoft.project.model.palpite.Palpite;
import com.ufcg.psoft.project.model.partida.Partida;
import com.ufcg.psoft.project.repository.PalpiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PontuacaoServiceImpl implements PontuacaoService {

    @Autowired
    private PalpiteRepository palpiteRepository;

    @Override
    @Transactional
    public void calcularPontuacoesDaPartida(Partida partida) {
        if (partida.getGolsMandante() == null || partida.getGolsVisitante() == null) {
            return;
        }

        List<Palpite> palpites = palpiteRepository.findByPartidaId(partida.getId());

        int golsMandanteReal = partida.getGolsMandante();
        int golsVisitanteReal = partida.getGolsVisitante();
        boolean isEmpateReal = golsMandanteReal == golsVisitanteReal;
        boolean isVitoriaMandanteReal = golsMandanteReal > golsVisitanteReal;

        for (Palpite palpite : palpites) {
            RegraPontuacao regra = palpite.getGrupo().getRegraPontuacao();

            if (regra == null) continue;

            int pontos = 0;
            boolean acertouPlacarExato = false;
            boolean acertouVencedor = false;
            boolean acertouEmpate = false;

            int golsMandantePalpite = palpite.getGolsMandante();
            int golsVisitantePalpite = palpite.getGolsVisitante();
            boolean isEmpatePalpite = golsMandantePalpite == golsVisitantePalpite;
            boolean isVitoriaMandantePalpite = golsMandantePalpite > golsVisitantePalpite;


            if (golsMandantePalpite == golsMandanteReal && golsVisitantePalpite == golsVisitanteReal) {
                acertouPlacarExato = true;
                pontos += regra.getPontosPlacarExato();
            }

            else if (isEmpateReal && isEmpatePalpite) {
                acertouEmpate = true;
                pontos += regra.getPontosAcertoEmpate();
            }

            else if (!isEmpateReal && !isEmpatePalpite && (isVitoriaMandanteReal == isVitoriaMandantePalpite)) {
                acertouVencedor = true;
                pontos += regra.getPontosAcertoVencedor();
            }

            palpite.setAcertouPlacarExato(acertouPlacarExato);
            palpite.setAcertouEmpate(acertouEmpate);
            palpite.setAcertouVencedor(acertouVencedor);
            palpite.setPontosObtidos(pontos);
        }

        palpiteRepository.saveAll(palpites);
    }
}