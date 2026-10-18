\# Organizador de Arquivos



CLI em Java 21 que organiza uma pasta por tipo, data ou extensão.



\## Stack

\- Java 21 (use records, switch com pattern matching e sealed quando fizer sentido)

\- Maven

\- JUnit 5 para testes

\- Sem outras dependências. Use apenas java.nio.file e a biblioteca padrão.



\## Estrutura de pacotes

\- Código principal: `src/main/java/com/sidneyrvj/organizador/`

\- Testes: `src/test/java/com/sidneyrvj/organizador/`

\- Não crie pacotes novos sem perguntar antes.



\## Comandos

\- Compilar e testar: `mvn clean verify`

\- Gerar o jar: `mvn package`

\- Executar: `java -jar target/organizador.jar <pasta> --modo <tipo|data|extensao> \[--simular]`



\## Regras de segurança (obrigatórias)

\- Nunca apagar arquivos. Só mover.

\- Nunca sobrescrever um arquivo existente. Em caso de nome repetido, adicione um sufixo numérico.

\- O modo `--simular` não pode alterar nada no disco. Teste isso.

\- Antes de qualquer movimentação real, o programa deve validar que a pasta existe e é um diretório.



\## Convenções de código

\- Cada classe tem uma responsabilidade. Não crie classes "Utils" genéricas.

\- Métodos curtos e nomes descritivos, em português ou inglês, mas com consistência.

\- Toda lógica de decisão (classificar, calcular destino) deve ter teste unitário.

\- Mensagens de erro para o usuário devem ser claras e em português.

\- Não use `System.exit` dentro das classes de lógica. Só o `Main` decide o código de saída.



\## Git

\- Commits pequenos, um por funcionalidade.

\- Mensagens no padrão: `feat: ...`, `fix: ...`, `test: ...`, `docs: ...`.

\- Não faça commit de arquivos da pasta `target/`.

\- Antes de cada commit, rode `mvn clean verify` e confirme que passou.



\## Fluxo de trabalho

\- Antes de implementar algo grande, apresente um plano e espere minha aprovação.

\- Depois de cada etapa, rode `mvn clean verify` e mostre o resultado.

\- Se um teste falhar, explique a causa antes de alterar o código.

\- Não altere testes existentes só para fazê-los passar, a menos que eu peça.

