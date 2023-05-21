Схема базы данных

![Диаграмма базы данных](/database_diagram.png)

Примечания:
1. В таблице user_friends_status тип status - enum.
2. В этой же таблице user_id - id пользователя, инициировавшего добавление в друзья, 
   friend_id - id пользователя, подтверждающего статус дружбы.


Пример запроса к базе, возвращающего логины пользователей, которые отправили запрос на добавление в друзья к
пользователю с логином login3 и статус дружбы которых не подтвержден:
    
    SELECT DISTINCT
           u.login
    FROM users AS u
    INNER JOIN user_friends_status AS ufs ON u.users_id = ufs.user_id
    INNER JOIN users AS ua ON ua.users_id = ufs.friend_id
    WHERE ufs.status_of_friendship = 'Requested' AND ua.login = 'login3';
