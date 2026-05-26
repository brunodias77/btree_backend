btree_server/
├── pom.xml
├── README.md
├── docker-compose.yml
├── .env.example
├── .gitignore
│
├── docs/
│   ├── architecture/
│   │   ├── overview.md
│   │   ├── bounded-contexts.md
│   │   ├── dependency-rules.md
│   │   ├── database-schema.md
│   │   └── module-boundaries.md
│   ├── adr/
│   │   └── 0001-modular-monolith.md
│   └── database/
│       ├── schemas.md
│       ├── partitions.md
│       └── migrations.md
│
└── modules/
│
├── shared/
│   ├── pom.xml
│   │
│   ├── shared-kernel/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/shared/
│   │       │   ├── domain/
│   │       │   │   ├── AggregateRoot.java
│   │       │   │   ├── Entity.java
│   │       │   │   ├── ValueObject.java
│   │       │   │   ├── Identifier.java
│   │       │   │   └── DomainEvent.java
│   │       │   ├── enums/
│   │       │   │   ├── ProductStatus.java
│   │       │   │   ├── StockMovementType.java
│   │       │   │   ├── CartStatus.java
│   │       │   │   ├── OrderStatus.java
│   │       │   │   ├── ShippingMethod.java
│   │       │   │   ├── PaymentMethodType.java
│   │       │   │   ├── PaymentStatus.java
│   │       │   │   ├── TransactionType.java
│   │       │   │   ├── CouponType.java
│   │       │   │   ├── CouponScope.java
│   │       │   │   ├── CouponStatus.java
│   │       │   │   ├── CardBrand.java
│   │       │   │   ├── CancellationReason.java
│   │       │   │   ├── TokenType.java
│   │       │   │   ├── RefundStatus.java
│   │       │   │   └── ChargebackStatus.java
│   │       │   ├── exception/
│   │       │   │   ├── DomainException.java
│   │       │   │   ├── NotFoundException.java
│   │       │   │   └── ValidationException.java
│   │       │   ├── gateway/
│   │       │   │   ├── OutboxGateway.java
│   │       │   │   ├── AuditLogGateway.java
│   │       │   │   └── TransactionGateway.java
│   │       │   ├── pagination/
│   │       │   │   ├── Page.java
│   │       │   │   └── SearchQuery.java
│   │       │   ├── usecase/
│   │       │   │   ├── UseCase.java
│   │       │   │   ├── UnitUseCase.java
│   │       │   │   └── QueryUseCase.java
│   │       │   ├── validation/
│   │       │   │   ├── Validator.java
│   │       │   │   ├── ValidationHandler.java
│   │       │   │   └── Notification.java
│   │       │   └── utils/
│   │       └── test/java/com/btree/shared/
│   │
│   └── shared-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/shared/infrastructure/
│           │   │   ├── audit/
│           │   │   │   ├── persistence/
│           │   │   │   │   ├── AuditLogJpaEntity.java
│           │   │   │   │   └── AuditLogJpaRepository.java
│           │   │   │   └── AuditLogPostgresGateway.java
│           │   │   ├── outbox/
│           │   │   │   ├── persistence/
│           │   │   │   │   ├── DomainEventJpaEntity.java
│           │   │   │   │   ├── DomainEventJpaRepository.java
│           │   │   │   │   ├── ProcessedEventJpaEntity.java
│           │   │   │   │   └── ProcessedEventJpaRepository.java
│           │   │   │   ├── OutboxEventPostgresGateway.java
│           │   │   │   ├── OutboxPublisher.java
│           │   │   │   └── OutboxConsumer.java
│           │   │   ├── id/
│           │   │   │   └── UuidGenerator.java
│           │   │   ├── json/
│           │   │   │   ├── JsonMapper.java
│           │   │   │   └── JacksonJsonMapper.java
│           │   │   ├── persistence/
│           │   │   │   ├── converter/
│           │   │   │   └── type/
│           │   │   └── config/
│           │   │       └── SharedInfrastructureConfig.java
│           │   └── resources/
│           │       └── db/migration/shared/
│           │           ├── V001__create_schemas.sql
│           │           ├── V002__create_extensions.sql
│           │           ├── V003__create_uuid_v7_function.sql
│           │           ├── V004__create_shared_enums.sql
│           │           ├── V005__create_domain_events.sql
│           │           ├── V006__create_processed_events.sql
│           │           ├── V007__create_audit_logs.sql
│           │           └── V008__create_enum_casts.sql
│           └── test/java/com/btree/shared/infrastructure/
│
├── users/
│   ├── pom.xml
│   │
│   ├── users-domain/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/users/domain/
│   │       │   ├── model/
│   │       │   │   ├── User.java
│   │       │   │   ├── Role.java
│   │       │   │   ├── UserRole.java
│   │       │   │   ├── UserAuthority.java
│   │       │   │   ├── SocialLogin.java
│   │       │   │   ├── UserToken.java
│   │       │   │   ├── Profile.java
│   │       │   │   ├── Address.java
│   │       │   │   ├── LoginHistory.java
│   │       │   │   ├── Session.java
│   │       │   │   ├── Notification.java
│   │       │   │   └── NotificationPreference.java
│   │       │   ├── valueobject/
│   │       │   │   ├── UserId.java
│   │       │   │   ├── RoleId.java
│   │       │   │   ├── Email.java
│   │       │   │   ├── Username.java
│   │       │   │   ├── PasswordHash.java
│   │       │   │   ├── PhoneNumber.java
│   │       │   │   ├── Cpf.java
│   │       │   │   └── AddressId.java
│   │       │   ├── event/
│   │       │   │   ├── UserRegisteredEvent.java
│   │       │   │   ├── EmailVerifiedEvent.java
│   │       │   │   ├── PasswordResetRequestedEvent.java
│   │       │   │   └── UserLoggedInEvent.java
│   │       │   ├── gateway/
│   │       │   │   ├── UserGateway.java
│   │       │   │   ├── RoleGateway.java
│   │       │   │   ├── ProfileGateway.java
│   │       │   │   ├── AddressGateway.java
│   │       │   │   ├── SessionGateway.java
│   │       │   │   ├── TokenGateway.java
│   │       │   │   └── NotificationGateway.java
│   │       │   ├── service/
│   │       │   │   ├── PasswordPolicy.java
│   │       │   │   └── AccountLockPolicy.java
│   │       │   └── exception/
│   │       └── test/java/com/btree/users/domain/
│   │
│   ├── users-application/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/users/application/
│   │       │   ├── usecase/
│   │       │   │   ├── createuser/
│   │       │   │   ├── authenticateuser/
│   │       │   │   ├── verifyemail/
│   │       │   │   ├── requestpasswordreset/
│   │       │   │   ├── resetpassword/
│   │       │   │   ├── updateprofile/
│   │       │   │   ├── addaddress/
│   │       │   │   ├── updateaddress/
│   │       │   │   ├── removeaddress/
│   │       │   │   ├── createnotification/
│   │       │   │   └── marknotificationread/
│   │       │   ├── query/
│   │       │   │   ├── getuserbyid/
│   │       │   │   ├── getprofile/
│   │       │   │   └── listaddresses/
│   │       │   └── presenter/
│   │       └── test/java/com/btree/users/application/
│   │
│   └── users-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/users/infrastructure/
│           │   │   ├── persistence/
│           │   │   │   ├── entity/
│           │   │   │   ├── repository/
│           │   │   │   ├── mapper/
│           │   │   │   └── gateway/
│           │   │   ├── web/
│           │   │   │   ├── controller/
│           │   │   │   ├── request/
│           │   │   │   ├── response/
│           │   │   │   └── mapper/
│           │   │   ├── security/
│           │   │   ├── email/
│           │   │   └── config/
│           │   │       └── UsersModuleConfig.java
│           │   └── resources/
│           │       └── db/migration/users/
│           │           ├── V010__create_users_tables.sql
│           │           ├── V011__create_users_indexes.sql
│           │           ├── V012__create_login_history_partitions.sql
│           │           └── V013__seed_default_roles.sql
│           └── test/java/com/btree/users/infrastructure/
│
├── catalog/
│   ├── pom.xml
│   │
│   ├── catalog-domain/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/catalog/domain/
│   │       │   ├── model/
│   │       │   │   ├── Category.java
│   │       │   │   ├── Brand.java
│   │       │   │   ├── Product.java
│   │       │   │   ├── ProductImage.java
│   │       │   │   ├── StockMovement.java
│   │       │   │   ├── StockReservation.java
│   │       │   │   ├── ProductReview.java
│   │       │   │   └── UserFavorite.java
│   │       │   ├── valueobject/
│   │       │   │   ├── ProductId.java
│   │       │   │   ├── CategoryId.java
│   │       │   │   ├── BrandId.java
│   │       │   │   ├── Sku.java
│   │       │   │   ├── Slug.java
│   │       │   │   ├── Money.java
│   │       │   │   ├── Dimensions.java
│   │       │   │   └── StockQuantity.java
│   │       │   ├── event/
│   │       │   │   ├── ProductCreatedEvent.java
│   │       │   │   ├── ProductPublishedEvent.java
│   │       │   │   ├── ProductArchivedEvent.java
│   │       │   │   ├── StockReservedEvent.java
│   │       │   │   ├── StockReleasedEvent.java
│   │       │   │   └── StockAdjustedEvent.java
│   │       │   ├── gateway/
│   │       │   │   ├── ProductGateway.java
│   │       │   │   ├── CategoryGateway.java
│   │       │   │   ├── BrandGateway.java
│   │       │   │   ├── StockMovementGateway.java
│   │       │   │   ├── StockReservationGateway.java
│   │       │   │   ├── ProductReviewGateway.java
│   │       │   │   └── UserFavoriteGateway.java
│   │       │   ├── service/
│   │       │   │   ├── StockReservationService.java
│   │       │   │   └── ProductPricingService.java
│   │       │   └── exception/
│   │       └── test/java/com/btree/catalog/domain/
│   │
│   ├── catalog-application/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/catalog/application/
│   │       │   ├── usecase/
│   │       │   │   ├── createcategory/
│   │       │   │   ├── updatecategory/
│   │       │   │   ├── createbrand/
│   │       │   │   ├── updatebrand/
│   │       │   │   ├── createproduct/
│   │       │   │   ├── updateproduct/
│   │       │   │   ├── publishproduct/
│   │       │   │   ├── archiveproduct/
│   │       │   │   ├── addproductimage/
│   │       │   │   ├── reserveStock/
│   │       │   │   ├── releaseStock/
│   │       │   │   ├── registerStockMovement/
│   │       │   │   ├── createProductReview/
│   │       │   │   └── favoriteProduct/
│   │       │   ├── query/
│   │       │   │   ├── searchproducts/
│   │       │   │   ├── getproductbyid/
│   │       │   │   ├── getproductbyslug/
│   │       │   │   ├── listcategories/
│   │       │   │   └── listbrands/
│   │       │   └── presenter/
│   │       └── test/java/com/btree/catalog/application/
│   │
│   └── catalog-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/catalog/infrastructure/
│           │   │   ├── persistence/
│           │   │   │   ├── entity/
│           │   │   │   ├── repository/
│           │   │   │   ├── mapper/
│           │   │   │   └── gateway/
│           │   │   ├── web/
│           │   │   │   ├── controller/
│           │   │   │   ├── request/
│           │   │   │   ├── response/
│           │   │   │   └── mapper/
│           │   │   ├── search/
│           │   │   ├── image/
│           │   │   └── config/
│           │   │       └── CatalogModuleConfig.java
│           │   └── resources/
│           │       └── db/migration/catalog/
│           │           ├── V020__create_catalog_tables.sql
│           │           ├── V021__create_catalog_indexes.sql
│           │           └── V022__create_stock_movement_partitions.sql
│           └── test/java/com/btree/catalog/infrastructure/
│
├── cart/
│   ├── pom.xml
│   │
│   ├── cart-domain/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/cart/domain/
│   │       │   ├── model/
│   │       │   │   ├── Cart.java
│   │       │   │   ├── CartItem.java
│   │       │   │   ├── CartActivityLog.java
│   │       │   │   └── SavedCart.java
│   │       │   ├── valueobject/
│   │       │   │   ├── CartId.java
│   │       │   │   ├── CartItemId.java
│   │       │   │   ├── SessionId.java
│   │       │   │   └── CartTotals.java
│   │       │   ├── event/
│   │       │   │   ├── CartCreatedEvent.java
│   │       │   │   ├── ItemAddedToCartEvent.java
│   │       │   │   ├── ItemRemovedFromCartEvent.java
│   │       │   │   ├── CouponAppliedToCartEvent.java
│   │       │   │   └── CartConvertedEvent.java
│   │       │   ├── gateway/
│   │       │   │   ├── CartGateway.java
│   │       │   │   ├── CartItemGateway.java
│   │       │   │   └── SavedCartGateway.java
│   │       │   ├── service/
│   │       │   │   ├── CartPricingService.java
│   │       │   │   └── CartExpirationPolicy.java
│   │       │   └── exception/
│   │       └── test/java/com/btree/cart/domain/
│   │
│   ├── cart-application/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/cart/application/
│   │       │   ├── usecase/
│   │       │   │   ├── createcart/
│   │       │   │   ├── additem/
│   │       │   │   ├── updateitemquantity/
│   │       │   │   ├── removeitem/
│   │       │   │   ├── clearcart/
│   │       │   │   ├── applycoupon/
│   │       │   │   ├── removecoupon/
│   │       │   │   ├── savecart/
│   │       │   │   └── convertcart/
│   │       │   ├── query/
│   │       │   │   ├── getcart/
│   │       │   │   └── listsavedcarts/
│   │       │   └── presenter/
│   │       └── test/java/com/btree/cart/application/
│   │
│   └── cart-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/cart/infrastructure/
│           │   │   ├── persistence/
│           │   │   │   ├── entity/
│           │   │   │   ├── repository/
│           │   │   │   ├── mapper/
│           │   │   │   └── gateway/
│           │   │   ├── web/
│           │   │   │   ├── controller/
│           │   │   │   ├── request/
│           │   │   │   ├── response/
│           │   │   │   └── mapper/
│           │   │   └── config/
│           │   │       └── CartModuleConfig.java
│           │   └── resources/
│           │       └── db/migration/cart/
│           │           ├── V030__create_cart_tables.sql
│           │           └── V031__create_cart_indexes.sql
│           └── test/java/com/btree/cart/infrastructure/
│
├── orders/
│   ├── pom.xml
│   │
│   ├── orders-domain/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/orders/domain/
│   │       │   ├── model/
│   │       │   │   ├── Order.java
│   │       │   │   ├── OrderItem.java
│   │       │   │   ├── OrderStatusHistory.java
│   │       │   │   ├── TrackingEvent.java
│   │       │   │   ├── Invoice.java
│   │       │   │   └── OrderRefund.java
│   │       │   ├── valueobject/
│   │       │   │   ├── OrderId.java
│   │       │   │   ├── OrderNumber.java
│   │       │   │   ├── OrderItemId.java
│   │       │   │   ├── ShippingAddress.java
│   │       │   │   ├── BillingAddress.java
│   │       │   │   ├── OrderTotals.java
│   │       │   │   └── TrackingCode.java
│   │       │   ├── event/
│   │       │   │   ├── OrderCreatedEvent.java
│   │       │   │   ├── OrderConfirmedEvent.java
│   │       │   │   ├── OrderCancelledEvent.java
│   │       │   │   ├── OrderShippedEvent.java
│   │       │   │   ├── OrderDeliveredEvent.java
│   │       │   │   └── OrderRefundRequestedEvent.java
│   │       │   ├── gateway/
│   │       │   │   ├── OrderGateway.java
│   │       │   │   ├── OrderItemGateway.java
│   │       │   │   ├── InvoiceGateway.java
│   │       │   │   ├── TrackingEventGateway.java
│   │       │   │   └── OrderRefundGateway.java
│   │       │   ├── service/
│   │       │   │   ├── OrderNumberGenerator.java
│   │       │   │   ├── OrderPricingService.java
│   │       │   │   └── OrderStatusPolicy.java
│   │       │   └── exception/
│   │       └── test/java/com/btree/orders/domain/
│   │
│   ├── orders-application/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/orders/application/
│   │       │   ├── usecase/
│   │       │   │   ├── createorder/
│   │       │   │   ├── confirmorder/
│   │       │   │   ├── cancelorder/
│   │       │   │   ├── processorder/
│   │       │   │   ├── shiporder/
│   │       │   │   ├── deliverorder/
│   │       │   │   ├── createinvoice/
│   │       │   │   ├── addtrackingevent/
│   │       │   │   └── requestrefund/
│   │       │   ├── query/
│   │       │   │   ├── getorderbyid/
│   │       │   │   ├── getorderbyordernumber/
│   │       │   │   ├── listordersbyuser/
│   │       │   │   └── listorderhistory/
│   │       │   └── presenter/
│   │       └── test/java/com/btree/orders/application/
│   │
│   └── orders-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/orders/infrastructure/
│           │   │   ├── persistence/
│           │   │   │   ├── entity/
│           │   │   │   ├── repository/
│           │   │   │   ├── mapper/
│           │   │   │   └── gateway/
│           │   │   ├── web/
│           │   │   │   ├── controller/
│           │   │   │   ├── request/
│           │   │   │   ├── response/
│           │   │   │   └── mapper/
│           │   │   ├── invoice/
│           │   │   ├── shipping/
│           │   │   └── config/
│           │   │       └── OrdersModuleConfig.java
│           │   └── resources/
│           │       └── db/migration/orders/
│           │           ├── V040__create_order_number_function.sql
│           │           ├── V041__create_orders_tables.sql
│           │           └── V042__create_orders_indexes.sql
│           └── test/java/com/btree/orders/infrastructure/
│
├── payments/
│   ├── pom.xml
│   │
│   ├── payments-domain/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/payments/domain/
│   │       │   ├── model/
│   │       │   │   ├── UserPaymentMethod.java
│   │       │   │   ├── Payment.java
│   │       │   │   ├── PaymentTransaction.java
│   │       │   │   ├── PaymentRefund.java
│   │       │   │   ├── Chargeback.java
│   │       │   │   └── PaymentWebhook.java
│   │       │   ├── valueobject/
│   │       │   │   ├── PaymentId.java
│   │       │   │   ├── PaymentMethodId.java
│   │       │   │   ├── TransactionId.java
│   │       │   │   ├── IdempotencyKey.java
│   │       │   │   ├── GatewayTransactionId.java
│   │       │   │   ├── CardInfo.java
│   │       │   │   └── PixInfo.java
│   │       │   ├── event/
│   │       │   │   ├── PaymentCreatedEvent.java
│   │       │   │   ├── PaymentAuthorizedEvent.java
│   │       │   │   ├── PaymentCapturedEvent.java
│   │       │   │   ├── PaymentFailedEvent.java
│   │       │   │   ├── PaymentRefundedEvent.java
│   │       │   │   └── ChargebackOpenedEvent.java
│   │       │   ├── gateway/
│   │       │   │   ├── PaymentGateway.java
│   │       │   │   ├── UserPaymentMethodGateway.java
│   │       │   │   ├── PaymentTransactionGateway.java
│   │       │   │   ├── PaymentRefundGateway.java
│   │       │   │   ├── ChargebackGateway.java
│   │       │   │   ├── PaymentWebhookGateway.java
│   │       │   │   └── PaymentProviderGateway.java
│   │       │   ├── service/
│   │       │   │   ├── PaymentCapturePolicy.java
│   │       │   │   ├── RefundPolicy.java
│   │       │   │   └── FraudAnalysisPolicy.java
│   │       │   └── exception/
│   │       └── test/java/com/btree/payments/domain/
│   │
│   ├── payments-application/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/payments/application/
│   │       │   ├── usecase/
│   │       │   │   ├── createpayment/
│   │       │   │   ├── authorizepayment/
│   │       │   │   ├── capturepayment/
│   │       │   │   ├── cancelpayment/
│   │       │   │   ├── refundpayment/
│   │       │   │   ├── savepaymentmethod/
│   │       │   │   ├── processwebhook/
│   │       │   │   └── handlechargeback/
│   │       │   ├── query/
│   │       │   │   ├── getpaymentbyid/
│   │       │   │   ├── getpaymentbyorder/
│   │       │   │   └── listpaymentmethods/
│   │       │   └── presenter/
│   │       └── test/java/com/btree/payments/application/
│   │
│   └── payments-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/payments/infrastructure/
│           │   │   ├── persistence/
│           │   │   │   ├── entity/
│           │   │   │   ├── repository/
│           │   │   │   ├── mapper/
│           │   │   │   └── gateway/
│           │   │   ├── provider/
│           │   │   │   ├── stripe/
│           │   │   │   ├── pix/
│           │   │   │   ├── boleto/
│           │   │   │   └── fake/
│           │   │   ├── web/
│           │   │   │   ├── controller/
│           │   │   │   ├── webhook/
│           │   │   │   ├── request/
│           │   │   │   ├── response/
│           │   │   │   └── mapper/
│           │   │   └── config/
│           │   │       └── PaymentsModuleConfig.java
│           │   └── resources/
│           │       └── db/migration/payments/
│           │           ├── V050__create_payments_tables.sql
│           │           ├── V051__create_payments_indexes.sql
│           │           └── V052__create_webhook_partitions.sql
│           └── test/java/com/btree/payments/infrastructure/
│
├── coupons/
│   ├── pom.xml
│   │
│   ├── coupons-domain/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/coupons/domain/
│   │       │   ├── model/
│   │       │   │   ├── Coupon.java
│   │       │   │   ├── EligibleCategory.java
│   │       │   │   ├── EligibleProduct.java
│   │       │   │   ├── EligibleUser.java
│   │       │   │   ├── CouponUsage.java
│   │       │   │   └── CouponReservation.java
│   │       │   ├── valueobject/
│   │       │   │   ├── CouponId.java
│   │       │   │   ├── CouponCode.java
│   │       │   │   ├── DiscountValue.java
│   │       │   │   ├── CouponValidity.java
│   │       │   │   └── UsageLimit.java
│   │       │   ├── event/
│   │       │   │   ├── CouponCreatedEvent.java
│   │       │   │   ├── CouponActivatedEvent.java
│   │       │   │   ├── CouponReservedEvent.java
│   │       │   │   ├── CouponReleasedEvent.java
│   │       │   │   └── CouponUsedEvent.java
│   │       │   ├── gateway/
│   │       │   │   ├── CouponGateway.java
│   │       │   │   ├── CouponReservationGateway.java
│   │       │   │   └── CouponUsageGateway.java
│   │       │   ├── service/
│   │       │   │   ├── CouponEligibilityService.java
│   │       │   │   ├── CouponDiscountCalculator.java
│   │       │   │   └── CouponUsagePolicy.java
│   │       │   └── exception/
│   │       └── test/java/com/btree/coupons/domain/
│   │
│   ├── coupons-application/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/java/com/btree/coupons/application/
│   │       │   ├── usecase/
│   │       │   │   ├── createcoupon/
│   │       │   │   ├── updatecoupon/
│   │       │   │   ├── activatecoupon/
│   │       │   │   ├── pausecoupon/
│   │       │   │   ├── validatecoupon/
│   │       │   │   ├── reservecoupon/
│   │       │   │   ├── releasecoupon/
│   │       │   │   └── consumecoupon/
│   │       │   ├── query/
│   │       │   │   ├── getcouponbycode/
│   │       │   │   ├── listcoupons/
│   │       │   │   └── listcouponusages/
│   │       │   └── presenter/
│   │       └── test/java/com/btree/coupons/application/
│   │
│   └── coupons-infrastructure/
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/com/btree/coupons/infrastructure/
│           │   │   ├── persistence/
│           │   │   │   ├── entity/
│           │   │   │   ├── repository/
│           │   │   │   ├── mapper/
│           │   │   │   └── gateway/
│           │   │   ├── web/
│           │   │   │   ├── controller/
│           │   │   │   ├── request/
│           │   │   │   ├── response/
│           │   │   │   └── mapper/
│           │   │   └── config/
│           │   │       └── CouponsModuleConfig.java
│           │   └── resources/
│           │       └── db/migration/coupons/
│           │           ├── V060__create_coupons_tables.sql
│           │           └── V061__create_coupons_indexes.sql
│           └── test/java/com/btree/coupons/infrastructure/
│
└── api/
├── pom.xml
└── src/
├── main/
│   ├── java/com/btree/api/
│   │   ├── BtreeApplication.java
│   │   ├── config/
│   │   │   ├── ApplicationConfig.java
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JacksonConfig.java
│   │   │   ├── JpaConfig.java
│   │   │   ├── FlywayConfig.java
│   │   │   └── ScheduledJobsConfig.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ApiErrorResponse.java
│   │   ├── health/
│   │   │   └── HealthController.java
│   │   └── lifecycle/
│   │       └── ApplicationStartupLogger.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       ├── application-prod.yml
│       └── logback-spring.xml
└── test/java/com/btree/api/