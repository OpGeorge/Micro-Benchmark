import threading
import time
import random
from tkinter import *

ARRAY_SIZE = 10000

frecProc = [0.0]  # Use a list to pass the reference and update the value
vector_mutex = threading.Lock()

output_file = "output.txt"

def write_output(message):
    with open(output_file, 'w') as file:
        file.write(message + '\n')
    print(message)

def cmmdc(a, b):
    if b != 0:
        return cmmdc(b, a % b)
    else:
        return a

def processor(frec):
    start = time.perf_counter()
    time.sleep(1)
    end = time.perf_counter()

    elapsed_time = end - start - 1  # Adjust for the sleep time
    frequency = 1 / elapsed_time
    frec[0] = frequency

def thread_function(arr, start, end):
    container = 0
    for i in range(start, end - 1):
        with vector_mutex:
            container = cmmdc(arr[i], arr[i + 1])

def rand_array(arr, nr):
    for i in range(nr):
        arr[i] = random.randint(0, 999)

def run_benchmark(num_tests, num_threads):
    global frecProc
    big = time.perf_counter()
    nr_of_tests = num_tests
    processor_thread = threading.Thread(target=processor, args=(frecProc,))
    processor_thread.start()
    processor_thread.join()

    string_message = "FREC:\n{:.10f}\n".format(frecProc[0])

    print("FREC:", frecProc[0])

    i1 = time.perf_counter()
    t_to_alloc_static = 0

    loop = nr_of_tests
    while loop:
        i2 = time.perf_counter()
        i3 = i2 - i1
        t_to_alloc_static += i3 * 1000
        loop -= 1

    t_to_alloc_static /= nr_of_tests

    string_message +="Numar microsecunde pentru alocare statica:\n{:.10f}\n".format(t_to_alloc_static * 1000)


    loop = nr_of_tests
    static_array = [0] * ARRAY_SIZE
    time_for_initialize = 0

    while loop:
        i1 = time.perf_counter()
        rand_array(static_array, ARRAY_SIZE)
        i2 = time.perf_counter()
        i3 = i2 - i1
        time_for_initialize += i3 * 1000
        loop -= 1

    time_for_initialize /= nr_of_tests


    string_message+= "Numar microsecunde pentru initializarea sirului alocat static:\n{:.10f}\n".format(time_for_initialize * 1000)

    print("Numar microsecunde pentru initializarea sirului alocat static:")
    print("{:.10f}".format(time_for_initialize * 1000))

    vec_array = list(static_array)

    no_threads =num_threads

    if no_threads != 0 and no_threads != 1:
        chunk_size = ARRAY_SIZE // no_threads
        remaining = ARRAY_SIZE % no_threads

    if no_threads > 1:
        i1 = time.perf_counter()
        start = 0  # Initialize start variable
        threads = []
        for i in range(no_threads):
            chunk = chunk_size + 1 if i < remaining else chunk_size
            end_index = start + chunk
            thread = threading.Thread(target=thread_function, args=(vec_array, start, end_index))
            threads.append(thread)
            start = end_index

        for thread in threads:
            thread.start()

        for thread in threads:
            thread.join()

        i2 = time.perf_counter()
        i3 = i2 - i1
        new_timer = i3 * 1000

        string_message += "Numar microsecunde pentru completare task pe sir alocat static cu {} Thread-uri:\n".format(no_threads)
        string_message += "{:.10f}\n".format(new_timer * 1000)

        print("Numar microsecunde pentru completare task pe sir alocat static cu", no_threads, "Thread-uri:")
        print("{:.10f}".format(new_timer * 1000))

    else:
        i1 = time.perf_counter()
        for i in range(ARRAY_SIZE - 1):
            cmmdc(static_array[i], static_array[i + 1])

        i2 = time.perf_counter()
        i3 = i2 - i1
        end_single = i3 * 1000

        string_message += "Numar microsecunde pentru completare task pe sir alocat static:\n{:.10f}\n".format(end_single * 1000)
        print("Numar microsecunde pentru completare task pe sir alocat static:")
        print("{:.10f}\n".format(end_single * 1000))

    loop = nr_of_tests
    dynamic_array = []
    dynamic_alloc_time = 0

    while loop:
        i1 = time.perf_counter()
        dynamic_array = [random.randint(0, 999) for _ in range(ARRAY_SIZE)]
        i2 = time.perf_counter()
        i3 = i2 - i1
        dynamic_alloc_time += i3 * 1000
        loop -= 1
    dynamic_alloc_time /= nr_of_tests

    string_message +="Numarul de microsecunde pentru alocare dinamica:\n{:.10f}\n".format(dynamic_alloc_time * 1000)
    print("Numarul de microsecunde pentru alocare dinamica:")
    print("{:.10f}".format(dynamic_alloc_time * 1000))

    loop = nr_of_tests
    time_for_initialize = 0

    while loop:
        i1 = time.perf_counter()
        rand_array(dynamic_array, ARRAY_SIZE)
        i2 = time.perf_counter()
        i3 = i2 - i1
        time_for_initialize += i3 * 1000
        loop -= 1

    time_for_initialize /= nr_of_tests

    string_message += "Numar microsecunde pentru initializarea sirului alocat dinamic:\n{:.10f}\n".format(time_for_initialize * 1000)
    print("Numar microsecunde pentru initializarea sirului alocat dinamic:")
    print("{:.10f}".format(time_for_initialize * 1000))

    if no_threads > 1:
        i1 = time.perf_counter()
        start = 0  # Initialize start variable
        threads = []
        for i in range(no_threads):
            chunk = chunk_size + 1 if i < remaining else chunk_size
            end_index = start + chunk
            thread = threading.Thread(target=thread_function, args=(dynamic_array, start, end_index))
            threads.append(thread)
            start = end_index

        for thread in threads:
            thread.start()

        for thread in threads:
            thread.join()

        dynamic_alloc_time /= 1000
        string_message += "Numar microsecunde pentru completare task pe sir alocat dinamic cu {} Thread-uri:\n".format(no_threads)
        string_message += "{:.10f}\n".format(dynamic_alloc_time * 1000)


    else:
        i1 = time.perf_counter()
        for i in range(ARRAY_SIZE - 1):
            cmmdc(dynamic_array[i], dynamic_array[i + 1])

        i2 = time.perf_counter()
        i3 = i2 - i1
        end_single = i3 * 1000

        string_message += "Numar microsecunde pentru completare task pe sir alocat dinamic:\n"
        string_message += "{:.10f}\n".format(end_single * 1000)

        print("Numar microsecunde pentru completare task pe sir alocat dinamic:")
        print("{:.10f}".format(end_single * 1000))


    bigf = time.perf_counter()
    i3 = bigf - big  # Correct the variable name
    total = i3 * 1000

    print("Numar microsecunde total", total*1000)
    write_output(string_message)
    print("||||||||||||||||||||||||||||")
    print(string_message)

if __name__ == "__main__":
    root = Tk()
    root.title("Micro Benchmark")
    root.config(bg="skyblue")
    root.maxsize(900, 600)

    main_frame = Frame(root, width=600, height=300, bg='#CECECE')
    main_frame.grid (row=0,column=0,padx=45,pady=100)

    image = PhotoImage(file="Logo.png")
    original_img = image.subsample(3, 3)

    image_frame = Frame(root, width=500, height=500)
    image_frame.grid(row=0, column=1, padx=10, pady=10)
    Label(image_frame, image=original_img).grid(row=0, column=1, padx=5, pady=20)

    Label(main_frame, text="Nr. of tests").grid(row=0, column=1, padx=5, pady=5)
    Label(main_frame, text="Nr. of threads").grid(row=0, column=3, padx=5, pady=5)

    ent_tests = Entry(main_frame, width=20)

    ent_tests.grid(row=1, column=1, padx=5, pady=5)

    ent_threads = Entry(main_frame, width=20)

    ent_threads.grid(row=1, column=3, padx=5, pady=5)


    def show():
        nr_test = int(ent_tests.get())
        nr_threads = int(ent_threads.get())
        text1 = ent_threads.get()
        text2 = ent_tests.get()
        text = text2 + " " + text1
        sum = nr_test + nr_threads
        run_benchmark(nr_test, nr_threads)


    start = Button(main_frame, text="Start", relief=RAISED, command=show)
    start.grid(row=2, column=2, padx=10, pady=5)
    root.mainloop()

