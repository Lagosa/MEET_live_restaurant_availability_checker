CREATE TABLE blank_location_fulness
(
   id bigserial NOT NULL,
   location_id integer NOT NULL,
   user_id integer NOT NULL,
   rating smallint NOT NULL,
   last_update timestamp NOT NULL,

   CONSTRAINT blank_location_fulness_pk PRIMARY KEY (id)
);


ALTER TABLE blank_location_fulness ADD CONSTRAINT blank_location_fulness_loc_id_fk FOREIGN KEY(location_id) REFERENCES blank_location(location_id);
ALTER TABLE blank_location_fulness ADD CONSTRAINT blank_location_fulness_user_id_fk FOREIGN KEY(user_id) REFERENCES blank_user(id);

CREATE INDEX blank_location_fulnesslast_update_idx ON blank_location_fulness(last_update);