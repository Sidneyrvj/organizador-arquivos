# 📂 Organizador de Arquivos

O **Organizador de Arquivos** é uma ferramenta de linha de comando (CLI) desenvolvida em Java 21 que automatiza a organização de arquivos em um diretório. Ele agrupa arquivos em subpastas com base em critérios selecionáveis: tipo de conteúdo, extensão do arquivo ou data de modificação.

## 🚀 Funcionalidades

A ferramenta permite organizar pastas bagunçadas de forma rápida e segura, oferecendo três modos de classificação:

- **Por Tipo**: Agrupa extensões comuns em categorias legíveis (ex: `.jpg` e `.png` vão para a pasta `Imagens`).
- **Por Extensão**: Cria pastas baseadas na extensão exata do arquivo (ex: `.pdf` vai para a pasta `pdf`).
- **Por Data**: Organiza arquivos em pastas baseadas no ano e mês de modificação (ex: `2023-10`).

### 🛡️ Segurança em Primeiro Lugar
- **Nunca apaga arquivos**: A ferramenta apenas move arquivos.
- **Não sobrescreve**: Se um arquivo com o mesmo nome já existir no destino, ele adiciona um sufixo numérico (ex: `foto_1.jpg`).
- **Modo de Simulação**: Permite visualizar todas as movimentações que seriam feitas sem alterar um único byte no disco.

---

## 🛠️ Pré-requisitos

Para compilar e executar o projeto, você precisará de:
- **Java 21** (JDK)
- **Apache Maven**

---

## 📦 Instalação e Build

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd organizador-arquivos
   ```

2. Compile o projeto e execute os testes:
   ```bash
   mvn clean verify
   ```

3. Gere o arquivo JAR executável:
   ```bash
   mvn package
   ```
   O arquivo será gerado em `target/organizador.jar`.

---

## 💻 Como Executar

A sintaxe básica para execução é:
```bash
java -jar target/organizador.jar <pasta> --modo <tipo|data|extensao> [--simular]
```

### Exemplos Reais

**1. Organizando por Tipo (Categorias):**
Move fotos para `Imagens`, PDFs para `Documentos`, etc.
```bash
java -jar target/organizador.jar "C:/Users/User/Downloads" --modo tipo
```

**2. Organizando por Extensão:**
Cria pastas separadas para cada extensão encontrada.
```bash
java -jar target/organizador.jar "C:/Users/User/Documents" --modo extensao
```

**3. Organizando por Data:**
Agrupa arquivos por mês e ano (ex: `2024-01`, `2024-02`).
```bash
java -jar target/organizador.jar "C:/Users/User/Pictures" --modo data
```

**4. Simulando a Organização:**
Verifica o que aconteceria sem mover arquivos reais.
```bash
java -jar target/organizador.jar "C:/Downloads" --modo tipo --simular
```

---

## 🧪 Testes

O projeto conta com uma bateria completa de testes unitários e de integração (End-to-End) que validam a lógica de classificação, o tratamento de colisões e a interface CLI.

Para rodar os testes:
```bash
mvn test
```

---

## 📂 Estrutura do Projeto

```text
src/main/java/com/sidneyrvj/organizador/
├── Main.java                # Ponto de entrada, parsing de argumentos e CLI
├── Organizador.java         # Orquestrador da varredura e movimentação de arquivos
├── ClassificadorArquivo.java # Lógica de decisão de destino (estratégias)
├── ModoOrganizacao.java     # Enum com os modos suportados (TIPO, DATA, EXTENSAO)
└── ArquivoMetadata.java      # Record com metadados do arquivo para desacoplamento de I/O
```

---

## 🛠️ Como foi desenvolvido

Este projeto foi desenvolvido utilizando uma abordagem de engenharia assistida por IA, focada em qualidade e segurança.

### Metodologia de Desenvolvimento
O desenvolvimento foi dividido em 6 fases incrementais, garantindo que cada funcionalidade fosse testada antes da próxima:
1. **Fase 1**: Setup do projeto com Maven e Java 21.
2. **Fase 2**: Implementação da lógica de classificação e testes unitários.
3. **Fase 3**: Implementação da orquestração de arquivos e movimentação física.
4. **Fase 4**: Refinamentos de CLI e precisão do modo de simulação.
5. **Fase 5**: Implementação de testes de integração End-to-End.
6. **Fase 6**: Documentação final.

### Ferramentas Utilizadas
- **Claude Code**: Utilizado como agente de desenvolvimento para a escrita do código, refatorações e criação de testes.
- **CLAUDE.md**: Um arquivo de governança utilizado para manter as regras de segurança, convenções de código e a stack tecnológica alinhadas durante todo o ciclo de vida do projeto.
