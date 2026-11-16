package com.ufcg.psoft.project.client;

import com.ufcg.psoft.project.dto.integration.FootballDataResponseDTO;
import com.ufcg.psoft.project.dto.integration.MatchResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClienteFootballData {

    @Value("${api.football.url}")
    private String urlApiBase;

    @Value("${api.football.token}")
    private String tokenApi;

    public boolean configurado() {
        return tokenApi != null && !tokenApi.isBlank();
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public FootballDataResponseDTO buscarCampeonatos() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", tokenApi);
        HttpEntity<String> entidade = new HttpEntity<>(headers);
        ResponseEntity<FootballDataResponseDTO> resposta = restTemplate.exchange(
                urlApiBase,
                HttpMethod.GET,
                entidade,
                FootballDataResponseDTO.class
        );
        return resposta.getBody();
    }

    public MatchResponseDTO buscarPartidas(String urlCampeonato) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", tokenApi);
        HttpEntity<String> entidade = new HttpEntity<>(headers);
        ResponseEntity<MatchResponseDTO> resposta = restTemplate.exchange(
                urlCampeonato + "/matches",
                HttpMethod.GET,
                entidade,
                MatchResponseDTO.class
        );
        return resposta.getBody();
    }
}
