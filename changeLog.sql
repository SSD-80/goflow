-- change log
ALTER TABLE rider
    ADD COLUMN oauth_provider VARCHAR(30) NULL,
  ADD COLUMN oauth_sub VARCHAR(128) NULL;

ALTER TABLE driver
    ADD COLUMN oauth_provider VARCHAR(30) NULL,
    ADD COLUMN oauth_sub VARCHAR(128) NULL;

ALTER TABLE rider MODIFY password VARCHAR(250) NULL;

ALTER TABLE driver MODIFY password VARCHAR(250) NULL;