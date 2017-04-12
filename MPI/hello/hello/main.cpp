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
    
        MPI_Init(&argc, &argv);
    
      	int world_rank;
      	MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
    
      	int world_size;
      	MPI_Comm_size(MPI_COMM_WORLD, &world_size);
    
    int intsPerProcess = 0;
    
    if(world_rank == 0){ //if we are in the main process, then read the input and create the matrix and vector
        
        int numRows;
        IntVector2D matrix;
        IntVector vec;
        string s;
    
  		ifstream in("matrix.txt");
    
  		in >> numRows;
    
  		for(int i = 0; i <= numRows; i++){
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
        int rowsPerProcess = matrix.size()/argc;
        int extraRows = matrix.size()%argc;
        int columns = matrix[0].size();
        intsPerProcess = rowsPerProcess*columns;
        
        int* vectorArr;
        vectorArr = new int[columns];
        
        int* matrixArr;
        matrixArr = new int[matrix.size()*columns];
        
        for (int i = 0; i < matrix.size(); i++)
        {
            for (int j = 0; j < matrix[i].size(); j++)
            {
                matrixArr[i*columns+j] = matrix[i][j];
            }
            std::cout << '\n';
        }
        for(int i = 0; i <vec.size(); i++){
            vectorArr[i]=vec[i];
        }
        
        
        
    
    
/*  		for (std::vector<int>::iterator i = vec.begin(); i != vec.end(); ++i)
            std::cout << *i << ' ';
    
    //std::cout << '\n';
    //std::cout << numRows;
    
    for (int i = 0; i < matrix.size(); i++)
    {
        for (int j = 0; j < matrix[i].size(); j++)
        {
            std::cout << matrix[i][j];
            std::cout << ' ';
        }
        std::cout << '\n';
    }
*/

    
    }
    else{
        
    }
    MPI_Scatter(matrixArr, intsPerProcess, MPI_INT, receiveMatrixArr,
                intsPerProcess, MPI_INT, 0, MPI_COMM_WORLD);
    
    MPI_Finalize();
    
    return 0;
    
}
