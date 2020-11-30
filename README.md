docker network create --driver bridge prj3-network
docker build . -t prj3
docker run --rm -it --name hostname1 --hostname hostname1 --network prj3-network prj3
docker run --rm -it --name hostname2 --hostname hostname2 --network prj3-network prj3
docker run --rm -it --name hostname3 --hostname hostname3 --network prj3-network prj3
docker run --rm -it --name hostname4 --hostname hostname4 --network prj3-network prj3
docker run --rm -it --name hostname5 --hostname hostname5 --network prj3-network prj3