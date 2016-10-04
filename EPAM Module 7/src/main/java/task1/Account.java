package task1;

/**
 * Class to hold account info. NOT thread-safe!
 */
public class Account implements Cloneable, Comparable<Account> {
    private final long number;
    private double balance;

    Account(long number, double initialBalance) {
        this.number = number;
        balance = initialBalance;
    }

    public Account deposit(double amt) {
        balance += amt;
        return this;
    }

    public Account withdraw(double amt) throws IllegalStateException {
        if (balance < amt) throw new IllegalStateException("Not enough money on acc. " + number);
        balance -= amt;
        return this;
    }

    public long getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public Account clone() {
        try {
            return (Account) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;            // Should never happen
        }
    }

    @Override
    public int compareTo(Account acc2) {
        return Long.compare(this.number, acc2.number);
    }
}
