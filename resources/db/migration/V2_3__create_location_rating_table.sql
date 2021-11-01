CREATE TABLE blank_location_rating
(
   location_id integer NOT NULL,
   user_id integer NOT NULL,
   rating smallint NOT NULL,
   last_update timestamp NOT NULL,

   CONSTRAINT blank_location_rating_pk PRIMARY KEY (location_id, user_id)
);

   
ALTER TABLE blank_location_rating ADD CONSTRAINT blank_location_rating_loc_id_fk FOREIGN KEY(location_id) REFERENCES blank_location(location_id);
ALTER TABLE blank_location_rating ADD CONSTRAINT blank_location_rating_user_id_fk FOREIGN KEY(user_id) REFERENCES blank_user(id);

CREATE VIEW blank_location_rating_view AS
SELECT location_id, avg(rating)
FROM blank_location_rating
GROUP BY location_id;
  