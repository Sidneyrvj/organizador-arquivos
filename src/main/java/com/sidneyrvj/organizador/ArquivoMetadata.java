package com.sidneyrvj.organizador;

import java.time.LocalDateTime;

public record ArquivoMetadata(String nome, String extensao, LocalDateTime dataModificacao) {
}
