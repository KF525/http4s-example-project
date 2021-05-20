
###About Application:


###Running Application:

The app environment is defined within its Dockerfile. The services that make up the app are defined in the docker-compose.yml file which enable them to be run together in an isolated environment.  An image is a template for the desired environment and generatesa container when run. Each container should contain a single process or service. Docker-compose lets us define all 
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

Define side effect, but we execute it at the latest stage -- push side effects to border of our program. Because the Cats IO is a Monad, we can compose effects. 

TODO:
- Tests
- README
- User
- Think through models/tables
- Working compound poem creation
- Authentication
- Make your compound poems private
- metrics
- Elastic Search data store?
 