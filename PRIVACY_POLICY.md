# Política de Privacidade — Antídoto

**Última atualização:** 08/08/2026

O Antídoto é um treinador de imunidade algorítmica **local-first**. Esta política
explica, em linguagem simples, como o app trata seus dados. Resumo: **nenhum dado
seu sai do seu aparelho.**

## 1. Quais dados o app usa

- **Estatísticas de uso de apps** (via a API oficial `UsageStatsManager` do Android):
  usadas para montar o painel de atenção e as metas. Ficam **somente no seu
  aparelho**, em um banco de dados local (SQLite/Room).
- **Check-ins de humor/gatilho, metas e progresso das lições**: informações que
  você mesmo registra, armazenadas **localmente**.

O app **não coleta** nome, e-mail, localização, contatos, identificadores de
publicidade nem qualquer dado pessoal para envio a servidores.

## 2. Compartilhamento e transferência

- **Não há servidores.** O Antídoto não envia, compartilha nem vende dados.
- **Nenhum SDK de analytics ou de anúncios** de terceiros é incluído.
- Backups do sistema Android (se você os ativar) seguem as regras do próprio
  Android e permanecem na sua conta/dispositivo.

## 3. Permissões

- **Acesso a estatísticas de uso (`PACKAGE_USAGE_STATS`)**: concedida por você nas
  Configurações do sistema. Usada apenas para exibir seu uso localmente. Pode ser
  revogada a qualquer momento nas Configurações do Android.
- **Notificações (`POST_NOTIFICATIONS`)**: usada apenas para o lembrete diário e
  opcional de check-in. Pode ser desativada no app ou no sistema.

O Antídoto **não** usa Serviço de Acessibilidade, sobreposição de tela (overlay),
VPN nem `QUERY_ALL_PACKAGES`.

## 4. Retenção e exclusão

Como os dados ficam apenas no dispositivo, você tem controle total: desinstalar o
app ou limpar os dados do app remove todas as informações. Revogar a permissão de
uso interrompe imediatamente a coleta local.

## 5. Crianças e adolescentes

O app não coleta dados pessoais e é adequado à classificação indicativa L (livre).

## 6. Conformidade com a LGPD

Como não há tratamento de dados pessoais em servidores nem compartilhamento com
terceiros, o Antídoto foi projetado para minimizar ao máximo o tratamento de dados
pessoais, em linha com os princípios da Lei Geral de Proteção de Dados (Lei nº
13.709/2018): finalidade, necessidade, transparência e segurança.

## 7. Alterações nesta política

Mudanças relevantes serão refletidas neste documento e na data de atualização
acima.

## 8. Contato

Dúvidas sobre privacidade: Kairos Tecnologia — abra uma issue no repositório do
projeto.
