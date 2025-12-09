# JTech TaskList - Backend

API REST para gerenciamento de tarefas com autenticação JWT e arquitetura limpa.

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.5.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 16
- Docker Compose
- JUnit 5 + Mockito

## 📋 Funcionalidades

- ✅ Autenticação JWT (access + refresh tokens)
- ✅ Registro e login de usuários
- ✅ CRUD de tarefas com validação de propriedade
- ✅ Múltiplas listas por usuário
- ✅ Testes unitários
- ✅ Tratamento global de exceções
- ✅ Arquitetura com princípios SOLID

## 🛠️ Como Executar

### Pré-requisitos
- Java 21
- Docker Desktop

### Passos

1. Inicie o PostgreSQL:
```bash
cd composer
docker-compose up -d
```

2. Execute a aplicação:
```bash
cd ..
./gradlew bootRun
```

API disponível em: `http://localhost:8080`

### Endpoints Principais

**Autenticação:**
- `POST /auth/register` - Cadastro
- `POST /auth/login` - Login
- `POST /auth/refresh` - Renovar token
- `GET /auth/me` - Dados do usuário

**Tarefas:**
- `GET /tasks` - Listar tarefas
- `POST /tasks` - Criar tarefa
- `PUT /tasks/{id}` - Atualizar
- `DELETE /tasks/{id}` - Remover
- `PATCH /tasks/{id}/toggle` - Marcar como concluída

## 🧪 Testes

```bash
./gradlew test
```

## 📚 Arquitetura

O projeto segue Clean Architecture com separação em camadas:
- **Controllers**: Recebem requisições HTTP
- **Services**: Lógica de negócio
- **Repositories**: Acesso a dados
- **Entities**: Modelos JPA
- **Domains**: Objetos de domínio

Para mais detalhes, consulte o `IMPLEMENTATION.md` na raiz do projeto.