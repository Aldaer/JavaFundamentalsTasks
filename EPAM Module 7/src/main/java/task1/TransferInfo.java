package task1;

/**
 * Class to hold money transfer operations. Will be loaded from XML.
 */
public class TransferInfo {
    long accountFrom;
    long accountTo;
    double amount;
    String comment;

    public String toString() {
        return "{ From: " + accountFrom + " to: " + accountTo + " amount: " + String.format("%8.2f", amount) + ((comment == null) ? "" : " [" + comment + "]") + " }";
    }
}
