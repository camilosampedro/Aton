# Contributing to Aton
Aton is an opensource, if you want to contribute to Aton development you can do it in a number of ways.

## Spread the word
You can always help Aton to grow by sharing it to people who could use it.

## Suggesting functionalities
Have you thought about sending images? Sharing your screen to the users? You can help sharing us functionalities that would make Aton greater! Just create a issue.

## Translating Aton
To translate Aton to your native language, you can visit [Aton Transifex project](https://www.transifex.com/universidad-de-antioquia/aton-computer-laboratory-administrator) and translate application Strings.

## Programming
To help programming in Aton, you can help designing and proramming `HTML` + `CSS` + `Javascript`|`Angular2`|`Typescript` frontend or programming `Scala` backend. You can look for __[bitesize](https://github.com/camilosampedro/Aton/labels/bitesize)__ issues to get a good start point. You can find more information on the [__Aton Wiki__](https://github.com/camilosampedro/Aton/wiki).

You can also use [Vagrant](https://github.com/camilosampedro/Aton/wiki/Using-Vagrant-for-running-Aton-(Development)) for running the application.

### Commit and Pull Request formats
When creating a pull request, please follow this simple format to keep the project mantainable and clean.

```markdown
**Simple commit/pull request comment**

**[Issue #](Link to issue, if any)**
1. Why this change is necessary?
**Explain why is it necessary to pull this request, it should be resolving an given issue**

2. How does it address the issue?
**Explain briefly what are the changes**

3. What side effects does this change have?
**Is it a big change or a small change?. Do you think it brokes some other part of the project?**
```

### Project structure
Aton has the following code structure:

```
.
├── app
│   ├── config
│   ├── controllers
│   │   ├── admin
│   │   └── api
│   ├── dao
│   │   └── impl
│   ├── model
│   │   ├── form
│   │   │   └── data
│   │   ├── json
│   │   └── table
│   ├── services
│   │   ├── exec
│   │   └── impl
│   └── views
├── app-2.11
├── build.sbt
├── conf
│   ├── application.conf
│   ├── default
│   │   └── create.sql
│   ├── logback.xml
│   ├── messages.en
│   ├── messages.es
│   ├── plugins.sbt
│   ├── production.conf
│   └── routes
├── CONTRIBUTING.md
├── LICENSE
├── public
│   ├── images
│   │   ├── favicon.ico
│   │   └── favicon.png
│   ├── javascripts
│   │   ├── general.js
│   │   └── hello.js
│   └── stylesheets
│       ├── global.css
│       ├── login.css
│       └── main.css
├── README.md
├── test
└── test-2.11
```

#### `app` folder
App folder has all Scala code: controllers, services, daos, pojos. Everything contained here is written in Scala (Or Twirl's `.scala.html`).

##### `controllers` package (folder)
Contains all the application controllers. They receive requests from the client and create responses for them. It has two packages: `admin` and `api`.

- `api` package contains controllers that are able to receive Json requests and create Json responses.

- `admin` package contains controllers that need an `admin` user role to be accessed.

#### `dao` package (folder)
DAO stands for _Data Access Object_, they are used to control flow between the application and the database. All tables has its own DAO and they include CRUD methods (_Create, Read, Update, Delete_) these DAOs are not ment to have _"business logic"_, so anything beside database actions should be placed on the `services` package.

All DAOs have an interface in `dao` and an implementation in `dao.impl`.

#### `model` package (folder)
This package contains all the POJOs (_Plain Old Java Object_) that will encapsulate __only__ information. This classes should be very simple.

`model` contains a `form` package for information that is received from client forms and should not be all the POJO (For example, computer registration does not need all the `computer` fields, like `state`). It also contains a `json` package for `json` conversions and a `table` package for a Slick _"mapping"_.

#### `services` package (folder)
This package contains some _"business logic"__ classes. Some of them are placed between `controllers` and `daos` for performing extra tasks. As `dao` package, every service class has an interface in `services` and an implementation in `services.impl`.

There is also a `services.exec.SSHFunction` that contains all SSH commands as Strings.

#### `views` package (folder)
This package contains all the views with Twirl format. These files are with `.scala.html`, but they are like Scala injected HTML, so here you can find HTML with a few Scala codes. Scala code are usually written with `@/* all scala code */` or `@{/* all scala code */}`.

### `build.sbt` file
This is a very important file. Here will be placed all dependencies and build instructions.

### `conf` folder
This folder contains Play Framework and Slick specific configurations.

#### `conf/application.conf` file
Play Framework and Slick `key: value` configurations.

#### `conf/default/create.sql` file
MySQL script that need to be run before the first Aton execution. It creates all the tables, fields, conections and a `aton` user.

#### `conf/logback.xml` file
Running log configurations.

#### `conf/messages.*` files
Contain key resources with specific language translations. These translations are given to the user based on their browser languages.

#### `conf/production.conf` file
Production configurations. Play key can be changed here.

#### `conf/routes` file
Contains all the URL routes of the application and what controller will respond them.

### `CONTRIBUTING.md`
This file.

### `public` folder
Includes all files that can be accessed without an explicit controller. For example `javascript/general.js` can be accessed with `/javascript/general.js`.

These files are `javascript` files, `stylesheets` CSS and `images`.
