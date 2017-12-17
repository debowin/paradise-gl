package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private Vector3f position = new Vector3f(100, 35, -50);
    private float pitch = 10;
    private float yaw;
    private float roll;

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        checkReset();
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistanceFromPlayer = (float) Math.cos(Math.toRadians(pitch)) * distanceFromPlayer;
        float verticalDistanceFromPlayer = (float) Math.sin(Math.toRadians(pitch)) * distanceFromPlayer;
        calculateCameraPositionAndYaw(horizontalDistanceFromPlayer, verticalDistanceFromPlayer);
    }

    public void invertPitch() {
        pitch = -pitch;
    }

    private void checkReset() {
        if (Mouse.isButtonDown(1)) {
            angleAroundPlayer = 0;
            pitch = 10;
            distanceFromPlayer = 50;
        }
    }

    private void calculateCameraPositionAndYaw(float hDistance, float vDistance) {
        position.y = player.getPosition().y + vDistance;
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = hDistance * (float) Math.sin(Math.toRadians(theta));
        float offsetZ = hDistance * (float) Math.cos(Math.toRadians(theta));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        yaw = 180 - theta;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(0)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
}
