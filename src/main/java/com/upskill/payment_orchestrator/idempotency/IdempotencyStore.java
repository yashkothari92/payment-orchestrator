package com.upskill.payment_orchestrator.idempotency;

public interface IdempotencyStore {
    StartResult tryStart(String merchantId, String idempotencyKey, String requestHash);
    boolean takeOverIfStale(String merchantId, String idempotencyKey);
    void complete(String merchantId, String idempotencyKey, int httpCode, String responseBody);
}
