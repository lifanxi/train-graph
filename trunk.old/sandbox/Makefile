
all:
	g++ -c -g main.cpp -o main.o `pkg-config --cflags gtk+-2.0 libglade-2.0` 
	g++ -c -g train_route.cpp -o train_route.o 
	g++ -c -g train.cpp -o train.o 
	g++ -o main main.o train.o train_route.o -export-dynamic  `pkg-config --libs gtk+-2.0 libglade-2.0` 
clean:
	rm -rf *.o main