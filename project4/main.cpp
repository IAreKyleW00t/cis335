/**
 *  Name:           Kyle Colantonio
 *  CSU ID:         2595744
 *  Assignment:     4
 *  Description:    C++ program that calls ASM function
 *  Date:           3-17-2015
 *
 *  Updated:        3-6-15
 **/

#include <iostream>
using namespace std;

extern "C" {
    unsigned int Mobonacci(unsigned int n);
}

int main(int argc, char **argv) {
    unsigned int N;

    cout << "Enter an integer: ";
    cin >> N;

    cout << "Mobonacci(" << N << ") = " << Mobonacci(N) << endl;
    return 0;
}