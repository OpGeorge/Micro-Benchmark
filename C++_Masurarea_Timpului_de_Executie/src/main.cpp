#include <iostream>
#include <vector>
#include <ctime>
#include <iomanip>
#include <fstream>
#include <thread>
#include <mutex>
#include <intrin.h>
#include <ctime>
#include <unistd.h>
using namespace std;
#define ARRAY_SIZE 10000

double frecProc;
int cmmdc(int a, int b)
{
    if (b != 0)
    {
        cmmdc(b, a % b);
    }
    else
    {
        return a;
    }
}


void Processor(double *frec)

{

    unsigned long long int start, end;
    double elapsed_time;

    // Read the TSC twice with a fixed time interval
    start = __rdtsc();
    clock_t start_time = clock();

    // Wait for a specific duration (e.g., 1 second)
    sleep(1);

    end = __rdtsc();
    clock_t end_time = clock();

    // Calculate the elapsed time in seconds
    elapsed_time = (double)(end_time - start_time) / CLOCKS_PER_SEC;

    // Calculate the processor frequency
    double frequency = ((end - start) / elapsed_time);
    
    // get the processor frequency in Hz
    
    *frec = frequency;  
}


mutex vectorMutex;
void threadFuntion(vector<int> arr, int start, int end)
{
    int i;
    int container;
    for (i = start; i < end - 1; i++)
    {
        vectorMutex.lock();
        container = cmmdc(arr[i], arr[i + 1]);
        vectorMutex.unlock();
    }
}

void threadFuntionDinamic(int *arr, int start, int end)
{
    int i;
    int container;
    for (i = start; i < end - 1; i++)
    {
        vectorMutex.lock();
        container = cmmdc(arr[i], arr[i + 1]);
        vectorMutex.unlock();
    }
}

void randArray(int *arr, const int nr)
{
    int i;
    for (i = 0; i < nr; i++)
    {
        arr[i] = rand() % 1000;
    }
}

int main()
{

    Processor(&frecProc);
    cout << "FREC:" << frecProc<<"\n";
    unsigned __int64 i1, i2, i3, Big, Bigf;
    Big = __rdtsc();
    cout << "Specificati numarul de teste (nr_teste<=1000): \n";
    int nr;
    cin >> nr;

    cout << "\n";

    cout << "Specificati numarul de thread-uri\n";

    int noThreads;
    cin >> noThreads;

    thread threads[noThreads];

    FILE *outputFile = fopen("output.txt", "w");
    fprintf(outputFile, "Frec:\n");
    fprintf(outputFile, "%.10lf\n", frecProc);

    if (outputFile == nullptr)
    {
        cout << "Could not open file";
        return -1;
    }
    int loop = nr;
    double t_to_alloc_static = 0;
    while (loop)
    {
        i1 = __rdtsc();
        int staticArray[ARRAY_SIZE];
        i2 = __rdtsc();
        i3 = i2 - i1;
        t_to_alloc_static += (i3 / frecProc) * 1000;
        loop--;
    }
    // double timeToallocStaticarr = (i3 / frecProc) * 1000;
    t_to_alloc_static /= nr;
    fprintf(outputFile, "Numar microsecunde pentru alocare statica: \n");
    fprintf(outputFile, "%.10lf\n", t_to_alloc_static*1000);

    loop = nr;
    int staticArray[ARRAY_SIZE];
    double timeForInitialize = 0;

    while (loop)
    {
        i1 = __rdtsc();
        randArray(staticArray, ARRAY_SIZE);
        i2 = __rdtsc();
        i3 = i2 - i1;
        timeForInitialize += ((i3 / frecProc) * 1000);
        loop--;
    }
    timeForInitialize /= nr;
    fprintf(outputFile, "Numar microsecunde pentru initalizarea sirului alocat static: \n");
    fprintf(outputFile, "%.10lf\n", timeForInitialize*1000);

    vector<int> vecArray;

    for (int i = 0; i < ARRAY_SIZE; i++)
    {
        vecArray.emplace_back(staticArray[i]);
    }

    int endIndex = 0;
    int chunkSize = 0;
    int remaning = 0;

    if (noThreads != 0 && noThreads != 1)
    {
        chunkSize = ARRAY_SIZE / noThreads;
        remaning = ARRAY_SIZE % noThreads;
    }

    if (noThreads > 1)
    {

        i1 = __rdtsc();

        for (int i = 0, start = 0; i < noThreads; i++)
        {
            int chunk = (i < remaning) ? (chunkSize + 1) : chunkSize;
            endIndex = start + chunk;
            threads[i] = thread(threadFuntion, vecArray, start, endIndex);
            start = endIndex;
        }

        i2 = __rdtsc();
        i3 = i2 - i1;
        double newTimer = (i3 / frecProc) * 1000;

        fprintf(outputFile, "Numar microsecunde pentru completare task pe sir alocat static cu %d Thread-uri: \n", noThreads);
        fprintf(outputFile, "%.10lf\n", newTimer*1000);

        for (int i = 0; i < noThreads; ++i)
        {
            threads[i].join();
        }
    }
    else
    {

        i1 = __rdtsc();
        for (int i = 0; i < ARRAY_SIZE - 1; i++)
        {
            cmmdc(staticArray[i], staticArray[i + 1]);
        }
        i2 = __rdtsc();
        i3 = i2 - i1;
        double endSingle = (i3 / frecProc) * 1000;

        fprintf(outputFile, "Numar microsecunde pentru completare task pe sir alocat static: \n");
        fprintf(outputFile, "%.10lf\n", endSingle*1000);
    }

    int *dinamycArray;
    loop = nr;
    double dinamycAlocTime = 0;
    while (loop)
    {
        i1 = __rdtsc();
        dinamycArray = (int *)malloc(ARRAY_SIZE * sizeof(int));
        i2 = __rdtsc();
        i3 = i2 - i1;
        free(dinamycArray);
        dinamycAlocTime += ((i3 / frecProc) * 1000);
        loop--;
    }
    fprintf(outputFile, "Numarul de microsecunde pentru alocare dinamica: \n");
    fprintf(outputFile, "%.10f\n", dinamycAlocTime*1000);

    dinamycArray = (int *)malloc(ARRAY_SIZE * sizeof(int));
    loop = nr;
    timeForInitialize = 0;
    while (loop)
    {
        i1 = __rdtsc();
        randArray(dinamycArray, ARRAY_SIZE);
        i2 = __rdtsc();
        i3 = i2 - i1;
        loop--;
        timeForInitialize+=((i3 / frecProc) * 1000);
        loop--;
    }

    timeForInitialize /= nr;
    fprintf(outputFile, "Numar microsecunde pentru initalizarea sirului alocat dinamic: \n");
    fprintf(outputFile, "%.10lf\n", timeForInitialize*1000);

    if (noThreads > 1)
    {

        i1 = __rdtsc();

        for (int i = 0, start = 0; i < noThreads; i++)
        {
            int chunk = (i < remaning) ? (chunkSize + 1) : chunkSize;
            endIndex = start + chunk;
            threads[i] = thread(threadFuntionDinamic, dinamycArray, start, endIndex);
            start = endIndex;
        }

        i2 = __rdtsc();
        i3 = i2 - i1;
        double newTimer = (i3 / frecProc) * 1000;

        fprintf(outputFile, "Numar microsecunde pentru completare task pe sir alocat dinamic cu %d Thread-uri: \n", noThreads);
        fprintf(outputFile, "%.10lf\n", newTimer*1000);

        for (int i = 0; i < noThreads; ++i)
        {
            threads[i].join();
        }
        free(dinamycArray);
    }
    else
    {

        i1 = __rdtsc();
        for (int i = 0; i < ARRAY_SIZE - 1; i++)
        {
            cmmdc(dinamycArray[i], dinamycArray[i + 1]);
        }
        i2 = __rdtsc();
        i3 = i2 - i1;
        double endSingle = (i3 / frecProc) * 1000;

        fprintf(outputFile, "Numar microsecunde pentru completare task pe sir alocat dinamic: \n");
        fprintf(outputFile, "%.10lf\n", endSingle *1000);
    }

    Bigf = __rdtsc();
    i3 = Bigf - Big;
    double total = (i3 / frecProc) * 1000;

    printf("Numar microsecunde total %lf \n", total);
    

    return 0;
}