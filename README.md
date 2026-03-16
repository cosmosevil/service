# Insurance System

REST API сервис для управления страховыми полисами и заявлениями на выплату.

## Тема

Система автоматизации страховой деятельности. Полис оформляется для покупателя и содержит набор покрытий; взносы учитываются в Payment. При наступлении страхового случая создаётся заявление (Claim), которое проходит проверку и расчёт выплаты. Сумма выплаты не может превышать лимит по соответствующему покрытию полиса.

## Основные сущности

| Сущность   | Таблица      | Описание |
|------------|-------------|----------|
| `Customer` | `customers` | Страхователь (физическое лицо). Email уникален. Содержит персональные данные: ФИО, дата рождения, адрес, телефон. |
| `Coverage` | `coverages` | Вид страхового покрытия (например, ОСАГО, КАСКО, ДМС). Определяет лимит выплаты и ежемесячный взнос. Название уникально. |
| `Policy`   | `policies`  | Страховой полис. Оформляется на клиента, содержит набор покрытий. Имеет срок действия и статус. Номер полиса уникален. |
| `Payment`  | `payments`  | Ежемесячный страховой взнос по полису. Фиксирует сумму, срок оплаты и статус (PENDING/PAID/OVERDUE/CANCELLED). |
| `Claim`    | `claims`    | Заявление на страховую выплату. Привязано к полису и конкретному покрытию. Проходит цикл проверки: PENDING → UNDER_REVIEW → APPROVED/REJECTED → PAID. |

### Связи между таблицами

```
Customer    ──1:N──  Policy
Policy      ──M:N──  Coverage      (таблица policy_coverages)
Policy      ──1:N──  Payment
Policy      ──1:N──  Claim
Coverage    ──1:N──  Claim
```

## Настройка и запуск

### Требования
- Java 21
- Maven 3.8+
- PostgreSQL 14+

### База данных

Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE supportdb;
```

### Переменные окружения

Настройте `src/main/resources/application.properties` или задайте переменные окружения:

```bash
SERVER_PORT=8443
SSL_ENABLED=true
SSL_KEY_STORE_PASSWORD=yourPassword
```

### Запуск

```bash
./mvnw spring-boot:run
```

## Операции сервиса

### CRUD по каждой сущности

| Метод  | Путь                                        | Описание |
|--------|---------------------------------------------|----------|
| POST   | `/api/coverages`                            | Создать покрытие |
| GET    | `/api/coverages`                            | Все покрытия |
| GET    | `/api/coverages/{id}`                       | Покрытие по ID |
| PUT    | `/api/coverages/{id}`                       | Обновить покрытие |
| DELETE | `/api/coverages/{id}`                       | Удалить покрытие |
| POST   | `/api/customers`                            | Создать клиента |
| GET    | `/api/customers`                            | Все клиенты |
| GET    | `/api/customers/{id}`                       | Клиент по ID |
| PUT    | `/api/customers/{id}`                       | Обновить клиента |
| DELETE | `/api/customers/{id}`                       | Удалить клиента |
| POST   | `/api/policies?customerId={id}`             | Создать полис для клиента |
| GET    | `/api/policies`                             | Все полисы |
| GET    | `/api/policies/{id}`                        | Полис по ID |
| GET    | `/api/policies/customer/{customerId}`       | Полисы клиента |
| PUT    | `/api/policies/{id}`                        | Обновить даты полиса |
| PUT    | `/api/policies/{id}/status?status={status}` | Изменить статус полиса |
| POST   | `/api/policies/{id}/coverages/{covId}`      | Добавить покрытие к полису |
| DELETE | `/api/policies/{id}/coverages/{covId}`      | Удалить покрытие из полиса |
| DELETE | `/api/policies/{id}`                        | Удалить полис |
| POST   | `/api/claims?policyId={id}&coverageId={id}` | Создать заявление |
| GET    | `/api/claims`                               | Все заявления |
| GET    | `/api/claims/{id}`                          | Заявление по ID |
| GET    | `/api/claims/policy/{policyId}`             | Заявления по полису |
| GET    | `/api/claims/status/{status}`               | Заявления по статусу |
| PUT    | `/api/claims/{id}`                          | Обновить заявление (только PENDING) |
| DELETE | `/api/claims/{id}`                          | Удалить заявление |
| POST   | `/api/payments?policyId={id}`               | Создать платёж вручную |
| GET    | `/api/payments`                             | Все платежи |
| GET    | `/api/payments/{id}`                        | Платёж по ID |
| GET    | `/api/payments/policy/{policyId}`           | Платежи по полису |
| GET    | `/api/payments/overdue`                     | Просроченные платежи |
| PUT    | `/api/payments/{id}/pay`                    | Отметить платёж оплаченным |
| PUT    | `/api/payments/{id}/cancel`                 | Отменить платёж |
| DELETE | `/api/payments/{id}`                        | Удалить платёж |

### Бизнес-операции

| №  | Метод | Путь | Затронутые сущности | Описание |
|----|-------|------|---------------------|----------|
| 1  | POST  | `/api/insurance/claims/{id}/submit`                        | Claim                    | Подаёт заявление на рассмотрение: PENDING → UNDER_REVIEW |
| 2  | POST  | `/api/insurance/claims/{id}/approve?approvedAmount={сумма}` | Claim + Coverage         | Одобряет заявление и фиксирует сумму выплаты. Сумма не может превышать лимит покрытия. UNDER_REVIEW → APPROVED |
| 3  | POST  | `/api/insurance/claims/{id}/reject?reason={причина}`       | Claim                    | Отклоняет заявление с указанием причины. UNDER_REVIEW → REJECTED |
| 4  | POST  | `/api/insurance/claims/{id}/pay`                           | Claim                    | Производит выплату по одобренному заявлению. APPROVED → PAID |
| 5  | POST  | `/api/insurance/policies/{id}/generate-payments`           | Policy + Coverage + Payment | Генерирует ежемесячные взносы на весь срок полиса. Сумма = суммарный monthlyPremium всех покрытий |
| 6  | POST  | `/api/insurance/payments/mark-overdue`                     | Payment                  | Обновляет статус просроченных PENDING-платежей (dueDate < сегодня) на OVERDUE |
| 7  | GET   | `/api/insurance/stats/policies`                            | Policy                   | Сводная статистика: количество полисов в каждом статусе |
| 8  | GET   | `/api/insurance/stats/claims`                              | Claim                    | Сводная статистика: количество заявлений в каждом статусе + суммарные выплаты |

## Статусная машина заявлений (Claim)

```
PENDING → UNDER_REVIEW → APPROVED → PAID
                    ↓
                REJECTED
```

| Переход | Операция | Условие |
|---------|----------|---------|
| PENDING → UNDER_REVIEW | `submit` | — |
| UNDER_REVIEW → APPROVED | `approve` | approvedAmount ≤ coverageLimit |
| UNDER_REVIEW → REJECTED | `reject` | — |
| APPROVED → PAID | `pay` | — |

## Статусы полиса (Policy)

| Статус | Описание |
|--------|----------|
| `ACTIVE` | Полис действует, можно подавать заявления |
| `SUSPENDED` | Полис приостановлен |
| `EXPIRED` | Срок действия истёк |
| `CANCELLED` | Полис аннулирован |

## Статусы взноса (Payment)

| Статус | Описание |
|--------|----------|
| `PENDING` | Ожидает оплаты |
| `PAID` | Оплачен |
| `OVERDUE` | Просрочен |
| `CANCELLED` | Отменён |

## Безопасность (Spring Security + JWT)

### Таблицы безопасности

| Таблица        | Описание |
|----------------|----------|
| `app_users`    | Учётные записи для входа: username, BCrypt-пароль, роль. |
| `user_sessions`| Refresh-сессии: jti (UUID), SHA-256 хэш токена, статус, сроки. |

### Роли

| Роль         | Описание |
|--------------|----------|
| `ROLE_USER`  | Страхователь. Может создавать заявления. |
| `ROLE_AGENT` | Страховой агент. Управляет полисами, клиентами, обрабатывает заявления и взносы. |
| `ROLE_ADMIN` | Полный доступ ко всем операциям включая управление покрытиями и удаление записей. |

### Матрица доступа

| Эндпоинт | Без токена | USER | AGENT | ADMIN |
|----------|-----------|------|-------|-------|
| `POST /api/auth/register` | ✅ | ✅ | ✅ | ✅ |
| `POST /api/auth/login`    | ✅ | ✅ | ✅ | ✅ |
| `POST /api/auth/refresh`  | ✅ | ✅ | ✅ | ✅ |
| `GET /api/coverages/**` | ❌ | ✅ | ✅ | ✅ |
| `POST/PUT/DELETE /api/coverages/**` | ❌ | ❌ | ❌ | ✅ |
| `GET /api/customers/**` | ❌ | ❌ | ✅ | ✅ |
| `POST/PUT/DELETE /api/customers/**` | ❌ | ❌ | ❌ | ✅ |
| `GET/POST/PUT /api/policies/**` | ❌ | ❌ | ✅ | ✅ |
| `DELETE /api/policies/**` | ❌ | ❌ | ❌ | ✅ |
| `GET/PUT /api/payments/**` | ❌ | ❌ | ✅ | ✅ |
| `DELETE /api/payments/**` | ❌ | ❌ | ❌ | ✅ |
| `POST /api/claims` | ❌ | ✅ | ✅ | ✅ |
| `GET/PUT /api/claims/**` | ❌ | ❌ | ✅ | ✅ |
| `DELETE /api/claims/**` | ❌ | ❌ | ❌ | ✅ |
| `GET/POST /api/insurance/**` | ❌ | ❌ | ✅ | ✅ |

### Аутентификация — JWT Bearer

Рекомендуемый способ. Все защищённые запросы требуют заголовка:
```
Authorization: Bearer <accessToken>
```

В Postman: вкладка **Authorization → Bearer Token** → вставьте `accessToken`.

### Регистрация пользователей

```
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "admin",
  "password": "Admin123!",
  "role": "ROLE_ADMIN"
}
```

Доступные роли: `ROLE_USER`, `ROLE_AGENT`, `ROLE_ADMIN`.

#### Требования к паролю

- Минимум **8 символов**
- Хотя бы одна **заглавная буква** (A–Z)
- Хотя бы одна **цифра** (0–9)
- Хотя бы один **спецсимвол** (`!@#$%^&*` и др.)

### Эндпоинты аутентификации

#### Вход
```
POST /api/auth/login
```
```json
{ "username": "admin", "password": "Admin123!" }
```
Ответ:
```json
{
  "accessToken":  "eyJ...",
  "refreshToken": "eyJ...",
  "tokenType":    "Bearer"
}
```

#### Обновление пары токенов
```
POST /api/auth/refresh
```
```json
{ "refreshToken": "eyJ..." }
```
Возвращает новую пару. Старый refresh-токен становится недействительным (token rotation).

### Токены

| Тип           | Время жизни | Назначение |
|---------------|-------------|------------|
| Access token  | 15 минут    | Доступ к API-эндпоинтам |
| Refresh token | 7 дней      | Получение новой пары токенов |

### Управление сессиями

Каждый refresh-токен привязан к записи в таблице `user_sessions`.

| Статус    | Описание |
|-----------|----------|
| `ACTIVE`  | Сессия активна, refresh-токен можно использовать |
| `REVOKED` | Токен использован — сессия отозвана (token rotation) |
| `EXPIRED` | Срок сессии истёк |

Повторное использование отозванного refresh-токена возвращает `401`.

Для контроля сессий в БД:
```sql
SELECT jti, status, created_at, expires_at, last_used_at
FROM user_sessions
ORDER BY created_at DESC;
```

### Хранение данных

- Пароли — BCrypt-хэш в таблице `app_users`
- Refresh-токены — SHA-256 хэш в таблице `user_sessions`
- В коде и скриптах никаких паролей и токенов нет

---

## TLS / HTTPS

### Структура цепочки сертификатов

```
Root CA  (STS-RootCA, самоподписанный, 10 лет)
  └── Intermediate CA  (STS-IntermediateCA, подписан Root CA, 5 лет)
        └── Server cert  (CN=localhost, подписан Intermediate CA, 1 год)
```

Все сертификаты содержат `OU=Student-<23120>`.

### Генерация сертификатов

```bash
export STUDENT_ID=23120
export KEYSTORE_PASSWORD=yourStrongPassword

bash generate-certs.sh
```

Созданные файлы (в директории `certs/` — исключены из git):
| Файл | Описание |
|------|----------|
| `certs/sts-root-ca.crt` | Корневой CA — добавить в доверенные |
| `certs/sts-intermediate-ca.crt` | Промежуточный CA |
| `certs/sts-server.crt` | Серверный сертификат |
| `certs/sts-chain.crt` | Полная цепочка |
| `src/main/resources/keystore.p12` | PKCS12 keystore для Spring Boot (не в git) |

### Запуск с HTTPS

```bash
export SSL_ENABLED=true
export SSL_KEY_STORE_PASSWORD=yourStrongPassword
export SERVER_PORT=8443
mvn spring-boot:run
```

Приложение будет доступно по адресу: `https://localhost:8443`

### Добавление Root CA в доверенные

**Windows** (через certlm.msc):
1. Win+R → `certlm.msc`
2. Trusted Root Certification Authorities → All Tasks → Import
3. Выбрать `certs/sts-root-ca.crt`

**macOS:**
```bash
sudo security add-trusted-cert -d -r trustRoot \
  -k /Library/Keychains/System.keychain certs/sts-root-ca.crt
```

**Ubuntu/Debian:**
```bash
sudo cp certs/sts-root-ca.crt /usr/local/share/ca-certificates/sts-root-ca.crt
sudo update-ca-certificates
```

### Переменные окружения для TLS

| Переменная | По умолчанию | Описание |
|------------|-------------|----------|
| `SSL_ENABLED` | `true` | Включить HTTPS |
| `SSL_KEY_STORE_PATH` | `classpath:keystore.p12` | Путь к keystore |
| `SSL_KEY_STORE_PASSWORD` | `MyPassword!` | Пароль keystore |
| `SERVER_PORT` | `8443` | Порт (8443 для HTTPS) |

> Keystore и приватные ключи **никогда** не коммитятся в репозиторий — добавлены в `.gitignore`.

### Настройка Postman для HTTPS

В Postman: **Settings → General → SSL certificate verification → OFF**
(для self-signed сертификатов в dev-среде)

Или добавьте `certs/sts-root-ca.crt` как доверенный CA:
**Settings → Certificates → CA Certificates → Add**

В коллекции переменная `baseUrl` уже установлена на `https://localhost:8443`. Для HTTP замените на `http://localhost:8080`.

---

## CI/CD (GitHub Actions)

### Пайплайн

Файл: `.github/workflows/ci.yml`

При каждом push/PR в ветку `main` запускается:

| Шаг | Описание |
|-----|----------|
| Checkout | Клонирование репозитория |
| Set up JDK 21 | Установка Java |
| Restore keystore | Декодирование keystore из GitHub Secret |
| Compile | `mvn compile` |
| Test | `mvn test` (с PostgreSQL service) |
| Package | `mvn package -DskipTests` |
| Upload artifact | JAR загружается в GitHub Artifacts (хранится 30 дней) |

### GitHub Secrets

Перейдите в **Settings → Secrets and variables → Actions** репозитория и добавьте:

| Secret | Описание |
|--------|----------|
| `KEYSTORE_BASE64` | Base64-кодированный keystore.p12 |
| `KEYSTORE_PASSWORD` | Пароль от keystore |

Получить base64 от keystore:
```bash
# Linux/macOS
base64 -w 0 src/main/resources/keystore.p12

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("src\main\resources\keystore.p12"))
```

> Приватные ключи, keystore и сертификаты **не хранятся в репозитории**. Только в GitHub Secrets.

---

## Коллекция запросов

Все запросы (CRUD + бизнес-операции + JWT-сценарий) находятся в файле `postman_collection.json`.

Импорт в Postman: **File → Import → выбрать `postman_collection.json`**.

**Быстрый старт:**
1. Выполни `POST /api/auth/register` — создай пользователей (ADMIN, AGENT, USER)
2. Выполни `POST /api/auth/login` — токены сохранятся в переменные коллекции автоматически
3. Все остальные запросы используют `Bearer {{accessToken}}` из переменных

**Полный страховой цикл** (папка «Полный сценарий»):
1. Создать покрытие → клиента → полис → добавить покрытие в полис
2. Сгенерировать ежемесячные взносы → оплатить взнос
3. Создать заявление → подать → одобрить → выплатить
4. Посмотреть статистику
