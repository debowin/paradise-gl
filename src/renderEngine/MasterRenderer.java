package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
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

    private Vector3f skyColor = new Vector3f(0.5444f, 0.62f, 0.69f);
    private float fogDensity = 0.0035f;
    private float fogGradient = 5f;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    private SkyboxRenderer skyboxRenderer;

    public MasterRenderer(Loader loader) {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
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

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        prepare();
        staticShader.start();
        staticShader.loadClipPlane(clipPlane);
        staticShader.loadLights(lights);
        staticShader.loadSkyColor(skyColor);
        staticShader.loadFogDensity(fogDensity);
        staticShader.loadFogGradient(fogGradient);
        staticShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        staticShader.stop();

        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadLights(lights);
        terrainShader.loadSkyColor(skyColor);
        terrainShader.loadFogDensity(fogDensity);
        terrainShader.loadFogGradient(fogGradient);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        skyboxRenderer.render(camera);
        terrains.clear();
        entities.clear();
    }

    public void renderScene(List<Entity> entities, Terrain[][] terrains,
                            List<Light> lights, Camera camera, Vector4f clipPlane) {
        for (int i = 0; i < 1; i++)
            processTerrain(terrains[i / 2][i % 2]);
        for (Entity entity : entities) {
            processEntity(entity);
        }
        render(lights, camera, clipPlane);
    }

    private void processEntity(Entity entity) {
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

    private void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    private void prepare() {
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
