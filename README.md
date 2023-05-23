Схема базы данных

![Диаграмма базы данных](/database_diagram.png)

Примечания:
1. В таблице user_friends_status тип поля status_of_friendship - varchar с ограничениями на принимаемые значения
   (Confirmed или Unconfirmed).
2. В этой же таблице user_id - id пользователя, инициировавшего добавление в друзья, 
   friend_id - id пользователя, подтверждающего статус дружбы.


Пример запроса к базе, возвращающего логины пользователей, которые отправили запрос на добавление в друзья к
пользователю с логином login3 и статус дружбы которых не подтвержден:
    
    SELECT DISTINCT
           u.login
    FROM users AS u
    INNER JOIN user_friends_status AS ufs ON u.users_id = ufs.user_id
    INNER JOIN users AS ua ON ua.users_id = ufs.friend_id
    WHERE ufs.status_of_friendship = 'Unconfirmed' AND ua.login = 'login3';
