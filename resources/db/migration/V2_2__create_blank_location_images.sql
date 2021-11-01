CREATE TABLE blank_location_images
(
  image_id serial NOT NULL,
  location_id integer NOT NULL, 
  image_url varchar(1000) NOT NULL,

  CONSTRAINT blank_location_images_pk PRIMARY KEY (image_id),
  CONSTRAINT blank_location_images_url_unq UNIQUE (image_url)
  
);


ALTER TABLE blank_location_images ADD CONSTRAINT blank_location_images_loc_id_fk FOREIGN KEY(location_id) REFERENCES blank_location(location_id);