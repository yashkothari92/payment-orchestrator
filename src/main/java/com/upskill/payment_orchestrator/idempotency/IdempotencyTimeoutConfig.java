package com.upskill.payment_orchestrator.idempotency;

import java.time.Duration;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "timeout")
@Validated 
public record IdempotencyTimeoutConfig (@NotNull Duration pspTimeout, @NotNull Duration lockMargin) {
    public Duration lockTimeout() {
        return pspTimeout.plus(lockMargin);
    }

    public IdempotencyTimeoutConfig {
        Objects.requireNonNull(pspTimeout, "timeout.psp-timeout is required");
        Objects.requireNonNull(lockMargin, "timeout.lock-margin is required");
        
        if(pspTimeout.isZero() || pspTimeout.isNegative()) {
            throw new IllegalArgumentException("timeout.psp-timeout must be positive");
        }

        if(lockMargin.isZero() || lockMargin.isNegative()) {
            throw new IllegalArgumentException("timeout.lock-margin must be positive");
        }
    }
}
