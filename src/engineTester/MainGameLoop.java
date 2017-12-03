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
import terrain.Terrain;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        ModelData treeData = OBJFileLoader.loadOBJ("tree");
        ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
        ModelData fernData = OBJFileLoader.loadOBJ("fern");
        ModelData lowPolyTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(),
                treeData.getNormals(), treeData.getIndices());
        RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(),
                grassData.getNormals(), grassData.getIndices());
        RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(),
                fernData.getNormals(), fernData.getIndices());
        RawModel lowPolyTreeModel = loader.loadToVAO(lowPolyTreeData.getVertices(),
                lowPolyTreeData.getTextureCoords(), lowPolyTreeData.getNormals(),
                lowPolyTreeData.getIndices());

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
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            entities.add(new Entity(tree,
                    new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                    0, 0, 0, 3));
            entities.add(new Entity(grass,
                    new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                    0, 0, 0, 1));
            entities.add(new Entity(flower,
                    new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                    0, 0, 0, 1));
            entities.add(new Entity(fern,
                    new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                    0, 0, 0, .6f));
            entities.add(new Entity(lowPolyTree,
                    new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),
                    0, 0, 0, 0.3f));
        }

        Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

        Terrain terrain = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));

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
