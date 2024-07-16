package com.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;
import java.util.LinkedList;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GameStart {
        
	// The window handle
	private long window;
	private CardRenderer cardRenderer;
	private EnemyCardRenderer enemyCardRenderer;
	private LinkedList<Card> deck;
	private Player player;
	private boolean cardsDealt = false;
	private boolean cardsDealtToEnemy = false;
	private Dealer dealer;
	private Enemy enemy;
	private boolean initializedRenderers = false;
	private ChipRenderer chipRenderer;

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		chipRenderer.cleanup();
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(800, 600, "PokerGame!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		dealer = new Dealer();

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS && !cardsDealt) {
                dealer.dealCardsToPlayer(cardsDealt, deck, player);
                dealer.dealCardsToEnemy(cardsDealtToEnemy, deck, enemy);
                cardsDealt = true;
                cardsDealtToEnemy = true;
            }
        });

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		GL.createCapabilities();

		deck = Card.createDeck();
		player = new Player();
		enemy = new Enemy();
        // レンダラーの初期化はここでは行わない
        float windowAspectRatio = 800.0f / 600.0f;
        cardRenderer = new CardRenderer(windowAspectRatio);
        enemyCardRenderer = new EnemyCardRenderer(windowAspectRatio);

		chipRenderer = new ChipRenderer(800.0f / 600.0f);
		chipRenderer.init();
		
	}

	private void loop() {
		// Set the clear color
		glClearColor(0.0f, 0.2f, 0.0f, 1.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
			if (cardsDealt && !initializedRenderers) {
				cardRenderer.init(player.getHands());
				enemyCardRenderer.init();
				initializedRenderers = true;
			}
	
			if (initializedRenderers) {
				cardRenderer.render();
				enemyCardRenderer.render();
				cardRenderer.render();
				enemyCardRenderer.render();
				chipRenderer.render(player.getChips());
			}

	
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new GameStart().run();
	}

    }
