package com.ufcg.psoft.project.service.grupo;

import com.ufcg.psoft.project.dto.grupo.GrupoRequestDTO;
import com.ufcg.psoft.project.dto.grupo.GrupoResponseDTO;
import com.ufcg.psoft.project.dto.grupo.CriteriosDesempateDTO;
import com.ufcg.psoft.project.exception.AcessoNegadoException;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.campeonato.Campeonato;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.grupo.Privacidade;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.CampeonatoRepository;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.project.model.grupo.CriterioDesempateFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupoServiceImpl implements GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CriterioDesempateFactory criterioDesempateFactory;

    @Override
    public GrupoResponseDTO criarGrupo(GrupoRequestDTO grupoRequestDTO) {
        Campeonato campeonato = campeonatoRepository.findById(grupoRequestDTO.getCampeonatoId())
                .orElseThrow(() -> new ProjectException("Campeonato não encontrado!"));
        if (!campeonato.isAtivo()) {
            throw new ProjectException("Não é possível criar um grupo para um campeonato desativado!");
        }
        Usuario organizador = usuarioRepository.findById(grupoRequestDTO.getCriadorId())
                .orElseThrow(() -> new ProjectException("Usuário criador não encontrado!"));
        Grupo novo = Grupo.builder()
                .nome(grupoRequestDTO.getNome())
                .descricao(grupoRequestDTO.getDescricao())
                .privacidade(grupoRequestDTO.getPrivacidade())
                .limiteParticipantes(grupoRequestDTO.getLimiteParticipantes())
                .campeonato(campeonato)
                .organizador(organizador)
                .minutosParaAbertura(grupoRequestDTO.getMinutosParaAbertura() != null ? grupoRequestDTO.getMinutosParaAbertura() : 0)
                .minutosParaFechamento(grupoRequestDTO.getMinutosParaFechamento() != null ? grupoRequestDTO.getMinutosParaFechamento() : 0)
                .build();
        Grupo salvo = grupoRepository.save(novo);
        return modelMapper.map(salvo, GrupoResponseDTO.class);
    }

    @Override
    public GrupoResponseDTO findById(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new ProjectException("Grupo não encontrado"));
        return modelMapper.map(grupo, GrupoResponseDTO.class);
    }

    @Override
    public List<GrupoResponseDTO> findByNome(String nome) {
        List<Grupo> grupos = grupoRepository.findByNomeContaining(nome);
        return grupos.stream()
                .map((grupo) -> modelMapper.map(grupo, GrupoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GrupoResponseDTO> findAll() {
        List<Grupo> grupos = grupoRepository.findAll();
        return grupos.stream()
                .map((grupo) -> modelMapper.map(grupo, GrupoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GrupoResponseDTO adicionarParticipanteAGrupoPublico(Long grupoId, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ProjectException("Grupo não encontrado!"));
        if (!grupo.getPrivacidade().equals(Privacidade.PUBLICA)) {
            throw new ProjectException("Grupo não é público!");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ProjectException("Usuário não encontrado!"));
        if (grupo.getParticipantes().contains(usuario)) {
            throw new ProjectException("Usuário já está no grupo!");
        } else if (!grupo.getCampeonato().isAtivo()) {
            throw new ProjectException("O campeonato do grupo está desativado!");
        } else if (!grupo.addParticipante(usuario)) {
            throw new ProjectException("Grupo atingiu o limite de participantes!");
        }
        Grupo salvo = grupoRepository.save(grupo);
        return modelMapper.map(salvo, GrupoResponseDTO.class);
    }

    @Override
    public GrupoResponseDTO configurarCriteriosDesempate(Long grupoId, Long usuarioId, CriteriosDesempateDTO dto) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new ProjectException("Grupo não encontrado!"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ProjectException("Usuário não encontrado!"));

        if (!grupo.getOrganizador().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Apenas o organizador do grupo pode alterar os critérios de desempate!");
        }

        List<String> validos = List.of(
                "MAIOR_PLACAR_EXATO",
                "MENOR_ERRO",
                "MAIOR_ACERTO_VENCEDOR",
                "MAIOR_ACERTO_EMPATE"
        );

        if (dto.getCriterios() == null) {
            throw new ProjectException("A lista de critérios de desempate é obrigatória");
        }

        if (dto.getCriterios().size() != 4) {
            throw new ProjectException("A lista de critérios de desempate deve conter exatamente 4 critérios");
        }

        for (String criterio : dto.getCriterios()) {
            if (criterio == null || !validos.contains(criterio)) {
                throw new ProjectException("Critério de desempate inválido: " + criterio);
            }
        }

        long countUnicos = dto.getCriterios().stream().distinct().count();
        if (countUnicos != 4) {
            throw new ProjectException("A lista de critérios de desempate não pode conter duplicatas");
        }

        grupo.setCriteriosDesempate(dto.getCriterios());
        Grupo salvo = grupoRepository.save(grupo);
        return modelMapper.map(salvo, GrupoResponseDTO.class);
    }
}
