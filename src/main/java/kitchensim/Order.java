package kitchensim;

/**
 * POJO that matches the JSON order data
 */
class Order {
    private String id;
    private String name;
    private String temp;
    private int shelfLife;
    private float decayRate;

    public Order() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTemp() {
        return temp;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public float getDecayRate() {
        return decayRate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setShelfLife(int shelfLife) {
        this.shelfLife = shelfLife;
    }

    public void setDecayRate(float decayRate) {
        this.decayRate = decayRate;
    }

    @Override
    public String toString() {
        return getId() + ", " + getName() + ", " + getTemp() + ", " + getDecayRate() + ", " + getShelfLife();
    }
}