[![Build Status](https://travis-ci.org/ProjectAton/AtonLab.svg?branch=master)](https://travis-ci.org/ProjectAton/AtonLab)
# ATON
Administrador de laboratorios de computadoras.

## Advertencia
Este proyecto todavía se encuentra en fase de desarrollo.

## Como instalar
### 1. Descargar una copia de este proyecto
```bash
git clone https://github.com/ProjectAton/Aton.git
```

### 2. Instalar postgresql. Ir al paso 4 si ya se tiene configurado PostgreSQL.

Instalar la base de datos PostgreSQL, usando el gestor de paquetes por defecto o con cualquier otro método.

**Instalación en Ubuntu - Debian**

```bash
sudo apt-get install postgresql
```

**Instalación en RHEL**

```bash
sudo yum install postgresql-server
```

### 3. Asignar un password para acceder a PostgreSQL:

**Ingresar al shell de PostgreSQL:**

```bash
sudo -u postgres psql
```

**Ejecutar para cambiar el password**

```bash
\password postgres
```

### 4. Importar la base de datos de database.sql
```bash
sudo -u postgres psql < database.sql
```

### 5. Modificar los archivos de configuración

#### `src/main/resources/aplicacion.properties`
En este archivo se encuentran todas las configuraciones de la base de datos. Las propiedades importantes son las siguientes:

**jdbc.url**
En este campo se inserta el URL de la base de datos. El formato es *jdbc:postgresql://host:puerto/basededatos*

**jdbc.username**
Nombre de usuario para acceder a la base de datos.

**jdbc.password**
Contraseña para acceder a la base de datos.

### 6. Compilar
Si no se tiene instalado maven instalar antes de este paso.

**Instalación en Ubuntu - Debian**
```bash
sudo apt-get install maven
```

**Instalación en RHEL**
```bash
sudo yum install maven
```

Ejecutar la compilación con Maven

```bash
mvn clean install
```

##