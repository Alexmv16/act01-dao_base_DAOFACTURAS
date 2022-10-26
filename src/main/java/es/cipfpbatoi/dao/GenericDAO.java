package es.cipfpbatoi.dao;

import java.util.List;

public interface GenericDAO<T> {
	T find(int id);
    
    List<T> findAll();   
         
    boolean insert(T t);
     
    boolean update(T t);
    
    boolean save(T t);
     
    boolean delete(int id);
    boolean delete(T t);
    
    int size();
    boolean exists(int id);
    List<T> findByExample(T t);    
    
}
