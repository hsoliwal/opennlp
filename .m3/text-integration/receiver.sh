#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
# Thin receiver: canonical reusable recipe source remains in hsoliwal/com.synexia.
set -euo pipefail
root="$(cd "${1:?repository root}" && pwd)"
mode="${2:-verify}"
module="$root/.m3/text-integration"
packet="$module/target/canonical"
for dir in "$root/.m3" "$module" "$module/target" "$packet"; do
  [[ ! -L "$dir" ]] || { echo "symlink refused: $dir" >&2; exit 2; }
done
mkdir -p "$packet"
while IFS=$'\t' read -r ref source expected dest extra; do
  [[ "$ref" =~ ^[0-9a-f]{40}$ && "$expected" =~ ^[0-9a-f]{64}$ && -z "$extra" ]] || exit 2
  for path in "$source" "$dest"; do
    [[ "$path" =~ ^[a-zA-Z0-9_./-]+$ && "$path" != /* && "$path" != *..* ]] || exit 2
  done
  target="$packet/$dest"
  mkdir -p "$(dirname "$target")"
  tmp="$(mktemp "$packet/receive.XXXXXXXX")"
  if [[ -n "${M3_CANONICAL_CHECKOUT:-}" ]]; then
    cp -- "$M3_CANONICAL_CHECKOUT/$source" "$tmp"
  else
    curl --fail --silent --show-error --location --proto '=https' --proto-redir '=https' \
      --max-time 60 --max-filesize 200000 \
      "https://raw.githubusercontent.com/hsoliwal/com.synexia/$ref/$source" -o "$tmp"
  fi
  actual="$(sha256sum "$tmp" | cut -d' ' -f1)"
  if [[ "$actual" != "$expected" ]]; then rm -f "$tmp"; echo "canonical source drift: $source" >&2; exit 2; fi
  mv -- "$tmp" "$target"
done < "$module/source-packet.tsv"
exec bash "$packet/qualify.sh" "$root" "$mode" "$packet"
