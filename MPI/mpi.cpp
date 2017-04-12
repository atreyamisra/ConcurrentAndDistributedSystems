//sr39533_am73676
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

int vectorMult(vector<int>& inRow, vector<int>& inVec){
  int result = 0;
  for(int i = 0; i < inRow.size(); i++){
    result = result + (inRow[i]*inVec[i]);
  }
  return result;
}

int main(int argc, char** argv){
    
    MPI_Init(&argc, &argv);
    
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
  
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);
    
    if(world_rank == 0){ 

        //read the input and create the matrix and vector
        
        int numRows, numCols;
        IntVector2D mat;
        IntVector vec;
        string s;
    
  		  ifstream in("matrix.txt");
    
  		  in >> numRows;
    
  		  for(int i = 0; i <= numRows; i++){
              
            std::getline(in, s);
              if(i==0){
                  continue;
              }
            mat.push_back(IntVector());
            IntVector& vBack = mat.back();
            
            istringstream ss(s);
            
            copy(istream_iterator<int>(ss), istream_iterator<int>(), back_inserter(vBack));

        }

        numCols = mat[0].size();
    
  		  ifstream in2("vector.txt");
    
  		  int i;
    
  		  while(in2 >> i){
            vec.push_back(i);
          }

        //fails if the sizes do not match
        if(numCols != vec.size()){
            MPI_Finalize();
          return 0;
        }
    
        //convert the vectors into arrays for easier communication
        // int** mat;
        // mat = new int*[numRows];

        // for(int i = 0; i < numRows; i++)
        // { 
        //   mat[i] = new int[numCols];
        //   for(int j = 0; j < numCols; j++)
        //   {
        //     mat[i][j] = matrix[i][j];
        //   } 
        // }

        // int* vec;
        // vec = new int[numCols];    
  		  
        // for (int i = 0; i < numCols; i++){
        //   vec[i] = vect[i];
        // }

        //first send the size of our vector along with the vector itself

        for (int i = 0; i < world_size; i++){
          if (i != 0){
            MPI_Send(&numCols, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
          }
        }

        for (int i = 0; i < world_size; i++){
          if(i != 0){
            MPI_Send(&vec[0], numCols, MPI_INT, i, 1, MPI_COMM_WORLD);
          }
        }

        //send off each of the rows: first send the row number and then the row itself

        for(int i = 0; i < numRows; i++){
          if(i%world_size != 0){ //don't send a row to the parent process
            int target = i%world_size; //the target process

            MPI_Send(&i, 1, MPI_INT, target, 2, MPI_COMM_WORLD); //the row number

            MPI_Send(&mat[i][0], numCols, MPI_INT, target, 3, MPI_COMM_WORLD); //the row itself
              
          }
        }

        //send a stop message to end the other processes' while loops
        int stop = -1;

        for(int i = 0; i < world_size; i++){
          if(i != 0){
            MPI_Send(&stop, 1, MPI_INT, i, 2, MPI_COMM_WORLD);
          }
        }

        //get the results back from the individual processes and aggregate them

        int* result;
        result = new int[numRows];

        int val;

        for(int i = 0; i < numRows; i++){
            if(i%world_size != 0){ //don't send a row to the parent process

              MPI_Recv(&val, 1, MPI_INT, MPI_ANY_SOURCE, i, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

              result[i] = val;

            }
            else{
              result[i] = vectorMult(mat[i], vec); //calculate the dot product and place in result vector if this row belongs to the root
            }
        }

        //Print out the result array to a file

        FILE *out = fopen("result.txt", "w");
        if (out == NULL) {
            printf("ERROR - CAN'T FIND OUTFILE");
        }

        for (i = 0; i < numRows; i++) {
            if (i == numRows - 1) {
                fprintf(out, "%d", result[i]);
            } else {
                fprintf(out, "%d ", result[i]);
            }
        }

        fclose(out);

        //free all the references
        vector<int>().swap(vec);

        delete [] result;

        for(int i = 0; i < numRows; i++){
          vector<int>().swap(mat[i]);
        }

    }

    //if we are no the root process, wait for inputs and process them
    else{
      //First grab the size of the vector
      int cols;

      MPI_Recv(&cols, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

      //Then grab the vector itself
      vector<int> vec;
      vec.resize(cols);

      MPI_Recv(&vec[0], cols, MPI_INT, 0, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

      //Next grab the row number and row and process them, then send them back
      int rowNum;
      int res;
      vector<int> row;
      row.resize(cols);

      while(1){
        MPI_Recv(&rowNum, 1, MPI_INT, 0, 2, MPI_COMM_WORLD, MPI_STATUS_IGNORE); //grab the row number

        if(rowNum == -1){
          break;
        }

        MPI_Recv(&row[0], cols, MPI_INT, 0, 3, MPI_COMM_WORLD, MPI_STATUS_IGNORE); //grab the row itself
        
        res = vectorMult(row, vec);

        MPI_Send(&res, 1, MPI_INT, 0, rowNum, MPI_COMM_WORLD); //send the dot product back to the root
          
          row.clear();
          row.resize(cols);
      }

      //free all the references
      vector<int>().swap(vec);
      vector<int>().swap(row);

    }

    
  MPI_Finalize();
}
