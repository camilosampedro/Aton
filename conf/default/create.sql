-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-06-15 20:07:56.171

DROP DATABASE IF EXISTS aton;
CREATE DATABASE aton;
USE aton;

-- tables
-- Table: computer
CREATE TABLE computer (
    name varchar(50) NULL,
    ip varchar(20) NOT NULL,
    ssh_user varchar(32) NOT NULL,
    ssh_password text NOT NULL,
    description text NULL,
    room_id bigint NULL,
    CONSTRAINT computer_pk PRIMARY KEY (ip)
) COMMENT ''
COMMENT 'Computador que será la unidad a controlar por la aplicación. Debe tener usuario y contraseña para conectarse por ssh';

CREATE INDEX Equipo_idx_ip ON computer (ip);

CREATE INDEX Equipo_idx_sala ON computer (room_id);

-- Table: computer_state
CREATE TABLE computer_state (
    computer_ip varchar(20) NOT NULL,
    registered_date timestamp NOT NULL,
    operating_system varchar(100) NULL,
    mac varchar(17) NULL,
    state_id int NOT NULL,
    CONSTRAINT computerstate_pk PRIMARY KEY (computer_ip,registered_date)
);

CREATE INDEX estado_idx_ip ON computer_state (computer_ip);

CREATE INDEX estado_idx_fecha ON computer_state (registered_date);

CREATE EVENT AutoDeleteOldComputerStates
ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 1 DAY
ON COMPLETION PRESERVE
DO
DELETE LOW_PRIORITY FROM aton.computer_state WHERE registered_date < DATE_SUB(NOW(), INTERVAL 7 DAY);

-- Table: connected_user
CREATE TABLE connected_user (
    id bigint NOT NULL AUTO_INCREMENT,
    username varchar(32) NOT NULL,
    computer_state_computer_ip varchar(20) NOT NULL,
    computer_state_registered_date timestamp NOT NULL,
    CONSTRAINT connected_user_pk PRIMARY KEY (id)
);

-- Table: laboratory
CREATE TABLE laboratory (
    id bigint NOT NULL AUTO_INCREMENT,
    name varchar(100) NOT NULL,
    location varchar(1000) NULL,
    administration varchar(1000) NULL,
    CONSTRAINT laboratorio_pk PRIMARY KEY (id)
) COMMENT ''
COMMENT 'Conjunto de salas que usualmente están en el mismo espacio.';

CREATE INDEX Laboratorio_idx_id ON laboratory (id);

-- Table: role
CREATE TABLE role (
    id int NOT NULL,
    description varchar(50) NOT NULL,
    CONSTRAINT rolusuario_pk PRIMARY KEY (id)
);

INSERT INTO role(id, description) VALUES(1,"Administrator");
INSERT INTO role(id, description) VALUES(2,"Normal user");

-- Table: room
CREATE TABLE room (
    id bigint NOT NULL AUTO_INCREMENT,
    name varchar(100) NOT NULL,
    audiovisual_resources text NULL,
    basic_tools text NULL,
    laboratory_id bigint NOT NULL,
    CONSTRAINT sala_pk PRIMARY KEY (id)
) COMMENT ''
COMMENT 'Conjunto de computadores que pueden ser controlados conjuntamente.';

CREATE INDEX Sala_idx_id ON room (id);

CREATE INDEX Sala_idx_laboratorio ON room (laboratory_id);

-- Table: ssh_order
CREATE TABLE ssh_order (
    id bigint NOT NULL AUTO_INCREMENT,
    superuser bool NOT NULL,
    sent_datetime timestamp NOT NULL,
    interrupt bool NOT NULL,
    command text NOT NULL,
    web_user varchar(32) NOT NULL,
    CONSTRAINT orden_pk PRIMARY KEY (id)
) COMMENT ''
COMMENT 'OrdenSSH enviada a un equipo.';

CREATE INDEX OrdenSSH_idx_id ON ssh_order (sent_datetime);

CREATE EVENT AutoDeleteOldSSHOrders
ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 1 DAY
ON COMPLETION PRESERVE
DO
DELETE LOW_PRIORITY FROM aton.ssh_order WHERE sent_datetime < DATE_SUB(NOW(), INTERVAL 7 DAY);

-- Table: ssh_order_to_computer
CREATE TABLE ssh_order_to_computer (
    computer_ip varchar(20) NOT NULL,
    sent_datetime timestamp NOT NULL,
    result text NULL,
    exit_code int NULL,
    ssh_order_id bigint NOT NULL,
    CONSTRAINT ssh_order_to_computer_pk PRIMARY KEY (computer_ip,ssh_order_id)
);

CREATE EVENT AutoDeleteOldSSHOrderToComputers
ON SCHEDULE AT CURRENT_TIMESTAMP + INTERVAL 1 DAY
ON COMPLETION PRESERVE
DO
DELETE LOW_PRIORITY FROM aton.ssh_order_to_computer WHERE sent_datetime < DATE_SUB(NOW(), INTERVAL 7 DAY);

-- Table: state
CREATE TABLE state (
    id int NOT NULL,
    code varchar(30) NOT NULL,
    CONSTRAINT state_pk PRIMARY KEY (id)
);

INSERT INTO state(id,code) VALUES(1,"state.connected");
INSERT INTO state(id,code) VALUES(2,"state.notconnected");
INSERT INTO state(id,code) VALUES(3,"state.authfailed");
INSERT INTO state(id,code) VALUES(4,"state.unknownerror");;

-- Table: suggestion
CREATE TABLE suggestion (
    id bigint NOT NULL AUTO_INCREMENT,
    suggestion_text text NOT NULL,
    registered_date timestamp NOT NULL,
    username varchar(32) NULL,
    CONSTRAINT suggestion_pk PRIMARY KEY (id)
) COMMENT ''
COMMENT 'Sugerencia hecha por un usuario a los administradores.';

-- Table: user
CREATE TABLE `user` (
    username varchar(32) NOT NULL,
    password text NOT NULL,
    role int NOT NULL,
    name varchar(100) NULL,
    CONSTRAINT user_pk PRIMARY KEY (username)
);

INSERT INTO user(username,password,role) VALUES("admin","adminaton",1);
INSERT INTO user(username,password,role) VALUES("Scheduled Checker","@JZhY4ut)3)Lp}9",1);

-- foreign keys
-- Reference: OrdenSSH_UsuarioWeb (table: ssh_order)
ALTER TABLE ssh_order ADD CONSTRAINT OrdenSSH_UsuarioWeb FOREIGN KEY OrdenSSH_UsuarioWeb (web_user)
    REFERENCES `user` (username)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: computer (table: ssh_order_to_computer)
ALTER TABLE ssh_order_to_computer ADD CONSTRAINT computer FOREIGN KEY computer (computer_ip)
    REFERENCES computer (ip)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: computer_room_fk (table: computer)
ALTER TABLE computer ADD CONSTRAINT computer_room_fk FOREIGN KEY computer_room_fk (room_id)
    REFERENCES room (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: computer_state_computer_fk (table: computer_state)
ALTER TABLE computer_state ADD CONSTRAINT computer_state_computer_fk FOREIGN KEY computer_state_computer_fk (computer_ip)
    REFERENCES computer (ip)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: connected_user_computer_state (table: connected_user)
ALTER TABLE connected_user ADD CONSTRAINT connected_user_computer_state FOREIGN KEY connected_user_computer_state (computer_state_computer_ip,computer_state_registered_date)
    REFERENCES computer_state (computer_ip,registered_date)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: room_laboratory_fk (table: room)
ALTER TABLE room ADD CONSTRAINT room_laboratory_fk FOREIGN KEY room_laboratory_fk (laboratory_id)
    REFERENCES laboratory (id);

-- Reference: ssh_order_to_computer_ssh_order (table: ssh_order_to_computer)
ALTER TABLE ssh_order_to_computer ADD CONSTRAINT ssh_order_to_computer_ssh_order FOREIGN KEY ssh_order_to_computer_ssh_order (ssh_order_id)
    REFERENCES ssh_order (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: state_fk (table: computer_state)
ALTER TABLE computer_state ADD CONSTRAINT state_fk FOREIGN KEY state_fk (state_id)
    REFERENCES state (id);

-- Reference: suggestion_user_fk (table: suggestion)
ALTER TABLE suggestion ADD CONSTRAINT suggestion_user_fk FOREIGN KEY suggestion_user_fk (username)
    REFERENCES `user` (username)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: usuarioweb_rolusuario_fk (table: user)
ALTER TABLE `user` ADD CONSTRAINT usuarioweb_rolusuario_fk FOREIGN KEY usuarioweb_rolusuario_fk (role)
    REFERENCES role (id);

CREATE USER 'aton'@'localhost' IDENTIFIED BY 'aton';
GRANT ALL PRIVILEGES ON aton.* TO 'aton'@'localhost' WITH GRANT OPTION;

-- End of file.
