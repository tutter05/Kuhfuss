package de.tutter05.kuhfuss.utils;

/**
 * Represents a numeric brute-forcer
 */
public class Bruteforcer {

    private int currentElement, stringLength;
    private String lastElement;

    /**
     * Initializes a new brute-forcer object starting from the specified last attempted code
     */
    public Bruteforcer(final String lastCode) {
        this.currentElement = Integer.parseInt(lastCode);
        this.stringLength = lastCode.length();
    }

    /**
     * Calculates the next code
     * @return code
     */
    public String nextCode() {
        final String code = String.format("%0"+this.stringLength+"d", this.currentElement);

        if(this.currentElement+1 == (int)Math.pow(10, this.stringLength)) {
            this.stringLength++;
            this.currentElement = 0;
        } else {
            this.currentElement++;
        }

        lastElement = code;
        return code;
    }

    /**
     * Rolls bruteforcer back to the previous element
     */
    public void previousElement() {
        if (this.currentElement == 0) {
            this.stringLength--;
            this.currentElement = (int) Math.pow(10, this.stringLength) - 1;
        } else {
            this.currentElement--;
        }

        this.lastElement = String.format("%0" + this.stringLength + "d", this.currentElement);

    }

    /**
     * Returns the last generated code
     * @return last code
     */
    public String getLastElement() {
        return lastElement;
    }

}
