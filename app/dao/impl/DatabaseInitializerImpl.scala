package dao.impl

import com.google.inject.{Inject, Singleton}
import dao.DatabaseInitializer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import slick.profile.SqlAction

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by camilosampedro on 8/12/16.
  */
@Singleton
class DatabaseInitializerImpl @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends DatabaseInitializer with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def createTables: SqlAction[Int, NoStream, Effect] =
    sqlu"""-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2016-06-15 20:07:56.171


-- tables
-- Table: computer
CREATE TABLE IF NOT EXISTS computer (
  name         VARCHAR(50) NULL,
  ip           VARCHAR(20) NOT NULL,
  ssh_user     VARCHAR(32) NOT NULL,
  ssh_password TEXT        NOT NULL,
  description  TEXT        NULL,
  room_id      BIGINT      NULL,
  CONSTRAINT computer_pk PRIMARY KEY (ip)
);

CREATE INDEX IF NOT EXISTS Equipo_idx_ip
  ON computer (ip);

CREATE INDEX IF NOT EXISTS Equipo_idx_sala
  ON computer (room_id);

-- Table: computer_state
CREATE TABLE IF NOT EXISTS computer_state (
  computer_ip      VARCHAR(20)  NOT NULL,
  registered_date  TIMESTAMP    NOT NULL,
  operating_system VARCHAR(100) NULL,
  mac              VARCHAR(17)  NULL,
  state_id         INT          NOT NULL,
  CONSTRAINT computerstate_pk PRIMARY KEY (computer_ip, registered_date)
);

CREATE INDEX IF NOT EXISTS estado_idx_ip
  ON computer_state (computer_ip);

CREATE INDEX IF NOT EXISTS estado_idx_fecha
  ON computer_state (registered_date);

-- Table: connected_user
CREATE TABLE IF NOT EXISTS connected_user (
  id                             BIGINT      NOT NULL AUTO_INCREMENT,
  username                       VARCHAR(32) NOT NULL,
  computer_state_computer_ip     VARCHAR(20) NOT NULL,
  computer_state_registered_date TIMESTAMP   NOT NULL,
  CONSTRAINT connected_user_pk PRIMARY KEY (id)
);

-- Table: laboratory
CREATE TABLE IF NOT EXISTS laboratory (
  id             BIGINT        NOT NULL AUTO_INCREMENT,
  name           VARCHAR(100)  NOT NULL,
  location       VARCHAR(1000) NULL,
  administration VARCHAR(1000) NULL,
  CONSTRAINT laboratorio_pk PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS Laboratorio_idx_id
  ON laboratory (id);

-- Table: role
CREATE TABLE IF NOT EXISTS role (
  id          INT         NOT NULL,
  description VARCHAR(50) NOT NULL,
  CONSTRAINT rolusuario_pk PRIMARY KEY (id)
);

INSERT INTO role SELECT *
                 FROM (
                        SELECT
                          1,
                          'Administrator'
                        UNION
                        SELECT
                          2,
                          'Normal user'
                      ) x
                 WHERE NOT EXISTS(SELECT *
                                  FROM role);

-- Table: room
CREATE TABLE IF NOT EXISTS room (
  id                    BIGINT       NOT NULL AUTO_INCREMENT,
  name                  VARCHAR(100) NOT NULL,
  audiovisual_resources TEXT         NULL,
  basic_tools           TEXT         NULL,
  laboratory_id         BIGINT       NOT NULL,
  CONSTRAINT sala_pk PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS Sala_idx_id
  ON room (id);

CREATE INDEX IF NOT EXISTS Sala_idx_laboratorio
  ON room (laboratory_id);

-- Table: ssh_order
CREATE TABLE IF NOT EXISTS ssh_order (
  id            BIGINT      NOT NULL AUTO_INCREMENT,
  superuser     BOOL        NOT NULL,
  sent_datetime TIMESTAMP   NOT NULL,
  interrupt     BOOL        NOT NULL,
  command       TEXT        NOT NULL,
  web_user      VARCHAR(32) NOT NULL,
  CONSTRAINT orden_pk PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS OrdenSSH_idx_id
  ON ssh_order (sent_datetime);

-- Table: ssh_order_to_computer
CREATE TABLE IF NOT EXISTS ssh_order_to_computer (
  computer_ip   VARCHAR(20) NOT NULL,
  sent_datetime TIMESTAMP   NOT NULL,
  result        TEXT        NULL,
  exit_code     INT         NULL,
  ssh_order_id  BIGINT      NOT NULL,
  CONSTRAINT ssh_order_to_computer_pk PRIMARY KEY (computer_ip, ssh_order_id)
);

-- Table: state
CREATE TABLE IF NOT EXISTS state (
  id   INT         NOT NULL,
  code VARCHAR(30) NOT NULL,
  CONSTRAINT state_pk PRIMARY KEY (id)
);

INSERT INTO state SELECT *
                  FROM (
                         SELECT
                           1,
                           'state.connected'
                         UNION
                         SELECT
                           2,
                           'state.notconnected'
                         UNION
                         SELECT
                           3,
                           'state.authfailed'
                         UNION
                         SELECT
                           4,
                           'state.unknownerror'
                       ) x
                  WHERE NOT EXISTS(SELECT *
                                   FROM state);

-- Table: suggestion
CREATE TABLE IF NOT EXISTS suggestion (
  id              BIGINT      NOT NULL AUTO_INCREMENT,
  suggestion_text TEXT        NOT NULL,
  registered_date TIMESTAMP   NOT NULL,
  username        VARCHAR(32) NULL,
  CONSTRAINT suggestion_pk PRIMARY KEY (id)
);

-- Table: user
CREATE TABLE IF NOT EXISTS `user` (
  username VARCHAR(32)  NOT NULL,
  password TEXT         NOT NULL,
  role     INT          NOT NULL,
  name     VARCHAR(100) NULL,
  CONSTRAINT user_pk PRIMARY KEY (username)
);

INSERT INTO `user` SELECT *
                  FROM (
                         SELECT
                           'admin', 'adminaton', 1, ''
                         UNION
                         SELECT
                           'Scheduled Checker', '@JZhY4ut)3)Lp}9', 1, ''
                       ) x
                  WHERE NOT EXISTS(SELECT *
                                   FROM `user`);

-- foreign keys
-- Reference: OrdenSSH_UsuarioWeb (table: ssh_order)
ALTER TABLE ssh_order
  ADD CONSTRAINT IF NOT EXISTS OrdenSSH_UsuarioWeb FOREIGN KEY (web_user)
REFERENCES `user` (username)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: computer (table: ssh_order_to_computer)
ALTER TABLE ssh_order_to_computer
  ADD CONSTRAINT IF NOT EXISTS computer FOREIGN KEY (computer_ip)
REFERENCES computer (ip)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: computer_room_fk (table: computer)
ALTER TABLE computer
  ADD CONSTRAINT IF NOT EXISTS computer_room_fk FOREIGN KEY (room_id)
REFERENCES room (id)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: computer_state_computer_fk (table: computer_state)
ALTER TABLE computer_state
  ADD CONSTRAINT IF NOT EXISTS computer_state_computer_fk FOREIGN KEY (computer_ip)
REFERENCES computer (ip)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: connected_user_computer_state (table: connected_user)
ALTER TABLE connected_user
  ADD CONSTRAINT IF NOT EXISTS connected_user_computer_state FOREIGN KEY (computer_state_computer_ip, computer_state_registered_date)
REFERENCES computer_state (computer_ip, registered_date)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: room_laboratory_fk (table: room)
ALTER TABLE room
  ADD CONSTRAINT IF NOT EXISTS room_laboratory_fk FOREIGN KEY (laboratory_id)
REFERENCES laboratory (id);

-- Reference: ssh_order_to_computer_ssh_order (table: ssh_order_to_computer)
ALTER TABLE ssh_order_to_computer
  ADD CONSTRAINT IF NOT EXISTS ssh_order_to_computer_ssh_order FOREIGN KEY (ssh_order_id)
REFERENCES ssh_order (id)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: state_fk (table: computer_state)
ALTER TABLE computer_state
  ADD CONSTRAINT IF NOT EXISTS state_fk FOREIGN KEY (state_id)
REFERENCES state (id);

-- Reference: suggestion_user_fk (table: suggestion)
ALTER TABLE suggestion
  ADD CONSTRAINT IF NOT EXISTS suggestion_user_fk FOREIGN KEY (username)
REFERENCES `user` (username)
ON DELETE CASCADE
ON UPDATE RESTRICT;

-- Reference: usuarioweb_rolusuario_fk (table: user)
ALTER TABLE `user`
  ADD CONSTRAINT IF NOT EXISTS usuarioweb_rolusuario_fk FOREIGN KEY (role)
REFERENCES role (id);

-- End of file.
"""

  override def initialize(): Future[Int] = db.run {
    createTables
  }
}
