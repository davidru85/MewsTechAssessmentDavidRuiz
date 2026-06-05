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

# Rule 1: domain purity.
FORBIDDEN_DOMAIN='^\s*import\s+(android\.|androidx\.|dagger\.|com\.google\.dagger\.|javax\.inject\.|kotlinx\.coroutines\.flow\.MutableStateFlow)'
while IFS= read -r -d '' file; do
  if grep -nEq "$FORBIDDEN_DOMAIN" "$file"; then
    echo "✗ Domain purity violation in: ${file#"$ROOT"/}"
    grep -nE "$FORBIDDEN_DOMAIN" "$file" | sed 's/^/    /'
    FAIL=1
  fi
done < <(find "$FEATURE_DIR" -path '*/domain/*' -name '*.kt' -print0 2>/dev/null)

# Rule 2: no cross-feature imports.
# For each feature <name>, flag files importing com.mews.guestroom.feature.<other>.
for fpath in "$FEATURE_DIR"/*/; do
  [[ -d "$fpath" ]] || continue
  name="$(basename "$fpath")"
  while IFS= read -r -d '' file; do
    if grep -nE '^\s*import\s+com\.mews\.guestroom\.feature\.[a-zA-Z0-9_]+' "$file" \
        | grep -vE "com\.mews\.guestroom\.feature\.${name}\b" >/dev/null; then
      echo "✗ Cross-feature import in: ${file#"$ROOT"/}"
      grep -nE '^\s*import\s+com\.mews\.guestroom\.feature\.[a-zA-Z0-9_]+' "$file" \
        | grep -vE "com\.mews\.guestroom\.feature\.${name}\b" | sed 's/^/    /'
      FAIL=1
    fi
  done < <(find "$fpath" -name '*.kt' -print0 2>/dev/null)
done

if [[ "$FAIL" -ne 0 ]]; then
  echo ""
  echo "Architecture rules violated. See ARCHITECTURE.md / project-structure-blueprint.md."
  exit 1
fi

echo "✓ Architecture check passed: domain purity + no cross-feature imports."
