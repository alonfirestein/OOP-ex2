# Pokemon Challenge Game

### Introduction:

In this project I implemented a directed weighted graph and several algorithms and using that I created a self-running game where different number of agents/trainers have to catch as many Pokémon as possible with the least number of moves, and not every path in the graph is possible given that the graph is not necessarily strongly connected. The final score is a calculation of the Pokémon caught, their value and the number of moves made.
A "good" number of moves is less than 10 moves per second.

There are 24 different scenarios available to run with different graphs, different number of agents and Pokémon in each one.



## Running the program:

Clone the project to your current workspace directory:

`git clone https://github.com/alonfirestein/pokemon-challenge.git`

Open your terminal and navigate to the directory where the project and jar file is located, make sure the jar file **and the data folder and images folder** are in the same directory.

Run the command: 	 `java -jar Ex2.java` 

Then the program will start, enter the information as asked and the game will will pop up, start and the agents will automatically start capturing the Pokémon.... because they.... Gotta catch 'em all! ™ 😉

Another option is to run the command using the following parameters: < ID number, Level Number >

For example: `java -jar Ex2.java 12345678 11` 

12345678 serving as your ID number if you want to save your progress, or any other number if you don't.

11 - The level chosen out of the 24 possible scenarios: [0-23]



#### Example:

Below is an example captured during the game in level number 11 .

As we can see in this level there are 3 different trainers on the graph trying to find the shortest path to a Pokémon(if such path exists), and there are six different types of Pokémon (depending on the direction of the edge and the value of the pokemon), once a Pokémon gets captured, another one appears on a different edge in the graph. Once the agent captures a certain value of Pokémon, he speeds up during his traversal.

The overall length of each game varies from level to level.

![example](https://user-images.githubusercontent.com/57404551/102344347-6e246f00-3fa4-11eb-9376-506800e714ff.gif)



# API:

This package contains the implementation classes of the graph and all of its corresponding data classes (nodes, edges, geolocation) and their interfaces.

In order to utilize the main algorithms needed for the game, such as finding if the graph is strongly connected, and the shortest path between two nodes (and their distance), I used several well known algorithms such as DFS, Dijkstra's algorithm and Kosaraju's algorithm. In addition, there are functions that save and load the graphs in a JSON format that stores all of its necessary information.



## GAMECLIENT:

This package contains all the classes needed to create the game, including utilizing the agents/trainers, the Pokémon, the arena, and a simple GUI class using JFrame to "bring it to life".

The main challenge was finding a way to move the agents in a productive way to catch the Pokémon so that it will catch the closest Pokémon with the biggest value in the least amount of moves. To read more please visit the Wiki tab of the repository.



## Tests: 

This package contains all the tests that I ran in order to check the functionality of all the classes. These tests were written and executed using JUnit 5.





### Other Folders:

data - Contains the different graphs in the game, stored in JSON format.

images - Contains the different media types that are used during the game.

libs/lib - Contains the needed libraries to run and create this game.

META-INF - Contains the manifest file that is used to define extension and package related data.





## Please enjoy!

And if you didn't... well here's a dancing Pikachu just in case!

![dancing Pikachu-min](https://user-images.githubusercontent.com/57404551/101994558-d5ec6880-3ccb-11eb-8c10-e0973d1c352e.gif)





#### External Sources:

- [Shortest Path Problem](https://en.wikipedia.org/wiki/Shortest_path_problem)

- [Kosaraju's Algorithm](https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)

- [Dijkstra's Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)

- [Distance between two points formula](https://www.engineeringtoolbox.com/distance-relationship-between-two-points-d_1854.html)

- [JFrame](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JFrame.html)

  



## 