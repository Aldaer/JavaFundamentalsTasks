package bureaucratic.stationery;

class Pen extends WritingImplement {
    public Pen(String name, String color, int price) {
        super(name, color, price);
    }

    @Override
    protected String getType() {
        return "Pen";
    }
}

class Pencil extends WritingImplement {
    public Pencil(String name, String color, int price) {
        super(name, color, price);
    }

    @Override
    protected String getType() {
        return "Pencil";
    }
}

class Eraser extends StationeryItem {
    public Eraser(String name, int price) {
        super(name, price);
    }

    @Override
    protected String getType() {
        return "Eraser";
    }
}

class Toner extends Consumable {
    public Toner(String name, int price) {
        super(name, "kg", price);
    }

    @Override
    protected String getType() {
        return "Toner";
    }
}

class Ink extends Consumable {
    public Ink(String name, int price) {
        super(name, "100 ml", price);
    }

    @Override
    protected String getType() {
        return "Ink";
    }
}

class Folder extends StationeryItem {
    public Folder(String name, char size, int price) {
        super(name, price);
        this.size = size;
    }

    @Override
    protected String getType() {
        return "Folder";
    }

    @Override
    public String toPrintableEntry(Category type) {
        switch (type) {
            case SIZE:
                return "" + size;
        }
        return super.toPrintableEntry(type);
    }

    private char size;
}

class Paper extends Consumable {
    public Paper(String name, int price) {
        super(name, "pack", price);
    }

    @Override
    protected String getType() {
        return "Paper";
    }
}