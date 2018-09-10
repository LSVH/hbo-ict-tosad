package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Comparison;
import edu.hu.tosad.util.dao.CommonDaoImpl;
import edu.hu.tosad.util.dao.ComparisonDao;
import edu.hu.tosad.util.dto.ComparisonDto;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComparisonDaoImpl extends CommonDaoImpl implements ComparisonDao {
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

    public int add(int id, Comparison comparison) {
        JSONObject json = new JSONObject(comparison);
        return Integer.parseInt(api.post("rule/" + Integer.toString(id) + "/comparison", json.toString()));
    }
}
