docker network create --driver bridge prj3-network  
docker build . -t prj3  
docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname1 --hostname hostname1 --network prj3-network prj3  
docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname2 --hostname hostname2 --network prj3-network prj3  
docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname3 --hostname hostname3 --network prj3-network prj3    
docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname4 --hostname hostname4 --network prj3-network prj3    
docker run --rm -it -v "$(pwd)/src/main/resources":/app/data --name hostname5 --hostname hostname5 --network prj3-network prj3    
  
docker container inspect DOCKER_CONTAINER  
IP_ADDRESS_OF_DOCKER_CONTAINER:8080/upsert_key  
    
