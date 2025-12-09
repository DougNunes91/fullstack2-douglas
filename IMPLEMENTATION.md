# Sistema TODO List Multi-usuário - JTech Fullstack Challenge

## 📋 Visão Geral da Arquitetura

Este projeto implementa um sistema completo de gerenciamento de tarefas (TODO List) multi-usuário com arquitetura robusta e escalável, seguindo os princípios SOLID e melhores práticas de desenvolvimento.

### Arquitetura Backend (Spring Boot)

O backend segue uma **arquitetura em camadas hexagonal (Ports & Adapters)**, garantindo separação clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  (Controllers - REST API Endpoints)                         │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│  (Use Cases / Services - Business Logic)                    │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                            │
│  (Entities, Value Objects - Core Business)                  │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                        │
│  (Repositories, Database, External Services)                │
└─────────────────────────────────────────────────────────────┘
```

### Arquitetura Frontend (Vue 3)

O frontend implementa **arquitetura modular com gerenciamento de estado centralizado**:

```
┌─────────────────────────────────────────────────────────────┐
│                        Views Layer                           │
│  (Page Components - Smart Components)                        │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                     Components Layer                         │
│  (Reusable UI Components - Dumb Components)                 │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                       Store Layer                            │
│  (Pinia - State Management with Persistence)                │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                      Services Layer                          │
│  (API Communication, Business Logic)                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠 Stack Tecnológica

### Backend

| Tecnologia | Versão | Justificativa |
|------------|--------|---------------|
| **Java** | 21 | Última versão LTS com record classes e pattern matching |
| **Spring Boot** | 3.5.5 | Framework robusto para APIs REST com DI e IoC |
| **Spring Security** | 6.x | Autenticação e autorização enterprise-grade |
| **Spring Data JPA** | 3.x | Abstração de persistência com Hibernate |
| **PostgreSQL** | 16 | Banco relacional confiável e performático |
| **JWT (jjwt)** | 0.12.3 | Token seguro para autenticação stateless |
| **Lombok** | Latest | Redução de boilerplate code |
| **JUnit 5** | 5.10.x | Framework de testes moderno |
| **Mockito** | 5.x | Mocks para testes unitários isolados |
| **SpringDoc OpenAPI** | 2.0.4 | Documentação automática da API |

### Frontend

| Tecnologia | Versão | Justificativa |
|------------|--------|---------------|
| **Vue 3** | 3.5.18 | Framework reativo com Composition API |
| **TypeScript** | 5.8.0 | Tipagem estática para maior segurança |
| **Pinia** | 3.0.3 | Store moderna para Vue com DevTools |
| **Vue Router** | 4.5.1 | Roteamento SPA com guards de autenticação |
| **Vuetify** | 3.7.4 | Material Design components |
| **Axios** | 1.7.9 | Cliente HTTP com interceptors |
| **Vitest** | 3.2.4 | Framework de testes rápido |
| **Vite** | 7.0.6 | Build tool extremamente rápido |

---

## 🚀 Como Rodar Localmente

### Pré-requisitos

- Java 21 ou superior
- Node.js 20.19.0 ou 22.12.0+
- PostgreSQL 16+ (ou Docker)
- Git

### Backend

1. **Clone o repositório:**
```bash
git clone https://github.com/jtech-carreer/fullstack2.git
cd fullstack2/jtech-tasklist-backend
```

2. **Inicie o PostgreSQL com Docker:**
```bash
cd composer
docker-compose up -d
```

Isso iniciará:
- PostgreSQL na porta 5432
- PgAdmin na porta 5050 (admin@admin.com / admin)

3. **Configure as variáveis de ambiente (opcional):**
```bash
# Windows PowerShell
$env:DS_URL="localhost"
$env:DS_PORT="5432"
$env:DS_DATABASE="tasklist_db"
$env:DS_USER="postgres"
$env:DS_PASS="postgres"
$env:JWT_SECRET="your-secret-key-at-least-256-bits"
```

4. **Execute o backend:**
```bash
# Usando Gradle Wrapper
.\gradlew bootRun

# Ou compile e execute
.\gradlew build
java -jar build/libs/jtech-tasklist-backend-*.jar
```

O backend estará disponível em `http://localhost:8080`

**Documentação da API:**
- Swagger UI: http://localhost:8080/doc/tasklist/v1/api.html
- OpenAPI JSON: http://localhost:8080/doc/tasklist/v3/api-documents

### Frontend

1. **Navegue para o diretório do frontend:**
```bash
cd ../jtech-tasklist-frontend
```

2. **Instale as dependências:**
```bash
npm install
```

3. **Execute em modo desenvolvimento:**
```bash
npm run dev
```

O frontend estará disponível em `http://localhost:5173`

4. **Acesse a aplicação:**
- URL: http://localhost:5173
- A aplicação iniciará na tela de login
- Qualquer combinação de email/senha válida funcionará (autenticação mock)

---

## 🧪 Como Rodar os Testes

### Testes Backend

```bash
cd jtech-tasklist-backend

# Executar todos os testes
.\gradlew test

# Executar com relatório de cobertura
.\gradlew test jacocoTestReport

# Ver relatório de cobertura
start build/reports/jacoco/test/html/index.html
```

**Cobertura de Testes:**
- Testes unitários para Services (AuthService, TaskService)
- Testes de integração para Controllers
- Mocks com Mockito para isolamento
- Assertions com AssertJ

### Testes Frontend

```bash
cd jtech-tasklist-frontend

# Executar testes unitários
npm run test:unit

# Executar em modo watch
npm run test:unit -- --watch

# Executar com cobertura
npm run test:unit -- --coverage
```

---

## 📁 Estrutura de Pastas Detalhada

### Backend
```
jtech-tasklist-backend/
├── src/
│   ├── main/
│   │   ├── java/br/com/jtech/tasklist/
│   │   │   ├── adapters/                    # Camada de Adaptadores
│   │   │   │   ├── input/
│   │   │   │   │   ├── controllers/         # REST Controllers
│   │   │   │   │   │   ├── auth/            # Autenticação endpoints
│   │   │   │   │   │   └── task/            # Task endpoints
│   │   │   │   │   └── protocols/           # DTOs (Request/Response)
│   │   │   │   │       ├── auth/
│   │   │   │   │       └── task/
│   │   │   │   └── output/
│   │   │   │       └── repositories/        # JPA Repositories
│   │   │   │           └── entities/        # JPA Entities
│   │   │   ├── application/                 # Camada de Aplicação
│   │   │   │   ├── core/
│   │   │   │   │   ├── domains/             # Domain Models
│   │   │   │   │   └── usecases/            # Business Logic Services
│   │   │   │   │       ├── auth/
│   │   │   │   │       └── task/
│   │   │   │   └── ports/                   # Interfaces (Hexagonal)
│   │   │   │       ├── input/
│   │   │   │       └── output/
│   │   │   └── config/                      # Configurações
│   │   │       ├── infra/
│   │   │       │   └── exceptions/          # Exception Handlers
│   │   │       ├── security/                # Spring Security Config
│   │   │       └── usecases/                # Bean Configurations
│   │   └── resources/
│   │       ├── application.yml              # Configuração principal
│   │       └── db/migration/                # Scripts SQL
│   └── test/
│       └── java/br/com/jtech/tasklist/      # Testes espelhando src/
├── build.gradle                             # Dependências Gradle
├── composer/
│   └── docker-compose.yml                   # PostgreSQL + PgAdmin
└── README.md
```

### Frontend
```
jtech-tasklist-frontend/
├── src/
│   ├── assets/                    # Estilos e recursos estáticos
│   ├── components/                # Componentes reutilizáveis
│   ├── router/                    # Configuração de rotas
│   │   └── index.ts               # Definições de rotas + guards
│   ├── stores/                    # Pinia stores
│   │   ├── auth.ts                # Store de autenticação
│   │   ├── lists.ts               # Store de listas
│   │   └── tasks.ts               # Store de tarefas
│   ├── types/                     # TypeScript types/interfaces
│   │   ├── auth.ts
│   │   └── task.ts
│   ├── views/                     # Páginas/Views
│   │   ├── LoginView.vue          # Tela de login
│   │   ├── RegisterView.vue       # Tela de registro
│   │   ├── HomeView.vue           # Dashboard principal
│   │   └── ListView.vue           # View de lista específica
│   ├── App.vue                    # Componente raiz
│   └── main.ts                    # Entry point (Pinia, Router, Vuetify)
├── package.json                   # Dependências NPM
├── vite.config.ts                 # Configuração Vite + proxy
├── tsconfig.json                  # Configuração TypeScript
└── README.md
```

---

## 🏗 Decisões Técnicas Aprofundadas

### 1. Princípios SOLID Aplicados

#### **Single Responsibility Principle (SRP)**
- **Backend:** Cada classe tem uma única responsabilidade:
  - `AuthService` → Apenas lógica de autenticação
  - `TaskService` → Apenas lógica de tarefas
  - `JwtTokenProvider` → Apenas operações JWT
- **Frontend:** Separação clara entre Stores (estado), Views (apresentação) e Components (UI)

#### **Open/Closed Principle (OCP)**
- Controllers usam DTOs que podem ser estendidos sem modificar o core
- Services trabalham com interfaces (Ports) permitindo extensão
- Validações centralizadas com Bean Validation

#### **Liskov Substitution Principle (LSP)**
- Repositories implementam `JpaRepository` mantendo contratos
- Entidades mantêm invariantes através de métodos helper

#### **Interface Segregation Principle (ISP)**
- Repositories têm métodos específicos, não genéricos demais
- DTOs específicos para cada operação (RegisterRequest, LoginRequest)

#### **Dependency Inversion Principle (DIP)**
- Services dependem de abstrações (Repositories, não implementações)
- Injeção de dependência gerenciada pelo Spring
- Frontend: Stores abstraem lógica de negócio das Views

### 2. Segurança Implementada

#### Backend
- **Autenticação JWT:** Tokens assinados com HS512
- **BCrypt:** Hash de senhas com salt (12 rounds)
- **Refresh Tokens:** Tokens de longa duração para renovação
- **CORS Configurado:** Permite apenas origens específicas
- **Validação de Propriedade:** Tasks só acessíveis por seus donos
- **Security Context:** Validação em cada requisição

#### Frontend
- **Route Guards:** Proteção de rotas não autenticadas
- **Persistência Segura:** Tokens em localStorage com Pinia persist
- **Validação de Formulários:** Client-side validation
- **Mock Authentication:** Aceita qualquer credencial válida conforme requisito

### 3. Gerenciamento de Estado (Frontend)

**Por que Pinia?**
- Substituição oficial do Vuex para Vue 3
- API mais simples e TypeScript-first
- DevTools integrado
- Plugin de persistência nativo

**Stores Implementadas:**
1. **auth.ts:** Gerencia autenticação e usuário logado
2. **lists.ts:** CRUD de listas com validações
3. **tasks.ts:** CRUD de tasks com prevenção de duplicatas

**Persistência:**
- Usa `pinia-plugin-persistedstate`
- Dados salvos automaticamente no localStorage
- Restauração automática ao recarregar página

### 4. Validações Implementadas

#### Backend (Bean Validation)
```java
@NotBlank(message = "Title is required")
@Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
private String title;
```

#### Frontend (Vuetify Rules)
```typescript
const emailRules = [
  (v: string) => !!v || 'Email is required',
  (v: string) => /.+@.+\..+/.test(v) || 'Email must be valid'
]
```

### 5. Tratamento de Erros

**Backend:**
- `GlobalExceptionHandler` centraliza tratamento
- Respostas consistentes com `ApiError`
- Logging estruturado com SLF4J

**Frontend:**
- Snackbars para notificações de sucesso/erro
- Validação em tempo real nos formulários
- Mensagens de erro amigáveis

---

## ✅ Funcionalidades Implementadas

### Backend ✅
- [x] POST /auth/register - Cadastro com hash BCrypt
- [x] POST /auth/login - Login com JWT
- [x] POST /auth/refresh - Refresh token
- [x] GET /auth/me - Usuário autenticado
- [x] POST /tasks - Criar tarefa
- [x] GET /tasks - Listar tarefas do usuário
- [x] GET /tasks/{id} - Buscar tarefa específica
- [x] PUT /tasks/{id} - Atualizar tarefa
- [x] DELETE /tasks/{id} - Deletar tarefa
- [x] PATCH /tasks/{id}/toggle - Alternar conclusão
- [x] GET /tasks/lists - Listar categorias
- [x] Validação de propriedade em todos endpoints
- [x] Exception handling centralizado
- [x] Documentação Swagger/OpenAPI
- [x] Testes unitários (AuthService, TaskService)

### Frontend ✅
- [x] Tela de Login com validação
- [x] Tela de Registro
- [x] Autenticação mock (aceita qualquer credencial válida)
- [x] Dashboard principal
- [x] Sidebar com listas
- [x] CRUD completo de listas
- [x] CRUD completo de tarefas
- [x] Filtros (All, Pending, Completed)
- [x] Toggle de conclusão
- [x] Prevenção de duplicatas
- [x] Material Design (Vuetify)
- [x] Persistência de estado
- [x] Route guards
- [x] Notificações (Snackbar)
- [x] Responsividade

---

## 🚀 Melhorias e Roadmap

### Curto Prazo
- [ ] Adicionar campo de data de vencimento nas tasks
- [ ] Implementar prioridades (Alta, Média, Baixa)
- [ ] Adicionar tags/labels às tasks
- [ ] Busca e filtros avançados
- [ ] Ordenação customizável

### Médio Prazo
- [ ] Compartilhamento de listas entre usuários
- [ ] Notificações em tempo real (WebSocket)
- [ ] Anexos em tarefas (upload de arquivos)
- [ ] Comentários em tarefas
- [ ] Histórico de alterações
- [ ] API de relatórios e estatísticas

### Longo Prazo
- [ ] App mobile (React Native / Flutter)
- [ ] Integração com calendários (Google, Outlook)
- [ ] Integração com Slack/Discord
- [ ] Modo offline (PWA)
- [ ] IA para sugestões de tarefas
- [ ] Gamificação (pontos, badges)

### Escalabilidade
- [ ] Cache Redis para sessões
- [ ] Message Queue (RabbitMQ/Kafka)
- [ ] Microserviços (separar Auth, Tasks)
- [ ] GraphQL API
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline completo
- [ ] Monitoramento (Prometheus + Grafana)
- [ ] Logs centralizados (ELK Stack)

---

## 📝 Licença

Copyright © 2025 J-Tech Soluções em Informática. Todos os direitos reservados.

---

## 👥 Autor

Desenvolvido para o **JTech Fullstack Challenge** demonstrando expertise em:
- Arquitetura de software enterprise
- Princípios SOLID
- Spring Boot ecosystem
- Vue 3 + TypeScript
- Segurança e autenticação
- Testes automatizados
- Boas práticas de desenvolvimento

---

## 📞 Suporte

Para dúvidas ou sugestões, entre em contato através do repositório no GitHub.
