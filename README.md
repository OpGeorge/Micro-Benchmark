
# Performance Evaluation of Static vs Dynamic Memory Allocation in Multi-threaded Applications

## Overview
This micro-benchmark project aims to evaluate the performance differences between static and dynamic memory allocation techniques within the context of multi-threaded applications. The project involves measuring and analyzing various aspects such as memory allocation time, array initialization time, and task execution time using different memory allocation strategies.

## Objective
The primary objective of this project is to provide insights into the performance characteristics of static and dynamic memory allocation methods in multi-threaded scenarios. By conducting experiments and analyzing the results, we aim to identify the advantages and trade-offs associated with each memory allocation approach, helping developers make informed decisions when designing and optimizing multi-threaded applications.

## Key Components
- **Processor Frequency Measurement:** The project includes functionality to measure the processor frequency, facilitating the normalization of timing results across different hardware configurations.
- **Memory Allocation:** Two types of memory allocation methods are evaluated - static allocation using arrays and dynamic allocation using `malloc()`.
- **Array Initialization:** Random integer values are generated to initialize arrays, simulating real-world data scenarios.
- **Multi-threaded Task Execution:** The program utilizes multi-threading to parallelize the execution of computational tasks, specifically computing the greatest common divisor (GCD) of adjacent elements in the arrays.
- **Timing and Output:** Timing measurements are obtained using CPU cycle counts via the `__rdtsc()` intrinsic function. The results are written to an output file (`output.txt`) for further analysis.

## Experimental Setup
- **Number of Tests (`nr`):** Users can specify the number of tests to be conducted.
- **Number of Threads (`noThreads`):** Users can specify the number of threads to be utilized in multi-threaded experiments.

## Experimental Procedure
1. **Static Memory Allocation:**
   - Measurement of memory allocation time for statically allocated arrays.
   - Measurement of initialization time for statically allocated arrays.

2. **Dynamic Memory Allocation:**
   - Measurement of memory allocation time for dynamically allocated arrays.
   - Measurement of initialization time for dynamically allocated arrays.

3. **Multi-threaded Task Execution:**
   - Measurement of task execution time on static and dynamic arrays with varying numbers of threads.

## Output
The results of each experiment, including memory allocation time, initialization time, and task execution time, are written to an output file (`output.txt`). The output file contains detailed timing measurements for further analysis.

## Conclusion
By analyzing the output data, this project aims to provide insights into the performance trade-offs between static and dynamic memory allocation strategies in multi-threaded applications. The findings can be valuable for optimizing memory management techniques and improving the performance of real-world software systems.
