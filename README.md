
###About Application:


###Running Application:

The app environment is defined within its Dockerfile. The services that make up the app are defined in the docker-compose.yml file which enable them to be run together in an isolated environment.  An image is a template for the desired environment and generates a container when run. Each container should contain a single process or service. Docker-compose lets us define all 
of our services in a single configuration file, and how those services are able to talk to one another. 
Start the application with `docker compose up -d` and stop the application with `docker compose down`. 
-----
This application makes use of:

- *Http4s*:

- *Monix*:  

- *Circe*: Library that handles Json input/output. Supports automatic generation of encoders and decoders (including case classes with normal types). 

- *Doobie*: Pure functional JDBC layer. Not an ORM. Write SQL queries with the ability to map query to scala types.

- *Cats Effect*: 

- *PureConfig:* 

TODO:
- README
- User
- Think through models/tables
- Authentication
- Make your compound poems private
- metrics
- Elastic Search data store?
 