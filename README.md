# p2p
as a ring structure, with server.java as a center server that contains the large file. Then devide function devides the larger file into small chunks and assigned them to coreesponding clients.
Client1         port: 8081   connect with 2 and 5
Client2         port: 8082   1,3
Client3               8083   2,4
Client4               8084   3,5
Client5               8085   1,4

Client1  as server: port 8015 listen to client5; port 8012 listen to Client2 
Client2  as server: port 8021 listen to client1; port 8023 listen to client3
