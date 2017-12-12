package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

public class Lamp extends Entity {

    private Light light;

    public Lamp(TexturedModel model, Vector3f lightColour, Vector3f position,
                float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        // place the actual light slightly above the lamp
        Vector3f attenuation = new Vector3f(1, 0.01f, 0.002f);
        light = new Light(Vector3f.add(position, new Vector3f(0, 12, 0), null), lightColour, attenuation);
    }

    public Light getLight() {
        return light;
    }
}
