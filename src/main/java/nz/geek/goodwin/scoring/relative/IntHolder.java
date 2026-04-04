package nz.geek.goodwin.scoring.relative;

class IntHolder {
    private Integer value;

    public IntHolder(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void incValue() {
        this.value++;
    }

    public String toString() {
        return value == 1
                ? "Maj: 1st"
                : "Maj: 1st to " + ordinal(value);
    }

    private String ordinal(int n) {
        int mod100 = n % 100;
        if (mod100 >= 11 && mod100 <= 13) {
            return n + "th";
        }

        return switch (n % 10) {
            case 1 -> n + "st";
            case 2 -> n + "nd";
            case 3 -> n + "rd";
            default -> n + "th";
        };
    }
}
