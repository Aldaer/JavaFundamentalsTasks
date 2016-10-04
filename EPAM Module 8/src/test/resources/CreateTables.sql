CREATE TABLE AUTHORS (
  ID          BIGINT AUTO_INCREMENT PRIMARY KEY,
  FIRSTNAME   VARCHAR(30) NOT NULL,
  LASTNAME    VARCHAR(30) NOT NULL,
  MIDDLENAMES VARCHAR(30),
  SHORTNAME   VARCHAR(50) NOT NULL UNIQUE,
  BIRTHDATE   DATE
);

CREATE TABLE BOOKS (
  ID     BIGINT AUTO_INCREMENT PRIMARY KEY,
  AUTHOR VARCHAR(50),
  TITLE  VARCHAR(255) NOT NULL,
  FOREIGN KEY (AUTHOR) REFERENCES AUTHORS (SHORTNAME)
);