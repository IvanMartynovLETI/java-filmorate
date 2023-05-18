Схема базы данных

![database diagram](https://github.com/IvanMartynovLETI/java-filmorate/blob/add-database/database_diagram.png)

Примечания:
1. Типы status и rating - enum.
2. В таблице friendship_status id_requester - id пользователя, инициировавшего добавление в друзья, 
   id_addressed - id пользователя, подтверждающего статус дружбы.


Пример запроса к базе, возвращающего логины пользователей, которые отправили запрос на добавление в друзья к
пользователю с логином login3 и статус дружбы которых не подтвержден:

    SELECT u.login
    FROM users as u
    WHERE u.users_id IN (SELECT fs.id_requester
                         FROM friendship_status AS fs
                         WHERE fs.status_of_friendship='Requested' 
                         AND fs.id_addressed IN (SELECT u.users_id 
                                                 FROM users AS u
                                                 WHERE u.login='login3'
                                                 GROUP BY u.users_id
                                                 )
                         GROUP BY fs.id_requester)
    GROUP BY u.login;
