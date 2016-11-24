package org.medlab.reservation.dao;


import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public interface GenericDao<T, PK extends Serializable> {

    T create(T t);

    T read(PK id);

    T update(T t);

    void delete(T t);

    List<T> findAll();
}
