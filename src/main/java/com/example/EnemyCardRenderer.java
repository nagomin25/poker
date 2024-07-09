package com.example;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.*;

import org.lwjgl.BufferUtils;

public class EnemyCardRenderer {

    private Integer texture;
	private float windowAspectRatio;

	public EnemyCardRenderer(float windowAspectRatio) {
		this.windowAspectRatio = windowAspectRatio;
	}

    public void init() {
        glEnable(GL_TEXTURE_2D);
        texture = loadTexture("/back.png");
    }



	public void render() {
		if (texture == null) {
			return; // カードがない場合は何もレンダリングしない
		}
		float cardAspectRatio = 2.0f / 3.0f;
		float cardHeight = 0.6f; // 画面の高さの60%
		float cardWidth = (cardHeight * cardAspectRatio) / windowAspectRatio;

		// カード間の間隔（カード幅の10%）
		float cardSpacing = cardWidth * 0.1f;

		// 2枚のカードの合計幅（カード2枚 + 間隔）
		float totalWidth = (cardWidth * 2) + cardSpacing;

		// 左端のカードのX座標（画面中央を基準に左にずらす）
		float leftCardX = -totalWidth / 2;

		// Y座標（画面上）
		float cardY = 1 - cardHeight - (cardHeight * 0.1f);

		// 1枚目のカード
		drawCard(texture, leftCardX, cardY, cardWidth, cardHeight);

		// 2枚目のカード
		drawCard(texture, leftCardX + cardWidth + cardSpacing, cardY, cardWidth, cardHeight);
	}

	private void drawCard(int texture, float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, texture);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1);
		glVertex2f(x, y);
		glTexCoord2f(1, 1);
		glVertex2f(x + width, y);
		glTexCoord2f(1, 0);
		glVertex2f(x + width, y + height);
		glTexCoord2f(0, 0);
		glVertex2f(x, y + height);
		glEnd();
	}

	// loadTexture メソッドの実装をここに移動
	private int loadTexture(String resourcePath) {
		try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
			if (is == null) {
				throw new RuntimeException("Resource not found: " + resourcePath);
			}
			byte[] imageData = is.readAllBytes();
			ByteBuffer buffer = BufferUtils.createByteBuffer(imageData.length);
			buffer.put(imageData);
			buffer.flip();

			IntBuffer width = BufferUtils.createIntBuffer(1);
			IntBuffer height = BufferUtils.createIntBuffer(1);
			IntBuffer channels = BufferUtils.createIntBuffer(1);

			ByteBuffer image = stbi_load_from_memory(buffer, width, height, channels, 4);
			if (image == null) {
				throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
			}

			int textureId = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureId);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			stbi_image_free(image);
			return textureId;

		} catch (IOException e) {
			throw new RuntimeException("Failed to load texture", e);
		}
			
		
	}
}
