package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Comparison;
import edu.hu.tosad.util.dao.CommonDao;
import edu.hu.tosad.util.dao.CommonDaoImpl;
import edu.hu.tosad.util.dto.ComparisonDto;

import java.util.ArrayList;
import java.util.List;

public class ComparisonDaoImpl extends CommonDaoImpl implements CommonDao<Comparison> {
    public Comparison get(int id) {
        ComparisonDto dto = getItem("comparison/" + Integer.toString(id), ComparisonDto.class);
        return dto.convertToComparison();
    }

    public Comparison[] list() {
        ComparisonDto[] dtos = getItems("comparisons", ComparisonDto[].class);

        List<Comparison> list = new ArrayList<Comparison>();
        for (ComparisonDto dto : dtos) {
            list.add(dto.convertToComparison());
        }

        Comparison[] comparisons = new Comparison[list.size()];
        return list.toArray(comparisons);
    }
}
