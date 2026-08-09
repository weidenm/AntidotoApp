# Play Store — Checklist de Publicação (MVP)

Guia de release para o Antídoto, alinhado ao `CLAUDE.md` (Play Store Compliance).

## Ficha da loja
- **Título:** `fastlane/metadata/android/pt-BR/title.txt`
- **Descrição curta:** `fastlane/metadata/android/pt-BR/short_description.txt`
- **Descrição completa:** `fastlane/metadata/android/pt-BR/full_description.txt`
- **Categoria:** Saúde e fitness / Estilo de vida ("Digital Wellbeing & Controls")
- **Classificação indicativa:** Livre (L) — sem anúncios, sem conteúdo sensível
- **Idioma-alvo:** Português (Brasil)
- **Política de privacidade (URL pública):** hospedar `PRIVACY_POLICY.md`

## Conformidade de permissões
- [x] Manifesto declara apenas `PACKAGE_USAGE_STATS` + `POST_NOTIFICATIONS`
- [x] Sem `AccessibilityService`, sem `SYSTEM_ALERT_WINDOW` (overlay), sem VPN
- [x] Sem `QUERY_ALL_PACKAGES` — visibilidade via `<queries>` de apps lançáveis
- [x] Fluxo de consentimento in-app antes do deep-link para as Configurações
- [ ] Declaração de uso de `PACKAGE_USAGE_STATS` no formulário da Play Console

## Build de release
1. Gere/pegue o keystore e configure a assinatura (ver `keystore.properties.example`):
   - Local: crie `keystore.properties` (gitignored).
   - CI/Console: use as variáveis `ANTIDOTO_STORE_FILE`, `ANTIDOTO_STORE_PASSWORD`, `ANTIDOTO_KEY_ALIAS`, `ANTIDOTO_KEY_PASSWORD`.
2. Gere o App Bundle assinado:
   ```bash
   ./gradlew bundleRelease
   ```
   Sem keystore configurado, o release cai para assinatura de debug (apenas para validação; **não** publicável).
3. Valide o R8/ProGuard: `./gradlew assembleRelease` deve concluir e o APK abrir sem crash.

## Qualidade
- [x] `./gradlew build` (debug + release/R8 + testes + lint) verde
- [x] Testes unitários (domain + data ≥ 60% de cobertura — ver `jacocoTestReport`)
- [x] Testes instrumentados de banco (Room in-memory)
- [ ] Vídeo de review de ~30s (dashboard + check-in)
- [ ] Teste final em dispositivo físico

## Cobertura de testes
```bash
./gradlew jacocoTestReport
# Relatório: app/build/reports/jacoco/jacocoTestReport/html/index.html
```
