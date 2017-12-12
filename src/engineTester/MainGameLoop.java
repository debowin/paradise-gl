package engineTester;

import entities.*;
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
import toolbox.Maths;

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

        Terrain[][] terrains = new Terrain[2][2];
        terrains[0][0] = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
        terrains[1][0] = new Terrain(1, -1, loader, texturePack, blendMap, "heightmap");
        terrains[0][1] = new Terrain(0, -2, loader, texturePack, blendMap, "heightmap");
        terrains[1][1] = new Terrain(1, -2, loader, texturePack, blendMap, "heightmap");

        // ENTITIES
        RawModel treeModel = OBJFileLoader.loadOBJModel("tree", loader);
        RawModel pineModel = OBJFileLoader.loadOBJModel("pine", loader);
        RawModel fernModel = OBJFileLoader.loadOBJModel("fern", loader);
        RawModel lowPolyTreeModel = OBJFileLoader.loadOBJModel("lowPolyTree", loader);
        RawModel boxModel = OBJFileLoader.loadOBJModel("box", loader);
        RawModel dragonModel = OBJFileLoader.loadOBJModel("dragon", loader);
        RawModel bunnyModel = OBJFileLoader.loadOBJModel("bunny", loader);
        RawModel lampModel = OBJFileLoader.loadOBJModel("lamp", loader);

        TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
        TexturedModel pine = new TexturedModel(pineModel, new ModelTexture(loader.loadTexture("pine")));
        ModelTexture lowPolyTreeTextureAtlas = new ModelTexture(loader.loadTexture("lowPolyTree"));
        lowPolyTreeTextureAtlas.setNumberOfRows(2);
        TexturedModel lowPolyTree = new TexturedModel(lowPolyTreeModel, lowPolyTreeTextureAtlas);
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(fernModel, fernTextureAtlas);
        fern.getTexture().setTransparent(true);

        TexturedModel dragon = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("yellow")));
        TexturedModel bunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));
        TexturedModel box = new TexturedModel(boxModel, new ModelTexture(loader.loadTexture("box")));
        TexturedModel lamp = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setFakeLighting(true);

        List<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(0, 1000, -7000), new Vector3f(0.7f, 0.7f, 0.7f)));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            if (i % 10 == 0) {
                entities.add(new Entity(box,
                        Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                        0, random.nextFloat() * 360, 0, random.nextFloat() + 1.5f));
            }
            if (i % 2 == 0) {
                entities.add(new Entity(fern, random.nextInt(4), Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                        0, random.nextFloat() * 360, 0, .9f));
            }
            if (i % 5 == 0) {
                entities.add(new Entity(lowPolyTree, random.nextInt(4), Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                        0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                entities.add(new Entity(tree, Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                        0, 0, 0, random.nextFloat() + 4));
                entities.add(new Entity(pine, Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                        0, 0, 0, random.nextFloat() + 0.6f));
            }
            if(i % 100 == 0) {
                Lamp lampEntity = new Lamp(lamp, new Vector3f(random.nextFloat() * 5, random.nextFloat() * 5, random.nextFloat() * 5),
                        Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                        0, random.nextFloat() * 360, 0, 1);
                entities.add(lampEntity);
                lights.add(lampEntity.getLight());
            }
        }
        entities.add(new Entity(dragon, Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                0, 180, 0, 1));
        entities.add(new Entity(bunny, Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains),
                0, 0, 0, 0.8f));

        MasterRenderer renderer = new MasterRenderer();

        // PLAYER
        RawModel playerModel = OBJFileLoader.loadOBJModel("cruiser", loader);
        TexturedModel texturedModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("cruiser")));
        Player player = new Player(texturedModel, Maths.randomXYZ(random, (int) Terrain.getSIZE() * 2, (int) Terrain.getSIZE() * 2, terrains), 0, 180, 0, 3);
        Camera camera = new Camera(player);

        while (!Display.isCloseRequested()) {
            camera.move();
            player.move(terrains);
            renderer.processEntity(player);
            for (int i = 0; i < 4; i++)
                renderer.processTerrain(terrains[i / 2][i % 2]);
            for (Entity entity : entities) {
                renderer.processEntity(entity);
            }
            renderer.render(lights, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
