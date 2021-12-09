
# Fault Tolerant Key-Value Store using Raft algorithm.

Presentation uploaded to Google Drive: https://drive.google.com/file/d/1Ygh8zZ43BwjGIlL4en_hmlioj6RzA62h/view?usp=sharing

We use Raft algorithm to maintain consistency among servers that host database.

The servers present as part of the database network are listed in the file `src/main/resources/peer_file.txt`.

##### Follow the steps below to create a simple network consisting of 5 database servers.
- Create a docker network by running
    > docker network create --driver bridge prj3-network  

- Create a docker image by running
   >docker build . -t prj3  

- Run 5 containers by using the image built above by running the 5 commands below.
    >docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname1 --hostname hostname1 --network prj3-network prj3  
    >
    >docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname2 --hostname hostname2 --network prj3-network prj3  
    >
    >docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname3 --hostname hostname3 --network prj3-network prj3    
    >
    >docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname4 --hostname hostname4 --network prj3-network prj3    
    >
    >docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname5 --hostname hostname5 --network prj3-network prj3    

-   Inspect IP of of the docker containers in the docker network.
  
    > docker container inspect DOCKER_CONTAINER  

-   Send network requests using postman by using the IP address obtained above. The requests get forwarded to the leader which then performs the corresponding            operation.
    > IP_ADDRESS_OF_DOCKER_CONTAINER:8080/upsert_key  
    
-   To crash a peer or leader use `docker kill <container_name>.

Logs get replicated across these servers. Leader-crash, follower crash, log replication, network partitions, insert key to the database, delete key from a database, fetch key from a database, crash and restore a database server scenarious can be tested. 
