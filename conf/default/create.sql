-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-05-16 18:10:18.228

-- tables
-- Table: computer
CREATE TABLE computer (
    name varchar(50) NULL,
    ip varchar(20) NOT NULL,
    mac varchar(17) NULL,
    ssh_user varchar(32) NOT NULL,
    ssh_password text NOT NULL,
    description text NULL,
    room_id bigint NULL,
    CONSTRAINT computer_pk PRIMARY KEY (ip)
) COMMENT ''
    COMMENT 'Computador que será la unidad a controlar por la aplicación. Debe tener usuario y contraseña para conectarse por ssh';

CREATE INDEX Equipo_idx_ip ON computer (ip);

CREATE INDEX Equipo_idx_mac ON computer (mac);

CREATE INDEX Equipo_idx_sala ON computer (room_id);

-- Table: computer_session
CREATE TABLE computer_session (
    connection_time timestamp NOT NULL,
    computer_ip varchar(20) NOT NULL,
    active bool NULL,
    connected_user varchar(32) NULL,
    CONSTRAINT session_pk PRIMARY KEY (connection_time,computer_ip)
) COMMENT ''
    COMMENT 'Conexión de usuario en un equipo';

CREATE INDEX Sesion_idx_fecha ON computer_session (connection_time);

CREATE INDEX Sesion_idx_equipo ON computer_session (computer_ip);

-- Table: computer_state
CREATE TABLE computer_state (
    computer_ip varchar(20) NOT NULL,
    registered_date timestamp NOT NULL,
    description text NOT NULL,
    CONSTRAINT computerstate_pk PRIMARY KEY (computer_ip,registered_date)
);

CREATE INDEX estado_idx_ip ON computer_state (computer_ip);

CREATE INDEX estado_idx_fecha ON computer_state (registered_date);

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
    superuser bool NOT NULL,
    sent_datetime timestamp NOT NULL,
    interrupt bool NOT NULL,
    command text NOT NULL,
    web_user varchar(32) NOT NULL,
    CONSTRAINT orden_pk PRIMARY KEY (sent_datetime)
) COMMENT ''
    COMMENT 'OrdenSSH enviada a un equipo.';

CREATE INDEX OrdenSSH_idx_id ON ssh_order (sent_datetime);

-- Table: ssh_order_to_computer
CREATE TABLE ssh_order_to_computer (
    computer_ip varchar(20) NOT NULL,
    ssh_order_datetime timestamp NOT NULL,
    result text NULL,
    exit_code int NULL,
    CONSTRAINT ssh_order_to_computer_pk PRIMARY KEY (computer_ip,ssh_order_datetime)
);

-- Table: suggestion
CREATE TABLE suggestion (
    id bigint NOT NULL AUTO_INCREMENT,
    suggestion_text text NULL,
    registered_date timestamp NOT NULL,
    username varchar(32) NOT NULL,
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

-- foreign keys
-- Reference: OrdenSSH_UsuarioWeb (table: ssh_order)
ALTER TABLE ssh_order ADD CONSTRAINT OrdenSSH_UsuarioWeb FOREIGN KEY OrdenSSH_UsuarioWeb (web_user)
REFERENCES `user` (username);

-- Reference: computer (table: ssh_order_to_computer)
ALTER TABLE ssh_order_to_computer ADD CONSTRAINT computer FOREIGN KEY computer (computer_ip)
REFERENCES computer (ip);

-- Reference: computer_room_fk (table: computer)
ALTER TABLE computer ADD CONSTRAINT computer_room_fk FOREIGN KEY computer_room_fk (room_id)
REFERENCES room (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT;

-- Reference: computer_state_computer_fk (table: computer_state)
ALTER TABLE computer_state ADD CONSTRAINT computer_state_computer_fk FOREIGN KEY computer_state_computer_fk (computer_ip)
REFERENCES computer (ip);

-- Reference: room_laboratory_fk (table: room)
ALTER TABLE room ADD CONSTRAINT room_laboratory_fk FOREIGN KEY room_laboratory_fk (laboratory_id)
REFERENCES laboratory (id);

-- Reference: session_computer_fk (table: computer_session)
ALTER TABLE computer_session ADD CONSTRAINT session_computer_fk FOREIGN KEY session_computer_fk (computer_ip)
REFERENCES computer (ip);

-- Reference: ssh_order_to_computer_ssh_order (table: ssh_order_to_computer)
ALTER TABLE ssh_order_to_computer ADD CONSTRAINT ssh_order_to_computer_ssh_order FOREIGN KEY ssh_order_to_computer_ssh_order (ssh_order_datetime)
REFERENCES ssh_order (sent_datetime);

-- Reference: suggestion_user_fk (table: suggestion)
ALTER TABLE suggestion ADD CONSTRAINT suggestion_user_fk FOREIGN KEY suggestion_user_fk (username)
REFERENCES `user` (username);

-- Reference: usuarioweb_rolusuario_fk (table: user)
ALTER TABLE `user` ADD CONSTRAINT usuarioweb_rolusuario_fk FOREIGN KEY usuarioweb_rolusuario_fk (role)
REFERENCES role (id);

-- End of file.
