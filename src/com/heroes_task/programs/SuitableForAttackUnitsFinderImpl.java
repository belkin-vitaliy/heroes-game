package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        // Ваше решение
        List<Unit> suitableUnits = new ArrayList<>();
        // Для каждой строки в unitsByRow
        for (List<Unit> row : unitsByRow) {
            // Найти цель с минимальной или максимальной координатой Y в зависимости от isLeftArmyTarget
            Comparator<Unit> comparator = isLeftArmyTarget
                    ? Comparator.comparingInt(Unit::getyCoordinate) // Минимальная координата Y
                    : Comparator.comparingInt(Unit::getyCoordinate).reversed(); // Максимальная координата Y

            // Найти подходящего юнита
            row.stream()
                    .filter(Unit::isAlive) // Только живые юниты
                    .min(comparator) // Выбираем минимальную/максимальную координату Y
                    .ifPresent(suitableUnits::add); // Если цель найдена, добавляем её в список
        }

        return suitableUnits;
    }

}
