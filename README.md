# Antídoto — Treinador de Imunidade Algorítmica

[![Build Status](https://github.com/weidenm/AntidotoApp/actions/workflows/ci.yml/badge.svg)](https://github.com/weidenm/AntidotoApp/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> Uma ferramenta de bem-estar digital para usuários brasileiros: alfabetização sobre manipulação algorítmica + consciência de uso + privacidade 100% local-first.

## 📱 O que é?

**Antídoto não é um bloqueador.** É um treinador de consciência: ajuda você a entender os mecanismos manipulativos dos apps, visualizar seu próprio uso de atenção, e criar metas gentis — sem culpa.

### Diferenciais
- **Único no Brasil:** Alinhado ao momento regulatório (ECA Digital)
- **Local-first:** Nenhum dado sai do seu celular
- **Educativo:** 15 micro-lições sobre design manipulativo
- **Gentil:** Nunca coercitivo; toda fricção é consensual

## ✨ Funcionalidades (MVP)

Navegação por 5 abas: **Início · Check-in · Metas · Lições · Ajustes**.

- **Painel de atenção** (Início): tempo de tela de hoje, "Custo de Atenção" projetado no ano (horas, livros não lidos, cursos não feitos), gráfico de uso na semana e detalhamento por app.
- **Check-in gentil**: registro de humor + gatilho, com sequência (streak) de dias.
- **Metas por app**: limites semanais por aplicativo.
- **Trilha de alfabetização**: 15 micro-lições com desbloqueio sequencial e acompanhamento de conclusão.
- **Ajustes**: lembrete diário de check-in (opcional) e atalho para o acesso ao uso.
- **Sincronização em background** do uso via WorkManager, tudo em um banco local (Room/SQLite).

## 🚀 Começando

### Requisitos
- Android Studio Iguana+ (ou mais novo)
- JDK 17
- Android SDK: `compileSdk`/`targetSdk` 34, `minSdk` 31
- Gradle 8.4 (via wrapper — não precisa instalar)

### Setup Local

```bash
# Clone o repositório
git clone https://github.com/weidenm/AntidotoApp.git
cd AntidotoApp

# (opcional) Aponte o SDK do Android
echo "sdk.dir=/caminho/para/Android/sdk" > local.properties

# Build
./gradlew build

# Rodar no emulador/dispositivo
./gradlew installDebug
adb shell am start -n com.antidoto/.MainActivity
```

## 📚 Arquitetura

Ver [`CLAUDE.md`](./CLAUDE.md) para estrutura de pacotes, convenções, Room, state management, testing e Play Store compliance.

**TL;DR:** Arquitetura em camadas (UI → Domain → Data), Hilt DI, Jetpack Compose (Material 3), Navigation Compose, Room + SQLite local-first e WorkManager.

```
app/src/main/kotlin/com/antidoto/
├── ui/            # Compose: navigation, screens, components, viewmodels, theme
├── domain/        # model + usecase (lógica pura, testável)
├── data/          # db (entities/dao), repository, settings, lessons, usage
├── notifications/ # canal, helper e agendador do lembrete
└── workers/       # SyncUsageWorker, CheckInReminderWorker (Hilt + WorkManager)
```

## 🔄 Desenvolvimento por Fases (MVP)

| Fase | Escopo | Status |
| --- | --- | --- |
| 1.0 | Infra & Setup (Kotlin, Compose, CI, tema) | ✅ Completa |
| 1.1 | Data Layer (Room, UsageStatsManager, workers) | ✅ Completa |
| 1.2 | Dashboard Core (custo de atenção, gráfico, apps) | ✅ Completa |
| 1.3 | Habit Loop (check-in, metas, streaks, lembrete) | ✅ Completa |
| 1.4 | Lições & Trilha (15 micro-lições) | ✅ Completa |
| 1.5 | Polish & App Store (testes, a11y, assinatura, loja) | ✅ Completa |

**MVP feature-complete.** Restam apenas passos manuais de pré-lançamento (teste em dispositivo físico e submissão à Play Store). Roadmap pós-MVP em [`CLAUDE.md`](./CLAUDE.md): Fase 2 (Atalho de Intenção), Fase 3 (importador LGPD), Fase 4 (monetização/iOS).

## 🧪 Testes & Qualidade

```bash
# Testes unitários (JVM)
./gradlew testDebugUnitTest

# Cobertura (JaCoCo) — relatório em app/build/reports/jacoco/jacocoTestReport/html
./gradlew jacocoTestReport

# Testes instrumentados de banco (Room in-memory; requer emulador/dispositivo)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

Cobertura de instruções nas camadas `domain/` + `data/` ≈ **65%** (meta ≥ 60%).

## 📦 Build para Release

A assinatura de release é lida de `keystore.properties` (gitignored) **ou** das variáveis de ambiente `ANTIDOTO_*`. Sem keystore configurado, o release cai para assinatura de debug (apenas para validação; **não** publicável). Ver [`keystore.properties.example`](./keystore.properties.example).

```bash
# Local: copie keystore.properties.example -> keystore.properties e preencha
# CI/Console: exporte ANTIDOTO_STORE_FILE / ANTIDOTO_STORE_PASSWORD /
#             ANTIDOTO_KEY_ALIAS / ANTIDOTO_KEY_PASSWORD

./gradlew assembleRelease   # APK (com R8/ProGuard)
./gradlew bundleRelease     # App Bundle (.aab) para a Play Store
```

## 🔒 Permissões

**Permitidas:**
- `PACKAGE_USAGE_STATS` — bem-estar digital (estatísticas de uso), concedida nas Configurações
- `POST_NOTIFICATIONS` — lembrete diário de check-in (opcional)

**Proibidas (Play Store compliance):**
- Nada de `AccessibilityService` (reservado para apps de acessibilidade)
- Nada de overlays (`SYSTEM_ALERT_WINDOW`)
- Nada de VPN local ou bloqueio forçado
- Nada de `QUERY_ALL_PACKAGES` — visibilidade via `<queries>` de apps lançáveis

Ver [`CLAUDE.md#permissions-policy`](./CLAUDE.md#permissions-policy) para detalhes.

## 📲 Play Store & Privacidade

- **Ficha da loja (pt-BR):** [`fastlane/metadata/android/pt-BR/`](./fastlane/metadata/android/pt-BR)
- **Checklist de publicação:** [`docs/PLAY_STORE.md`](./docs/PLAY_STORE.md)
- **Política de privacidade (LGPD):** [`PRIVACY_POLICY.md`](./PRIVACY_POLICY.md) — 100% local-first, sem servidores, sem rastreadores
- **Categoria:** "Digital Wellbeing & Controls"; classificação Livre (L); target Brasil/pt-BR

## 🤝 Contribuindo

1. Crie uma branch de feature a partir de `main`
2. Atomic commits com mensagens descritivas (`[Fase X.Y] ...`)
3. Garanta `./gradlew build` verde (inclui testes + lint) antes do PR
4. Self-review e abra o PR para `main`

Não há abertura para contribuições externas no MVP; isso pode mudar pós-lançamento.

## 📖 Documentação

- **[CLAUDE.md](./CLAUDE.md)** — Arquitetura, convenções, permissões, testing, Play Store checklist
- **[CHANGELOG.md](./CHANGELOG.md)** — Histórico por fase
- **[docs/PLAY_STORE.md](./docs/PLAY_STORE.md)** — Checklist de publicação
- **[PRIVACY_POLICY.md](./PRIVACY_POLICY.md)** — Política de privacidade (LGPD)

## 📄 Licença

MIT License. Ver [`LICENSE`](./LICENSE) para detalhes.

---

**Versão:** 1.0.0-mvp (Fases 1.0–1.5 completas)  
**Última atualização:** 08/08/2026  
**Mantido por:** Weiden (Kairos Tecnologia)
