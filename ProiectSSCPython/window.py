from tkinter import *


if __name__ == '__main__':
    print("mere?")
    root = Tk()
    root.title("Micro Benchmark")
    root.config(bg="skyblue")
    root.maxsize(900,600)


    main_frame = Frame (root,width = 600, height = 300,bg = '#CECECE')
    main_frame.grid (row=0,column=0,padx=45,pady=100)


    image = PhotoImage(file ="Logo.png")
    original_img = image.subsample(3,3)


    image_frame = Frame (root, width=500, height = 500)
    image_frame.grid (row = 0, column = 1, padx=10, pady=10 )
    Label (image_frame,image = original_img). grid(row=0, column =1, padx = 5, pady=20)


    Label(main_frame,text = "Nr. of tests"). grid(row= 0,column = 1 , padx = 5,pady=5)
    Label(main_frame, text="Nr. of threads").grid(row=0, column=3, padx=5, pady=5)

    ent_tests = Entry(main_frame,width= 20)

    ent_tests.grid(row = 1,column=1, padx= 5,pady=5)

    ent_threads = Entry(main_frame,width= 20)

    ent_threads.grid(row = 1,column=3, padx= 5,pady=5)


    def show():
        text1 = ent_threads.get()
        text2 = ent_tests.get()
        text = text2 + " " +text1
        print(text)

    start = Button(main_frame, text = "Start",relief= RAISED, command = show)
    start.grid(row = 2, column = 2, padx = 10, pady =5)

    root.mainloop()
