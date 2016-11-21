#![Aton](https://github.com/camilosampedro/Aton/raw/gh-pages/images/AtonLogoMedium.png) 

[![Build Status](https://travis-ci.org/camilosampedro/Aton.svg?branch=master)](https://travis-ci.org/camilosampedro/Aton)
[![GitHub version](https://badge.fury.io/gh/camilosampedro%2FAton.svg)](https://badge.fury.io/gh/camilosampedro%2FAton)
[![Code Climate](https://codeclimate.com/github/camilosampedro/Aton/badges/gpa.svg)](https://codeclimate.com/github/camilosampedro/Aton)
[![Issue Count](https://codeclimate.com/github/camilosampedro/Aton/badges/issue_count.svg)](https://codeclimate.com/github/camilosampedro/Aton)
[![Open Source Love](https://badges.frapsoft.com/os/v2/open-source.svg?v=102)](https://github.com/ellerbrock/open-source-badge/)
[![Open Source Love](https://badges.frapsoft.com/os/gpl/gpl.svg?v=102)](https://github.com/ellerbrock/open-source-badge/)
[![Join the chat at https://gitter.im/Aton-admin/Lobby](https://badges.gitter.im/Aton-admin/Lobby.svg)](https://gitter.im/Aton-admin/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


Web computer laboratory administrator.

![Screenshot](https://github.com/camilosampedro/Aton/raw/gh-pages/images/Aton_screenshot.png)

## Warning
This project is still in development stage. Use it under your own risk because it doesn't come with any kind of warranty.

## Dependencies

Before installing it is required:
 - `Java 8`
 - `MySQL 5.7`

## How to install
### Install from release file

- For more detailed instructions, please go to the [Installation wiki page](https://github.com/camilosampedro/Aton/wiki/How-to-install-Aton). 

Go to [releases page](https://github.com/camilosampedro/Aton/releases) and download the ones who fits you most.

#### Debian / Ubuntu / Linux Mint `.deb`
After downloading, execute:
```bash
sudo dpkg -i aton-VERSION.deb
mysql -p < /usr/share/aton/conf/default/create.sql
```

### Uninstalling from release file
#### Debian / Ubuntu / Linux Mint
```bash
sudo apt-get remove aton
```

### Compile from source code
#### Compile requirements
You will need to have installed:
 - [Lightbend Activator](https://www.lightbend.com/activator/download)


#### Clone GitHub repository
```bash
git clone https://github.com/camilosampedro/Aton.git
```

#### Try it in non-production mode
```bash
activator run
```

#### Compile it with activator
You can generate your own native package with the following syntax
```bash
activator [target]:packageBin
```
`target` can be replaced depending on the target package format. The following are included:
 - `rpm` for RPM based Linux distributions. You will need `rpmbuild` installed
 - `debian` for Debian based Linux distributions. You will need `dpkg` installed
 - `windows` for generating a MSI installation file. You will need `wix` installed
 - `jdkPackage` for generating a Java file.

## Service
When you install from a release file you will have a service called `aton` in your system (For reference, located in `/etc/init/aton.conf`) that can be managed with service:
 - __To start:__ `sudo service aton start`
 - __To restart:__ `sudo service aton restart`
 - __To stop:__ `sudo service aton stop`
 - __To check status:__ `sudo service aton status`

## Port
By default, Aton uses Play Framework's default port: `9000`. If you want to start Aton on another port, you can do it by editing the service script `/etc/init/aton.conf`. You will need to look for a segment similar to

```bash
# Start the process
script
  exec sudo -u aton bin/aton
end script
```

And add `-Dhttp.port=THE_NEW_PORT`

```bash
# Start the process
script
  exec sudo -u aton bin/aton -Dhttp.port=8080
end script
```

# Testing
Thanks to [LIS](lis.udea.edu.co), Aton could be tested on a real 80 computers environment. The system specifications was:
## Aton server
 - __Operating system:__ `Ubuntu 14.04.5 LTS`
 - __RAM:__ 512 mb
 - __Cores:__ 1 core @ 1.60GHz
 
## Managed computers
 - __Operatings system:__ `Linux Mint Debian Edition 2`
 - __RAM:__ ~4 gb (Some have 2gb, others have 8gb)
 - __Processors:__ Intel core 2 Duo and Intel core i7
 - A room full of software engineering students! Most of them use IDEs, test commands, navigate through SSH.
 - LDAP user management.
 
