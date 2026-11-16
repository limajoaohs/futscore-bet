package com.ufcg.psoft.project.service.ranking;

import com.ufcg.psoft.project.dto.ranking.RankingResponseDTO;
import com.ufcg.psoft.project.model.palpite.Palpite;
import com.ufcg.psoft.project.repository.PalpiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private PalpiteRepository palpiteRepository;

    @Override
    public List<RankingResponseDTO> gerarRankingDoGrupo(Long grupoId) {
        List<Palpite> palpites = palpiteRepository.findByGrupoId(grupoId);
        return consolidarEOrdenarRanking(palpites);
    }

    @Override
    public List<RankingResponseDTO> gerarRankingGlobal() {
        List<Palpite> palpites = palpiteRepository.findAll();
        return consolidarEOrdenarRanking(palpites);
    }

    private List<RankingResponseDTO> consolidarEOrdenarRanking(List<Palpite> palpites) {
        List<Palpite> palpitesAvaliados = palpites.stream()
                .filter(p -> p.getPontosObtidos() != null)
                .collect(Collectors.toList());

        Map<Long, RankingResponseDTO> consolidado = palpitesAvaliados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getUsuario().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), lista -> {
                            Palpite primeiro = lista.get(0);
                            return RankingResponseDTO.builder()
                                    .usuarioNome(primeiro.getUsuario().getNome())
                                    .usuarioUsername(primeiro.getUsuario().getUsername())
                                    .pontuacaoTotal(lista.stream().mapToInt(Palpite::getPontosObtidos).sum())
                                    .quantidadePlacaresExatos((int) lista.stream().filter(Palpite::isAcertouPlacarExato).count())
                                    .quantidadeAcertosVencedor((int) lista.stream().filter(Palpite::isAcertouVencedor).count())
                                    .quantidadeAcertosEmpate((int) lista.stream().filter(Palpite::isAcertouEmpate).count())
                                    .build();
                        })
                ));

        List<RankingResponseDTO> rankingOrdenado = consolidado.values().stream()
                .sorted(Comparator.comparingInt(RankingResponseDTO::getPontuacaoTotal)
                        .thenComparingInt(RankingResponseDTO::getQuantidadePlacaresExatos)
                        .reversed())
                .collect(Collectors.toList());

        AtomicInteger posicao = new AtomicInteger(1);
        rankingOrdenado.forEach(r -> r.setPosicao(posicao.getAndIncrement()));

        return rankingOrdenado;
    }
}