# Implementation TODO

## Phase 1: User & Authentication

- [ ] Create User domain entity and repository
- [ ] Implement user registration endpoint
- [ ] Implement user login with JWT
- [ ] Add Spring Security configuration
- [ ] Create UserError sealed interface

## Phase 2: Payment Methods

- [ ] Create PaymentMethod domain entity
- [ ] Implement save payment method endpoint
- [ ] Implement list user payment methods endpoint
- [ ] Implement delete payment method endpoint
- [ ] Add Stripe/PayPal tokenization integration

## Phase 3: Inventory Operations

- [ ] Add version field to Inventory for optimistic locking
- [ ] Implement reserve inventory method
- [ ] Implement release inventory method (compensation)
- [ ] Implement confirm inventory method
- [ ] Add inventory business rule validations

## Phase 4: Order Domain

- [ ] Create Order domain entity with OrderLine
- [ ] Create OrderError sealed interface
- [ ] Implement create order endpoint (reserve inventory only)
- [ ] Implement get order by id endpoint
- [ ] Implement list user orders endpoint

## Phase 5: Payment Processing

- [ ] Create Payment domain entity
- [ ] Implement payment processing service
- [ ] Implement process order payment endpoint
- [ ] Add payment idempotency handling
- [ ] Handle payment failure compensation

## Phase 6: Order Scenarios

- [ ] Scenario 5: Create order happy path
- [ ] Scenario 6: Order fails - insufficient inventory
- [ ] Scenario 7: Order fails - payment rejected (with compensation)
- [ ] Scenario 8: Handle concurrent orders with optimistic locking
- [ ] Add domain events (OrderPaid, OrderFailed)

## Phase 7: Testing & Polish

- [ ] Write integration tests for order flow
- [ ] Write tests for compensation scenarios
- [ ] Write tests for concurrent order handling
- [ ] Add API documentation (OpenAPI/Swagger)
