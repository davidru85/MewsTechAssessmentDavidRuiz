#!/usr/bin/env bash
# Architecture guardrails for the feature-isolated Clean Architecture.
#
# Enforces two rules from project-structure-blueprint.md / ARCHITECTURE.md:
#   1. :feature:<name>:domain must be pure Kotlin — no Android/Compose/Hilt/data imports.
#   2. No cross-feature dependencies — a feature must not import another feature's package.
#
# This is intentionally a no-op until feature modules exist; it activates
# automatically the moment the first :feature:<name> slice lands.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FEATURE_DIR="$ROOT/feature"
FAIL=0

if [[ ! -d "$FEATURE_DIR" ]]; then
  echo "✓ Architecture check: no feature/ modules yet — nothing to verify."
  exit 0
fi

# Rule 1: domain purity — production domain source must not import Android,
# Compose, or Hilt/Dagger. Only src/main is checked: test sources legitimately
# use test-only libraries. javax.inject (JSR-330) and kotlinx.coroutines are
# allowed in domain — they are pure-JVM and used by use cases / repository
# contracts.
FORBIDDEN_DOMAIN='^\s*import\s+(android\.|androidx\.|dagger\.|com\.google\.dagger\.)'
while IFS= read -r -d '' file; do
  hits="$(grep -nE "$FORBIDDEN_DOMAIN" "$file" || true)"
  if [[ -n "$hits" ]]; then
    echo "✗ Domain purity violation in: ${file#"$ROOT"/}"
    printf '%s\n' "$hits" | sed 's/^/    /'
    FAIL=1
  fi
done < <(find "$FEATURE_DIR" -path '*/domain/src/main/*' -name '*.kt' -print0 2>/dev/null)

# Rule 2: no cross-feature imports.
# For each feature <name>, flag files importing com.mews.guestroom.feature.<other>.
for fpath in "$FEATURE_DIR"/*/; do
  [[ -d "$fpath" ]] || continue
  name="$(basename "$fpath")"
  while IFS= read -r -d '' file; do
    hits="$(grep -nE '^\s*import\s+com\.mews\.guestroom\.feature\.[a-zA-Z0-9_]+' "$file" \
        | grep -vE "com\.mews\.guestroom\.feature\.${name}\b" || true)"
    if [[ -n "$hits" ]]; then
      echo "✗ Cross-feature import in: ${file#"$ROOT"/}"
      printf '%s\n' "$hits" | sed 's/^/    /'
      FAIL=1
    fi
  done < <(find "$fpath" -path '*/src/*' -name '*.kt' -print0 2>/dev/null)
done

if [[ "$FAIL" -ne 0 ]]; then
  echo ""
  echo "Architecture rules violated. See ARCHITECTURE.md / project-structure-blueprint.md."
  exit 1
fi

echo "✓ Architecture check passed: domain purity + no cross-feature imports."
