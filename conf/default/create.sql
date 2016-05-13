-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-05-07 18:16:14.791

-- tables
-- Table: RolUsuario
CREATE TABLE RolUsuario (
    rolusuario_id int NOT NULL,
    rolusuario_rol varchar(50) NOT NULL,
    CONSTRAINT rolusuario_pk PRIMARY KEY (rolusuario_id)
);

-- Table: UsuarioWeb
CREATE TABLE UsuarioWeb (
    usuarioweb_usuario varchar(32) NOT NULL,
    usuarioweb_password text NOT NULL,
    usuarioweb_rolusuario_id int NOT NULL,
    CONSTRAINT UsuarioWeb_pk PRIMARY KEY (usuarioweb_usuario)
);

-- Table: computer
CREATE TABLE computer (
    name varchar(50) NOT NULL,
    ip varchar(20) NOT NULL,
    mac varchar(17) NOT NULL,
    ssh_user varchar(32) NULL,
    ssh_password text NULL,
    description text NOT NULL,
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
    superuser bool NULL,
    interrupt bool NULL,
    command text NULL,
    result text NOT NULL,
    exit_code int NOT NULL,
    web_user varchar(32) NOT NULL,
    CONSTRAINT orden_pk PRIMARY KEY (id)
) COMMENT ''
COMMENT 'OrdenSSH enviada a un equipo.';

CREATE INDEX OrdenSSH_idx_id ON ssh_order (id);

-- Table: suggestion
CREATE TABLE suggestion (
    id bigint NOT NULL AUTO_INCREMENT,
    suggestion_text text NULL,
    registered_date timestamp NULL,
    CONSTRAINT suggestion_pk PRIMARY KEY (id)
) COMMENT ''
COMMENT 'Sugerencia hecha por un usuario a los administradores.';

-- foreign keys
-- Reference: OrdenSSH_UsuarioWeb (table: ssh_order)
ALTER TABLE ssh_order ADD CONSTRAINT OrdenSSH_UsuarioWeb FOREIGN KEY OrdenSSH_UsuarioWeb (web_user)
    REFERENCES UsuarioWeb (usuarioweb_usuario);

-- Reference: computer_room_fk (table: computer)
ALTER TABLE computer ADD CONSTRAINT computer_room_fk FOREIGN KEY computer_room_fk (room_id)
    REFERENCES room (id);

-- Reference: computer_state_computer_fk (table: computer_state)
ALTER TABLE computer_state ADD CONSTRAINT computer_state_computer_fk FOREIGN KEY computer_state_computer_fk (computer_ip)
    REFERENCES computer (ip);

-- Reference: room_laboratory_fk (table: room)
ALTER TABLE room ADD CONSTRAINT room_laboratory_fk FOREIGN KEY room_laboratory_fk (laboratory_id)
    REFERENCES laboratory (id);

-- Reference: session_computer_fk (table: computer_session)
ALTER TABLE computer_session ADD CONSTRAINT session_computer_fk FOREIGN KEY session_computer_fk (computer_ip)
    REFERENCES computer (ip);

-- Reference: usuarioweb_rolusuario_fk (table: UsuarioWeb)
ALTER TABLE UsuarioWeb ADD CONSTRAINT usuarioweb_rolusuario_fk FOREIGN KEY usuarioweb_rolusuario_fk (usuarioweb_rolusuario_id)
    REFERENCES RolUsuario (rolusuario_id);

-- End of file.
