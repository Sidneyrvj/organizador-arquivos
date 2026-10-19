package com.sidneyrvj.organizador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OrganizadorTest {
    private Organizador organizador;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        organizador = new Organizador();
    }

    @Test
    void testMovimentacaoSimples() throws IOException {
        Path arquivo = tempDir.resolve("teste.pdf");
        Files.createFile(arquivo);

        organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, false);

        Path destino = tempDir.resolve("pdf/teste.pdf");
        assertTrue(Files.exists(destino), "O arquivo deveria ter sido movido para a pasta pdf");
        assertFalse(Files.exists(arquivo), "O arquivo original não deveria mais existir na raiz");
    }

    @Test
    void testColisaoSimples() throws IOException {
        // Cria pasta de destino com arquivo já existente
        Path pastaPdf = tempDir.resolve("pdf");
        Files.createDirectories(pastaPdf);
        Files.createFile(pastaPdf.resolve("teste.pdf"));

        // Cria arquivo na raiz para mover
        Path arquivoRaiz = tempDir.resolve("teste.pdf");
        Files.createFile(arquivoRaiz);

        organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, false);

        assertTrue(Files.exists(pastaPdf.resolve("teste.pdf")), "Original deve permanecer");
        assertTrue(Files.exists(pastaPdf.resolve("teste_1.pdf")), "Novo deve ser renomeado para _1");
    }

    @Test
    void testColisaoEmCadeia() throws IOException {
        Path pastaPdf = tempDir.resolve("pdf");
        Files.createDirectories(pastaPdf);
        Files.createFile(pastaPdf.resolve("teste.pdf"));
        Files.createFile(pastaPdf.resolve("teste_1.pdf"));

        Path arquivoRaiz = tempDir.resolve("teste.pdf");
        Files.createFile(arquivoRaiz);

        organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, false);

        assertTrue(Files.exists(pastaPdf.resolve("teste_2.pdf")), "Novo deve ser renomeado para _2");
    }

    @Test
    void testModoSimular() throws IOException {
        Path arquivo = tempDir.resolve("teste.pdf");
        Files.createFile(arquivo);

        organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, true);

        assertTrue(Files.exists(arquivo), "Arquivo não deveria ter sido movido no modo simular");
        assertFalse(Files.exists(tempDir.resolve("pdf")), "Pasta de destino não deveria ter sido criada");
    }

    @Test
    void testPastaInvalida() {
        Path arquivoComoPasta = tempDir.resolve("não_sou_pasta.txt");
        try {
            Files.createFile(arquivoComoPasta);
        } catch (IOException ignored) {}

        assertThrows(IllegalArgumentException.class, () -> {
            organizador.organizar(arquivoComoPasta, ModoOrganizacao.EXTENSAO, false);
        });
    }

    @Test
    void testPastaVazia() throws IOException {
        // tempDir já está vazio
        assertDoesNotThrow(() -> organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, false));
    }
}
