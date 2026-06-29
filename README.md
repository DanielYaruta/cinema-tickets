# Cinema Ticket System

Консольное Java-приложение для продажи билетов в кинотеатр.  
Стек: **Java 17 · Maven · PostgreSQL · JDBC** (без Spring, без Hibernate, без Lombok).

---

## Быстрый старт

### 1. Создать базу данных

```bash
psql -U danielyaruta -c "CREATE DATABASE cinema_db;"
```

### 2. Настроить подключение

Откройте файл `src/main/resources/config.properties` и при необходимости скорректируйте:

```properties
db.url=jdbc:postgresql://localhost:5432/cinema_db
db.user=danielyaruta
db.password=
```

> Таблицы создаются **автоматически** при первом запуске через `CREATE TABLE IF NOT EXISTS`.

### 3. Собрать проект

```bash
mvn clean install
```

### 4. Запустить

```bash
mvn exec:java
```

---

## Команды меню

| # | Команда |
|---|---------|
| 1 | Добавить фильм |
| 2 | Добавить зал |
| 3 | Добавить сеанс |
| 4 | Показать список сеансов (с названием фильма и зала, по времени) |
| 5 | Забронировать билет (с проверкой свободного места) |
| 6 | Отменить билет |
| 7 | Показать все билеты по сеансу |
| 8 | Оплатить билет (BOOKED → PAID) |
| 9 | Показать все фильмы |
| 10 | Показать все залы |
| 0 | Выход |

---

## Архитектура

```
com.danielyaruta.cinema
├── model/          Movie, Hall, Session, Ticket, TicketStatus
├── db/             ConnectionManager, SchemaInitializer
├── dao/            Интерфейсы + JDBC-реализации
├── service/        Бизнес-логика
├── cli/            Command, CommandRegistry, Cli
│   └── commands/   Конкретные команды
└── Main.java       Composition root (ручной DI)
```

---

## Принципы SOLID

### S — Single Responsibility Principle

Каждый класс имеет ровно одну причину для изменения:

| Класс | Единственная ответственность |
|-------|------------------------------|
| `MovieDaoJdbc` | SQL-операции с таблицей `movies` |
| `MovieService` | Бизнес-валидация данных фильма |
| `AddMovieCommand` | Ввод/вывод в консоль для добавления фильма |
| `ConnectionManager` | Создание JDBC-соединений из config.properties |
| `SchemaInitializer` | DDL-инициализация схемы при старте |
| `Cli` | Главный цикл ввода/вывода пользователя |

### O — Open/Closed Principle

Интерфейс `Command` (`cli/Command.java`) — точка расширения CLI.  
Чтобы добавить новую команду:
1. Создать класс, реализующий `Command`.
2. Зарегистрировать его в `Main.java` через `registry.register(...)`.

`CommandRegistry` и `Cli` **не изменяются**. Система открыта для расширения,  
закрыта для модификации.

### L — Liskov Substitution Principle

`MovieDaoJdbc`, `HallDaoJdbc`, `SessionDaoJdbc`, `TicketDaoJdbc` полностью  
реализуют свои интерфейсы и могут быть подставлены везде, где ожидается  
интерфейс, без изменения поведения вызывающего кода.  
Например, `MovieService` получает `MovieDao` — конкретная реализация может быть  
заменена на `MovieDaoInMemory` для тестов без каких-либо правок в сервисе.

### I — Interface Segregation Principle

Нет единого «жирного» `GenericDao<T>` на все сущности.  
Каждая сущность имеет свой специализированный интерфейс:

- `MovieDao` — только операции с фильмами  
- `HallDao` — только операции с залами  
- `SessionDao` — только операции с сеансами  
- `TicketDao` — операции с билетами + `findActiveBySessionIdAndSeatNumber`, `updateStatus`  

Клиент (`TicketService`) зависит только от `TicketDao`, `SessionDao` и `HallDao` —  
не от методов, которые ему не нужны.

### D — Dependency Inversion Principle

Верхние слои зависят от абстракций, а не от конкретных классов:

```
TicketService(TicketDao, SessionDao, HallDao)   ← интерфейсы
SessionService(SessionDao, MovieDao, HallDao)   ← интерфейсы
MovieService(MovieDao)                          ← интерфейс
```

Конкретные реализации (`*DaoJdbc`) создаются **только в `Main.java`** и  
передаются через конструктор (composition root, ручной DI без фреймворка).
