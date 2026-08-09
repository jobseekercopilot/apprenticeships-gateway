# Apprenticeships Gateway

This service integrates Job Seeker Copilot with the official Department for
Education Find an apprenticeship Display Advert API v2. It intermittently
fetches vacancies into an atomic in-memory snapshot and serves searches from
that snapshot, following DfE's server-side integration and rate-limit guidance.
Provider pages are never scraped.

The full apprenticeship model preserves all advertised locations, course and
training details, wage, hours, duration, skills, qualifications, dates,
employer, training provider, and official apply/listing URLs.

## Modes and configuration

`EXTERNAL_PROVIDER_MODE=FIXTURE` is the safe local/E2E default. Production
profiles reject fixture mode. `LIVE` mode requires `APPRENTICESHIPS_API_KEY`;
the secret has no repository default and is sent only in the server-side
`Ocp-Apim-Subscription-Key` header. `APPRENTICESHIPS_ENABLED=false` is the kill
switch. Only API version 2 is accepted.

| Variable | Default | Purpose |
| --- | --- | --- |
| `APPRENTICESHIPS_BASE_URL` | DfE vacancies URL | Provider endpoint |
| `APPRENTICESHIPS_API_KEY` | none | Secret subscription key |
| `APPRENTICESHIPS_API_VERSION` | `2` | `X-Version` header |
| `APPRENTICESHIPS_SYNC_PAGE_SIZE` | `100` | Provider page size |
| `APPRENTICESHIPS_SYNC_DELAY_MS` | `900000` | Refresh interval |
| `APPRENTICESHIPS_SYNC_MAX_PAGES` | `150` | Bounded refresh limit |
| `APPRENTICESHIPS_INITIAL_SYNC_DELAY_MS` | `1000` | Initial refresh delay |

## Local verification

```bash
mvn -B clean verify
docker build -t local/apprenticeships-gateway .
```

The internal contract is in `api/openapi.yaml`; runtime mode and snapshot age
are observable at `GET /internal/provider-mode`.
