
About Application:


Running Application:
App environment is defined with Dockerfile. Services that make up app are defined in docker-compose.yml so they can be run together in an isolated environment. Run ```docker compose up -d``` and the Docker compose command starts and runs your entire app. Image is a template for the environment that I want to run and when you run an image you get a container. Each container should contain a single process/provide a single service that are able to talk to each other.
docker-compose lets us define all of our services in a single configuration file.



Http4s: 

Circe: Library that handles Json input/output. Supports automatic generation of encoders and decoders (including normal case classes with normal types). 

Doobie: Pure functional JDBC layer. Not an ORM.Actual write SQL queries. Ability to map query to scala types.

Cats Effect: 
Cats IO Monad: Define side effect, but we execute it at the latest stage -- push side effects to border of our program. Because the Cats IO is a Monad, we can compose effects. 

Doobie
