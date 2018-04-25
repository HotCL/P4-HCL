//arraylist.cpp

#include "arraylist.h"

#define NULL 0

template <class t>
void arraylist<T>::resize()
{
    T *temp = new T[arrlength * 2];
    for (int i = 0; i <arrlength; i++)
        temp[i] = data[i];
        data = temp;
        arrlength *= 2; 
        delete *temp;       
}

template <class T>
bool arraylist<T>::needtoresize()
{
    return arrlength == listsize;
}

template <class T>
void arraylist<T>::add(T item)
{
    if (needtoresize())
        resize();
    data[listsize] = item;
    listsize ++;
}

template <class T>
void arraylist<T>::add(int index, T item)
{
    if (needtoresize())
        resize();

    for (int i = listsize; i >= index; i --)
    data[listsize+1] = data[listsize];

    data[index] = item;
    listsize ++;
}

template <class T>
void arraylist<T>::removeElementAt(int index)
{
    for int i = index; i < listsize; i++)
        data[i] = data[i+1];
    listsize --;   
}

template <class T>
T arraylist<T>::get(int index)
{
    if (index <= listsize)
        return data[index];

    return NULL;
}

template <class T>
void arraylist<T>::set(int index, T item)
{
    if (index >= 0 && index <= listsize)
        data[index] = item;
}

//Potentieltfjern
template <class T>
int arraylist<T>::indexof(T item)
{
    for (int i = 0; i <= listsize; i++)
        if (item == data[i])
            return i;

    return -1;
}

template <class T>
int arraylist<T>::lastindexof(T item)
{
    for (int i = listsize; i >= 0; i--)
        if(item == data[i])
            return i;

    return -1;
}
template <class T>
bool arraylist<T>::contains(T item)
{
    return (indexof(item) > -1);
}

template <class T>
int arraylist<T>::size()
{
    return listsize;
}
