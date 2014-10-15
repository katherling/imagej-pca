
public class Pixel {
	
private int x;
private int y;
private float red;
private float green;
private float blue;

public Pixel(){
	x = 0;
	y = 0;
	red = 0;
	green = 0;
	blue = 0;
}

public Pixel (int x, int y, float red, float green, float blue){
	this.x = x;
	this.y = y;
	this.red = red;
	this.green = green;
	this.blue = blue;
}

public int getX(){
	return this.x;
}
public void setX(int x){
	this.x = x;
}
public int getY(){
	return this.y;
}
public void setY(int y){
	this.y = y;
}
public float getRed(){
	return this.red;
}
public void setRed(float red){
	this.red = red;
}
public float getGreen(){
	return this.green;
}
public void setGreen(float green){
	this.red = green;
}
public float getBlue(){
	return this.blue;
}
public void setBlue(float blue){
	this.red = blue;
}





}
