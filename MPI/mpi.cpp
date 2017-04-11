#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <mpi.h>
#include <assert.h>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <iostream>

typedef std::vector<int> IntVector;
typedef std::vector<IntVector> IntVector2D;

using namespace std;

int main(int argc, char** argv){

	// MPI_INIT(NULL, NULL);

 //  	int world_rank;
 //  	MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

 //  	int world_size;
 //  	MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  	int numRows;
  	IntVector2D matrix;
  	IntVector vec;
  	string s;

  //	if(world_rank == 0){ //if we are in the main process, then read the input and create the matrix and vector

  		ifstream in("matrix.txt");

  		in >> numRows;

  		for(int i = 0; i < numRows; i++){
  			std::getline(in, s);

  			matrix.push_back(IntVector());
        	IntVector& vBack = matrix.back();

        	istringstream ss(s);

        	copy(istream_iterator<int>(ss), istream_iterator<int>(), back_inserter(vBack));
  		}

  		ifstream in2("vector.txt");

  		int i;

  		while(in2 >> i){
  			vec.push_back(i);
  		}


  		for (std::vector<char>::const_iterator i = vec.begin(); i != path.end(); ++i)
   			std::cout << *i << ' ';

   		std::cout << '\n' << numRows;

   		for (int i = 0; i < vec.size(); i++)
		{
   			for (int j = 0; j < vec[i].size(); j++)
    		{
        		cout << vec[i][j];
    		}
		}

  	//}
}
