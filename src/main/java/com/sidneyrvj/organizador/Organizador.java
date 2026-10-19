package com.sidneyrvj.organizador;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
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

        // Coletamos para lista antes de processar para evitar manter a stream do diretório aberta
        // Isso evita FileSystemException em alguns sistemas operacionais durante o move.
        List<Path> files;
        try (Stream<Path> stream = Files.list(sourceDir)) {
            files = stream.filter(Files::isRegularFile).toList();
        }

        // Conjunto para rastrear caminhos reservados durante a sessão (especialmente para simulação)
        Set<Path> reservedPaths = new HashSet<>();

        for (Path file : files) {
            try {
                processarArquivo(file, sourceDir, modo, simular, reservedPaths);
            } catch (IOException e) {
                System.err.println("Erro ao processar arquivo " + file.getFileName() + ": " + e.getMessage());
            }
        }
    }

    private void processarArquivo(Path file, Path sourceDir, ModoOrganizacao modo, boolean simular, Set<Path> reservedPaths) throws IOException {
        // 1. Extrair metadados
        String nome = file.getFileName().toString();
        String extensao = extrairExtensao(nome);
        LocalDateTime dataMod = Files.getLastModifiedTime(file).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        ArquivoMetadata metadata = new ArquivoMetadata(nome, extensao, dataMod);

        // 2. Calcular pasta de destino
        String pastaDestinoNome = classificador.classificar(metadata, modo);
        Path pastaDestino = sourceDir.resolve(pastaDestinoNome);

        // 3. Resolver colisão de nome (considerando disco e reservas da sessão)
        Path destinoFinal = resolverColisao(pastaDestino, nome, reservedPaths);

        // Adicionamos ao conjunto de reservas para que próximos arquivos vejam esta reserva
        reservedPaths.add(destinoFinal);

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

    private Path resolverColisao(Path pastaDestino, String nomeArquivo, Set<Path> reservedPaths) {
        Path destino = pastaDestino.resolve(nomeArquivo);

        // Verifica se o caminho já está ocupado no disco OU foi reservado nesta sessão
        if (!Files.exists(destino) && !reservedPaths.contains(destino)) {
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
        while (Files.exists(destino) || reservedPaths.contains(destino)) {
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
