package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
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
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
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

        List<Entity> entities = new ArrayList<>();
        Random random = new Random(765246);
        for (int i = 0; i < 400; i++) {
            if(i%7==0){
                entities.add(new Entity(grass,
                        new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),
                        0, 0, 0, 1));
                entities.add(new Entity(flower,
                        new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),
                        0, 0, 0, 1));
            }
            if(i%3==0) {
                entities.add(new Entity(fern,
                        new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),
                        0, random.nextFloat()*360, 0, .9f));
                entities.add(new Entity(lowPolyTree,
                        new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                        0, random.nextFloat()*360, 0, random.nextFloat() * 0.1f + 0.6f));
                entities.add(new Entity(tree,
                        new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                        0, 0, 0, random.nextFloat() + 4));
            }
        }

        Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

        Camera camera = new Camera();
        camera.setPosition(new Vector3f(0, 2, 0));
        MasterRenderer renderer = new MasterRenderer();

        while (!Display.isCloseRequested()) {
            camera.move();

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
