# BookStore

Spring Boot + PostgreSQL + JWT. Фронт: `src/main/resources/static/` ішіндегі HTML беттер.

## Іске қосу

1. PostgreSQL-де `product_db` базасын жасаңыз.
2. `application.properties` әдепкі мәндері: `postgres` / `5757`. Өзгерту үшін environment variable қолданыңыз:

| Айнымалы | Мағынасы |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | дерекқор |
| `JWT_SECRET` | JWT құпия сөзі (кемінде 32 символ), өндірісте міндетті түрде өзгертіңіз |
| `ADMIN_EMAIL` | осы email-ге ADMIN рөлі беріледі |
| `ADMIN_USERNAME`, `ADMIN_PASSWORD` | `ADMIN_EMAIL` бойынша пайдаланушы жоқ болса, жаңа админ жасалады (пароль ≥ 8 таңба) |

3. `./mvnw spring-boot:run`, содан кейін http://localhost:8080

Алғаш қосылғанда `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN` рөлдері автоматты жасалады.

## Рөлдер

| Рөл | Не істей алады |
|---|---|
| USER | кітап көру, себет, тапсырыс беру, өз тапсырыстары |
| SELLER | USER + кітап қосу; **тек өз кітабын** өзгерту/өшіру/қойма санын қосу |
| ADMIN | барлығы: кез келген кітап, тапсырыстар, категориялар, пайдаланушыларға рөл беру |

Админ мен сатушы да кез келген кітапты, өз кітабын да сатып ала алады (шектеу жоқ).
Рөлді `/admin/users` бетінде ADMIN береді.

## Қойма

- Әр кітапта `stock` (дана саны) бар. Ескі кітаптарда ол бос, яғни 0 деп есептеледі: админ/сатушы `/admin` бетінде толтыруы керек.
- Саны 0 болса, дүкенде «Oops! Out of stock» көрінеді, себетке салуға болмайды.
- Себетке қойма жеткенше ғана қосуға болады. Тапсырыс берілгенде қойма серверде азаяды (бір мезгілде екі адам соңғы данаға тапсырыс бере алмайды).
- Тапсырыс `CANCELLED` болса, кітаптар қоймаға қайтады.
- Біткен кітапқа дана қосу: `/admin` бетінде кітапты таңдап, «Add copies».

## Беттер

| URL | Не |
|---|---|
| `/` | дүкен, іздеу (атауы/авторы), фильтрлер |
| `/details?id=1` | кітап беті |
| `/cart` | себет |
| `/orders` | менің тапсырыстарым |
| `/login` | кіру / тіркелу |
| `/admin` | кітаптарды басқару (ADMIN: барлығы, SELLER: өз кітаптары) |
| `/admin/orders` | тапсырыстар және статус (ADMIN) |
| `/admin/users` | пайдаланушыларға рөл беру (ADMIN) |

## API

| Әдіс | URL | Кім |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | барлығы |
| GET | `/api/me` | кірген (жаңа рөлдермен) |
| GET | `/api/books`, `/api/books/{id}`, `/search`, `/category/{id}`, `/price` | барлығы |
| GET | `/api/books/mine` | SELLER, ADMIN |
| POST | `/api/books` | SELLER, ADMIN (иесі: қосқан адам) |
| PUT/DELETE | `/api/books/{id}` | ADMIN, не кітап иесі SELLER |
| POST | `/api/books/{id}/restock?amount=N` | ADMIN, не кітап иесі SELLER (санына N қосады) |
| GET | `/api/categories[/{id}]` | барлығы |
| POST/PUT/DELETE | `/api/categories[/{id}]` | ADMIN |
| GET | `/api/admin/users` | ADMIN |
| POST/DELETE | `/api/admin/users/{id}/roles/{SELLER\|ADMIN}` | ADMIN |
| POST | `/api/cart` | кірген |
| POST | `/api/cart/{id}/add?bookId=&quantity=` | кірген (өз себеті, қойма тексеріледі) |
| GET | `/api/cart/{id}` | кірген (өз себеті) |
| PUT | `/api/cart/{id}/items/{bookId}?quantity=` | кірген (0 болса өшіреді) |
| DELETE | `/api/cart/{id}/items/{bookId}`, `/api/cart/{id}` | кірген |
| POST | `/api/orders` | кірген (сома серверде есептеледі, қойма азаяды) |
| GET | `/api/orders/my` | кірген |
| GET | `/api/orders`, `/api/orders/{id}` | ADMIN |
| PUT | `/api/orders/{id}/status?status=` | ADMIN |

Статустар: `NEW`, `PROCESSING`, `SHIPPED`, `COMPLETED`, `CANCELLED`.
Қойма жетпесе, API `409` және `Oops! ...` хабарын қайтарады.# BookStore
project
