package task1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

/**
 * Class task1.Pen describes a pen (surprise, surprise!)<br>
 * Pens have vendors, model names, body colors and ink colors.<br>
 * Two pens are considered equals if they have the same vendor, model and body color, regardless of ink color.
 */
public class Pen {

    /**
     * Creates a new pen (without ink) with provided parameters.
     *
     * @param vendor    Vendor name
     * @param model     Model name
     * @param bodyColor Pen's body color
     */
    public Pen(@NotNull String vendor, @NotNull String model, @NotNull Color bodyColor) {
        this.vendor = vendor;
        this.model = model;
        this.bodyColor = bodyColor;
    }

    /**
     * Returns pen's vendor name.
     */
    @NotNull
    public String getVendor() {
        return vendor;
    }

    /**
     * Returns pen's model name.
     */
    @NotNull
    public String getModel() {
        return model;
    }

    /**
     * Returns pen's body color.
     */
    @NotNull
    public Color getBodyColor() {
        return bodyColor;
    }

    /**
     * Returns pen's ink color, or empty if the pen wasn't filled
     */
    @NotNull
    public Optional<Color> getInkColor() {
        return Optional.ofNullable(inkColor);
    }

    /**
     * Fills the pen with ink of specified color. You may provide null to make the pen empty.
     *
     * @param inkColor Color of the ink.
     */

    public void fillWithInk(@Nullable Color inkColor) {
        this.inkColor = inkColor;
    }

    /**
     * Returns true if object o2 is a pen of the same vendor, model and body color, false otherwise.
     *
     * @param o2 Object to compare
     * @return True if the pens are equal
     */

    @Override
    public boolean equals(@Nullable Object o2) {
        if (this == o2) return true;
        if (o2 == null) return false;
        if (!(o2 instanceof Pen)) return false;

        Pen pen2 = (Pen) o2;

        return ((vendor.equals(pen2.vendor)) && (model.equals(pen2.model)) && bodyColor.equals(pen2.bodyColor));
    }

    /**
     * Return this pen's hashcode
     *
     * @return {task1.Pen's hashcode}
     */
    @Override
    public int hashCode() {
        return vendor.hashCode() + 31 * model.hashCode() + 97 * bodyColor.hashCode();
    }

    private String col2Str(Color c) {
        return String.format("%#08x", c.getRGB() & 0xffffff);
    }

    @Override
    public String toString() {
        return String.format("%s made by %s in color %s (%s)", model, vendor, col2Str(bodyColor),
                inkColor == null ? "no ink" : "ink: " + col2Str(inkColor));
    }

    private final String vendor;
    private final String model;
    private final Color bodyColor;
    private Color inkColor;
}

