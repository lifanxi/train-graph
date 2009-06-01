#include "train.h"
#include <fstream>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <sstream>
using namespace std;


bool Train::ReadFromFile(string filename)
{
    ifstream inFile(filename.c_str());
    inFile >> Number;
    inFile >> From;
    inFile >> To;
    while (!inFile.eof())
    {
        string line;
        getline(inFile, line);
        if ((line.size() == 0) || (line[0] == '\r') || (line[1] == '\n'))
            continue;
//        stringstream linestream(line);
        TrainADData data;
        char * t = strdup(line.c_str());
        data.Station = strtok(t, ",");
        data.Arrival = strtok(NULL, ",");
        data.Departural = strtok(NULL, ",");
        free(t);
        
        Detail.push_back(data);
    }
    inFile.close();
    return true;
}

bool Train::SaveToFile(string filename)
{
    return true;
}
