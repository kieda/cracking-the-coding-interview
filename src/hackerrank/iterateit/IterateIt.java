package hackerrank.iterateit;

import java.util.*;
import java.util.stream.Collectors;

public class IterateIt {

    private final int BRUTE_MAX = 11;

    public static Integer getMaxDistance(List<Integer> a) {
        if(a.size() <= 1)
            return null;
        Integer max = null;
        Integer min = null;
        for(int item : a) {
            if(max == null || item > max)
                max = item;
            if(min == null || item < min)
                min = item;
        }
        return max - min;
    }

    public static Integer getMinDistanceSorting(List<Integer> a, int fromIdx, int toIdx) {
        if(toIdx - fromIdx <= 1)
            return null;
        Integer minDistance = null;
        Collections.sort(a.subList(fromIdx, toIdx));
        for(int i = fromIdx + 1; i < toIdx; i++) {
            int distance = a.get(i) - a.get(i-1);
            if(distance != 0 && (minDistance == null || distance < minDistance)) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    public Integer getMinDistanceBrute(List<Integer> a, int fromIdx, int toIdx) {
        if(toIdx - fromIdx <= 1)
            return null;
        Integer minDistance = null;
        for(int i = fromIdx; i < toIdx; i++) {
            for(int j = i + 1; j < toIdx; j++) {
                int distance = Math.abs(a.get(i) - a.get(j));
                if(distance != 0 && (minDistance == null || distance < minDistance)) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }


    private static class GridCell{
        Integer min;
        Integer max;
        public boolean isEmpty() {
            return min == null && max == null;
        }
        public boolean isFull() {
            return min != max;
        }

        public boolean contains(int position) {
            return position == min || position == max;
        }
        public boolean add(int position) {
            if(isFull() || contains(position)) {
                return false;
            } else if(isEmpty()) {
                min = position;
                max = position;
            } else if(position < min) {
                min = position;
            } else if(position > max) {
                max = position;
            }
            return true;
        }
        public int minDistance(int position) {
            // position is after min and max
            if(position >= max) {
                return position - max;
            }
            // position is before min and max
            if(position <= min) {
                return min - position;
            }
            // min < position < max
            return Math.min(position - min, max - position);
        }
    }

    int sqrtInt(int val) {
        int lo = 0;
        int hi = val + 1;
        int mid;
        while (lo != hi - 1)
        {
            mid = lo + (hi - lo) / 2;

            if (mid * mid <= val)
                lo = mid;
            else
                hi = mid;
        }

        return lo;
    }

    public int getGridCell(int item, int gridSize) {
        return Math.floorDiv(item, gridSize);
    }

    public int maxCellNeighbor(int item, int currentMinDist, int gridSize) {
        // returns the max neighbor we should check
        return getGridCell(item + (currentMinDist - 1), gridSize);
    }
    public int minCellNeighbor(int item, int currentMinDist, int gridSize) {
        return getGridCell(item - (currentMinDist - 1), gridSize);
    }
    private static int log2(int i) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(i);
    }
    public Integer getMinDistanceRandomized(List<Integer> a) {
        if(a.size() <= 1)
            return null;
        Collections.shuffle(a);
        int log2 = log2(a.size());

        // todo - what if all of the items selected are the same?
        int currentMinDist = getMinDistanceSorting(a, 0, Math.max(a.size() / log2, 2));
        if(currentMinDist == 1)
            return 1;

        // min grid cell size is 4
        // since if we already have 2 positions inserted into a grid cell, then another unique position will have to have
        // a min distance of 1
        int gridSize = Math.max(currentMinDist / 3, 4);
        Map<Integer, GridCell> grid = new HashMap<>();

        for(int idx = 0; idx < a.size(); idx++) {
            int elem = a.get(idx);
            int cell = getGridCell(elem, gridSize);
            if(!grid.containsKey(cell)) {
                grid.put(cell, new GridCell());
            }
            GridCell elemCell = grid.get(cell);

            // we already have this elem in the grid, nothing happens
            if(elemCell.contains(elem))
                continue;

            // update our currentMinDist
            int cellMinDist = 0;

            // update it for our current cell. Don't add elem to the cell just so we can get a non-zero distance
            if(!elemCell.isEmpty() && (cellMinDist = elemCell.minDistance(elem)) < currentMinDist) {
                currentMinDist = cellMinDist;
            }

            // update min from cell + 1 to maxCellNeighbor
            for(int i = cell + 1; i <= maxCellNeighbor(elem, currentMinDist, gridSize); i++) {
                // traverse from our current cell to the max one
                if(grid.containsKey(i) && !grid.get(i).isEmpty() && (cellMinDist = grid.get(i).minDistance(elem)) < currentMinDist) {
                    currentMinDist = cellMinDist;
                    break;
                }
                // we're too far away and cellMinDist will only increase
                if(cellMinDist >= currentMinDist)
                    break;
            }
            // update min from cell - 1 to minCellNeighbor
            for(int i = cell - 1; i <= minCellNeighbor(elem, currentMinDist, gridSize); i--) {
                if(grid.containsKey(i) && !grid.get(i).isEmpty() && (cellMinDist = grid.get(i).minDistance(elem)) < currentMinDist) {
                    currentMinDist = cellMinDist;
                    break;
                }
                // we're too far away and cellMinDist will only increase
                if(cellMinDist >= currentMinDist)
                    break;
            }
            if(currentMinDist == 1)
                return currentMinDist;

            if(elemCell.isFull()) {
                // We can't add more than two to a grid. We refactor
                // update currentMinDist

                // new grid size
                gridSize = Math.max(currentMinDist / 3, 4);
                grid.clear();

                for(int j = 0; j <= idx; j++) {
                    int insertion = a.get(j);
                    int insertionCellKey = getGridCell(insertion, gridSize);
                    if(!grid.containsKey(insertionCellKey)) {
                        grid.put(cell, new GridCell());
                    }
                    GridCell insertionCell = grid.get(cell);
                    if(insertionCell.contains(insertion))
                        continue;
                    insertionCell.add(insertion);
                }
            } else {
                // add elem to our grid
                elemCell.add(elem);
            }
        }
        return currentMinDist;
    }
    public Integer getMinDistance(List<Integer> a) {
        if(a.size() <= BRUTE_MAX) {
            return getMinDistanceBrute(a, 0, a.size());
        }
        // 12 = 1100 -> 12/4 = 3
        return getMinDistanceRandomized(a);
    }

    public int iterateIt(List<Integer> a) {
        if(a.size() <= 1)
            return a.size();

        int maxDistance = getMaxDistance(a);
        int minDistance = getMinDistance(a);
        if(minDistance == maxDistance)
            return 2;
        return -Math.floorDiv(-maxDistance, minDistance) + 1;

    }

    private static Map<Integer, List<Set<Integer>>> stats = new HashMap<>();
    public static int iterateItDumb(List<Integer> a) {
//        System.out.println(getMaxDistance(a) + " " + getMinDistanceSorting(new ArrayList<>(a), 0, a.size()) );
        Set<Integer> current = new HashSet<>(a);
        Set<Integer> next = new HashSet<>();
        Set<Integer> toadd = null;
        int rep = 0;
        while(!current.isEmpty()) {
//            System.out.println(current);
            next.clear();
            for(int i : current) {
                for(int j : current) {
                    if(i != j) {
                        next.add(Math.abs(i - j));
                    }
                }
            }
            if(toadd == null) {
                toadd = new HashSet<>(next);
            }
            Set<Integer> temp = current;
            current = next;
            next = temp;
            rep++;
        }
        if(!stats.containsKey(rep))
            stats.put(rep, new ArrayList<>());
        stats.get(rep).add(toadd);

        return rep;
    }

    public static AbsNumber iterateItDumb2(List<Integer> a) {
//        Set<AbsNumber> initial = new
        NavigableSet<AbsNumber> current = new TreeSet<>(a.stream().map(AbsNumber::new).collect(Collectors.toList()));

//        System.out.println(getMaxDistance(a) + " " + getMinDistanceSorting(new ArrayList<>(a), 0, a.size()) );
        Set<Integer> current = new HashSet<>(a);
        Set<Integer> next = new HashSet<>();
        Set<Integer> toadd = null;
        int rep = 0;
        while(!current.isEmpty()) {
//            System.out.println(current);
            next.clear();
            for(int i : current) {
                for(int j : current) {
                    if(i != j) {
                        next.add(Math.abs(i - j));
                    }
                }
            }
            if(toadd == null) {
                toadd = new HashSet<>(next);
            }
            Set<Integer> temp = current;
            current = next;
            next = temp;
            rep++;
        }
        if(!stats.containsKey(rep))
            stats.put(rep, new ArrayList<>());
        stats.get(rep).add(toadd);

        return rep;
    }

    public static void main(String[] args) {
//        for(int i = 0; i < 1000; i++) {
//            System.out.println(iterateItDumb(List.of(10 + i, 3 + i, 5 + i)));
//            System.out.println(iterateItDumb(List.of(10*(i+1), 3*(i+1), 5*(i+1))));
//        }

                                                            // max min -> ceil(max/min) + 1 + overflow (for n = 3)
//        System.out.println(iterateItDumb(List.of(10,3,5))); // 7 2 -> 4 + 1 + 1
//        System.out.println(iterateItDumb(List.of(11,3,6))); // 8 3 -> 3 + 1 + 2
//        System.out.println(iterateItDumb(List.of(12,3,7))); // 9 4 -> 3 + 1 + 3
//
//        System.out.println(iterateItDumb(List.of(1,2,3))); // 2 1 -> 2 + 1 + 0
//        System.out.println(iterateItDumb(List.of(2,3,4)));
//        System.out.println(iterateItDumb(List.of(1,2,3,5,10,17))); // 16 1 -> 16 + 1 + 0
        //System.out.println(iterateItDumb(List.of(16,2,4)));    // 14 2 -> 7 + 1 + 0

//        System.out.println(iterateItDumb(List.of(3,5,7,17))); // 14 2 -> 8 (valid)
//            // [2, 4, 10, 12, 14]
//            // 1 + 14/2 = 8
//        System.out.println(iterateItDumb(List.of(3,5,8,17))); // 14 2 -> 14 (invalid)
//            // [2, 3, 5, 9, 12, 14]
//            // 1 + ...
//            // 14//2 = 7 -> 7 + (0)
//            // 14//3 = 4.6666 -> 5 (+ |12 - 14| = 2) -> 7
//            // 12//3 = 4 -> (12 - 12) -> 4
//            // 9//5 = 1.8 -> 2 + (10 - 9) -> 3
//            // could be: 1 + 14//2, 14//3
//            // could be: 14//2 + 12//3 + 9//5
//            // could be: 1 + 14/2, 1 + 14/3
//        System.out.println(iterateItDumb(List.of(3,5,9,17))); // 14 2 -> 8 (valid)
//            // [2, 4, 6, 8, 12, 14]
//            // 1 + 14/2 = 8
//        System.out.println(iterateItDumb(List.of(3,5,10,17))); // 14 2 -> 13 (invalid)
//            // [2, 5, 7, 12, 14]
//            // 14//2 = 7
//            // 14//5 = 2.8 -> 3 (+ 10-14) -> 7
//            // 12//5 = 2.4 -> 3 (+ 15-12) -> 6
//        System.out.println(iterateItDumb(List.of(3,5,11,17))); // 14 2 -> 8 (valid)
//            // [2, 6, 8, 12, 14]
//        System.out.println(iterateItDumb(List.of(3,5,12,17))); // 14 2 -> 13 (invalid)
//            // [2, 5, 7, 9, 12, 14]
//            //
//        System.out.println(iterateItDumb(List.of(3,5,13,17))); // 14 2 -> 8 (valid)
//            // [2, 4, 8, 10, 12, 14]
//        System.out.println(iterateItDumb(List.of(3,5,14,17))); // 14 2 -> 14 (invalid)
//            // [2, 3, 9, 11, 12, 14]
//        System.out.println(iterateItDumb(List.of(3,5,15,17))); // 14 2 -> 8 (valid)
            // [2, 10, 12, 14]


        for(int i = 7; i < 100; i++) {
        //    System.out.println(iterateItDumb(List.of(3,5,i,177))); // 14 2 -> 8 (valid)
        }
        System.out.println(iterateItDumb(List.of(85,87,172)));

        stats.forEach((i, val) ->
                val.forEach(j -> {
                    List<Integer> sorted = new ArrayList<>(j);
                    Collections.sort(sorted);
                    System.out.print(i + "\t");
                    sorted.forEach(x -> System.out.print(x + "\t"));
                    System.out.println();
                }));

        // 13
        // [2, 5, 7, 12, 14]
        // [2, 5, 7, 9, 12, 14]
        // 1+       | 1
        // 14/2 = 7 | 8
        //

        // 14
        // [2, 3, 5, 9, 12, 14]
        // [2, 3, 9, 11, 12, 14]
        // 1+       | 1
        // 14/2 = 7 | 8
        // 14/3 = 4.6666 -> 5 | 14
    }
}
