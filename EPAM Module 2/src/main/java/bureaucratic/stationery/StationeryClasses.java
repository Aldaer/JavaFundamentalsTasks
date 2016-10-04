package bureaucratic.stationery;

import java.security.InvalidParameterException;

abstract class StationeryItem extends Stationery {
    public StationeryItem(String name, int price) {
        super(name);
        this.price = price;
    }

    @Override
    public int getCost() {
        return price * quantity;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public StationeryItem setPrice(int price) {
        this.price = price > 0 ? price : 0;
        return this;
    }

    public StationeryItem setQuantity(int qty) {
        this.quantity = qty > 0 ? qty : 0;
        return this;
    }

    private int price;
    private int quantity;

    @Override
    public String toPrintableEntry(Category type) {
        switch (type) {

            case QTY:
                return ((Integer) quantity).toString();
            case PRICE:
                return ((Integer) price).toString();
        }
        return super.toPrintableEntry(type);
    }

    @Override
    public String getUnit() {
        return "pcs.";
    }

}

abstract class WritingImplement extends StationeryItem {

    public WritingImplement(String name, String color, int price) {
        super(name, price);
        this.color = color;
    }

    @Override
    public String toPrintableEntry(Category type) {
        switch (type) {
            case COLOR:
                return color;
        }
        return super.toPrintableEntry(type);
    }

    public String getColor() {
        return color;
    }

    private final String color;
}

abstract class Consumable extends Stationery {

    public Consumable(String name, String unit, int price) {
        super(name);
        this.unit = unit;
        this.price = price;
    }

    /**
     * Cost is always rounded UP
     */
    @Override
    public int getCost() {
        return (int) Math.round(Math.ceil(price * amount));
    }

    /**
     * Factor is a number of new units in an old unit.<br>     *
     * E.g. convertUnits("kg", "g", 1000.0)<br>
     * Be aware that price after the conversion can be incorrect due to rounding error.
     */
    public void convertUnits(String oldUnit, String newUnit, double factor) throws InvalidParameterException {
        if ((!unit.equals(oldUnit)) || (factor <= 0)) throw new InvalidParameterException("Illegal unit conversion");

        int oldcost = getCost();
        unit = newUnit;
        amount *= factor;
        price /= factor;
        int newcost = getCost();
        if (oldcost != newcost)
            System.err.printf("Rounding error converting %s to %s: %d -> %d%n", oldUnit, newUnit, oldcost, newcost);
    }

    @Override
    public String toPrintableEntry(Category type) {
        switch (type) {
            case QTY:
                return String.format("%7.3f", amount);
            case PRICE:
                return ((Integer) price).toString();
            case UNIT:
                return unit;
        }
        return super.toPrintableEntry(type);
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public Consumable setPrice(int price) {
        this.price = price > 0 ? price : 0;
        return this;
    }


    public double getAmount() {
        return amount;
    }

    public Consumable setAmount(double amount) {
        this.amount = (amount >= 0) ? amount : 0;
        return this;
    }

    private double amount;
    private int price;
    private String unit;
}