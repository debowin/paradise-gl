package water;

import entities.Camera;
import entities.Light;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.Maths;

import java.util.List;

public class WaterRenderer {

    private static final String DUDV_MAP = "waterDUDV";
    private static final String NORMAL_MAP = "normalMap";
    private static final float WAVE_SPEED = 0.03f;

    private RawModel quad;
    private List<WaterTile> waterTiles;
    private WaterShader shader;
    private WaterFrameBuffers buffers;

    private int dudvTexture;
    private int normalTexture;

    private float moveFactor = 0;

    public WaterRenderer(Loader loader, WaterShader shader, List<WaterTile> waterTiles,
                         Matrix4f projectionMatrix, WaterFrameBuffers buffers) {
        this.shader = shader;
        this.waterTiles = waterTiles;
        this.buffers = buffers;
        dudvTexture = loader.loadTexture(DUDV_MAP);
        normalTexture = loader.loadTexture(NORMAL_MAP);
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadPlaneDistances(MasterRenderer.getNearPlane(), MasterRenderer.getFarPlane());
        shader.stop();
        setUpVAO(loader);
    }

    public void render(Camera camera, Light sun) {
        prepareRender(camera, sun);
        for (WaterTile tile : waterTiles) {
            Matrix4f modelMatrix = Maths.createTransformationMatrix(
                    new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
                    WaterTile.TILE_SIZE);
            shader.loadModelMatrix(modelMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
        }
        unbind();
    }

    private void prepareRender(Camera camera, Light sun) {
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();
        moveFactor %= 1;
        shader.loadMoveFactor(moveFactor);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers.getReflectionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers.getRefractionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers.getRefractionDepthTexture());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void unbind() {
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void setUpVAO(Loader loader) {
        // Just x and z vectex positions here, y is set to 0 in v.shader
        float[] vertices = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
        quad = loader.loadToVAO(vertices, 2);
    }

    public boolean isUnderWater(Vector3f position) {
        for (WaterTile waterTile : waterTiles) {
            if (position.x < (waterTile.getX() - WaterTile.TILE_SIZE) || position.x > (waterTile.getX() + WaterTile.TILE_SIZE)) {
                return false;
            }
            if (position.z < (waterTile.getZ() - WaterTile.TILE_SIZE) || position.z > (waterTile.getZ() + WaterTile.TILE_SIZE)) {
                return false;
            }
            if (position.y > waterTile.getHeight())
                return false;
        }
        return true;
    }
}
