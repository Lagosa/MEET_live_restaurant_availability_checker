insert into blank_location_rating (user_id, location_id, rating, last_update)
select u.id as user_id, l.location_id,
(round(random() * 10)::int % 5) as rating,
now() - random() * (timestamp '2018-08-01 00:00:00' - timestamp '2018-06-01 00:00:00') as last_update
from blank_user u, blank_location l;

delete from blank_location_rating where rating < 1;
