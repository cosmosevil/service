# Insurance Management System

Spring Boot REST API для управления страховыми полисами, клиентами и страховыми случаями.

## Быстрый старт

### Требования
- Java 21+
- Maven 3.8.1+
- Git

### Установка

1. Клонируй репозиторий:
```bash
git clone https://github.com/cosmosevil/service.git
cd service
```

2. Собери проект:
```bash
mvn clean package
```

3. Запусти приложение:
```bash
mvn spring-boot:run
```

4. Приложение будет доступно по адресу:
- API: http://localhost:8081/api
- Swagger UI: http://localhost:8081/swagger-ui.html
- H2 Console: http://localhost:8081/h2-console

### Credentials

- Username: admin
- Password: не будет 
- H2 JDBC URL: jdbc:h2:mem:insurancedb

##  API Endpoints

### Клиенты (/api/clients)
- GET /api/clients — Получить всех клиентов
- GET /api/clients/{id} — Получить клиента по ID
- POST /api/clients — Создать клиента
- PUT /api/clients/{id} — Обновить клиента
- DELETE /api/clients/{id} — Удалить клиента

### Полисы (/api/policies)
- GET /api/policies — Получить все полисы
- GET /api/policies/{id} — Получить полис по ID
- GET /api/policies/client/{clientId} — Получить полисы клиента
- POST /api/policies — Создать полис
- PUT /api/policies/{id} — Обновить полис
- DELETE /api/policies/{id} — Удалить полис

### Заявления (/api/claims)
- GET /api/claims — Получить все заявления
- GET /api/claims/{id} — Получить заявление по ID
- GET /api/claims/policy/{policyId} — Получить заявления по полису
- POST /api/claims — Создать заявление
- PATCH /api/claims/{id}/status — Обновить статус
- DELETE /api/claims/{id} — Удалить заявление

## Технологический стек

- Spring Boot 3.2.0
- Java 21
- Spring Data JPA
- Spring Security
- H2 Database
- OpenAPI/Swagger 2.0.2
- Maven

## Docker

Запуск через Docker Compose:
```bash
docker-compose up -d
```

## Примеры запросов

### Создание клиента
```bash
curl -X POST http://localhost:8081/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Иван",
    "lastName": "Иванов",
    "dateOfBirth": "1990-01-15",
    "email": "ivan@example.com",
    "phone": "+79991234567",
    "address": "Москва, ул. Примерная, д. 1"
  }'
```

### Оформление полиса
```bash
curl -X POST http://localhost:8081/api/policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "POL-2025-001",
    "type": "AUTO",
    "coverageAmount": 1000000.00,
    "premium": 5000.00,
    "startDate": "2025-01-01",
    "endDate": "2026-01-01",
    "clientId": 1
  }'
```

### Подача заявления
```bash
curl -X POST http://localhost:8081/api/claims \
  -H "Content-Type: application/json" \
  -d '{
    "claimNumber": "CLM-2025-001",
    "incidentDate": "2025-01-20",
    "reportDate": "2025-01-21",
    "description": "ДТП на пересечении улиц",
    "claimAmount": 50000.00,
    "policyId": 1
  }'
```

## Автор
Муравьев Егор \n
Учебный проект по разработке REST API на Spring Boot.