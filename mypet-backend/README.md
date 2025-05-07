# My Pet Project Details
This project is the back end for the My Pet project. The purpose of this back end is to receive from and send data to the client to:
- Manage pets (and all related objects/actions)
- Manage game loop
- Manage authentication
- Manage communication with the database

To make use of this project, the frontend client must be running to receive and send data to the client.  
However, unlike the client, this server can be run individually to accept requests via it's API end points.

This backend server is not for the use of end users and should be run and managed by the application host.

Please note: many API endpoints require authentication.
<br/><br/>

# Developers
The technology tools used/incorporated to build this application include (but not limited to):
- Spring Boot (https://spring.io/projects/spring-boot) with Java
- Intellij (https://www.jetbrains.com/idea/)

NOTE: 
- This application does NOT set up a database for you. Please setup a database separately.  

- This server application will not launch unless you configure the database configurations stored in the project.  
The server at the current time depends on some locally defined security details. These details must be set by you  
before launching this application. You can view the database configuration file (as mentioned below) locally,  
or hardcode those details into the file itself (discouraged).<br/><br/>


Please read below for information regarding the codebase.

- API:
  - To check the API endpoints please see the published api documentation at:  
  https://documenter.getpostman.com/view/42478987/2sB2j7epgV  
  <br/>
  

- Important codebase locations<br/>
  - To run the server, build and run the application at path:  
    src/main/java/com/mypetserver/mypetserver/MypetserverApplication.java

  - To see the configurations for various communication protocols please see the directory at:  
    src/main/java/com/mypetserver/mypetserver/config

  - To configure the database connection please see the file at:  
    src/main/resources/application.properties  

  - Logs can be found at:  
    /logs<br/><br/>
    These logs can be configured at:  
    src/main/resources/logback-spring.xml
<br/><br/>

# Disclosures
This application development did involve usage of LLMs which include actions such as (but not limited to):
  - Coding (helping to build skeletal code)
  - Debugging (helping to find issues)
  - Testing (helping to develop test cases)