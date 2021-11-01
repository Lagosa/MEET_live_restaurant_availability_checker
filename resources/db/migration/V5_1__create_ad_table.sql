CREATE TABLE blank_ad
(
   location_id integer NOT NULL,
   ad_url varchar(150) NOT NULL,

   CONSTRAINT blank_ad_pk PRIMARY KEY (location_id)
);


ALTER TABLE blank_ad ADD CONSTRAINT blank_ad_loc_id_fk FOREIGN KEY(location_id) REFERENCES blank_location(location_id);