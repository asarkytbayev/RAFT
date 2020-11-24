docker network create --driver bridge prj3-network
docker build . -t prj3
docker run --rm -it --name server1 --hostname server1 --network prj3-network prj3
docker run --rm -it --name server2 --hostname server2 --network prj3-network prj3
docker run --rm -it --name server3 --hostname server3 --network prj3-network prj3
docker run --rm -it --name server4 --hostname server4 --network prj3-network prj3
docker run --rm -it --name server5 --hostname server5 --network prj3-network prj3