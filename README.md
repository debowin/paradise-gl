# water-gl
Computer Graphics Final Project - Water Simulation in Nature.

## Setup Instructions
* Add the JARs under lib/jars to your project's libraries.
* Add `-Djava.library.path=lib/natives/` to your JVM options while building.
* IMPORTANT - The native link libraries included are for Linux distributions.
For other platforms, you'll need to download them separately from [here](https://sourceforge.net/projects/java-game-lib/files/Official%20Releases/LWJGL%202.9.3/).

## Snapshots

##### Nature's Beauty
![showcase](snaps/showcase.png)

##### Fake Lighting(Lamp) & Transparent Textures (Pine, Fern)
![fake_lighting](snaps/fake_lighting.png)

##### Fogging, Skybox, Multi-Textured Terrain, Texture Atlases (Bob, Fern)
![fogging_skybox](snaps/fogging_skybox.png)

##### Water Reflections - Fresnel Effect
![reflections](snaps/reflections.png)

##### Water Refractions - Fresnel Effect
![refractions](snaps/refractions.png)

## SIMULATION VIDEO
TBA

## CONTROLS

| Input | Action |
|:----|--------:|
| W | Move Forward |
| S | Move Backward |
| A | Turn Left |
| D | Turn Right |
| Q | Move Upward |
| E | Move Downward |
| Space | x3 Boost Move Speed |
| Mouse Wheel | Zoom In/Out |
| Mouse Left Click & Drag | Rotate Camera |
| Mouse Right Click | Restore Camera |

## Objective
The aim of the project is to render a simulation of a scene consisting of a terrain with a water body using the modern OpenGL pipeline.

## Group Members
* Debojeet Chatterjee
* Ameya Gurjar

## Project Goals

The goal will be to render a scene containing a terrain and a water body which is described below separately.

### Terrain Modelling

* The terrain consists of a surface which has some foliage like trees, grass, flowers and ferns.

* It is modelled as a triangulated quad.

* Multiple texturing is used to texture the terrain which consists of a path, mud, flower patches and grass.

* The entities are loaded as OBJ files.
  
### Water Modelling

* The water body will basically be a simple quad made up of two triangles.

* The quad will be textured so that it looks like a water body. A combination of reflection texture and refraction texture will be used. This will make use of frame buffer objects.

* The two textures will be combined using the fresnel effect. This can be achieved by the formula
`max(dot(water_normal, view_direction), 0)`

* A higher value will result in less reflectiveness and a lower value will result in less refraction.  

* The lighting position and the color of the light will be determined. A normal map will be used for the lighting calculations on the surface of the water.

## Progress Description

* We're using the Light-Weight Java Game Library for this project to try out a different style of OpenGL programming and also,
since using Java allows us to package and structure our code more easily in an Object Oriented manner.

* So far we have managed to create a terrain that supports transparent textures to render ferns, flowers and grass clumps
to let us see through areas where the alpha of the texture is less than 0.5

* We're also mixing between the sky colour and the terrain as well as model texture color to haze distant models and terrains exponentially
using the equation `visibility = e^(-(distance*fogDensity)^fogGradient)`, 
where Fog Density controls how thick the fog is while Gradient controls how smoothly the scene hazes into the distance.

* In addition, we are multi-texturing the terrain using a blend map to blend between 4 different textures based on the R, G, B components.

* We're also able to randomly generate and place a large amount of foliage on the map for which we're using an optimised approach to load
a multitude of instanced entities by loading their corresponding textures only once before rendering them.

* We have provided keyboard functions to navigate the world using W, A, S, D, LShift, LCtrl. Also, holding Space boosts the speed of 
camera movement by a factor of 3.

## FUTURE WORK

* Audio Effects.

* Entity Collision with Player.

* Particle Effects for flowing water, fountains, waterfalls and other natural phenomenon.

* Deferred Rendering could let us include a lot more lamps and let us have a beautiful Night scene.

* Shadow Maps and Entity Normal Maps.