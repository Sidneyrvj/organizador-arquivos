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
        Path pastaPdf = tempDir.resolve("pdf");
        Files.createDirectories(pastaPdf);
        Files.createFile(pastaPdf.resolve("teste.pdf"));

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
    void testModoSimularSemAlteracoes() throws IOException {
        Path arquivo = tempDir.resolve("teste.pdf");
        Files.createFile(arquivo);

        organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, true);

        assertTrue(Files.exists(arquivo), "Arquivo não deveria ter sido movido no modo simular");
        assertFalse(Files.exists(tempDir.resolve("pdf")), "Pasta de destino não deveria ter sido criada");
    }

    @Test
    void testSimulacaoPreveColisoesMutuas() throws IOException {
        // Dois arquivos que cairão na mesma pasta com o mesmo nome
        // Para forçar isso no teste, podemos criar arquivos com nomes diferentes
        // que o classificador trate como iguais (ou usar caminhos simulados)
        // Aqui, vamos criar dois arquivos na raiz que o classificador moveria para o mesmo destino.

        // Como o Classificador usa o nome real para o destino, vamos criar dois arquivos
        // com a mesma extensão, mas nomes diferentes.
        // O teste de colisão mútua na simulação é: se eu tenho arquivo1.txt e arquivo2.txt,
        // e por algum motivo eles fossem para o mesmo nome (ex: renomeação forçada), a simulação deveria pegar.
        // No nosso caso, o nome do arquivo é preservado.
        // Então, para testar colisão mútua, precisamos de arquivos com o mesmo nome em pastas diferentes
        // ou arquivos que já existam no destino.

        // Cenário: arquivo.txt na raiz e arquivo.txt já existindo no destino
        Path pastaPdf = tempDir.resolve("pdf");
        Files.createDirectories(pastaPdf);
        Files.createFile(pastaPdf.resolve("teste.pdf"));

        Path arquivoRaiz = tempDir.resolve("teste.pdf");
        Files.createFile(arquivoRaiz);

        // Captura o output do console para verificar a simulação
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(bos));

        try {
            organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, true);
            String output = bos.toString();
            assertTrue(output.contains("teste_1.pdf"), "A simulação deve prever o sufixo _1 devido ao arquivo existente no disco");
        } finally {
            System.setOut(originalOut);
        }
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
        assertDoesNotThrow(() -> organizador.organizar(tempDir, ModoOrganizacao.EXTENSAO, false));
    }
}
