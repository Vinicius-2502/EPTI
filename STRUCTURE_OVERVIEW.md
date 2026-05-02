# EPTI Backend - Estrutura Completa do Projeto

## 📁 **Estrutura de Diretórios**

```
EPTI/BackEnd/
├── 📄 pom.xml                              # Maven configuration
├── 📄 Dockerfile                           # Docker container definition
├── 📄 docker-compose.yml                   # Multi-container setup
├── 📄 Makefile                            # Build automation
├── 📄 .gitignore                          # Git ignored files
├── 📄 .editorconfig                        # Editor configuration
├── 📄 README.md                           # Project documentation
├── 📄 CHANGELOG.md                         # Version history
├── 📄 CONTRIBUTING.md                      # Development guidelines
├── 📄 LICENSE                              # MIT License
├── 📄 PROJECT_CONTEXT.md                    # Complete project context
├── 📄 INTEGRATION_GUIDE.md                # Frontend integration guide
├── 📄 PROJECT_ROADMAP.md                  # Future development plan
├── 📄 STRUCTURE_OVERVIEW.md               # This file
├── 📄 mvw                                 # Maven wrapper script
├── 📁 .mvn/                              # Maven wrapper files
│   └── 📁 wrapper/
│       ├── 📄 maven-wrapper.jar
│       └── 📄 maven-wrapper.properties
├── 📁 docker/                             # Docker initialization
│   └── 📄 init-db.sql                     # Database init script
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/epti/backend/
│   │   │   ├── 📄 BackendApplication.java      # Main application class
│   │   │   ├── 📁 config/                   # Configuration classes
│   │   │   │   ├── 📄 WebConfig.java
│   │   │   │   ├── 📄 JpaConfig.java
│   │   │   │   ├── 📄 SecurityConfig.java
│   │   │   │   ├── 📄 RateLimitingConfig.java
│   │   │   │   └── 📄 CacheConfig.java
│   │   │   ├── 📁 controller/               # REST controllers
│   │   │   │   ├── 📄 AuthController.java
│   │   │   │   ├── 📄 ProductController.java
│   │   │   │   ├── 📄 KitController.java
│   │   │   │   ├── 📄 CartController.java
│   │   │   │   ├── 📄 OrderController.java
│   │   │   │   └── 📄 HealthController.java
│   │   │   ├── 📁 dto/                     # Data Transfer Objects
│   │   │   │   ├── 📄 BaseResponse.java
│   │   │   │   ├── 📁 auth/
│   │   │   │   │   ├── 📄 LoginRequest.java
│   │   │   │   │   ├── 📄 LoginResponse.java
│   │   │   │   │   └── 📄 RegisterRequest.java
│   │   │   │   └── 📁 ecommerce/
│   │   │   │       ├── 📄 AddToCartRequest.java
│   │   │   │       ├── 📄 UpdateCartRequest.java
│   │   │   │       ├── 📄 OrderRequest.java
│   │   │   │       └── 📄 MarkOrderPaidRequest.java
│   │   │   ├── 📁 exception/               # Custom exceptions
│   │   │   │   ├── 📄 GlobalExceptionHandler.java
│   │   │   │   ├── 📄 ResourceNotFoundException.java
│   │   │   │   └── 📄 BadRequestException.java
│   │   │   ├── 📁 model/                   # JPA entities
│   │   │   │   ├── 📄 BaseEntity.java
│   │   │   │   ├── 📄 User.java
│   │   │   │   ├── 📄 Role.java
│   │   │   │   ├── 📄 Product.java
│   │   │   │   ├── 📄 Kit.java
│   │   │   │   ├── 📄 CartItem.java
│   │   │   │   ├── 📄 Order.java
│   │   │   │   ├── 📄 OrderItem.java
│   │   │   │   └── 📁 enums/
│   │   │   │       ├── 📄 Turma.java
│   │   │   │       └── 📄 PaymentStatus.java
│   │   │   ├── 📁 repository/              # Data access layer
│   │   │   │   ├── 📄 UserRepository.java
│   │   │   │   ├── 📄 RoleRepository.java
│   │   │   │   ├── 📄 ProductRepository.java
│   │   │   │   ├── 📄 KitRepository.java
│   │   │   │   ├── 📄 CartItemRepository.java
│   │   │   │   ├── 📄 OrderRepository.java
│   │   │   │   └── 📄 OrderItemRepository.java
│   │   │   ├── 📁 security/                # Security components
│   │   │   │   ├── 📄 JwtTokenProvider.java
│   │   │   │   ├── 📄 JwtAuthenticationEntryPoint.java
│   │   │   │   ├── 📄 JwtAuthenticationFilter.java
│   │   │   │   └── 📄 RateLimitingFilter.java
│   │   │   ├── 📁 service/                 # Business logic
│   │   │   │   ├── 📄 UserService.java
│   │   │   │   ├── 📄 ProductService.java
│   │   │   │   ├── 📄 KitService.java
│   │   │   │   ├── 📄 CartService.java
│   │   │   │   ├── 📄 OrderService.java
│   │   │   │   └── 📄 AuditService.java
│   │   │   └── 📁 util/                   # Utility classes
│   │   │       └── 📄 Constants.java
│   │   └── 📁 resources/
│   │       ├── 📄 application.yml           # Main configuration
│   │       ├── 📄 application-dev.yml       # Development config
│   │       ├── 📄 application-test.yml      # Test configuration
│   │       ├── 📄 application-local.yml     # Local overrides
│   │       └── 📁 db/migration/          # Database migrations
│   │           ├── 📄 V1__Create_initial_tables.sql
│   │           ├── 📄 V2__Add_ecommerce_tables.sql
│   │           └── 📄 V3__Insert_sample_data.sql
│   └── 📁 test/
│       ├── 📁 java/com/epti/backend/
│       │   ├── 📄 BackendApplicationTests.java
│       │   ├── 📁 integration/
│       │   │   └── 📄 AuthControllerIntegrationTest.java
│       │   └── 📁 service/
│       │       └── 📄 UserServiceTest.java
│       └── 📁 resources/
│           └── 📄 application-test.yml
```

## 🗄️ **Tabelas do Banco de Dados**

### **Schema Completo**
```sql
-- Users and Authentication
users (id, username, email, password, full_name, turma, has_paid, enabled, created_at, updated_at, version)
roles (id, name, description, created_at, updated_at, version)
user_roles (user_id, role_id) -- Junction table

-- Products and Kits
products (id, name, description, price, image_url, available, kit_discount_percentage, created_at, updated_at, version)
kits (id, name, description, price, image_url, available, created_at, updated_at, version)
product_turmas (product_id, turma) -- Product restrictions by turma
kit_turmas (kit_id, turma) -- Kit restrictions by turma
kit_products (kit_id, product_id) -- Products in kits

-- Shopping Cart
cart_items (id, user_id, product_id, kit_id, quantity, unit_price, total_price, created_at, updated_at, version)

-- Orders and Payments
orders (id, user_id, order_number, payment_status, total_amount, pix_key, payment_date, payment_proof_url, notes, created_at, updated_at, version)
order_items (id, order_id, product_id, kit_id, quantity, unit_price, total_price, created_at, updated_at, version)
```

## 🚀 **API Routes Mapping**

### **Authentication Routes**
```
POST   /api/auth/login              # User login
POST   /api/auth/register           # User registration
GET    /api/auth/me                 # Current user profile
```

### **Product Routes**
```
GET    /api/products                    # All available products
GET    /api/products/my-turma            # Products for user's turma
GET    /api/products/turma/{turma}        # Products for specific turma
GET    /api/products/search               # Search products
GET    /api/products/{id}                # Product details
GET    /api/products/{id}/validate       # Validate for user
POST   /api/products                    # Create product (Admin)
PUT    /api/products/{id}                # Update product (Admin)
DELETE /api/products/{id}                # Delete product (Admin)
PATCH  /api/products/{id}/toggle-availability # Toggle availability (Admin)
```

### **Kit Routes**
```
GET    /api/kits                        # All available kits
GET    /api/kits/my-turma              # Kits for user's turma
GET    /api/kits/{id}                   # Kit details
POST   /api/kits                        # Create kit (Admin)
```

### **Cart Routes**
```
GET    /api/cart                         # User's cart
GET    /api/cart/count                   # Cart item count
POST   /api/cart/add                    # Add item to cart
PUT    /api/cart/items/{id}              # Update item quantity
DELETE /api/cart/items/{id}              # Remove item from cart
DELETE /api/cart/clear                   # Clear cart
GET    /api/cart/checkout                # Cart for checkout
```

### **Order Routes**
```
POST   /api/orders/create                # Create order from cart
GET    /api/orders/my-orders             # User's orders
GET    /api/orders/my-orders/pending      # User's pending order
POST   /api/orders/{id}/payment-proof   # Upload payment proof
GET    /api/orders                       # All orders (Admin)
GET    /api/orders/status/{status}        # Orders by status (Admin)
POST   /api/orders/{id}/mark-paid       # Mark as paid (Admin)
POST   /api/orders/{id}/notes           # Add notes (Admin)
GET    /api/orders/unpaid-users          # Unpaid users (Admin)
GET    /api/orders/statistics            # Order statistics (Admin)
GET    /api/orders/{id}                 # Order details
```

### **Public Routes**
```
GET    /api/public/health                 # Health check
GET    /api/public/info                  # API information
```

## 🔐 **Security Flow**

### **Authentication Flow**
```
1. User sends credentials to /api/auth/login
2. Backend validates credentials
3. Backend generates JWT token
4. Backend returns token with user data
5. Frontend stores token
6. Frontend includes token in Authorization header
7. Backend validates token on protected routes
```

### **Authorization Matrix**
```
Endpoint                | Anonymous | User | Admin
------------------------|-----------|-------|-------
POST /auth/login         |    ✅     |   ❌   |   ❌
POST /auth/register        |    ✅     |   ❌   |   ❌
GET /auth/me             |    ❌     |   ✅   |   ✅
GET /products            |    ✅     |   ✅   |   ✅
GET /products/my-turma  |    ❌     |   ✅   |   ✅
POST /cart/add           |    ❌     |   ✅   |   ❌
GET /orders/my-orders    |    ❌     |   ✅   |   ✅
POST /orders/create      |    ❌     |   ✅   |   ❌
GET /orders              |    ❌     |   ❌   |   ✅
POST /orders/{id}/mark-paid | ❌     |   ❌   |   ✅
```

## 📊 **Data Flow Diagram**

### **E-commerce Flow**
```
User Login
    ↓
Browse Products (filtered by turma)
    ↓
Add to Cart (max 50 items)
    ↓
View Cart (real-time updates)
    ↓
Create Order (status: PENDENTE)
    ↓
Upload Payment Proof
    ↓
Admin Validates Payment
    ↓
Order Status: PAGO
    ↓
User has_paid: true
```

### **Rate Limiting Flow**
```
Request → RateLimitingFilter
    ↓
Check User/IP identifier
    ↓
Check request count in last minute
    ↓
If > 20 requests → Return 429
    ↓
If ≤ 20 requests → Continue to controller
```

### **Cache Flow**
```
Request → Check Cache
    ↓
If cache hit → Return cached data
    ↓
If cache miss → Execute query
    ↓
Store result in cache
    ↓
Return data
```

## 🧪 **Test Structure**

### **Test Categories**
```
src/test/java/
├── 📁 integration/           # Integration tests
│   ├── AuthControllerIntegrationTest.java
│   ├── ProductServiceTest.java
│   └── OrderServiceTest.java
├── 📁 service/               # Unit tests
│   ├── UserServiceTest.java
│   ├── CartServiceTest.java
│   └── PaymentServiceTest.java
└── 📁 resources/            # Test data
    └── application-test.yml
```

### **Test Coverage Areas**
- ✅ Authentication flow
- ✅ Product management
- ✅ Cart operations
- ✅ Order processing
- ✅ Security validations
- ✅ Error handling
- ✅ Rate limiting
- ⏳ Cache behavior
- ⏳ Payment processing

## 🚀 **Deployment Architecture**

### **Development Environment**
```
Developer Machine
├── Spring Boot (port 8080)
├── H2 Database (in-memory)
├── Local cache
└── File system storage
```

### **Production Environment**
```
Production Server
├── Docker Container
├── Spring Boot (port 8080)
├── PostgreSQL Database
├── Redis Cache
├── Nginx (reverse proxy)
└── File storage (S3/MinIO)
```

### **Docker Compose Services**
```yaml
services:
  postgres:  # Database
  backend:   # Spring Boot app
  redis:     # Cache (optional)
```

## 📈 **Performance Considerations**

### **Database Optimizations**
- ✅ Indexes on foreign keys
- ✅ Indexes on search fields
- ✅ Connection pooling (HikariCP)
- ✅ Query optimization
- ⏳ Read replicas for scaling

### **Application Optimizations**
- ✅ Caching layer
- ✅ Rate limiting
- ✅ Async processing where possible
- ✅ Pagination for large datasets
- ⏳ CDN for static assets

### **Monitoring Points**
- ✅ Response times
- ✅ Error rates
- ✅ Database performance
- ✅ Memory usage
- ✅ CPU usage
- ⏳ Business metrics

## 🔄 **CI/CD Pipeline**

### **Build Process**
```yaml
1. Code Checkout
2. Maven Build (mvn clean package)
3. Run Tests (mvn test)
4. Code Quality Check
5. Security Scan
6. Build Docker Image
7. Push to Registry
8. Deploy to Staging
9. Integration Tests
10. Deploy to Production
```

### **Quality Gates**
- ✅ Build must pass
- ✅ Tests must pass (>80% coverage)
- ✅ No critical vulnerabilities
- ✅ Performance benchmarks met
- ⏳ Security scan passed

---

Esta estrutura serve como mapa completo do projeto, facilitando entendimento rápido para novos desenvolvedores e manutenção futura.
