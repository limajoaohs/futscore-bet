package com.ufcg.psoft.project.model.usuario.perfil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PerfilUsuarioConverter implements AttributeConverter<PerfilUsuario, String> {

    @Override
    public String convertToDatabaseColumn(PerfilUsuario perfil) {
        if (perfil == null) {
            return null;
        }
        return perfil.getTipoPerfil().name();
    }

    @Override
    public PerfilUsuario convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return TipoPerfil.valueOf(dbData).toEntity();
    }
}
