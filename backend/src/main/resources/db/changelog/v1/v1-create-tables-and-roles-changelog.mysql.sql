-- liquibase formatted sql

-- changeset david.rabko:1706093701676-0.1
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `users_roles`;
SET FOREIGN_KEY_CHECKS = 1;

-- changeset david.rabko:1706093701676-1
CREATE TABLE `role` (name VARCHAR(30) NOT NULL, CONSTRAINT PK_ROLE PRIMARY KEY (name), UNIQUE (name));

-- changeset david.rabko:1706093701676-2
CREATE TABLE user (id INT AUTO_INCREMENT NOT NULL, username VARCHAR(50) NOT NULL, password CHAR(80) NOT NULL, enabled TINYINT(3) NOT NULL, CONSTRAINT PK_USER PRIMARY KEY (id));

-- changeset david.rabko:1706093701676-3
CREATE TABLE users_roles (user_id INT NOT NULL, role_name VARCHAR(30) NOT NULL, CONSTRAINT PK_USERS_ROLES PRIMARY KEY (user_id, role_name));

-- changeset david.rabko:1706093701676-4
CREATE INDEX FK_ROLE_namex ON users_roles(role_name);

-- changeset david.rabko:1706093701676-5
ALTER TABLE users_roles ADD CONSTRAINT FK_ROLE FOREIGN KEY (role_name) REFERENCES `role` (name) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset david.rabko:1706093701676-6
ALTER TABLE users_roles ADD CONSTRAINT FK_USER_05 FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset david.rabko:1706093701676-7
INSERT INTO role (name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER')

