#include "train_route.h"
#include <fstream>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <sstream>
using namespace std;


bool TrainRoute::ReadFromFile(string filename)
{
    ifstream inFile(filename.c_str());
    inFile >> Name;
    inFile >> Length;
    while (!inFile.eof())
    {
        string line;
        getline(inFile, line);
        if ((line.size() == 0) || (line[0] == '\r') || (line[1] == '\n'))
            continue;
//        stringstream linestream(line);
        TrainStation ts;
        char * t = strdup(line.c_str());
        ts.Name = strtok(t, ",");
        ts.Pos = (double) atoi(strtok(NULL, ","));
        ts.Level = atoi(strtok(NULL, ","));
        ts.IsHidden = (strtok(NULL, ",")[0] == 't')?true:false;
        free(t);
        
//        linestream >> ts.Name >> "," >> ts.Pos >> ","  >> ts.Level >> "," >> ts.IsHidden;
        Stations.push_back(ts);
    }
    inFile.close();
    return true;
}

bool TrainRoute::SaveToFile(string filename)
{
    return true;
}
