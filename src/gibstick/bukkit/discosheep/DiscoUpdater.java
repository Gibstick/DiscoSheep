public class DiscoUpdater extends BukkitRunnable{
	private final int defaultDuration = 1000;// ticks
	private final int defaultFrequency = 20;// ticks per state change
	
	int frequency=0,duration=0;

	private DiscoSheep parent;
	
	public DiscoUpdater(DiscoSheep parent){
		this.parent = parent;
		
	}
	
	public void stop(){
		this.duration = 0;
		parent.cleanUp();
	}
	
	public void start(int duration, int frequency){
		this.frequency = this.defaultFrequency;
		this.durtion = this.defaultDuration;
		parent.scheduleUpdate();
	}
	
	public void run(){
		if(duration > 0){
			cycleSheepColours();
			playSounds();
			duration -= frequency;
			parent.scheduleUpdate(this);
		} else {
			this.stop();
		}
	}
}	