package com.example;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.*;

import org.lwjgl.BufferUtils;

public class CardRenderer {
	private List<Integer> textures;
	private float windowAspectRatio;

	public CardRenderer(float windowAspectRatio) {
		this.windowAspectRatio = windowAspectRatio;
		this.textures = new ArrayList<>();
	}

    public void init(List<Card> cards) {
        glEnable(GL_TEXTURE_2D);
        for (Card card : cards) {
            String imagePath = getImagePath(card);
            int texture = loadTexture(imagePath);
            textures.add(texture);
        }
    }

	public String getImagePath(Card card){
		String suit = card.getSuit().toString().toLowerCase();
		suit = suit.substring(0,suit.length() - 1);
		String rank = getRankString(card.getRank());
		return "/" + suit + "/" + suit + "_" + rank + ".png";
	}

	public String getRankString(Rank rank){
		switch(rank){
			case ACE: return "A";
			case TWO: return "2";
			case THREE: return "3";
			case FOUR: return "4";
			case FIVE: return "5";
			case SIX: return "6";
			case SEVEN: return "7";
			case EIGHT: return "8";
			case NINE: return "9";
			case TEN: return "10";
			case JACK: return "J";
			case QUEEN: return "Q";
			case KING: return "K";
			default: return null;
		}

	}



	public void render() {
		if (textures.isEmpty()) {
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

		// Y座標（画面下）
		float cardY = -1 + (cardHeight * 0.1f);

		// 1枚目のカード
		drawCard(textures.get(0), leftCardX, cardY, cardWidth, cardHeight);

		// 2枚目のカード
		drawCard(textures.get(1), leftCardX + cardWidth + cardSpacing, cardY, cardWidth, cardHeight);
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
