package kitchensim;

/**
 * POJO that matches the JSON order data
 *
 * The field 'creationTimeStamp' isn't part of the JSON data. It is used to track shelf life after the
 * order is prepared.
 */
class Order {
    private String id;
    private String name;
    private String temp;
    private int shelfLife;
    private float decayRate;
    private long creationTimeStamp;

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

    public long getCreationTime() { return creationTimeStamp; }

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

    public void setCreationTime(long value) {
        creationTimeStamp = value;
    }

    @Override
    public String toString() {
        return getId() + ", " + getName() + ", " + getTemp() + ", " + getDecayRate() + ", " + getShelfLife();
    }
}