CREATE TABLE blank_user
(
  id serial NOT NULL,
  username varchar(100) NOT NULL,
  password varchar(512) NOT NULL,
  first_name varchar(256) NOT NULL,
  last_name varchar(256) NOT NULL,
  email varchar(200) NOT NULL,
  dob DATE NOT NULL,
  status varchar(20) NOT NULL DEFAULT 'NEW',
  token varchar(100),
  user_recommend  varchar(200),

  CONSTRAINT blank_user_pk PRIMARY KEY (id),
  CONSTRAINT blank_user_username_unq UNIQUE (username),
  CONSTRAINT blank_user_email_unq UNIQUE (email),
  CONSTRAINT blank_user_token_unq UNIQUE (token)
);

CREATE INDEX blank_user_role_idx ON blank_user(status);
CREATE INDEX blank_user_dob_idx ON blank_user(dob);