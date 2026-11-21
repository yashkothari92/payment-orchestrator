package com.upskill.payment_orchestrator.idempotency;

public sealed interface StartResult {
    record Started() implements StartResult {}  // process payment
    record InProgress() implements StartResult {} // 409
    record Completed(int responseCode, String responseBody) implements StartResult{} // replay stored response
    record HashMismatch() implements StartResult{} // 422
}
