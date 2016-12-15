DROP DATABASE IF EXISTS releve_ibmr;
CREATE DATABASE releve_ibmr;
USE releve_ibmr;
CREATE USER ibmr_user IDENTIFIED BY 'ibmr_pass';
GRANT ALL PRIVILEGES ON releve_ibmr.* TO ibmr_user;
