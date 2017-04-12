#include <mpi.h>
#include <stdio.h>
#include <iostream>
#include <string>
#include <algorithm>
#include <vector>
#include <fstream>
#include <sstream>
#include <iterator>

//#define DEBUG
void getArrayForRowFromMatrix(std::vector<std::vector <int> > matrix, int ar[],int row);
void masterProcessor(int numProcessors, int processorId);
void slaveProcessor(int numProcessors,int processorId);
void getArrayForVector(std::vector<int> vec,int ar[]);
//3 Functions below from stack overflow trim string
//trim from left
static inline std::string &ltrim(std::string &s) {
    s.erase(s.begin(), std::find_if(s.begin(), s.end(),
            std::not1(std::ptr_fun<int, int>(std::isspace))));
    return s;
}

// trim from end
static inline std::string &rtrim(std::string &s) {
    s.erase(std::find_if(s.rbegin(), s.rend(),
            std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s.end());
    return s;
}

// trim from both ends
static inline std::string &trim(std::string &s) {
    return ltrim(rtrim(s));
}
int main(int argc, char** argv) {
    // Initialize the MPI environment
    MPI_Init(NULL, NULL);

    // Get the number of processes
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    // Get the rank of the process
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

    // Get the name of the processor
    char processor_name[MPI_MAX_PROCESSOR_NAME];
    int name_len;
    MPI_Get_processor_name(processor_name, &name_len);

	if(world_rank==world_size-1){
		masterProcessor(world_size,world_rank);
	
	}
	else{
		slaveProcessor(world_size,world_rank);
		
	}
#ifdef DEBUG
    // Print off a hello world message
    printf("Processor %s, rank %d"
           " out of %d processors\n",
           processor_name, world_rank, world_size);
#endif
    // Finalize the MPI environment.
    MPI_Finalize();
}
void slaveProcessor(int numProcessors,int processorId){
	
	int size;
	MPI_Recv(&size,1,MPI_INT,numProcessors-1,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
	std::vector<int> vec;
	int buffer[size+1];
	MPI_Recv(&buffer,size,MPI_INT,numProcessors-1,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
	for(int i=0;i<size;i++){
		vec.push_back(buffer[i]);
	}
	
	bool moveOn=true;
	int result;
	while(moveOn){
		MPI_Recv(&buffer,size+1,MPI_INT,numProcessors-1,0,MPI_COMM_WORLD,MPI_STATUS_IGNORE);
		if(buffer[0]!=-1){
			int rowNum=buffer[0];
			result=0;
			for(int i=0;i<size;i++){
				result=result+vec[i]*buffer[i+1];
				
			}
			MPI_Send(&result,1,MPI_INT,numProcessors-1,rowNum,MPI_COMM_WORLD);
			
			
		}else{
		moveOn=false;
		}

	}
	
	
}

void masterProcessor(int numProcessors, int processorId){
	std::vector<std::vector <int> > matrix;
	std::vector<int> vec;
	std::ifstream file("matrix.txt");
   std::string line;
    std::string token;
    getline(file,line);
    trim(line);
    int size= std::stoi(line);



    for(int i=0;i<size;i++){
		std::getline(file,line);
		trim(line);
		std::istringstream iss(line);
		std::vector<int> row;
		for(int j=0;j<size;j++){
			iss>>token;
			row.push_back(std::stoi(token));
		}
		matrix.push_back(row);
	}
	file.close();
	
	std::ifstream vectorFile("vector.txt");
    std::getline(vectorFile,line);
    trim(line);
	std::istringstream iss(line);
	
	for(int j=0;j<size;j++){
		iss>>token;
		vec.push_back(std::stoi(token));
	}
	
	vectorFile.close();
	
		
	//send size and vector to all other processes
       int sendingVectorAr[size];//Array to hold vector
    getArrayForVector(vec,sendingVectorAr);
    for(int i=0;i<numProcessors;i++){
		if(i!=processorId){
		MPI_Send(&size,1,MPI_INT,i,0,MPI_COMM_WORLD);
		MPI_Send(sendingVectorAr,size,MPI_INT,i,0,MPI_COMM_WORLD);
		}
		
	}
    
    //Send row to all other processes
    int ar[size+1];//Array to hold matrix row
    int processToSendTo=0;//Process to send to
    int currentRow=0;//Current row of matrix
    while(currentRow<size){
		if(processToSendTo!=processorId){
			ar[0]=currentRow;
			getArrayForRowFromMatrix(matrix,ar,currentRow);
			MPI_Send(ar,size+1,MPI_INT,processToSendTo,0,MPI_COMM_WORLD);
			
		}
			
			currentRow++;
			processToSendTo=(processToSendTo+1)%numProcessors;
			
		
	}
	
	
	//Send kill to all other processes
		int kill=-1;
	 for(int i=0;i<numProcessors;i++){
		if(i!=processorId){
		MPI_Send(&kill,1,MPI_INT,i,0,MPI_COMM_WORLD);
		
		}
		
	}

	//Receiving vectors
	std::vector<int> vecResult;
	currentRow=0;
	processToSendTo=0;
	int res;
	while(currentRow<size){
		if(processToSendTo!=processorId){
			MPI_Recv(&res,1,MPI_INT,processToSendTo,currentRow,MPI_COMM_WORLD,MPI_STATUS_IGNORE);//Server expects to get certain row
			vecResult.push_back(res);
			
		}
		else{
			res=0;
			for(int i=0;i<size;i++){
				res=res+vec[i]*matrix[currentRow][i];
				
			}
			vecResult.push_back(res);
		}
		currentRow++;
			processToSendTo=(processToSendTo+1)%numProcessors;
	}
	

	
	std::stringstream ss;
    copy( vecResult.begin(), vecResult.end(), std::ostream_iterator<int>(ss, " "));
    std::string s = ss.str();
    s = s.substr(0, s.length()-1);  // get rid of the trailing space



	std::ofstream outfile ("result.txt");

	outfile << s << std::endl;

	outfile.close();

	
}

void getArrayForRowFromMatrix(std::vector<std::vector <int> > matrix, int ar[],int row){
	
		for(int j=1;j<matrix.size()+1;j++){
			ar[j]=matrix[row][j-1];
		}
		
	
	

}
//Different behavior than above; no need for payload for msg tag thus 1 less
void getArrayForVector(std::vector<int> vec,int ar[]){
	for(int j=0;j<vec.size();j++){
			ar[j]=vec[j];
		}
}

