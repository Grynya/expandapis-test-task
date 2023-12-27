CREATE DATABASE expandapisdb;

USE expandapisdb;

CREATE TABLE app_user (
                          id int PRIMARY KEY AUTO_INCREMENT,
                          username varchar(255),
                          password varchar(255));