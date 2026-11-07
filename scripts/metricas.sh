#!/usr/bin/env bash
# Métricas estáticas del código de producción.
#   Complejidad ciclomática, líneas y parámetros por método: lizard (pip install lizard)
#   Duplicación de código: jscpd (npx jscpd)
# Uso: scripts/metricas.sh [carpeta-de-salida]
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
SALIDA=${1:-docs/evidencias}
mkdir -p "$SALIDA"
lizard -l java -C 10 -L 40 -a 5 src/main/java > "$SALIDA/lizard.txt" || true
npx --yes jscpd@4.3.0 --min-lines 5 --min-tokens 40 --reporters console \
    --format java src/main/java > "$SALIDA/jscpd.txt" || true
echo "Métricas en $SALIDA/lizard.txt y $SALIDA/jscpd.txt"
