# RK Solutions - Technical Documentation

## 1. Project Configuration

### pom.xml
```xml
```
- **Spring Boot Parent 4.1.1**: Inherits dependency versions and plugins from Spring Boot
- **Java 21**: Uses LTS version with modern features (records, pattern matching, virtual threads)
- **Dependencies**:
  - `spring-boot-starter-web`: REST API foundation (Spring MVC, Tomcat)
  - `spring-boot-starter-data-jpa`: ORM via JPA/Hibernate
  - `spring-boot-starter-security`: Authentication, authorization, password encoding
  - `mysql-connector-j` → **replaced with H2**: In-memory/file database
  - `lombok`: Reduces boilerplate (getters, setters, constructors)
  - `jjwt-api/impl/jwt-jackson`: JWT token generation and validation (v0.12.6)
  - `spring-boot-starter-validation`: Bean validation (JSR-380 annotations)
  - `springdoc-openapi-starter-webmvc-ui`: Auto-generated Swagger/OpenAPI 3 docs
  - `spring-boot-devtools`: Live reload during development
  - `h2`: Database for dev and prod (file-based on Render)

### application.properties
- `spring.datasource.url=jdbc:h2:mem:rksolutions_db`: In-memory H2 for local dev
- `spring.jpa.hibernate.ddl-auto=update`: Auto-creates/updates tables from entities
- `spring.jpa.properties.hibernate.dialect=H2Dialect`: H2-specific SQL dialect
- `spring.h2.console.enabled=true`: Web console accessible at `/h2-console`
- `app.jwt.secret`: 256-bit secret key for HS512 JWT signing
- `app.jwt.expiration-ms=86400000`: Access token valid for 24 hours
- `app.jwt.refresh-expiration-ms=604800000`: Refresh token valid for 7 days
- `springdoc.api-docs.path=/v3/api-docs`: OpenAPI endpoint
- `springdoc.swagger-ui.path=/swagger-ui.html`: Swagger UI HTML interface

### application-prod.properties (Render deployment)
- `spring.datasource.url=jdbc:h2:file:/data/rksolutions_db`: File-based H2 on Render disk
- `spring.h2.console.enabled=false`: Security - disable console in production
- `app.jwt.secret=${JWT_SECRET:...}`: Uses Render environment variable with fallback
- `spring.mail.username=${MAIL_USERNAME:}`: Uses Render environment variables for email

---

## 2. Common Infrastructure

### `common/enums/RoleName.java`
- Defines the four system roles: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_SUPER_ADMIN`
- Used by `Role` entity and `@PreAuthorize` annotations

### `common/enums/UserStatus.java`
- `PENDING`: User registered but awaiting admin approval
- `ACTIVE`: User approved and can login
- `INACTIVE`: User deactivated
- `SUSPENDED`: User blocked by admin
- `REJECTED`: Registration rejected

### `common/enums/ApprovalStatus.java`
- `PENDING`/`APPROVED`/`REJECTED` for the approval workflow

### `common/enums/CustomerStatus.java`
- Lifecycle: `NEW` → `CALLED` → `INTERESTED` → `JOINED`
- Tracks customer journey through the sales pipeline

### `common/enums/OrderStatus.java`
- `PENDING`/`CONFIRMED`/`PROCESSING`/`SHIPPED`/`DELIVERED`/`CANCELLED`/`RETURNED`

### `common/enums/PaymentStatus.java`
- `PENDING`/`SUCCESS`/`FAILED`/`REFUNDED`/`PARTIAL`

### `common/enums/PaymentMethod.java`
- `CASH`/`UPI`/`CARD`/`BANK_TRANSFER`

### `common/enums/CommissionStatus.java`
- `PENDING`/`APPROVED`/`REJECTED`/`PAID` — tracks commission approval lifecycle

### `common/enums/CommissionType.java`
- `MEMBERSHIP`/`HANDBOOK`/`BONUS` — source of commission earnings

### `common/enums/FollowUpStatus.java`
- `PENDING`/`COMPLETED`/`CANCELLED`/`MISSED`

### `common/enums/TransactionType.java`
- `CREDIT`/`DEBIT` for wallet transactions

### `common/enums/ReferenceType.java`
- `COMMISSION`/`ORDER`/`BONUS`/`REFERRAL`/`ADJUSTMENT` — links wallet transactions to source

### `common/enums/ResourceType.java`
- `VIDEO`/`PDF`/`FAQ`/`SALES_SCRIPT`/`POSTER`/`HANDBOOK` for training materials

### `common/enums/NotificationType.java`
- `SYSTEM`/`CUSTOMER`/`COMMISSION`/`ORDER`/`PAYMENT`/`APPOINTMENT`

### `common/enums/ItemType.java`
- `MEMBERSHIP`/`HANDBOOK` — discriminator for order line items

### `common/entity/BaseEntity.java`
```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime createdAt;  // set on insert
    @UpdateTimestamp
    private LocalDateTime updatedAt;  // set on update
    @PrePersist / @PreUpdate hooks for timestamps
}
```
- `@MappedSuperclass`: Not an entity itself, but provides common fields to entities
- `@CreationTimestamp`/`@UpdateTimestamp`: Hibernate auto-manages timestamps
- Every entity (User, Customer, Order, etc.) extends this

### `common/entity/Role.java`
```java
@Entity @Table(name = "roles")
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private RoleName name;
}
```
- `EnumType.STRING`: Stores enum name in DB (not ordinal), preventing data corruption if enum order changes
- Many-to-many with User via `user_roles` join table

### `common/response/ApiResponse.java`
```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;
}
```
- Generic wrapper for all API responses
- `@JsonInclude(NON_NULL)`: Omits null fields in JSON output
- Static factory methods: `success()`, `success(msg)`, `created()`, `error()`

### `common/exception/GlobalExceptionHandler.java`
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)   → 404
    @ExceptionHandler(BadRequestException.class)        → 400
    @ExceptionHandler(UnauthorizedException.class)       → 401
    @ExceptionHandler(AccessDeniedException.class)       → 403
    @ExceptionHandler(BadCredentialsException.class)     → 401
    @ExceptionHandler(MethodArgumentNotValidException.class) → 400 with field errors
    @ExceptionHandler(Exception.class)                  → 500
}
```
- `@RestControllerAdvice`: Intercepts exceptions from controllers, returns JSON error responses
- Returns structured `{timestamp, status, error, message}` JSON for all errors

---

## 3. Authentication & Authorization Module

### `config/SecurityConfig.java`
```java
@EnableWebSecurity
@EnableMethodSecurity
```
- `@EnableWebSecurity`: Activates Spring Security's web security support
- `@EnableMethodSecurity`: Enables `@PreAuthorize` annotations on controller methods
- **SecurityFilterChain**:
  - CSRF disabled (stateless JWT doesn't need it)
  - Session creation policy: `STATELESS` — no HTTP session, every request needs a JWT
  - Public endpoints: `/api/v1/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`
  - URL-level protection:
    - `/api/v1/admin/**` → requires `ROLE_ADMIN`
    - `/api/v1/super-admin/**` → requires `ROLE_SUPER_ADMIN`
    - All other requests → authenticated
  - `JwtAuthenticationFilter` added before `UsernamePasswordAuthenticationFilter`
- **PasswordEncoder**: `BCryptPasswordEncoder` for password hashing
- **AuthenticationManager**: Exposes the auth manager for `AuthServiceImpl.login()`

### `auth/security/JwtService.java`
```java
@Service
public class JwtService {
    private SecretKey getSigningKey()  // Uses HS512 with the app secret
    public String generateAccessToken(UserDetails details)  // 24h expiry
    public String generateRefreshToken(UserDetails details) // 7d expiry
    public String extractUsername(String token)  // Parses subject from JWT
    public List<String> extractRoles(String token)  // Extracts roles claim
    private Claims extractAllClaims(String token)  // Uses Jjwt parser with verifyWith()
    public boolean isTokenExpired(String token)  // Checks exp claim
    public boolean validateToken(String token, UserDetails details)  // Username match + non-expired
}
```
- Uses **JJWT 0.12.6** API (`Jwts.builder()`, `Jwts.parser().verifyWith(key)`)
- Roles stored as a `List<String>` in the JWT `roles` claim
- `getSigningKey()` derives HMAC-SHA key from the secret string

### `auth/security/CustomUserDetails.java`
```java
public class CustomUserDetails implements UserDetails {
    private final Long id, String name, email, phone;
    private final Set<RoleName> roles;
    // ... UserDetails methods
}
```
- `fromUser(User)`: Factory method converting JPA `User` entity to Spring Security `UserDetails`
- `getAuthorities()`: Returns `SimpleGrantedAuthority` with role names (e.g., `ROLE_ADMIN`)
- `getUsername()`: Returns email (used as JWT subject)
- `isEnabled()`: Returns true only if user status is `ACTIVE`

### `auth/security/JwtAuthenticationFilter.java`
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain) {
        // 1. Extract Bearer token from Authorization header
        // 2. Parse username from JWT
        // 3. Load UserDetails from DB
        // 4. Validate token (username match + not expired)
        // 5. Build Authentication with roles from JWT
        // 6. Set in SecurityContext
        // 7. Continue filter chain
    }
}
```
- `OncePerRequestFilter`: Ensures the filter runs exactly once per request
- Extracts JWT from `Authorization: Bearer <token>` header
- **Important**: Roles from JWT claims are used directly (not re-fetched from DB) for stateless auth

### `auth/service/AuthServiceImpl.java`
Key methods:
```java
@PostConstruct
public void initRoles()  // Seeds all RoleName values into DB on startup

login(LoginRequest): 
    // 1. Find user by email/phone
    // 2. Check status is ACTIVE (reject PENDING users)
    // 3. Use AuthenticationManager to verify password
    // 4. Generate access + refresh tokens
    // Returns LoginResponse with tokens

sendOtp(OtpRequest): 
    // 1. If user exists and ACTIVE: send OTP to them
    // 2. If user exists but not active: reject
    // 3. If user doesn't exist: store OTP with ROLE_USER for registration
    // Uses in-memory ConcurrentHashMap with 5-min expiry

verifyOtp(OtpVerifyRequest): 
    // Validates OTP from store
    // If user exists and active: return login tokens
    // If user doesn't exist and has name/password: register as PENDING

refreshToken(RefreshTokenRequest): 
    // 1. Extract email from refresh token
    // 2. Validate not expired
    // 3. Generate new access + refresh tokens

register(RegisterRequest): 
    // Creates new User with PENDING status
    // Auto-assigns ROLE_USER
    // Returns UserRegisterResponse (no tokens — user must wait for approval)
```

### Auth API Flow:
```
POST /api/v1/auth/register          → PENDING user created
POST /api/v1/auth/send-otp          → OTP sent (console log)
POST /api/v1/auth/verify-otp        → Login tokens OR registration confirmation
POST /api/v1/auth/login             → Access + refresh tokens (requires ACTIVE user)
POST /api/v1/auth/refresh-token     → New access token
POST /api/v1/auth/logout            → Client-side token clearing
```

---

## 4. User Management Module

### `user/entity/User.java`
```java
@Entity @Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "phone")
})
public class User extends BaseEntity {
    private String name;
    private String phone;        // unique
    private String email;        // unique
    private String password;     // BCrypt hashed
    @ManyToMany(fetch = EAGER) Set<Role> roles;  // EAGER: roles needed immediately for auth
    @Enumerated(STRING) UserStatus status;
    @ManyToOne User manager;     // Hierarchical: manager can have many subordinates
    private String profileImage;
    private Boolean isApprovedByManager;
}
```
- **EAGER fetch**: Roles loaded eagerly because they're needed for `@PreAuthorize` checks
- **Manager relationship**: Self-referencing ManyToOne for team hierarchy

### `user/controller/UserController.java`
Endpoints (all under `/api/v1/users`):
- `POST` → Create user (admin-created users, requires password)
- `GET` → Paginated list with sorting
- `GET /{id}` → Get by ID
- `PUT /{id}` → Full update
- `PATCH /{id}/status` → Change status (e.g., ACTIVE, INACTIVE)
- `DELETE /{id}` → Delete user

### `approval/controller/UserApprovalController.java`
Admin-only endpoints under `/api/v1/admin/users`:
- `GET /pending` → List all PENDING users
- `PATCH /{id}/approve` → Set status to ACTIVE
- `PATCH /{id}/reject` → Set status to REJECTED

---

## 5. Customer Module

### `customer/entity/Customer.java`
```java
@Entity @Table(name = "customers")
public class Customer extends BaseEntity {
    private String name, phone, email, city, interestedIn;
    @Lob String remarks;
    @Enumerated(STRING) CustomerStatus status;
    @ManyToOne User createdBy;    // who added this customer
    @ManyToOne User assignedTo;   // who follows up
    LocalDateTime joinedAt;      // when customer became JOINED
}
```

### `customer/entity/CustomerStatusHistory.java`
- Tracks every status change: old → new, who changed it, remarks
- Enables audit trail for customer journey

### Lifecycle flow:
```
NEW → CALLED → INTERESTED → JOINED → ORDER CREATED → PAYMENT → COMMISSION
```

### `customer/controller/CustomerController.java`
- `GET /{id}/status-history`: Returns audit trail of all status changes

---

## 6. Membership Module

### `membership/entity/MembershipPlan.java`
```java
private String name, description;
private BigDecimal price;
private Integer duration;        // days
private BigDecimal commissionAmount;
private Integer maxCustomers;    // team limit
private Boolean active;
```

### `membership/entity/CustomerMembership.java`
```java
@ManyToOne Customer customer;
@ManyToOne MembershipPlan plan;
private LocalDateTime startDate, expiryDate;
private BigDecimal totalAmount, discountAmount;
enum MembershipStatus { ACTIVE, EXPIRED, CANCELLED }
```

---

## 7. Order Module

### `order/entity/Order.java`
```java
@Column(unique = true) String orderNumber;  // format: ORD-YYYYMMDD-XXXXXXXX
@ManyToOne Customer customer;
@ManyToOne User createdBy;                 // partner who created the order
BigDecimal totalAmount, discountAmount;
@Enumerated OrderStatus orderStatus;
@Enumerated PaymentStatus paymentStatus;
@OneToMany(cascade = ALL) List<OrderItem> items;
```

### `order/entity/OrderItem.java`
```java
@Enumerated(STRING) ItemType itemType;  // MEMBERSHIP or HANDBOOK
private Long itemId;                     // FK to handbook or membership_plan
private Integer quantity;
BigDecimal price, total;
```

### Order creation flow:
1. Validate customer exists
2. For each `OrderItemRequest`, resolve price from `Handbook` or `MembershipPlan`
3. Calculate item total = price × quantity
4. Set order total = sum of all item totals
5. Save order with generated order number

---

## 8. Payment Module

### `payment/entity/Payment.java`
```java
@ManyToOne Order order;
@ManyToOne Customer customer;
BigDecimal amount;
@Enumerated PaymentMethod paymentMethod;  // CASH, UPI, CARD, BANK_TRANSFER
@Column(unique = true) String transactionId;
@Enumerated PaymentStatus paymentStatus;
LocalDateTime paidAt;
```

### Key behavior:
When `paymentStatus` is set to `SUCCESS`:
- `Order.paymentStatus` is automatically updated to `SUCCESS`
- Transaction ID is auto-generated if not provided

---

## 9. Commission Module

### `commission/entity/Commission.java`
```java
@ManyToOne User user;      // the partner earning commission
@ManyToOne Customer customer;
@ManyToOne Order order;
@ManyToOne Payment payment;
@Enumerated CommissionType type;     // MEMBERSHIP or HANDBOOK
BigDecimal amount;
@Enumerated CommissionStatus status; // PENDING → APPROVED → PAID
@ManyToOne User approvedBy;           // admin who approved
LocalDateTime approvedAt;
```

### Commission calculation flow:
```
Payment SUCCESS
    → CommissionService.calculateCommissionFromPayment(paymentId)
    → For each OrderItem in Order:
        - HANDBOOK: commission = handbook.commissionAmount × quantity
        - MEMBERSHIP: commission = plan.commissionAmount (fixed)
    → Create Commission records with PENDING status
    → Notification sent to partner
```

### Approving commission (key method):
```java
@Transactional
public CommissionResponse approveCommission(Long id, Long approvedById) {
    // 1. Verify commission is in PENDING state
    // 2. Set status = APPROVED, record approver + timestamp
    // 3. Call WalletService.creditWallet() — credits partner's wallet
    // 4. Create Notification for partner
    // All in one transaction: rolls back if any step fails
}
```

---

## 10. Wallet Module

### `wallet/entity/Wallet.java`
```java
@OneToOne User user;    // unique: one wallet per user
BigDecimal balance;
BigDecimal totalMembershipEarnings;
BigDecimal totalHandbookEarnings;
BigDecimal totalBonus;
```

### `wallet/entity/WalletTransaction.java`
```java
@ManyToOne Wallet wallet;
@Enumerated TransactionType transactionType;  // CREDIT or DEBIT
BigDecimal amount;
@Enumerated ReferenceType referenceType;      // COMMISSION, ORDER, BONUS, REFERRAL, ADJUSTMENT
private Long referenceId;                       // FK to source entity
String description;
```

### Wallet behavior:
- `findOrCreateWallet(userId)`: Lazily creates wallet if it doesn't exist
- `creditWallet()`: Increments balance + tracks earnings by source type
- `debitWallet()`: Checks balance → decrements → throws if insufficient

---

## 11. FollowUp Module

### `followup/entity/FollowUp.java`
```java
@ManyToOne Customer customer;
@ManyToOne User assignedTo;     // telecaller responsible
LocalDateTime scheduledAt;
LocalDateTime completedAt;
@Enumerated FollowUpStatus status;
String remarks;
String callType;               // e.g., "CALL", "VISIT", "MESSAGE"
```

---

## 12. Training Module

### `training/entity/TrainingResource.java`
```java
String title;
@Lob String description;
@Enumerated ResourceType resourceType;  // VIDEO, PDF, FAQ, SALES_SCRIPT, POSTER, HANDBOOK
String fileUrl;
String thumbnailUrl;
String category;
@ManyToOne User createdBy;
Boolean active;
```

---

## 13. Notification Module

### `notification/entity/Notification.java`
```java
@ManyToOne User user;
String title;
@Lob String message;
@Enumerated NotificationType type;  // SYSTEM, COMMISSION, ORDER, PAYMENT
Boolean isRead = false;
String referenceType;
Long referenceId;
Boolean isBroadcast = false;
```

### Notification flow:
```
Commission Approved
    → NotificationService.createNotification()
    → Save to MySQL (H2)
    → (In production: also send via Firebase FCM)
```

---

## 14. Dashboard Module

### No database table — aggregates real-time data from all modules.

### Dashboard endpoints:
- `GET /api/v1/dashboard/user` — Partner view: assigned customers, pending calls, pending commission
- `GET /api/v1/dashboard/admin` — Admin overview: total members, today's orders/revenue, pending commission total
- `GET /api/v1/dashboard/manager` — Manager view: team members, team customers, team performance
- `GET /api/v1/dashboard/super-admin` — Full system overview
- `GET /api/v1/dashboard` — Auto-detects role and returns appropriate dashboard

### Response example:
```json
{
  "totalMembers": 18,
  "todayOrders": 32,
  "todayRevenue": 45250,
  "pendingCalls": 12,
  "pendingCommission": 12500,
  "totalCustomers": 150,
  "totalRevenue": 500000,
  "totalWalletBalance": 250000,
  "pendingUsers": 3
}
```

---

## 15. Settings Module

### `settings/entity/AppSettings.java`
Single-row table holding application-wide settings:
- `companyName`, `commissionEnabled`, `defaultCommissionRate`
- `currencyCode`, `paymentSettings` (JSON string), `notificationSettings` (JSON string)
- `otpEnabled`, `maintenanceMode`

---

## 16. Audit Log Module

### `audit/entity/AuditLog.java`
```java
@ManyToOne User user;        // who performed the action
String action;               // e.g., USER_APPROVED, ORDER_UPDATED
String module;               // e.g., USER, ORDER, PAYMENT
Long entityId;               // target entity ID
String oldValue, newValue;  // JSON snapshots
String ipAddress;            // client IP
String userAgent;            // browser/device info
```

---

## 17. Reports Module

### Report types with data sources:
| Report | Data Sources |
|--------|-------------|
| Sales Report | Order, Payment |
| Commission Report | Commission |
| Member Report | User, Commission |
| Performance Report | Order, Commission, Customer |
| Daily Report | Order, Customer, Commission |
| Monthly Report | Order, Customer, Commission |

### Export API:
```http
GET /api/v1/reports/export?reportType=sales&fromDate=2024-01-01&toDate=2024-12-31
```
Returns CSV file with all report data.
```

