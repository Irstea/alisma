
ALTER TABLE releve_ibmr.Points_prelev ADD COLUMN wgs84_x VARCHAR(20) AFTER id_station;

ALTER TABLE releve_ibmr.Points_prelev ADD COLUMN wgs84_y VARCHAR(20) AFTER wgs84_x;