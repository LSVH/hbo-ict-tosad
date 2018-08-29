package edu.hu.tosad.util.dao;

public interface CommonDao<T> {
    T get(int ID);

    T[] list();
}
