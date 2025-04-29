package hu.opau.voltvault.models;

import java.util.ArrayList;
import java.util.List;

import hu.opau.voltvault.logic.Condition;

public class Ad {
    private List<String> conditions;
    private String title;
    private String text;
    private String id;

    public List<Condition> getConditions() {
        ArrayList<Condition> ret = new ArrayList<>();
        if (conditions == null) {
            return ret;
        }
        for (String s : conditions) {
            ret.add(Condition.fromString(s));
        }

        return ret;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public String[] getConditionsAsArray() {
        return conditions.toArray(new String[conditions.size()]);
    }
}
