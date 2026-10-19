package com.sidneyrvj.organizador;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private void createTestFile(String relativePath) throws IOException {
        Path file = tempDir.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "conteúdo de teste");
    }

    @Test
    void testSucessoTipo() throws IOException {
        createTestFile("foto.jpg");
        createTestFile("doc.pdf");

        String[] args = {tempDir.toString(), "--modo", "tipo"};
        int exitCode = Main.run(args);

        assertEquals(0, exitCode);
        assertTrue(Files.exists(tempDir.resolve("Imagens/foto.jpg")));
        assertTrue(Files.exists(tempDir.resolve("Documentos/doc.pdf")));
        assertTrue(outContent.toString().contains("Organização concluída com sucesso!"));
    }

    @Test
    void testSucessoExtensao() throws IOException {
        createTestFile("foto.jpg");
        createTestFile("nota.txt");

        String[] args = {tempDir.toString(), "--modo", "extensao"};
        int exitCode = Main.run(args);

        assertEquals(0, exitCode);
        assertTrue(Files.exists(tempDir.resolve("jpg/foto.jpg")));
        assertTrue(Files.exists(tempDir.resolve("txt/nota.txt")));
    }

    @Test
    void testModoSimular() throws IOException {
        createTestFile("foto.jpg");

        String[] args = {tempDir.toString(), "--modo", "tipo", "--simular"};
        int exitCode = Main.run(args);

        assertEquals(0, exitCode);
        assertFalse(Files.exists(tempDir.resolve("Imagens")), "Pasta de destino não deveria ser criada no modo simular");
        assertTrue(Files.exists(tempDir.resolve("foto.jpg")), "Arquivo original não deveria ter sido movido");
        assertTrue(outContent.toString().contains("Modo de simulação ativo"));
        assertTrue(outContent.toString().contains("[SIMULAÇÃO] Moveria"));
    }

    @Test
    void testColisaoE2E() throws IOException {
        // Setup: arquivo já existindo no destino
        Path pastaImagens = tempDir.resolve("Imagens");
        Files.createDirectories(pastaImagens);
        Files.createFile(pastaImagens.resolve("foto.jpg"));

        // Arquivo na raiz para ser movido
        createTestFile("foto.jpg");

        String[] args = {tempDir.toString(), "--modo", "tipo"};
        int exitCode = Main.run(args);

        assertEquals(0, exitCode);
        assertTrue(Files.exists(pastaImagens.resolve("foto.jpg")));
        assertTrue(Files.exists(pastaImagens.resolve("foto_1.jpg")));
    }

    @Test
    void testPastaInexistente() {
        String[] args = {tempDir.resolve("não_existe").toString(), "--modo", "tipo"};
        int exitCode = Main.run(args);

        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Erro: A pasta especificada não existe"));
    }

    @Test
    void testModoInvalido() {
        String[] args = {tempDir.toString(), "--modo", "errado"};
        int exitCode = Main.run(args);

        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Modo inválido"));
    }

    @Test
    void testArgumentosAusentes() {
        String[] args = {};
        int exitCode = Main.run(args);

        assertEquals(1, exitCode);
        assertTrue(outContent.toString().contains("Uso: java -jar organizador.jar"));
    }

    @Test
    void testModoAusente() {
        String[] args = {tempDir.toString()};
        int exitCode = Main.run(args);

        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("O modo de organização deve ser especificado"));
    }
}
