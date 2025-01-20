package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    /**
     * Представляет собой фиксированное значение ширины, используемое для размещения координат в системе координат.
     * Обычно используется в расчетах или методах, требующих стандартного измерения ширины.
     * Это значение является неизменяемым и устанавливается как константа.
     */
    private static final int WIDTH_PLACE = 3;
    /**
     * Константа, представляющая размер по высоте, используемый для вычислений координат на основе сетки
     * в классе GeneratePresetImpl.
     * <p>
     * HEIGHT_PLACE определяет верхнюю границу для вычислений, связанных с высотой,
     * и используется в качестве фиксированного значения для определения вертикального предела
     * при генерации или проверке координат единиц измерения.
     */
    private static final int HEIGHT_PLACE = 21;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        System.out.println("start generating");

        // Армия и вспомогательные переменные
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = 0;

        // Множество занятых координат
        Set<String> occupiedCoordinates = new HashSet<>();
        Random random = new Random();

        // Сортировка юнитов по эффективности
        unitList.sort(Comparator.comparingDouble(GeneratePresetImpl::calculateUnitEfficiency));

        // Генерация армии
        for (Unit unit : unitList) {
            int unitCost = unit.getCost();
            int unitCount = 0;

            // Остановка при переполнении по очкам
            while (unitCount < 11 && currentPoints + unitCost <= maxPoints) {
                // Генерация уникальной случайной координаты
                String coordinateKey = getUniqueCoordinate(occupiedCoordinates, WIDTH_PLACE, HEIGHT_PLACE, random);

                // Клонируем юнита с новой координатой

                Unit newUnit = cloneWithCoordinates(
                        unit.getUnitType() + " " + unitCount,
                        coordinateKey, unit);

                // Логируем, добавляем в список
                System.out.println(newUnit.getName() + " x:" + newUnit.getxCoordinate() + " y:" + newUnit.getyCoordinate());
                selectedUnits.add(newUnit);

                // Обновляем состояние
                currentPoints += unitCost;
                unitCount++;
            }
        }

        // Устанавливаем список юнитов и очки в армию
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);

        System.out.println("finish generating");
        return computerArmy;
    }

    /**
     * Рассчитывает эффективность юнита на основе его базовой атаки, здоровья и стоимости.
     * Эффективность определяется как отношение базовой атаки и здоровья к стоимости юнита.
     * Если стоимость равна нулю или меньше, метод возвращает 0,0.
     *
     * @param unit единичный объект, для которого необходимо рассчитать эффективность
     * @return рассчитанная эффективность в виде двойного значения или 0,0, если удельная стоимость равна нулю или отрицательна
     */
    private static double calculateUnitEfficiency(Unit unit) {
        double cost = unit.getCost();
        return cost > 0 ? (unit.getBaseAttack() / cost + unit.getHealth() / cost) : 0.0;
    }

    /**
     * Генерирует уникальную координату в пределах заданных размеров по ширине и высоте,
     * которая еще не присутствует в заданном наборе занятых координат.
     *
     * @param occupiedCoordinates набор строк, представляющих уже занятые координаты
     * @param width               ширина координатной сетки
     * @param height              высота координатной сетки
     * @param random              экземпляр Random, используемый для генерации случайных координат
     * @return уникальная строка координат в формате "x_y"
     */
    private static String getUniqueCoordinate(Set<String> occupiedCoordinates, int width, int height, Random random) {
        String coordinateKey;
        do {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            coordinateKey = x + "_" + y;
        } while (!occupiedCoordinates.add(coordinateKey)); // .add вернет false для уже существующей координаты
        return coordinateKey;
    }

    /**
     * Создает новый экземпляр класса Unit с указанным именем и координатами,
     * копируя остальные свойства из заданного объекта.
     *
     * @param newName       новое уникальное имя, которое будет присвоено клонированному объекту
     * @param coordinateKey строка, представляющая новые координаты в формате "x_y"
     * @param unit          исходный объект, из которого будут скопированы свойства
     * @return новый экземпляр модуля с обновленным названием и координатами
     */
    public Unit cloneWithCoordinates(String newName, String coordinateKey, Unit unit) {
        // Разделяем координаты key на x и y
        String[] coordinates = coordinateKey.split("_");
        int x = Integer.parseInt(coordinates[0]);
        int y = Integer.parseInt(coordinates[1]);

        // Возвращаем новый объект типа Unit с обновленными значениями
        return new Unit(
                newName,                        // Уникальное имя юнита
                unit.getUnitType(),                  // Тип текущего юнита
                unit.getHealth(),                    // Остальное остается прежним
                unit.getBaseAttack(),
                unit.getCost(),
                unit.getAttackType(),
                new HashMap<>(unit.getAttackBonuses()),  // Создаем копии коллекций для независимости
                new HashMap<>(unit.getDefenceBonuses()),
                x,                              // Новая координата x
                y                               // Новая координата y
        );
    }
}