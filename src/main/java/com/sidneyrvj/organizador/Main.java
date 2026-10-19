package com.sidneyrvj.organizador;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            imprimirAjuda();
            System.exit(1);
        }

        try {
            Path pasta = Paths.get(args[0]);
            ModoOrganizacao modo = null;
            boolean simular = false;

            for (int i = 1; i < args.length; i++) {
                if ("--modo".equals(args[i]) && i + 1 < args.length) {
                    modo = parseModo(args[++i]);
                } else if ("--simular".equals(args[i])) {
                    simular = true;
                }
            }

            if (modo == null) {
                System.err.println("Erro: O modo de organização deve ser especificado (--modo <tipo|data|extensao>)");
                System.exit(1);
            }

            System.out.println("Iniciando organização de: " + pasta);
            if (simular) System.out.println("Modo de simulação ativo. Nenhuma alteração será feita no disco.");

            Organizador organizador = new Organizador();
            organizador.organizar(pasta, modo, simular);

            System.out.println("Organização concluída com sucesso!");

        } catch (IllegalArgumentException e) {
            System.err.println("Erro de validação: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static ModoOrganizacao parseModo(String modoStr) {
        return switch (modoStr.toLowerCase()) {
            case "tipo" -> ModoOrganizacao.TIPO;
            case "data" -> ModoOrganizacao.DATA;
            case "extensao" -> ModoOrganizacao.EXTENSAO;
            default -> throw new IllegalArgumentException("Modo inválido: " + modoStr + ". Use tipo, data ou extensao.");
        };
    }

    private static void imprimirAjuda() {
        System.out.println("Uso: java -jar organizador.jar <pasta> --modo <tipo|data|extensao> [--simular]");
        System.out.println("\nExemplos:");
        System.out.println("  java -jar organizador.jar C:/Downloads --modo tipo");
        System.out.println("  java -jar organizador.jar C:/Fotos --modo data --simular");
    }
}
