package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Ваше решение
        // Получаем юниты обеих армий
        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        playerUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));
        computerUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));

        // Имитируйте боевые раунды до тех пор, пока в обеих армиях есть живые юниты
        while (hasAliveUnits(playerUnits) && hasAliveUnits(computerUnits)) {
            // Симулируем раунд
            simulateRound(playerUnits, computerUnits);
        }

        //Объявляем победителя
        declareWinner(playerUnits, computerUnits);
    }

    /**
     * Проверяет, содержит ли заданный список юнитов какие-либо живые юниты.
     *
     * @param units список юнитов для проверки на наличие жизни
     * @return true, если хотя бы один юнит в списке жив, false в противном случае
     */
    private boolean hasAliveUnits(List<Unit> units) {
        return units.stream().anyMatch(Unit::isAlive);
    }

    /**
     * Моделирует один раунд битвы, в котором юниты из армий игрока и компьютера ходят по очереди
     * в порядке их базовой силы атаки. Юниты выполняют действия, например атакуют цель, если это возможно.
     *
     * @param playerUnits список юнитов из армии игрока, которые будут участвовать в раунде
     * @param computerUnits список юнитов из армии компьютера, которые будут участвовать в раунде
     * @throws InterruptedException если поток, выполняющий симуляцию, прерывается во время обработки.
     */
    private void simulateRound(List<Unit> playerUnits, List<Unit> computerUnits) throws InterruptedException {
        // Создайте приоритетную очередь для поддержания порядка атаки
        Queue<Unit> allUnits = new PriorityQueue<>((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));
        allUnits.addAll(playerUnits.stream().filter(Unit::isAlive).toList());
        allUnits.addAll(computerUnits.stream().filter(Unit::isAlive).toList());

        // Перебирайте юниты и выполняйте их ходы
        while (!allUnits.isEmpty()) {
            Unit currentUnit = allUnits.poll();

            // Пропускать мертвые объекты
            if (!currentUnit.isAlive()) continue;

            // Найдите цель для атаки
            Unit target = currentUnit.getProgram().attack();

            // Если действительная цель найдена и активна, выполните действие
            if (target != null && target.isAlive()) {
                attackAndLog(currentUnit, target);
            }
        }
    }

    /**
     * Выполняет атаку от атакующего юнита к целевому юниту, нанося урон и обновляя статус цели. Кроме того, записывает атаку в журнал, если ведение журнала включено.
     *
     * @param attacker сторона — юнит, выполняющий атаку
     * @param target — юнит, получающий атаку
     */
    private void attackAndLog(Unit attacker, Unit target) {
        // Выполнять расчеты атаки
        int damage = attacker.getBaseAttack();
        target.setHealth(target.getHealth() - damage);

        // Настройка панели инструментов…
        if (target.getHealth() <= 0) {
            target.setAlive(false);
        }

        // Регистрируйте атаку, если ведение журнала включено
        if (printBattleLog != null) {
            printBattleLog.printBattleLog(attacker, target);
        }
    }

    /**
     * Определяет победителя битвы, проверяя, есть ли у игрока или компьютера выжившие юниты.
     * Выводит результат битвы на консоль.
     *
     * @param playerUnits список юнитов, принадлежащих армии игрока
     * @param computerUnits список юнитов, принадлежащих армии компьютера
     */
    private void declareWinner(List<Unit> playerUnits, List<Unit> computerUnits) {
        // Объявляйте результаты, основанные на наличии живых подразделений в соответствующих армиях
        if (hasAliveUnits(playerUnits)) {
            System.out.println("Игрок выигрывает!");
        } else if (hasAliveUnits(computerUnits)) {
            System.out.println("Компьютер побеждает!");
        } else {
            System.out.println("Это ничья!");
        }
    }
}