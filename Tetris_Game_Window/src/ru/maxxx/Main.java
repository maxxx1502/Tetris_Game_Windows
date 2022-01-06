package ru.maxxx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main extends JFrame implements KeyListener {
    private JTextArea [] [] grids; // Превращаем весь интерфейс в текстовую область, и вся игра воспроизводится внутри
    private int data [] []; // Для данных каждой сетки 1 означает квадрат, 0 означает пустую область
    private int [] allRect; // Все типы блоков хранятся в 16 байтах, а графика Тетриса - в сетке 4 * 4
    private int rect; // Тип блока, на который падает текущая игра;
    private int x, y; // Координатная позиция текущего квадрата, x представляет строку, y представляет столбец
    private int score = 0; // Записываем текущий счет игры, по 10 очков для каждого уровня
    private JLabel label; // Отобразить метку оценки
    private JLabel label1; // Показываем, окончена ли игра
    private boolean running; // Используется, чтобы определить, окончена ли игра
    /* Конструктор без параметров */
    public Main() {
        grids = new JTextArea [26] [12]; // Устанавливаем строку и столбец игровой области
        data = new int [26] [12]; // Открытие пространства массива данных согласуется со строками и столбцами игровой области
        allRect = new int[] { 0x00cc, 0x8888, 0x000f, 0x0c44, 0x002e, 0x088c, 0x00e8, 0x0c88, 0x00e2, 0x044c, 0x008e,
                0x08c4, 0x006c, 0x04c8, 0x00c6, 0x08c8, 0x004e, 0x04c4, 0x00e4}; // 19 видов квадратных форм, например, 0x00cc равно 0000, что означает квадрат 2 * 2
        //0000
        //1100
        //1100
        label = new JLabel ("score: 0"); // Эта метка хранит счет, инициализированный 0 баллами
        label1 = new JLabel ("Начать игру"); // Этот ярлык напоминает о состоянии игры: начало или конец
        running = false; // это флаговая переменная, false означает, что игра окончена, true означает, что игра продолжается
        init (); // Инициализируем игровой интерфейс
    }
    /* Функция инициализации игрового интерфейса */
    public void init() {
        JPanel center = new JPanel (); // Эта панель является основной областью игры
        JPanel right = new JPanel (); // Эта панель является областью описания игры
        center.setLayout (new GridLayout (26, 12, 1, 1)); // делим строки и столбцы на основную область игры, всего 26 строк и 12 столбцов
        for (int i = 0; i <grids.length; i ++) {// инициализируем панель
            for (int j = 0; j < grids[i].length; j++) {
                grids[i][j] = new JTextArea(20, 20);
                grids[i][j].setBackground(Color.WHITE);
                grids [i] [j] .addKeyListener (this); // Добавляем событие прослушивания клавиатуры
                // Инициализируем границу игры
                if (j == 0 || j == grids[i].length - 1 || i == grids.length - 1) {
                    grids[i][j].setBackground(Color.PINK);
                    data[i][j] = 1;
                }
                grids [i] [j] .setEditable (false); // Текстовая область не редактируется
                center.add (grids [i] [j]); // Добавляем текстовую область на главную панель
            }
        }
        // Инициализируем панель описания игры
        right.setLayout(new GridLayout(4, 1));
        right.add(new JLabel(" a : left        d : right"));
        right.add(new JLabel(" s : down   w : change"));
        right.add(label);
        label1.setForeground (Color.RED); // Установите для содержимого метки красный шрифт
        right.add(label1);

        // Добавляем в форму основную панель и панель описания
        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
        this.add(right, BorderLayout.EAST);
        running = true; // Инициализируем текущее состояние на true, что означает, что программа запущена и игра запускается
        this.setSize (600, 850); // Устанавливаем размер окна
        this.setVisible (true); // Форма видна
        this.setLocationRelativeTo (null); // Устанавливаем центр формы
        this.setResizable (false); // Размер формы изменить нельзя
        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); // Освободить форму
    }
    /*Основная функция*/
    public static void main(String[] args) {
        Main m = new Main (); // Создаем объект Main, в основном используемый для инициализации данных
        m.go (); // Запускаем игру
    }
    /*Начать игру*/
    public void go () {// запускаем игру
        while (true) {// Игра запускается до тех пор, пока игра не завершится ошибкой и не закончится, иначе она была выполнена
            if (running == false) {// Если игра не удалась
                break;
            }
            ranRect (); // Рисуем падающую форму сетки
            start (); // запускаем игру
        }
        label1.setText ("Игра окончена!"); // Игра окончена
    }
    /* Рисуем падающую сетку */
    public void ranRect() {
        rect = allRect [(int) (Math.random () * 19)]; // Произвольно генерируем типы блоков (всего 7 типов, 19 форм)
    }
    /* Функция запуска игры */
    public void start() {
        x = 0;
        y = 5; // Инициализируем положение падающего квадрата
        for (int i = 0; i <26; i ++) {// Всего 26 слоев, падающих один за другим
            try {
                Thread.sleep (1000); // Задержка на 1 секунду на слой
                if (canFall (x, y) == false) {// Если его нельзя отбросить
                    saveData (x, y); // Помечаем эту квадратную область data [] [] как 1, что указывает на наличие данных
                    for (int k = x; k <x + 4; k ++) {// Пройдите по 4 слоям, чтобы увидеть, есть ли квадраты в каждом слое, чтобы удалить этот ряд квадратов и подсчитать результат
                        int sum = 0;
                        for (int j = 1; j <= 10; j++) {
                            if (data[k][j] == 1) {
                                sum++;
                            }
                        }
                        if (sum == 10) {// Если в слое k есть блоки, удалите блоки в слое k
                            removeRow(k);
                        }
                    }
                    for (int j = 1; j <= 10; j ++) {// 4 верхних слоя игры не могут иметь квадратов, иначе игра завершится ошибкой
                        if (data[3][j] == 1) {
                            running = false;
                            break;
                        }
                    }
                    break;
                }
                // если его можно отбросить
                x ++; // слой плюс один
                fall (x, y); // Падаем на один слой
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    /* Определяем, может ли падающий блок упасть */
    public boolean canFall(int m, int n) {
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i <4; i ++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j <4; j++) {
                if((temp & rect)!= 0) {//когда здесь квадрат
                    if (data [m + 1] [n] ==1) // Если на следующем месте стоит квадрат, сразу вернуть false
                        return false;
                    }
                    n ++; //Столбец плюс один
                    temp >>= 1;
                }
                m ++; // Следующая строка
                n = n-4; // Вернуться к первому столбцу
            }
        return true;// можно отбросить, чтобы вернуть true
        }


    /* Сохраняем соответствующие данные неубывающего блока как 1, указывая, что в этой координате есть блок */
    public void saveData(int m, int n) {
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i <4; i ++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {
                    data [m] [n] = 1; // массив данных хранится как 1
                }
                n ++; // Следующий столбец
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }

    }

    /* Удаляем все квадраты в ряду рядов, и вышеперечисленные будут спускаться по очереди */

    public void removeRow(int row) {
        for (int i = row; i >= 1; i--) {
            for (int j = 1; j <= 10; j++) {
                data[i][j] = data[i - 1][j];//
            }
        }
        reflesh (); // Обновляем область главной панели игры после удаления блока строки
        score += 10; // Оценка плюс 10;
        label.setText ("score:" + score); // Показать счет

    }

    /* Обновляем область главной панели игры после удаления блока строки */
    public void reflesh() {
        for(int i = 1; i < 25; i++){
            for( int j = 1; j < 11; j++) {
                if (data [i] [j] == 1) {
                    grids [i] [j].setBackground(Color.GREEN);
                }
                else {
                    grids [i] [j].setBackground(Color.WHITE);
                }
            }
        }
    }

    /* Блок отбрасывает слой */
    public void fall (int m, int n) {
        if (m> 0) // когда блок падает на один уровень
            clear (m-1, n); // Очищаем цветные квадраты на предыдущем слое
        draw (m, n); // перерисовываем квадратное изображение
    }

    /* Очищаем цветные области до падения блока */
    public void clear (int m, int n) {
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i < 4; i++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {// Когда здесь квадрат
                    grids [m] [n].setBackground(Color.WHITE);// Очищаем цвет и превращаем его в белый
                }
                n ++; // Следующий столбец
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }
    }

    /* Рисуем изображение обратного блока */
    public void draw(int m, int n){
        int temp = 0x8000; // означает 1000 0000 0000 0000
        for (int i = 0; i < 4; i++) {// Проходим через 16 квадратов (4 * 4)
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {// Когда здесь квадрат
                    grids [m] [n].setBackground(Color.GREEN);// Место с квадратами становится зеленым
                }
                n ++; // Следующий столбец
                temp >>= 1;
            }
            m ++; // Следующая строка
            n = n-4; // Вернуться к первому столбцу
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar () == 'a') { // Перемещаем квадрат влево
            if (running == false) {
                return;
            }
            if (y <= 1) // Когда попадает в левую стену
                return;
            int temp = 0x8000; // означает 1000 0000 0000 0000
            for (int i = x; i <x + 4; i++) {
                for(int j = y; j < y+4; j++) {
                    if ((rect & temp)!= 0) {
                        if (data [i] [j-1] ==1) {
                            return;
                        }
                    }
                    temp >>=1;
                }
            }
            clear(x, y); // Когда вы можете двигаться влево, очистите цвет квадрата перед перемещением влево
            y--;
            draw (x, y); // Затем перерисовываем изображение квадрата после сдвига влево
        }

        if (e.getKeyChar() == 'd') {// Квадрат перемещается вправо
            if (running == false) {
                return;
            }
            int temp = 0x8000;
            int m = x, n = y;
            int num = 7;
            for (int i = 0; i<4; i++) {
                for (int j = 0; j<4; j++) {
                    if ((temp & rect) != 0) {
                        if (n > num) {
                            num = n;
                        }
                    }
                    temp >>= 1;
                    n++;
                }
                m++;
                n = n-4;
            }
            if (num >= 10) {
                return;
            }
            temp = 0x8000;
            for (int i = x; i < x + 4; i++) {
                for (int j = y; j < y + 4; j++) {
                    if ((rect & temp) != 0) {
                        if (data[i][j + 1] == 1) {
                            return;
                        }
                    }
                    temp >>= 1;
                }
            }
            clear (x, y); // Когда вы можете двигаться вправо, очистите цвет квадрата перед перемещением вправо
            y++;
            draw (x, y); // Затем перерисовываем изображение квадрата после перемещения вправо
        }
        if (e.getKeyChar() == 's') {// Блок перемещается вниз
            if (running == false) {
                return;
            }
            if (canFall(x, y) == false) {
                saveData(x, y);
                return;
            }
            clear (x, y); // Когда вы можете двигаться вниз, очистите цвет квадрата перед перемещением вниз
            x++;
            draw (x, y); // Затем перерисовываем изображение квадрата после движения вниз
        }
        if (e.getKeyChar() == 'w') {// Изменение формы поля
            if (running == false) {
                return;
            }
            int i = 0;
            for (i = 0; i <= allRect.length; i++) {// Перебираем 19 квадратных фигур
                if(allRect [i] == rect) // Находим форму, соответствующую падающему квадрату, а затем меняем форму
                    break;
            }
            if (i == 0) // квадратный блок без изменения формы, это блок типа 1
                return;
            clear(x, y);
            if (i == 1 || i == 2) {// тип блочной графики 2
                rect = allRect[i == 1 ? 2 : 1];
                if (y > 7)
                    y = 7;
            }
            if (i>= 3 && i <= 6) {// тип графического блока 3
                rect = allRect[i + 1 > 6 ? 3 : i + 1];
            }
            if (i>= 7 && i <= 10) {// тип графики блока 4
                rect = allRect[i + 1 > 10 ? 7 : i + 1];
            }
            if (i == 11 || i == 12) {// тип блочной графики 5
                rect = allRect[i == 11 ? 12 : 11];
            }
            if (i == 13 || i == 14) {// тип блочной графики 6
                rect = allRect[i == 13 ? 14 : 13];
            }
            if (i>= 15 && i <= 18) {// тип графического блока 7
                rect = allRect[i + 1 > 18 ? 15 : i + 1];
            }
            draw(x, y);
        }

    }


}
// ДОМАШНЕЕ ЗАДАНИЕ
//собрать игру в файл jar
//переопределить кнопки и поменять цвета всех элементов
// * изменить игровое поле и логику игры СООТВЕТСТВЕННО (имеется ввиду размеры)
