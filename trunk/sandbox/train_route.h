#ifndef TRAIN_ROUTE_INCLUDED
#define TRAIN_ROUTE_INCLUDED

#include <string>
#include <vector>
using namespace std;

class TrainStation
{
public:
    string Name;
    double Pos;
    int Level;
    bool IsHidden;
};

class TrainRoute
{
public:
    string Name;
    double Length;
    vector <TrainStation> Stations;

    bool ReadFromFile(string filename);
    bool SaveToFile(string filename);
};

#endif
