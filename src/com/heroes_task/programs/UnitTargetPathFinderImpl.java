package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;
import java.util.stream.Collectors;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    /**
     * Представляет собой фиксированную константу, используемую для определения ширины координатного пространства в системе.
     * Это значение в основном применяется в вычислениях или методах, требующих стандартизированного измерения ширины.
     * Будучи неизменным, оно служит в качестве единой ссылки на размерность во всём классе.
     */
    private static final int WIDTH_PLACE = 27;
    /**
     * Представляет собой фиксированное значение высоты, используемое для вычислений в системе координат на основе сетки.
     * Эта константа определяет вертикальную границу для операций, требующих измерения высоты,
     * и служит неизменяемым параметром для размещения или проверки единичных координат.
     */
    private static final int HEIGHT_PLACE = 21;
    /**
     * Представляет возможные направления движения в двумерной сетке.
     * Каждый подмассив содержит два целых числа, где первое целое число представляет
     * изменение координаты x, а второе целое число представляет изменение координаты y.
     *
     * Эти направления соответствуют:
     * - {-1, 0}: движение вверх (уменьшение координаты x).
     * - {1, 0}: движение вниз (увеличение координаты x).
     * - {0, -1}: движение влево (уменьшение координаты y).
     * - {0, 1}: движение вправо (увеличение координаты y).
     *
     * Обычно используется для навигации или алгоритмов поиска пути в структуре 2D-сетки.
     */
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Ваше решение
        Set<String> occupiedCells = getOccupiedCells(existingUnitList, attackUnit, targetUnit);

        // Карта расстояний
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Инициализируем стартовый узел
        Node startNode = new Node(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        distances.put(startNode, 0);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Если достигли целевой точки, прерываем цикл
            if (current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate()) {
                break;
            }

            for (int[] dir : DIRECTIONS) {
                int neighborX = current.getX() + dir[0];
                int neighborY = current.getY() + dir[1];

                if (!isValid(neighborX, neighborY, occupiedCells)) {
                    continue; // Пропускаем недопустимые клетки
                }

                Node neighbor = new Node(neighborX, neighborY);
                int currentDistance = distances.getOrDefault(current, Integer.MAX_VALUE);
                int newDistance = currentDistance + 1;

                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return constructPath(previous, attackUnit, targetUnit);
    }

    private Set<String> getOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        return existingUnitList.stream()
                .filter(unit -> unit.isAlive() && unit != attackUnit && unit != targetUnit)
                .map(unit -> unit.getxCoordinate() + "," + unit.getyCoordinate())
                .collect(Collectors.toSet());
    }

    private boolean isValid(int x, int y, Set<String> occupiedCells) {
        return x >= 0 && x < WIDTH_PLACE && y >= 0 && y < HEIGHT_PLACE && !occupiedCells.contains(x + "," + y);
    }

    private List<Edge> constructPath(Map<Node, Node> previous, Unit attackUnit, Unit targetUnit) {
        Deque<Edge> pathStack = new ArrayDeque<>();
        Node current = new Node(targetUnit.getxCoordinate(), targetUnit.getyCoordinate());

        // Построение пути и добавление в стек
        while (current != null) {
            pathStack.push(new Edge(current.getX(), current.getY())); // Используем стек для добавления
            current = previous.get(current);
        }

        // Преобразуем стек в список
        return new ArrayList<>(pathStack);
    }

    private static class Node {
        private final int x;
        private final int y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

    }
}
