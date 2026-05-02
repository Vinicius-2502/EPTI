# Guia de Integração Frontend-Backend EPTI

## 🚀 **Setup Inicial**

### **Backend**
```bash
# Clonar repositório
git clone <repo-backend>
cd EPTI/BackEnd

# Configurar variáveis de ambiente
cp application-dev.yml.example application-local.yml

# Iniciar backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=development

# Acessar documentação
open http://localhost:8080/api/swagger-ui.html
```

### **Frontend (Angular)**
```bash
# Criar projeto Angular
ng new epti-frontend
cd epti-frontend

# Instalar dependências
npm install @auth0/angular-jwt axios

# Configurar proxy
# Em angular.json ou proxy.conf.json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

## 🔐 **Autenticação**

### **1. Serviço de Autenticação (auth.service.ts)**
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private tokenKey = 'epti_token';

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          if (response.success && response.data.token) {
            localStorage.setItem(this.tokenKey, response.data.token);
          }
        })
      );
  }

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, userData);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      const decoded = jwt_decode(token);
      return decoded.exp > Date.now() / 1000;
    } catch {
      return false;
    }
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  getCurrentUser(): Observable<User> {
    const token = this.getToken();
    if (!token) return throwError(() => new Error('No token found'));
    
    return this.http.get<User>(`${this.apiUrl}/auth/me`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}
```

### **2. Guard de Autenticação (auth.guard.ts)**
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
```

### **3. Interceptor de Token (auth.interceptor.ts)**
```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      req = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
    }
    
    return next.handle(req);
  }
}

// Registrar no app.module.ts
providers: [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }
]
```

## 🛒 **Fluxo de Compra**

### **1. Serviço de Produtos (product.service.ts)**
```typescript
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getMyTurmaProducts(): Observable<Product[]> {
    return this.http.get<any>(`${this.apiUrl}/products/my-turma`)
      .pipe(map(response => response.data));
  }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<any>(`${this.apiUrl}/products`)
      .pipe(map(response => response.data));
  }

  searchProducts(term: string): Observable<Product[]> {
    return this.http.get<any>(`${this.apiUrl}/products/search?name=${term}`)
      .pipe(map(response => response.data));
  }

  validateProduct(productId: number): Observable<Product> {
    return this.http.get<any>(`${this.apiUrl}/products/${productId}/validate`)
      .pipe(map(response => response.data));
  }
}
```

### **2. Serviço de Carrinho (cart.service.ts)**
```typescript
@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api';
  private cartSubject = new BehaviorSubject<CartItem[]>([]);

  constructor(private http: HttpClient) {}

  getCart(): Observable<CartItem[]> {
    return this.http.get<any>(`${this.apiUrl}/cart`)
      .pipe(
        map(response => response.data),
        tap(items => this.cartSubject.next(items))
      );
  }

  addToCart(item: AddToCartRequest): Observable<CartItem> {
    return this.http.post<any>(`${this.apiUrl}/cart/add`, item)
      .pipe(
        map(response => response.data),
        tap(() => this.refreshCart())
      );
  }

  updateCartItem(itemId: number, quantity: number): Observable<CartItem> {
    return this.http.put<any>(`${this.apiUrl}/cart/items/${itemId}`, { quantity })
      .pipe(
        map(response => response.data),
        tap(() => this.refreshCart())
      );
  }

  removeFromCart(itemId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/cart/items/${itemId}`)
      .pipe(
        tap(() => this.refreshCart())
      );
  }

  clearCart(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/cart/clear`)
      .pipe(
        tap(() => this.cartSubject.next([]))
      );
  }

  getCartCount(): Observable<number> {
    return this.http.get<any>(`${this.apiUrl}/cart/count`)
      .pipe(map(response => response.data.count));
  }

  private refreshCart(): void {
    this.getCart().subscribe();
  }

  getCartItems(): Observable<CartItem[]> {
    return this.cartSubject.asObservable();
  }
}
```

### **3. Componente de Carrinho (cart.component.ts)**
```typescript
@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  cartTotal = 0;
  itemCount = 0;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.loadCart();
    this.cartService.getCartItems().subscribe(items => {
      this.cartItems = items;
      this.calculateTotal();
    });
  }

  loadCart(): void {
    this.cartService.getCart().subscribe();
  }

  updateQuantity(itemId: number, quantity: number): void {
    if (quantity > 0) {
      this.cartService.updateCartItem(itemId, quantity).subscribe();
    }
  }

  removeItem(itemId: number): void {
    this.cartService.removeFromCart(itemId).subscribe();
  }

  clearCart(): void {
    if (confirm('Tem certeza que deseja limpar o carrinho?')) {
      this.cartService.clearCart().subscribe();
    }
  }

  calculateTotal(): void {
    this.cartTotal = this.cartItems.reduce((sum, item) => sum + (item.totalPrice || 0), 0);
    this.itemCount = this.cartItems.reduce((sum, item) => sum + item.quantity, 0);
  }

  checkout(): void {
    // Navegar para página de checkout
    // this.router.navigate(['/checkout']);
  }
}
```

### **4. Template do Carrinho (cart.component.html)**
```html
<div class="cart-container">
  <h2>Meu Carrinho</h2>
  
  <div *ngIf="cartItems.length === 0" class="empty-cart">
    <p>Seu carrinho está vazio</p>
    <button routerLink="/products">Continuar Comprando</button>
  </div>

  <div *ngIf="cartItems.length > 0" class="cart-items">
    <div *ngFor="let item of cartItems" class="cart-item">
      <div class="item-info">
        <h3>{{ item.itemName }}</h3>
        <p>Preço unitário: R$ {{ item.unitPrice | number:'2.2-2' }}</p>
      </div>
      
      <div class="item-quantity">
        <label>Quantidade:</label>
        <input 
          type="number" 
          [(ngModel)]="item.quantity" 
          (change)="updateQuantity(item.id, item.quantity)"
          min="1" 
          max="50">
      </div>
      
      <div class="item-total">
        <p>Total: R$ {{ item.totalPrice | number:'2.2-2' }}</p>
        <button (click)="removeItem(item.id)" class="remove-btn">Remover</button>
      </div>
    </div>

    <div class="cart-summary">
      <h3>Resumo do Pedido</h3>
      <p>Total de itens: {{ itemCount }}</p>
      <p>Valor total: R$ {{ cartTotal | number:'2.2-2' }}</p>
      
      <div class="cart-actions">
        <button (click)="clearCart()" class="clear-btn">Limpar Carrinho</button>
        <button (click)="checkout()" class="checkout-btn">Finalizar Compra</button>
      </div>
    </div>
  </div>
</div>
```

## 💳 **Fluxo de Pagamento**

### **1. Serviço de Pedidos (order.service.ts)**
```typescript
@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  createOrder(): Observable<Order> {
    return this.http.post<any>(`${this.apiUrl}/orders/create`, {})
      .pipe(map(response => response.data));
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<any>(`${this.apiUrl}/orders/my-orders`)
      .pipe(map(response => response.data));
  }

  getPendingOrder(): Observable<Order> {
    return this.http.get<any>(`${this.apiUrl}/orders/my-orders/pending`)
      .pipe(map(response => response.data));
  }

  uploadPaymentProof(orderId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('paymentProofUrl', 'temp'); // Backend vai substituir

    return this.http.post(`${this.apiUrl}/orders/${orderId}/payment-proof`, formData);
  }
}
```

### **2. Componente de Pagamento (payment.component.ts)**
```typescript
@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html'
})
export class PaymentComponent implements OnInit {
  pendingOrder: Order | null = null;
  pixKey = 'epti-evento-pix@example.com';
  selectedFile: File | null = null;
  uploadProgress = 0;
  uploading = false;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadPendingOrder();
  }

  loadPendingOrder(): void {
    this.orderService.getPendingOrder().subscribe({
      next: (order) => this.pendingOrder = order,
      error: () => console.log('Nenhum pedido pendente encontrado')
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  uploadPaymentProof(): void {
    if (!this.selectedFile || !this.pendingOrder) return;

    this.uploading = true;
    this.orderService.uploadPaymentProof(this.pendingOrder.id, this.selectedFile).subscribe({
      next: () => {
        alert('Comprovante enviado com sucesso!');
        this.uploading = false;
        this.selectedFile = null;
      },
      error: () => {
        alert('Erro ao enviar comprovante. Tente novamente.');
        this.uploading = false;
      }
    });
  }

  copyPixKey(): void {
    navigator.clipboard.writeText(this.pixKey);
    alert('Chave Pix copiada!');
  }
}
```

## 📱 **Componentes Principais**

### **1. Lista de Produtos (products.component.ts)**
```typescript
@Component({
  selector: 'app-products',
  templateUrl: './products.component.html'
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  kits: Kit[] = [];
  loading = false;
  searchTerm = '';

  constructor(
    private productService: ProductService,
    private kitService: KitService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadKits();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getMyTurmaProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.loading = false;
      },
      error: () => {
        alert('Erro ao carregar produtos');
        this.loading = false;
      }
    });
  }

  loadKits(): void {
    this.kitService.getMyTurmaKits().subscribe(kits => {
      this.kits = kits;
    });
  }

  search(): void {
    if (this.searchTerm.trim()) {
      this.productService.searchProducts(this.searchTerm).subscribe(products => {
        this.products = products;
      });
    } else {
      this.loadProducts();
    }
  }

  addToCart(productId: number, type: 'PRODUCT' | 'KIT'): void {
    const item = {
      itemId: productId,
      itemType: type,
      quantity: 1
    };

    // this.cartService.addToCart(item).subscribe({
    //   next: () => alert('Item adicionado ao carrinho!'),
    //   error: () => alert('Erro ao adicionar item')
    // });
  }
}
```

### **2. Template de Produtos (products.component.html)**
```html
<div class="products-container">
  <div class="search-bar">
    <input 
      type="text" 
      [(ngModel)]="searchTerm" 
      (input)="search()" 
      placeholder="Buscar produtos..."
      class="search-input">
  </div>

  <div *ngIf="loading" class="loading">
    <p>Carregando produtos...</p>
  </div>

  <div class="products-grid">
    <!-- Produtos Individuais -->
    <div *ngFor="let product of products" class="product-card">
      <img [src]="product.imageUrl" [alt]="product.name" class="product-image">
      <div class="product-info">
        <h3>{{ product.name }}</h3>
        <p>{{ product.description }}</p>
        <p class="price">R$ {{ product.price | number:'2.2-2' }}</p>
      </div>
      <button 
        (click)="addToCart(product.id, 'PRODUCT')" 
        class="add-to-cart-btn">
        Adicionar ao Carrinho
      </button>
    </div>

    <!-- Kits -->
    <div *ngFor="let kit of kits" class="kit-card">
      <div class="kit-badge">KIT</div>
      <img [src]="kit.imageUrl" [alt]="kit.name" class="kit-image">
      <div class="kit-info">
        <h3>{{ kit.name }}</h3>
        <p>{{ kit.description }}</p>
        <div class="kit-pricing">
          <p class="individual-price">R$ {{ kit.calculateIndividualPrice() | number:'2.2-2' }}</p>
          <p class="kit-price">R$ {{ kit.price | number:'2.2-2' }}</p>
          <p class="savings">Economia: R$ {{ kit.getSavings() | number:'2.2-2' }}</p>
        </div>
      </div>
      <button 
        (click)="addToCart(kit.id, 'KIT')" 
        class="add-to-cart-btn">
        Adicionar Kit ao Carrinho
      </button>
    </div>
  </div>
</div>
```

## 🔧 **Configurações Adicionais**

### **1. Environment Variables (Angular)**
```typescript
// environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  pixKey: 'epti-evento-pix@example.com'
};

// environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.epti.com/api',
  pixKey: 'epti-evento-pix@example.com'
};
```

### **2. HTTP Client Configuration**
```typescript
// app.module.ts
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  declarations: [...],
  imports: [
    HttpClientModule,
    // outros imports
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [...]
})
export class AppModule { }
```

### **3. Routing Configuration**
```typescript
// app-routing.module.ts
const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'products', 
    component: ProductsComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'cart', 
    component: CartComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'checkout', 
    component: CheckoutComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'orders', 
    component: OrdersComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'profile', 
    component: ProfileComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'admin', 
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    canActivate: [AuthGuard, AdminGuard]
  }
];
```

## 🚨 **Tratamento de Erros**

### **1. Error Interceptor (error.interceptor.ts)**
```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Ocorreu um erro inesperado';

        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 401) {
          errorMessage = 'Sessão expirada. Faça login novamente.';
          // Redirecionar para login
          // this.router.navigate(['/login']);
        } else if (error.status === 429) {
          errorMessage = 'Muitas requisições. Aguarde um momento.';
        }

        console.error('API Error:', error);
        
        // Mostrar toast/alerta
        // this.showError(errorMessage);
        
        return throwError(errorMessage);
      })
    );
  }
}
```

### **2. Toast Service (notificação)**
```typescript
@Injectable({
  providedIn: 'root'
})
export class ToastService {
  showSuccess(message: string): void {
    // Implementar toast de sucesso
    console.log('SUCCESS:', message);
  }

  showError(message: string): void {
    // Implementar toast de erro
    console.error('ERROR:', message);
  }

  showInfo(message: string): void {
    // Implementar toast informativo
    console.info('INFO:', message);
  }
}
```

## 📱 **Interface Responsiva**

### **CSS Base (styles.css)**
```css
/* Products Grid */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px;
}

.product-card, .kit-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  transition: transform 0.2s;
}

.product-card:hover, .kit-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.kit-badge {
  background: #28a745;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  position: absolute;
  top: 10px;
  right: 10px;
}

/* Cart */
.cart-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.cart-summary {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
    padding: 15px;
  }
  
  .cart-item {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
```

## 🔄 **Fluxo Completo de Integração**

### **1. Setup do Ambiente**
```bash
# Terminal 1 - Backend
cd EPTI/BackEnd
./mvnw spring-boot:run -Dspring-boot.run.profiles=development

# Terminal 2 - Frontend
cd epti-frontend
ng serve --proxy-config proxy.conf.json
```

### **2. Teste de Integração**
```bash
# 1. Testar saúde da API
curl http://localhost:8080/api/public/health

# 2. Testar cadastro
curl -X POST http://localhost:4200/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"123456","fullName":"Test User","turma":"PRIMEIRO_D"}'

# 3. Acessar frontend
open http://localhost:4200
```

### **3. Validação Final**
- [ ] Login funciona e redireciona corretamente
- [ ] Produtos carregam para turma do usuário
- [ ] Carrinho adiciona/remove itens
- [ ] Checkout cria pedido
- [ ] Upload de comprovante funciona
- [ ] Rate limiting protege contra excessos
- [ ] CORS permite acesso do frontend

---

Este guia fornece um caminho completo para integrar o frontend Angular com o backend Spring Boot, incluindo todos os serviços, componentes e configurações necessárias.
