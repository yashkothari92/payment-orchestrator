# Payment Orchestrator

![CI](https://github.com/yashkothari92/payment-orchestrator/actions/workflows/ci.yml/badge.svg)

Idempotent payment orchestration service built with Java 21 and Spring Boot.

## Problem
Network timeouts, client retries, and duplicate webhooks can cause
double charges or lost payments. This service guarantees that a payment
request is processed exactly once from the customer's point of view.

## Status
Week 1: design + idempotency core (in progress)
