ALTER TABLE blank_location DROP COLUMN latitude;
ALTER TABLE blank_location DROP COLUMN longitude;

ALTER TABLE blank_location ADD COLUMN latitude VARCHAR(20);
ALTER TABLE blank_location ADD COLUMN longitude VARCHAR(20);