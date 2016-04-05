# jonajump

This is a java application that implements a simple jump and run game.
It is pretty bare bones and not finished at all. But the basic mechanisms
are implemented and working. 

The class

 ch.jonajump.JonaJump
 
is a main class and runs the game.

The class

 ch.jonajump.leveleditor.LevelEditor
 
is a basic level editor.

There are three levels as of now. You can collect drops and coins and
stars. You have three lives.

In order to make testing easier, you can jump from level to level
by pressing the number 1, 2, 3 as appropriate.

The level and player artwork is contained in the resources of the
game. As I am no graphic designer, the existing bitmaps can only be
thought of as a proof of concept.

The level editors accepts the following keys:

```
 1 - toggle background
 2 - toggle items
 3 - toggle foreground
 4 - toggle grid
 b - select brick
 d - select drop
 g - select gold
 s - select star
 j - select jumper
 z - erase last item
 w - write changes to disk
```

Use left and right to scroll through the level and page up/down to
switch between levels. You draw elements using the mouse.

Creating a new level involves adding the required resources to 
the project.
