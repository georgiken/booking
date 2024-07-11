# Сервис бронирования столиков в ресторане

## Включает в себя:
### Для всех пользователей:

1) Регистрация по номеру телефона, email, паролю и имени с получением jwt-токена с временем жизни 1 час
2) Авторизация и аутентификация по email и паролю с выдачей jwt-токена с временем жизни 1 час
### Для авторизованных пользователей:
1) Получение столиков, доступных для бронирования, начиная с указанного времени в указанный день (GET-запрос, тело запроса: дата и время, ответ: список доступных столов, если таковые есть, иначе пустой список)
2) Бронирование столика по id стола (бронь доступна после 12:00 и до 22:00) (POST-запрос, тело запроса: id столика, дата и время бронирования, ответ: сообщение об успешном бронировании с информацией о бронировании (в ином случае сообщение о том, на каком параметре произошла ошибка))
3) Получение брони пользователем (GET-запрос, ответ: список забронированных пользователем столиков за всё время в обратном хронологическом порядке, если таковые есть, иначе пустой список)
4) Обновление брони пользователем (PUT-запрос, тело запроса: id столика, дата и время, на которое нужно изменить (только на доступное время), ответ: сообщение об успешном изменении бронирования с информацией о бронировании (в ином случае сообщение о том, на каком параметре произошла ошибка))
5) Отмена брони пользователем (Отмена бронирования доступна не менее чем за час до брони) (DELETE-запрос, тело запроса: id столика, ответ: сообщение об успешной отмене бронирования (в ином случае сообщение о том, на каком параметре произошла ошибка))

#### _Все запросы для авторизованных пользователей по умолчанию включают в себя jwt-токен, из которого сервис получает id пользователя_

## Реализация:
* Язык программирования и основной фреймворк — Java Spring Boot Web Framework
* База данных и взаимодействие с ней — PostgreSQL и Data JPA
* Безопасность (регистрация и авторизация, JWT-токены) — Spring Boot Security
* Документация — OpenAPI 3

## Для того, чтобы протестировать:
* Подключите свою базу данных в настройках src.main.resources.application.yml
* Создайте таблицы в БД, как указано в db_diagram.png (с произвольным количеством столиков и произвольной вместительностью)
* Запустите проект.
* перейдите по адресу http://localhost:8080/swagger-ui/index.html

# Планы на дальнейшее развитие проекта
### Данный сервис имеет потенциал для расширения и внедрения в различные сферы бизнеса.
### Вот некоторые из возможных направлений и улучшений, которые могут быть реализованы в будущем:

#### 1. Расширение функциональности для отелей
   Для внедрения в отели можно добавить следующие возможности:

* Бронирование номеров по аналогии с бронированием столиков, с учетом типа номера, даты заезда и выезда.
* Управление доступностью номеров в зависимости от сезона и текущей загруженности.
* Введение системы учета дополнительных услуг (завтрак, трансфер, экскурсии).
#### 2. Внедрение в кинотеатры
   Для адаптации системы бронирования для кинотеатров потребуется:

* Добавление функционала для бронирования мест в зале на конкретный сеанс.
* Управление сеансами, включая расписание, доступные фильмы и залы.
* Опция для выбора дополнительных услуг (попкорн, напитки и т.д.) при бронировании.
#### 3. Применение в конференц-залах и коворкингах
   Система может быть адаптирована для бронирования помещений для встреч и работы:

* Бронирование конференц-залов или рабочих мест на определенное время.
* Управление доступностью помещений в зависимости от текущих бронирований.
* Опции для заказа дополнительных услуг (оборудование, кофе-брейки).
#### 4. Адаптация для спортивных комплексов и фитнес-центров
   Для использования в спортивных учреждениях можно реализовать:

* Бронирование кортов, залов или тренажеров на определенное время.
* Управление расписанием занятий и доступностью тренеров.
* Интеграция с системами учета абонементов и оплат.
#### 5. Улучшение текущего функционала
*   Введение системы уведомлений для пользователей о предстоящих бронированиях и изменениях.
*   Улучшение интерфейса пользователя для более удобного взаимодействия с сервисом.
*   Добавление аналитики и отчетов для администраторов системы для лучшего управления ресурсами.
#### 6. Масштабируемость и интеграция
*   Возможность интеграции с внешними системами, такими как платежные шлюзы, CRM-системы, и системы учета.
*   Обеспечение масштабируемости системы для поддержки большого количества пользователей и бронирований одновременно.
##   Эти планы развития помогут не только расширить функциональность проекта, но и значительно увеличить его ценность для различных бизнесов, улучшая клиентский опыт и оптимизируя процессы управления ресурсами.
