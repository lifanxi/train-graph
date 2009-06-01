#ifndef TRAIN_INCLUDED
#define TRAIN_INCLUDED

#include <string>
#include <vector>
using namespace std;

class TrainADData
{
public:
    string Station;
    string Arrival;
    string Departural;
};

class Train
{
public:
    string Number;
    string From;
    string To;

    vector <TrainADData> Detail;

    bool ReadFromFile(string filename);
    bool SaveToFile(string filename);
};

#endif
