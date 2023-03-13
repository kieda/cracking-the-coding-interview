package hackerrank.iterateit;

import common.tuple.Tuple2;

import java.util.*;

// todo - absNumberFactory - we store a list of pairs (first, second) that will equal value for a given depth
public class AbsNumber {
    // we perform an evaluation on how we can get to a certain number by absolute value subtractions
    // we do this by storing the compositional makeup
    private final int value;

    // value such that value = first - second > 0
    public final AbsNumber first;
    public final AbsNumber second;

    public AbsNumber(AbsNumber first, AbsNumber second) {
        if(first.value > second.value) {
            this.first = first;
            this.second = second;
            this.value = first.value - second.value;
        } else {
            this.first = second;
            this.second = first;
            this.value = second.value - first.value;
        }
    }
    public AbsNumber(int initialValue) {
        this.value = initialValue;
        first = null;
        second = null;
    }

    public boolean isLeaf() {
        return first == null && second == null;
    }
    public AbsNumber subtract(AbsNumber other) {
        return new AbsNumber(other, this);
    }

    public boolean isZero() {
        return this.value == 0;
    }
    public boolean isOne() {
        return this.value == 1;
    }

    private void accumulate(Map<Integer, Integer> a, boolean subtract) {
        if(isLeaf()) {
            // accumulate the result
            a.put(value, a.getOrDefault(value, 0) + (subtract ? -1 : 1));
        } else {
            first.accumulate(a, subtract);
            second.accumulate(a, !subtract);
        }
    }

    public Map<Integer, Integer> accumulate() {
        Map<Integer, Integer> eqn = new HashMap<>();
        accumulate(eqn, false);
        return eqn;
    }

    public String accumulateString() {
        List<Map.Entry<Integer, Integer>> a = new ArrayList<>(accumulate().entrySet());
        a.sort(Comparator.comparingInt(Map.Entry::getKey));
        StringBuilder result = new StringBuilder();
        result.append(value);
        result.append(" = ");
        for(int i = 0; i < a.size(); i++) {
            Map.Entry<Integer, Integer> v = a.get(i);
            result.append(v.getValue());
            result.append("*");
            result.append(v.getKey());
            if(i + 1 < a.size())
                result.append(" + ");
        }
        return result.toString();
    }

    public void toString(StringBuilder sb) {
        if(isLeaf()) {
            sb.append(value);
        } else {
            sb.append('(');
            first.toString(sb);
            sb.append(" - ");
            second.toString(sb);
            sb.append(')');
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        toString(result);
        return result.toString();
    }
}
