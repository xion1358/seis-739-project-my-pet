# My Pet Project Details
This repository contains both the frontend and backend application required to run the My Pet browser based game.

mypet-backend: 
- Contains the My Pet server which serves API endpoints and data to the client  

mypet-frontend: 
- Contains the My Pet UI which receives and requests data from the server

<br/>
To see more details please look at the README inside each project directory in this repository.<br/>
Further documentation (including diagrams and initial UI design) can be seen in the /Documents of this repository.
<br/><br/>

# Developers
Developers will want to see both project directories to understand how the server and frontend UI communicate with each other, but I will go through a brief summary below.

The My Pet server opens endpoints to be queried. These API endpoints are documented at https://documenter.getpostman.com/view/42478987/2sB2j7epgV  

Server (backend)
- The server requires the use of a database which should already be set up.   
- The server does NOT setup the database and neither does it have an embedded database.  
- The host should setup their own database to be used with the My Pet backend.  
    - Please see the /mypet-backend directory README for more details.
<br/><br/>

UI (frontend)
- The My Pet frontend sends requests as per the documentation to fetch data to render the pet and allow some simple pet management options.

<br/>