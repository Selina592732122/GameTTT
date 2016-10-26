package tic_tac_toe.com.gamettt.bean;

import java.io.Serializable;

public class Position implements Serializable{
	private float x;
	private float y;
	private int color;
	private int score;
	private Point point;

	public Position(float x, float y, int color, int score, Point point) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.score = score;
		this.point = point;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	@Override
	public String toString() {
		return "Position{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
