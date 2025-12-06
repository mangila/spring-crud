# Test Module

Purpose
- Provide live test suites and guidance for validating the running application beyond unit/integration tests in code.
- Focus areas: End‑to‑End (E2E), Load/Performance, and basic Security testing.

Prerequisites
- A running instance of the API (local or remote)
- Tooling as needed per suite (e.g., Venom, Newman, k6)

Scope and philosophy
- Tests here target a running environment (local, staging, pre‑prod, or prod) to verify real behavior and integrations.
- Keep suites small, deterministic, and environment‑aware via variables (base URL, credentials, etc.).

E2E testing
- Recommended: Venom https://github.com/ovh/venom
  - YAML‑driven scenarios; easy to version control and parameterize.
- Alternative: Postman + Newman https://github.com/postmanlabs/newman
- If a browser UI exists, consider a separate suite using:
  - Selenium https://www.selenium.dev/
  - Selenide (Java) https://selenide.org/
  - Playwright https://playwright.dev/

Running examples (conceptual)
- Venom:
  - `venom run ./e2e` (where `./e2e` contains YAML test suites and an env file with `API_BASE_URL` etc.)
- Newman:
  - `newman run collection.json -e environment.json`

Load & performance testing
- Gauge system behavior under sustained and peak load.
- Tools:
  - Gatling https://gatling.io/
  - hey https://github.com/rakyll/hey
  - oha https://github.com/hatoo/oha
  - k6 https://k6.io/

Security and basic penetration checks
- Objectives:
  - Identify obvious misconfigurations and information disclosures.
  - Validate headers and TLS where applicable; check rate limiting and auth flows.
- Tools:
  - Burp Suite https://portswigger.net/burp

Repository conventions
- Place E2E scenarios under `test/e2e/`, load scripts under `test/load/`, and security checks under `test/security/`.
- Use environment files to decouple target hosts and credentials from the test logic.
- Keep generated reports under `test/reports/` and avoid committing large artifacts.