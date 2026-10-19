package com.sidneyrvj.organizador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClassificadorArquivoTest {
    private ClassificadorArquivo classificador;

    @BeforeEach
    void setUp() {
        classificador = new ClassificadorArquivo();
    }

    @Test
    void testExtensaoComum() {
        ArquivoMetadata arquivo = new ArquivoMetadata("documento.pdf", "pdf", LocalDateTime.now());
        assertEquals("pdf", classificador.classificar(arquivo, ModoOrganizacao.EXTENSAO));
    }

    @Test
    void testExtensaoSemExtensao() {
        ArquivoMetadata arquivo = new ArquivoMetadata("LICENSE", "", LocalDateTime.now());
        assertEquals("sem_extensao", classificador.classificar(arquivo, ModoOrganizacao.EXTENSAO));
    }

    @Test
    void testExtensaoMultiplosPontos() {
        ArquivoMetadata arquivo = new ArquivoMetadata("arquivo.tar.gz", "gz", LocalDateTime.now());
        assertEquals("gz", classificador.classificar(arquivo, ModoOrganizacao.EXTENSAO));
    }

    @ParameterizedTest
    @CsvSource({
        "png, Imagens",
        "jpg, Imagens",
        "pdf, Documentos",
        "docx, Documentos",
        "mp4, Videos",
        "mkv, Videos",
        "mp3, Audio",
        "flac, Audio",
        "sh, Outros",
        ", Outros"
    })
    void testTipo(String ext, String categoriaEsperada) {
        ArquivoMetadata arquivo = new ArquivoMetadata("teste." + ext, ext, LocalDateTime.now());
        assertEquals(categoriaEsperada, classificador.classificar(arquivo, ModoOrganizacao.TIPO));
    }

    @Test
    void testDataMesAtual() {
        LocalDateTime agora = LocalDateTime.now();
        ArquivoMetadata arquivo = new ArquivoMetadata("teste.txt", "txt", agora);
        String esperado = agora.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        assertEquals(esperado, classificador.classificar(arquivo, ModoOrganizacao.DATA));
    }

    @Test
    void testDataAnoAnterior() {
        LocalDateTime dataAntiga = LocalDateTime.of(2022, 12, 15, 10, 0);
        ArquivoMetadata arquivo = new ArquivoMetadata("velho.txt", "txt", dataAntiga);
        assertEquals("2022-12", classificador.classificar(arquivo, ModoOrganizacao.DATA));
    }
}
