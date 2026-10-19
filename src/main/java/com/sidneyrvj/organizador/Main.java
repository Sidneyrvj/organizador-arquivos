package com.sidneyrvj.organizador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.exit(run(args));
    }

    public static int run(String[] args) {
        if (args.length == 0) {
            imprimirAjuda();
            return 1;
        }

        try {
            // O primeiro argumento deve ser a pasta
            Path pasta = Paths.get(args[0]);

            if (!Files.exists(pasta) || !Files.isDirectory(pasta)) {
                System.err.println("Erro: A pasta especificada não existe ou não é um diretório: " + args[0]);
                imprimirAjuda();
                return 1;
            }

            ModoOrganizacao modo = null;
            boolean simular = false;

            // Parsing de flags
            for (int i = 1; i < args.length; i++) {
                if ("--modo".equals(args[i])) {
                    if (i + 1 < args.length) {
                        modo = parseModo(args[++i]);
                    } else {
                        System.err.println("Erro: A flag --modo requer um valor (tipo, data ou extensao).");
                        imprimirAjuda();
                        return 1;
                    }
                } else if ("--simular".equals(args[i])) {
                    simular = true;
                } else {
                    System.err.println("Erro: Argumento desconhecido: " + args[i]);
                    imprimirAjuda();
                    return 1;
                }
            }

            if (modo == null) {
                System.err.println("Erro: O modo de organização deve ser especificado usando --modo <tipo|data|extensao>.");
                imprimirAjuda();
                return 1;
            }

            System.out.println("Iniciando organização de: " + pasta.toAbsolutePath());
            if (simular) {
                System.out.println("Modo de simulação ativo. Nenhuma alteração será feita no disco.");
            }

            Organizador organizador = new Organizador();
            organizador.organizar(pasta, modo, simular);

            System.out.println("Organização concluída com sucesso!");
            return 0;

        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Erro inesperado durante a execução: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private static ModoOrganizacao parseModo(String modoStr) {
        return switch (modoStr.toLowerCase()) {
            case "tipo" -> ModoOrganizacao.TIPO;
            case "data" -> ModoOrganizacao.DATA;
            case "extensao" -> ModoOrganizacao.EXTENSAO;
            default -> throw new IllegalArgumentException("Modo inválido: '" + modoStr + "'. Os modos aceitos são: tipo, data ou extensao.");
        };
    }

    private static void imprimirAjuda() {
        System.out.println("\n--- Organizador de Arquivos ---");
        System.out.println("Uso: java -jar organizador.jar <pasta> --modo <tipo|data|extensao> [--simular]");
        System.out.println("\nArgumentos:");
        System.out.println("  <pasta>           Caminho para a pasta que deseja organizar.");
        System.out.println("  --modo <valor>    Modo de organização (tipo, data ou extensao).");
        System.out.println("  --simular        Exibe o que seria feito sem alterar nada no disco.");
        System.out.println("\nExemplos:");
        System.out.println("  java -jar organizador.jar C:/Downloads --modo tipo");
        System.out.println("  java -jar organizador.jar C:/Documentos --modo data --simular");
        System.out.println("------------------------------\n");
    }
}
