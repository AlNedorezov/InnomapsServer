# InnomapsServer
Innomaps application server component

/springBootApp - RESTful Web-service

- /springBootApp/src/main/java/db/          - contains classes that represent database tables

- /springBootApp/src/main/java/events/      - contains classes for syncrinization of the events from google calendar 
                                            to the database
                                            
- /springBootApp/src/main/java/mail/        - contains class with method that sends emails

- /springBootApp/src/main/java/pathfinding/ - contains classes that are used for coordinates graph building,
                                            finding shortest path between two coordinates and finding the closest
                                            coordinate to the one given
                                            

- /springBootApp/src/main/java/rest/        - main package, contains REST controllers and Application class

- /springBootApp/src/main/java/xmlToDB/     - classes that are used for transffering coorinates and graph data from xml file
                                            to the database

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/cf013c6f8cf8469d888995cb75a2a16f)](https://www.codacy.com/app/al73rus/InnomapsServer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlNedorezov/InnomapsServer&amp;utm_campaign=Badge_Grade)
