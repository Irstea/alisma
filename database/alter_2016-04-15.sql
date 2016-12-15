ALTER TABLE `alisma`.`Stations` 
ADD UNIQUE INDEX `station_cd_station_dx` (`cd_station` ASC),
ADD UNIQUE INDEX `station_station_idx` (`station` ASC);


ALTER TABLE alisma.Stations MODIFY COLUMN x DOUBLE PRECISION;

ALTER TABLE alisma.Stations MODIFY COLUMN y DOUBLE PRECISION;
