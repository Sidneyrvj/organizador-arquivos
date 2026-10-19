package com.sidneyrvj.organizador;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class ClassificadorArquivo {
    private static final Map<String, Set<String>> CATEGORIAS = Map.of(
        "Imagens", Set.of("jpg", "jpeg", "png", "gif", "bmp"),
        "Documentos", Set.of("pdf", "doc", "docx", "txt", "odt"),
        "Videos", Set.of("mp4", "mkv", "avi", "mov"),
        "Audio", Set.of("mp3", "wav", "flac")
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public String classificar(ArquivoMetadata arquivo, ModoOrganizacao modo) {
        return switch (modo) {
            case EXTENSAO -> classificarPorExtensao(arquivo);
            case TIPO -> classificarPorTipo(arquivo);
            case DATA -> classificarPorData(arquivo);
        };
    }

    private String classificarPorExtensao(ArquivoMetadata arquivo) {
        String ext = arquivo.extensao();
        return (ext == null || ext.isBlank()) ? "sem_extensao" : ext.toLowerCase();
    }

    private String classificarPorTipo(ArquivoMetadata arquivo) {
        String ext = arquivo.extensao();
        if (ext == null || ext.isBlank()) {
            return "Outros";
        }

        String extLower = ext.toLowerCase();
        for (var entry : CATEGORIAS.entrySet()) {
            if (entry.getValue().contains(extLower)) {
                return entry.getKey();
            }
        }
        return "Outros";
    }

    private String classificarPorData(ArquivoMetadata arquivo) {
        return arquivo.dataModificacao().format(DATE_FORMATTER);
    }
}
