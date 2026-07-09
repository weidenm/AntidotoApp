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

## 🚀 Começando

### Requisitos
- Android Studio Iguana+ (ou mais novo)
- JDK 17
- Android SDK 31+ (API Level 12)
- Gradle 8.3+

### Setup Local

```bash
# Clone o repositório
git clone https://github.com/weidenm/AntidotoApp.git
cd AntidotoApp

# Build
./gradlew build

# Rodar no emulador
./gradlew installDebug
adb shell am start -n com.antidoto/.MainActivity
```

### Primeiro Commit & Push

O projeto está em `claude/app-phased-plan-mvp-3fupmj`. Trabalhe nessa branch:

```bash
git checkout claude/app-phased-plan-mvp-3fupmj
# ... make changes ...
git add .
git commit -m "[Phase 1.0] Infra & setup: Android project with Kotlin + Compose + Room"
git push origin claude/app-phased-plan-mvp-3fupmj
```

## 📚 Arquitetura

Ver [`CLAUDE.md`](./CLAUDE.md) para:
- Estrutura de pacotes
- Convenções de código
- Room database setup
- State management (ViewModel + Compose)
- Testing strategy
- Play Store compliance

**TL;DR:** Arquitetura em camadas (UI → Domain → Data), Hilt DI, Compose, Room + SQLite local-first.

## 🔄 Desenvolvimento por Fases

### ✅ Fase 1.0 — Infra & Setup (Semana 1)
- Android project com Kotlin + Jetpack Compose
- Theme, colors, typography
- CI/CD (GitHub Actions)
- CLAUDE.md documentation

**Status:** Completa

### ⏭️ Fases Futuras
- **Fase 1.1:** Data Layer (Room, UsageStatsManager)
- **Fase 1.2:** Dashboard Core (visualização de uso)
- **Fase 1.3:** Habit Loop (check-in, metas, streaks)
- **Fase 1.4:** Lições & Trilha (alfabetização)
- **Fase 1.5:** Polish & App Store (testes, acessibilidade, build)

Ver [`/root/.claude/plans/`](./root/.claude/plans/) para o plano completo.

## 🧪 Testes

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requer emulador)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

## 📦 Build para Release

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (com ProGuard, precisa de keystore)
./gradlew assembleRelease -Pandroid.injected.signing.store.file=release.keystore \
  -Pandroid.injected.signing.store.password=... \
  -Pandroid.injected.signing.key.alias=... \
  -Pandroid.injected.signing.key.password=...
```

## 🔒 Permissões

**Permitidas:**
- `PACKAGE_USAGE_STATS` — Digital Wellbeing (dados de uso)
- `POST_NOTIFICATIONS` — Lembretes diários

**Proibidas (Play Store compliance):**
- Nada de `AccessibilityService` (reservado para apps de acessibilidade)
- Nada de overlays (`SYSTEM_ALERT_WINDOW`)
- Nada de VPN local ou bloqueio forçado

Ver [`CLAUDE.md#permissions-policy`](./CLAUDE.md#permissions-policy) para detalhes.

## 📲 Play Store

O MVP será enviado para a Play Store como **"Digital Wellbeing & Controls"** (categoria oficial):
- Título: Antídoto — Treinador de Imunidade Algorítmica
- Descrição: Entenda a manipulação algorítmica, visualize seu uso, crie metas
- Privacidade: 100% local-first, LGPD-compliant
- Target: Brasil, português-BR

## 🤝 Contribuindo

Esse é um projeto de prova de conceito desenvolvido via Claude Code. Para mudanças:
1. Branch da feature a partir de `claude/app-phased-plan-mvp-3fupmj` ou `main`
2. Atomic commits com mensagens descritivas
3. Self-review antes de PR
4. Merge via PR (squash-merge OK)

Não há aberto para contribuições externas no MVP; isso pode mudar pós-lançamento.

## 📖 Documentação

- **[CLAUDE.md](./CLAUDE.md)** — Arquitetura, convenções, permissões, testing, Play Store checklist
- **PRD** — Visão de produto, fases, roadmap (veja notas internas)

## 📄 Licença

MIT License. Ver [`LICENSE`](./LICENSE) para detalhes.

---

**Versão:** 1.0 (MVP Phase 1.0)  
**Última atualização:** 09/07/2026  
**Mantido por:** Weiden (Kairos Tecnologia)