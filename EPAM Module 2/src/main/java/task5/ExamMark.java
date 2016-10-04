package task5;

import org.jetbrains.annotations.NotNull;

/**
 * Оценки за экзамен. Могут выставляться буквами, целым баллами или вещественным числом.
 * Для каждого вида - свой способ сравнения.
 */
public class ExamMark implements Comparable<ExamMark> {
    @Override
    public int compareTo(@NotNull ExamMark o) {
        if (o.type != type) throw new IllegalArgumentException("Cannot compare " + o.type + " to " + this.type);
        switch (type) {
            case LETTER:
                return -((int) charMark - (int) o.charMark);       // "A" лучше, чем "B"
            case INT:
                return intMark - o.intMark;
            case FLOAT:
                return Math.round(Math.signum(floatMark - o.floatMark));
            default:
                throw new IllegalArgumentException("Uncomparable type: " + this.type);
        }
    }

    /**
     * Сдано или нет: true, если оценка не меньше пороговой
     */
    public boolean pass(ExamMark threshold) {
        return (compareTo(threshold) >= 0);
    }

    /**
     * Устанавливает значение оценки, раскодируя из строки
     */
    public ExamMark setMark(String mark) {
        try {
            switch (type) {
                case LETTER:
                    charMark = mark.toUpperCase().charAt(0);
                    return this;
                case INT:
                    intMark = Integer.parseInt(mark);
                    return this;
                case FLOAT:
                    floatMark = Float.parseFloat(mark);
                    return this;
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Mark " + this.type + " cannot be set to " + mark);
        }
        return this;
    }

    /**
     * Возвращает оценку любого типа в виде строки
     */
    @Override
    public String toString() {
        switch (type) {
            case LETTER:
                return "\"" + charMark + "\"";
            case INT:
                return String.valueOf(intMark);
            case FLOAT:
                return String.valueOf(floatMark);
            default:
                return "n/a";
        }
    }

    /**
     * Возможные типы оценок
     */
    public enum Type {
        LETTER, INT, FLOAT
    }

    ;

    /**
     * Тип оценки
     */
    public final Type type;

    public ExamMark(Type type) {
        this.type = type;
    }

    private char charMark;
    private int intMark;
    private float floatMark;
}
