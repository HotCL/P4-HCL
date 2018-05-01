#ifndef ARRAYLIST_H
#define ARRAYLIST_H

//Har literally bare liftet fra Java arraylist og set hvad jeg har troet der skulle med fra den
template <class T> 
class ARRAYLIST_H
{
    private:
        T *data;
        int arrlength;
        int listsize;
        void resize();
        bool needtoreseize();
    
    public:
        //constructor
        arraylist()
        {
            data = new T[5];

            arrlength = 5;
            listsize = 0;
        }

        bool contains(T item);
        int indexof (T item);
        int lastindexof (T item);
        T get (int index);
        int elementsInArray();

        void add(T item);
        void add(int index, T item);
        void set(int index, T item);
        void removeElementAt(int index);
        void remove(T item);
}
#endif
