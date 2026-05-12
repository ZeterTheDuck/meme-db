# About
A basic web server for hosting, tagging, and managing memes or other visual files. This server was created with the intention of hosting funny images and videos, but it could be tailored to your own needs.

# Installation & Setup
You may need to add the following directories (Github doesn't let you add empty directories)
* `src/main/resources/static/files`
* `src/main/resources/static/files/thumbnails`

Files and thumbnails for videos will get stored here.

## Database
This server uses MySQL for the database with JDBC, you'll need to set that up as well. In [application.properties], fill in the empty lines with the relavant information from your database program.
```
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=
```
# Usage
See [User Guide] for how the server works for clients.

[application.properties]: /src/main/resources/application.properties
[User Guide]: USER_GUIDE.md