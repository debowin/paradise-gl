package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        // TERRAIN TEXTURE
        TerrainTexture backGroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backGroundTexture, rTexture, gTexture, bTexture);

        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
        Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

        // ENTITIES
        RawModel treeModel = OBJFileLoader.loadOBJModel("tree", loader);
        RawModel grassModel = OBJFileLoader.loadOBJModel("grassModel", loader);
        RawModel fernModel = OBJFileLoader.loadOBJModel("fern", loader);
        RawModel lowPolyTreeModel = OBJFileLoader.loadOBJModel("lowPolyTree", loader);
        RawModel dragonModel = OBJFileLoader.loadOBJModel("dragon", loader);
        RawModel bunnyModel = OBJFileLoader.loadOBJModel("bunny", loader);

        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel lowPolyTree = new TexturedModel(lowPolyTreeModel, new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setTransparent(true);
        grass.getTexture().setFakeLighting(true);
        TexturedModel flower = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("flower")));
        flower.getTexture().setTransparent(true);
        flower.getTexture().setFakeLighting(true);
        TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setTransparent(true);
        TexturedModel dragon = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("yellow")));
        TexturedModel bunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random(765246);
        for (int i = 0; i < 400; i++) {
            if (i % 7 == 0) {
                entities.add(new Entity(grass,
                        new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),
                        0, 0, 0, 1));
                entities.add(new Entity(flower,
                        new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),
                        0, 0, 0, 1));
            }
            if (i % 3 == 0) {
                entities.add(new Entity(fern,
                        new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),
                        0, random.nextFloat() * 360, 0, .9f));
                entities.add(new Entity(lowPolyTree,
                        new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                        0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                entities.add(new Entity(tree,
                        new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                        0, 0, 0, random.nextFloat() + 4));
            }
        }
        entities.add(new Entity(dragon, new Vector3f(50, 0, -250), 0, 180, 0, 1));
        entities.add(new Entity(bunny, new Vector3f(100, 0, -250), 0, 0, 0, 0.8f));

        Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

        MasterRenderer renderer = new MasterRenderer();

        // PLAYER
        RawModel playerModel = OBJFileLoader.loadOBJModel("cruiser", loader);
        TexturedModel texturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("cruiser")));
        Player player = new Player(texturedModel, new Vector3f(100, 20, -60), 0, 180, 0, 2);
        Camera camera = new Camera(player);

        while (!Display.isCloseRequested()) {
            entities.get(entities.size() - 1).rotate(0, -1, 0);
            entities.get(entities.size() - 2).rotate(0, 1, 0);
            camera.move();
            player.move();

            renderer.processEntity(player);
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            for (Entity entity : entities) {
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
