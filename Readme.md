Тестовое задание: Backend-разработчик

Цель задания:

Разработать REST API для бронирования столов в ресторане.

Требования:

Обязательные:

    • Фреймворк: Spring Boot (Java).
    • База данных: PostgreSQL.
    • ORM:  JPA (Java).
    • Авторизация: JWT (JSON Web Token) для аутентификации пользователей.
    • Обработка исключений
    • Принципы SOLID
    • Чистая архитектура
    • Репозиторий Github: Код должен быть размещен в публичном репозитории на Github.
    • Сборка проекта: Реализовать сборку проекта с помощью инструментов (Maven)
    • Документация: Использовать Swagger для автоматической генерации документации API.

Не допускается:

    • Использование готовых решений с открытого кода.
    • Небрежное выполнение задания.

Плюсом будет:

    • Docker: Написать Dockerfile и выложить образ на Docker Hub.
    • Асинхронное программирование: Применить асинхронные операции (с использованием Async для Java).

Функционал:

    • Регистрация/авторизация пользователя:
        * Регистрация:
            * Метод: POST
            * Параметры: email, password, phone_number, name.
            * Ответ: JWT токен с временем жизни 1 час.
        * Авторизация:
            * Метод: POST
            * Параметры: email, password.
            * Ответ: JWT токен с временем жизни 1 час.
    • Бронирование столов:
        * Получение доступных столов:
            * Метод: GET
            * Параметры: date, time.
            * Ограничения:
                * Время бронирования: с 12:00 до 22:00.
                * Доступные столы: 7 столов на 2 персоны, 6 столов – 3 персоны, 3 стола - 6 персон.
                * Стол с занятыми местами считается недоступным для бронирования.
            * Ответ: JSON с информацией о доступных столах.
        * Бронирование:
            * Метод: POST
            * Параметры: table_id, date, time.
            * Бронирование: на 2 часа.
            * Ответ: сообщение об успешном бронировании.
        * Получение забронированных столов:
            * Метод: GET
            * Ответ: JSON с информацией о забронированных столах пользователя.
        * Отмена бронирования:
            * Метод: DELETE
            * Параметры: table_id.
            * Ограничение: отмена возможна не позднее чем за 1 час до времени бронирования.
            * Ответ: сообщение об успешном отмене бронирования.
        * Изменение бронирования:
            * Метод: PUT
            * Параметры: table_id, date, time.
            * Ответ: сообщение об успешном изменении бронирования.

Дополнительно:

    • Диаграмма БД: Предоставить диаграмму базы данных с описанием таблиц и связей.
    • Тестирование: Написать юнит-тесты для проверки функциональности API.

Критерии оценки:

    • Стиль кода: Соблюдение стандартов кодирования.
    • Логическая структура проекта: Правильная организация кода в модули и классы.
    • Соблюдение стандартов: Использование рекомендуемых конфигураций для выбранного фреймворка.
    • Реализация решений: Корректная реализация выбранных технологий и паттернов проектирования.

Варианты реализации:

• Java/Spring Boot:
* База данных: MS SQL.
* ORM: JPA.
* Авторизация: JWT.