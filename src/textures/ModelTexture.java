package textures;

public class ModelTexture {

    private int textureID;

    private float shineDamper = 100;
    private float reflectivity = 0.8f;

    private boolean transparent = false;
    private boolean fakeLighting = false;

    private int numberOfRows = 1;

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public ModelTexture(int id) {
        this.textureID = id;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public int getID() {
        return this.textureID;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public boolean needsFakeLighting() {
        return fakeLighting;
    }

    public void setFakeLighting(boolean fakeLighting) {
        this.fakeLighting = fakeLighting;
    }
}
