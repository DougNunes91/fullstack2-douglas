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

## 📚 Documentação

Para mais detalhes sobre arquitetura, SOLID e estrutura do projeto, consulte `IMPLEMENTATION.md`.

### Estrutura Obrigatória do `README.md`

1. **Visão Geral da Arquitetura**: Descrição detalhada da estrutura e decisões arquiteturais
2. **Stack Tecnológica**: Lista completa com justificativas para cada escolha
3. **Como Rodar Localmente**: Instruções passo a passo para setup e execução
4. **Como Rodar os Testes**: Comandos para executar suite completa de testes
5. **Estrutura de Pastas Detalhada**: Mapeamento completo da organização modular do código
6. **Decisões Técnicas Aprofundadas**: Justificativas detalhadas sobre escolhas arquiteturais, padrões e bibliotecas
7. **Melhorias e Roadmap**: Propostas técnicas para evolução e escalabilidade da aplicação

---

**Boa sorte! A JTech espera uma solução que demonstre maturidade em desenvolvimento frontend e visão arquitetural.**
