package world;


public interface ItemFactory {
	public Item makeSpec(int id);
	public Item makeTake(int id);
}