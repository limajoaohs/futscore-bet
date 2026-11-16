package com.ufcg.psoft.project.service.convite;

import com.ufcg.psoft.project.dto.convite.ConviteRequestDTO;
import com.ufcg.psoft.project.dto.convite.ConviteResponseDTO;
import com.ufcg.psoft.project.exception.ProjectException;
import com.ufcg.psoft.project.model.convite.Convite;
import com.ufcg.psoft.project.model.convite.StatusConvite;
import com.ufcg.psoft.project.model.grupo.Grupo;
import com.ufcg.psoft.project.model.grupo.Privacidade;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.repository.ConviteRepository;
import com.ufcg.psoft.project.repository.GrupoRepository;
import com.ufcg.psoft.project.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConviteServiceImpl implements ConviteService {

    @Autowired
    private ConviteRepository conviteRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ConviteResponseDTO enviarConvite(ConviteRequestDTO conviteRequestDTO) {
        Grupo grupo = grupoRepository.findById(conviteRequestDTO.getGrupoId())
                .orElseThrow(() -> new ProjectException("Grupo não encontrado!"));
        Usuario organizador = usuarioRepository.findById(conviteRequestDTO.getOrganizadorId())
                .orElseThrow(() -> new ProjectException("Organizador não encontrado!"));
        Usuario convidado = usuarioRepository.findById(conviteRequestDTO.getConvidadoId())
                .orElseThrow(() -> new ProjectException("Usuário não encontrado!"));
        if (grupo.getPrivacidade() != Privacidade.PRIVADA) {
            throw new ProjectException("Grupo não é privado!");
        } else if (!grupo.getOrganizador().getId().equals(organizador.getId())) {
            throw new ProjectException("Apenas o organizador pode enviar convites para este grupo!");
        } else if (conviteRepository.existsByGrupoAndConvidado(grupo, convidado)) {
            throw new ProjectException("Este usuário já possui um convite para este grupo!");
        }
        Convite novo = Convite.builder()
                .grupo(grupo)
                .organizador(organizador)
                .convidado(convidado)
                .status(StatusConvite.PENDENTE)
                .build();
        Convite salvo = conviteRepository.save(novo);
        System.out.println("Notificação enviada para: " + convidado.getEmail());
        System.out.println("Mensagem: Você foi convidado por " + organizador.getNome() + " para o grupo " + grupo.getNome());
        return modelMapper.map(salvo, ConviteResponseDTO.class);
    }

    @Override
    public List<ConviteResponseDTO> listarConvitesPendentes(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ProjectException("Usuário não encontrado!"));
        List<Convite> pendentes = conviteRepository.findByConvidadoAndStatus(usuario, StatusConvite.PENDENTE);
        return pendentes.stream()
                .map((convite) -> modelMapper.map(convite, ConviteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ConviteResponseDTO responderConvite(Long conviteId, Long usuarioId, String codigoAcesso, String acao) {
        Convite convite = conviteRepository.findById(conviteId)
                .orElseThrow(() -> new ProjectException("Convite não encontrado!"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ProjectException("Usuário não encontrado!"));
        if (!convite.getConvidado().getId().equals(usuarioId)) {
            throw new ProjectException("Este convite não pertence a este usuário!");
        } else if (!convite.getStatus().equals(StatusConvite.PENDENTE)) {
            throw new ProjectException("Este convite já foi respondido ou modificado!");
        } else if (!usuario.getCodigoAcesso().equals(codigoAcesso)) {
            throw new ProjectException("Código de acesso incorreto ou não fornecido para operação crítica!");
        }
        switch (acao.trim().toUpperCase()) {
            case "ACEITAR":
                convite.setStatus(StatusConvite.ACEITO);
                Grupo grupo = convite.getGrupo();
                if (!grupo.addParticipante(usuario)) {
                    throw new ProjectException("Grupo atingiu o limite de participantes!");
                }
                grupoRepository.save(grupo);
                break;
            case "RECUSAR":
                convite.setStatus(StatusConvite.RECUSADO);
                break;
            case "IGNORAR":
                convite.setStatus(StatusConvite.IGNORADO);
                break;
            default:
                throw new ProjectException("Ação inválida! Use ACEITAR, RECUSAR ou IGNORAR.");
        }
        Convite salvo = conviteRepository.save(convite);
        return modelMapper.map(salvo, ConviteResponseDTO.class);
    }
}
