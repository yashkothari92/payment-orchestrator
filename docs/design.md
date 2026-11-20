# Design: Payment Orchestrator

## 1. Problem & Goals
- What breaks without idempotency?
   - Duplicate financial transactions
   - Client times out and retries -> customer charged twice
   - PSP times out but actually charged -> unknown outcome -> another retry double charge
   - Duplicate PSP webhooks -> paymend marked or processed twice
   - Parallel requests with the same key → race, two charges
- Goals:
    - No double charge
    - Safe client retries
    - Recoverable after crash

## 2. Non-goals
- Real PSP integration, refunds, multi-currency (for now)

## 3. Happy-path flow
Client → POST /payments → insert idempotency key (IN_PROGRESS) → create payment → call PSP → SUCCEEDED → mark key `COMPLETED` and store response on the key.

## 4. Failure scenarios
| Scenario | Expected behavior |
|---|---|
| Client retries same key | Return stored response (200) |
| Same key, different body | 422 (Unprocessable Content) |
| PSP times out | 504 Gateway Timeout<br>Set status `PENDING_CONFIRMATION` (not `FAILED`)<br>On retry: query PSP status using payment ID as PSP idempotency key<br>Resolve via webhook or reconciliation job |
| Two parallel requests, same key | 409 Conflict + `Retry-After` while original is in flight<br>Lock expires after 60s so a crashed request can be retried |
| Duplicate PSP webhook | 200, no side effects<br>Deduplicate on webhook event ID (`processed_webhook_events`, unique) |

## 5. Data model
- payments(id, amount, status, idempotency_key, created_at)
- idempotency_key(key, request_hash, status (IN_PROGRESS/COMPLETED), response, locked_until, created_at)
- processed_webhook_events(event_id, payment_id, processed_at)
  
## 6. ADR-001: Idempotency key storage
- **Decision**: Postgres table with unique constraint on the key
- **Alternatives rejected**:
    - Redis: not atomic with payment writes; can lose recent writes on failover
    - In-memory: breaks with multiple instances and is lost on restart
- **Why Postgres**:
    - transaction guarantee: Native - you can save the idempotency key and update payment state in the exact same txn
    - durability: ACID compliant - writes are safely persisted to disk via WAL
    - Auditability & Debugging: Payment disputes require clear audit trails. Querying historical idempotency payloads, HTTP status codes, and timestamps in SQL is straightforward compared to inspecting expired keys in Redis
  
- **Consequences**:
    - extra DB load on every request
    - Scheduled job to purge old keys (e.g. after 24h)
