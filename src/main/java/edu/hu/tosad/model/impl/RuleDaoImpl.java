package edu.hu.tosad.model.impl;

import edu.hu.tosad.model.Comparison;
import edu.hu.tosad.model.Rule;
import edu.hu.tosad.util.dao.CommonDaoImpl;
import edu.hu.tosad.util.dao.RuleDao;
import edu.hu.tosad.util.dto.ComparisonDto;
import edu.hu.tosad.util.dto.RuleDto;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RuleDaoImpl extends CommonDaoImpl implements RuleDao {
    public Rule get(int id) {
        RuleDto dto = getItem("rule/" + Integer.toString(id), RuleDto.class);
        return dto.convertToRule();
    }

    public Comparison[] getComparisons(int id) {
        ComparisonDto[] dtos = getItems("rule/" + Integer.toString(id) + "/comparisons", ComparisonDto[].class);

        List<Comparison> list = new ArrayList<Comparison>();
        for (ComparisonDto dto : dtos) {
            list.add(dto.convertToComparison());
        }

        Comparison[] comparisons = new Comparison[list.size()];
        return list.toArray(comparisons);
    }

    public Rule[] list() {
        return getItems("rules", Rule[].class);
    }

    public int add(Rule rule) {
        rule.setComparisons(null);
        JSONObject json = new JSONObject(rule);
        return Integer.parseInt(api.post("rule", json.toString()));
    }
}