package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import water.WaterRenderer;

public class Player extends Entity {

    private static final float RUN_SPEED = 20;
    private static final float VTOL_SPEED = 10;
    private static final float TURN_SPEED = 120;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float currentVTOLSpeed = 0;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain[][] terrain, WaterRenderer waterRenderer) {
        // find which terrain player is in
        int terrainX = (int) Math.floor(super.getPosition().x / Terrain.getSIZE());
        int terrainZ = -(int) Math.floor(super.getPosition().z / Terrain.getSIZE()) - 1;
        handleInput();
        super.rotate(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = distance * (float) Math.sin(Math.toRadians(super.getRotY()));
        float dy = currentVTOLSpeed * DisplayManager.getFrameTimeSeconds();
        float dz = distance * (float) Math.cos(Math.toRadians(super.getRotY()));
        super.increasePosition(dx, dy, dz);
        float terrainHeight = terrain[terrainX][terrainZ].getTerrainHeight(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight + 5) {
            // if too close to ground
            super.getPosition().y = terrainHeight + 5;
        }
        if (waterRenderer.isUnderWater(super.getPosition())){
            // if underwater
            super.getPosition().y += 0.2f;
        }
    }

    private void handleInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            this.currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_Q)){
            this.currentVTOLSpeed = VTOL_SPEED;
        } else if(Keyboard.isKeyDown(Keyboard.KEY_E)){
            this.currentVTOLSpeed = -VTOL_SPEED;
        } else {
            this.currentVTOLSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            this.currentVTOLSpeed *= 3;
            this.currentSpeed *= 3;
        }
    }
}
