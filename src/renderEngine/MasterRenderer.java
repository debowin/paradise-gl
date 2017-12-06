package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;
    private StaticShader staticShader = new StaticShader();
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private List<Terrain> terrains = new ArrayList<>();
    private Matrix4f projectionMatrix;

    private Vector3f skyColor = new Vector3f(0, 0.5f, 0.7f);
    private float fogDensity = 0.005f;
    private float fogGradient = 1.5f;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void cleanUp() {
        staticShader.cleanUp();
        terrainShader.cleanUp();
    }

    public void render(Light sun, Camera camera) {
        prepare();
        staticShader.start();
        staticShader.loadLight(sun);
        staticShader.loadSkyColor(skyColor);
        staticShader.loadFogDensity(fogDensity);
        staticShader.loadFogGradient(fogGradient);
        staticShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        staticShader.stop();

        terrainShader.start();
        terrainShader.loadLight(sun);
        terrainShader.loadSkyColor(skyColor);
        terrainShader.loadFogDensity(fogDensity);
        terrainShader.loadFogGradient(fogGradient);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        terrains.clear();
        entities.clear();
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(skyColor.x, skyColor.y, skyColor.z, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float yScale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio;
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -(FAR_PLANE + NEAR_PLANE) / frustumLength;
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -(2 * FAR_PLANE * NEAR_PLANE) / frustumLength;
        projectionMatrix.m33 = 0;
    }
}
