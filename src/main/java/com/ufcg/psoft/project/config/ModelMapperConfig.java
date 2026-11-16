package com.ufcg.psoft.project.config;

import com.ufcg.psoft.project.dto.usuario.UsuarioPutDTO;
import com.ufcg.psoft.project.model.usuario.Usuario;
import com.ufcg.psoft.project.model.usuario.perfil.PerfilUsuario;
import com.ufcg.psoft.project.model.usuario.perfil.TipoPerfil;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        Converter<TipoPerfil, PerfilUsuario> perfilConverter = context ->
                context.getSource() == null ? null : context.getSource().toEntity();
        modelMapper.createTypeMap(UsuarioPutDTO.class, Usuario.class)
                .addMappings(mapper -> mapper.using(perfilConverter)
                        .map(UsuarioPutDTO::getPerfil, Usuario::setPerfil));
        return modelMapper;
    }
}
