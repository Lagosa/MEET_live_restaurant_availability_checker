CREATE TABLE blank_location
(
  location_id serial NOT NULL,
  location_name varchar (100) NOT NULL, 
  address varchar(150) NOT NULL,
  url varchar(1000) NOT NULL, 
  phone_number varchar(15),
  email varchar(100),
  latitude decimal(14,10),
  longitude decimal(14,10),
  ordering_phone varchar(15),
  ordering_email varchar(100),
  small_imagine_url varchar(150),
  large_imagine_url varchar(150),
  logo_url varchar(150),
  name_without_accents varchar(100) NOT NULL,
  status varchar(10) NOT NULL,

  CONSTRAINT blank_location_pk PRIMARY KEY (location_id),
  CONSTRAINT blank_location_name_unq UNIQUE (location_name)
);


CREATE INDEX blank_location_name_without_accents_idx ON blank_location(name_without_accents);
CREATE INDEX blank_location_status_idx ON blank_location(status);