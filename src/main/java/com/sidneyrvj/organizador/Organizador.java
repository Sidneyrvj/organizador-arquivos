package com.sidneyrvj.organizador;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class Organizador {
    private final ClassificadorArquivo classificador;

    public Organizador() {
        this.classificador = new ClassificadorArquivo();
    }

    public void organizar(Path sourceDir, ModoOrganizacao modo, boolean simular) throws IOException {
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("A pasta de origem especificada não existe ou não é um diretório: " + sourceDir);
        }

        try (Stream<Path> files = Files.list(sourceDir)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                try {
                    processarArquivo(file, sourceDir, modo, simular);
                } catch (IOException e) {
                    System.err.println("Erro ao processar arquivo " + file.getFileName() + ": " + e.getMessage());
                }
            });
        }
    }

    private void processarArquivo(Path file, Path sourceDir, ModoOrganizacao modo, boolean simular) throws IOException {
        // 1. Extrair metadados
        String nome = file.getFileName().toString();
        String extensao = extrairExtensao(nome);
        LocalDateTime dataMod = Files.getLastModifiedTime(file).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        ArquivoMetadata metadata = new ArquivoMetadata(nome, extensao, dataMod);

        // 2. Calcular pasta de destino
        String pastaDestinoNome = classificador.classificar(metadata, modo);
        Path pastaDestino = sourceDir.resolve(pastaDestinoNome);

        // 3. Resolver colisão de nome
        Path destinoFinal = resolverColisao(pastaDestino, nome);

        // 4. Mover ou Simular
        if (simular) {
            System.out.println("[SIMULAÇÃO] Moveria: " + nome + " -> " + pastaDestinoNome + "/" + destinoFinal.getFileName());
        } else {
            if (!Files.exists(pastaDestino)) {
                Files.createDirectories(pastaDestino);
            }
            Files.move(file, destinoFinal, StandardCopyOption.ATOMIC_MOVE);
        }
    }

    private Path resolverColisao(Path pastaDestino, String nomeArquivo) {
        Path destino = pastaDestino.resolve(nomeArquivo);
        if (!Files.exists(destino)) {
            return destino;
        }

        String nomeBase;
        String extensao = "";
        int lastDot = nomeArquivo.lastIndexOf('.');
        if (lastDot != -1) {
            nomeBase = nomeArquivo.substring(0, lastDot);
            extensao = nomeArquivo.substring(lastDot);
        } else {
            nomeBase = nomeArquivo;
        }

        int contador = 1;
        while (Files.exists(destino)) {
            String novoNome = nomeBase + "_" + contador + extensao;
            destino = pastaDestino.resolve(novoNome);
            contador++;
        }
        return destino;
    }

    private String extrairExtensao(String nomeArquivo) {
        int lastDot = nomeArquivo.lastIndexOf('.');
        if (lastDot == -1 || lastDot == nomeArquivo.length() - 1) {
            return "";
        }
        return nomeArquivo.substring(lastDot + 1).toLowerCase();
    }
}
