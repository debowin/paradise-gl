package engineTester;

import entities.*;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        // TERRAIN TEXTURE
        TerrainTexture backGroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backGroundTexture, rTexture, gTexture, bTexture);

        int TERRAIN_TILES_Z = 1;
        int TERRAIN_TILES_X = 1;
        Terrain[][] terrains = new Terrain[TERRAIN_TILES_X][TERRAIN_TILES_Z];
        // define the array of TERRAIN_TILES_X * TERRAIN_TILES_Z terrain tiles.
        terrains[0][0] = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");

        // ENTITIES
        RawModel treeModel = OBJFileLoader.loadOBJModel("tree", loader);
        RawModel pineModel = OBJFileLoader.loadOBJModel("pine", loader);
        RawModel fernModel = OBJFileLoader.loadOBJModel("fern", loader);
        RawModel lowPolyTreeModel = OBJFileLoader.loadOBJModel("lowPolyTree", loader);
        RawModel lampModel = OBJFileLoader.loadOBJModel("lamp", loader);

        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel pine = new TexturedModel(pineModel, new ModelTexture(loader.loadTexture("pine")));
        pine.getTexture().setTransparent(true);
        ModelTexture lowPolyTreeTextureAtlas = new ModelTexture(loader.loadTexture("lowPolyTree"));
        lowPolyTreeTextureAtlas.setNumberOfRows(2);
        TexturedModel lowPolyTree = new TexturedModel(lowPolyTreeModel, lowPolyTreeTextureAtlas);
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(fernModel, fernTextureAtlas);
        fern.getTexture().setTransparent(true);

        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setFakeLighting(true);

        List<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.7f, 0.7f, 0.7f)));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        random.setSeed(12345467);
        for (int i = 0; i < 1000; i++) {
            if (i % 4 == 0) {
                entities.add(new Entity(fern, random.nextInt(4), Maths.randomXYZ(random, (int) Terrain.getSIZE() * TERRAIN_TILES_X,
                        (int) Terrain.getSIZE() * TERRAIN_TILES_Z, terrains), 0, random.nextFloat() * 360, 0, .9f));
                entities.add(new Entity(lowPolyTree, random.nextInt(4), Maths.randomXYZ(random, (int) Terrain.getSIZE() * TERRAIN_TILES_X,
                        (int) Terrain.getSIZE() * TERRAIN_TILES_Z, terrains), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                entities.add(new Entity(tree, Maths.randomXYZ(random, (int) Terrain.getSIZE() * TERRAIN_TILES_X,
                        (int) Terrain.getSIZE() * TERRAIN_TILES_Z, terrains), 0, 0, 0, random.nextFloat() + 4));
                entities.add(new Entity(pine, Maths.randomXYZ(random, (int) Terrain.getSIZE() * TERRAIN_TILES_X,
                        (int) Terrain.getSIZE() * TERRAIN_TILES_Z, terrains), 0, 0, 0, random.nextFloat() + 0.6f));
            }
            if(i % 250 == 0) {
                Lamp lampEntity = new Lamp(lamp, new Vector3f(random.nextFloat() * 5, random.nextFloat() * 5, random.nextFloat() * 5),
                        Maths.randomXYZ(random, (int) Terrain.getSIZE() * TERRAIN_TILES_X,
                                (int) Terrain.getSIZE() * TERRAIN_TILES_Z, terrains), 0, random.nextFloat() * 360, 0, 1);
                entities.add(lampEntity);
                lights.add(lampEntity.getLight());
            }
        }

        MasterRenderer renderer = new MasterRenderer(loader);

        // WATER
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader,
                renderer.getProjectionMatrix(), buffers);
        List<WaterTile> waterTiles = new ArrayList<>();
        WaterTile waterTile = new WaterTile(420, -550, -10);
        waterTiles.add(waterTile);

        // PLAYER
        RawModel playerModel = OBJFileLoader.loadOBJModel("cruiser", loader);
        TexturedModel texturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("cruiser")));
        Player player = new Player(texturedModel, new Vector3f(450, 0, -50), 0, 180, 0, 3);
        Camera camera = new Camera(player);
        entities.add(player);

        while (!Display.isCloseRequested()) {
            camera.move();
            player.move(terrains);

            // render reflection texture
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - waterTile.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, terrains,
                    lights, camera, new Vector4f(0, 1, 0, -waterTile.getHeight()));

            // render refraction texture
            camera.getPosition().y += distance;
            camera.invertPitch();
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities, terrains,
                    lights, camera, new Vector4f(0, -1, 0, waterTile.getHeight()));

            // render display
            buffers.unbindCurrentFrameBuffer();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities, terrains,
                    lights, camera, new Vector4f(0, 1, 0, 1));
            waterRenderer.render(waterTiles, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
