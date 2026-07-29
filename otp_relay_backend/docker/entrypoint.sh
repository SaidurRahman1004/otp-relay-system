#!/bin/sh
set -e

python - <<'PY'
import os
import time
from urllib.parse import urlparse

import psycopg

database_url = os.environ.get("DATABASE_URL")
if not database_url:
    raise SystemExit("DATABASE_URL is required.")

parsed = urlparse(database_url)
for attempt in range(60):
    try:
        conn = psycopg.connect(database_url, connect_timeout=2)
        conn.close()
        break
    except psycopg.OperationalError:
        if attempt == 59:
            raise
        print(f"Waiting for PostgreSQL at {parsed.hostname}:{parsed.port or 5432}...")
        time.sleep(1)
PY

exec "$@"
