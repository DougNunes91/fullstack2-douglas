# JTech TaskList - Sistema TODO Multi-usuário

Sistema completo de gerenciamento de tarefas com backend Spring Boot e frontend Vue 3.

## 🚀 Tecnologias

**Backend:**
- Java 21 + Spring Boot 3.5.5
- Spring Security + JWT
- PostgreSQL 16
- Docker Compose

**Frontend:**
- Vue 3 + TypeScript
- Pinia (gerenciamento de estado)
- Vuetify (Material Design)
- Vite

## 📋 Funcionalidades

- ✅ Autenticação com JWT (registro, login, refresh token)
- ✅ Múltiplas listas de tarefas por usuário
- ✅ CRUD completo de tarefas
- ✅ Filtros (todas, pendentes, concluídas)
- ✅ Persistência de estado
- ✅ Proteção de rotas
- ✅ Validação de propriedade de tarefas

## 🛠️ Como Executar

### Backend

1. Inicie o PostgreSQL:
```bash
cd jtech-tasklist-backend/composer
docker-compose up -d
```

2. Execute a aplicação:
```bash
cd ..
./gradlew bootRun
```

API disponível em: `http://localhost:8080`

### Frontend

1. Instale as dependências:
```bash
cd jtech-tasklist-frontend
npm install
```

2. Execute o dev server:
```bash
npm run dev
```

Aplicação disponível em: `http://localhost:5173`

