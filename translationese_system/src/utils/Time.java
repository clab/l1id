package utils;

public class Time
{
	private int hours, minutes, seconds;
	private long millis;
	
	public Time(long millis)
	{
		this.millis = millis;
		long seconds = millis / 1000;
		this.seconds = (int)(seconds % 60);
		long minutes = seconds / 60;
		this.minutes = (int)(minutes % 60);
		hours = (int)(minutes / 60);
	}
	
	
	public String toString()
	{
		StringBuffer buff = new StringBuffer(String.valueOf(String.format("%02d", hours)));
		buff.append(":");
		buff.append(String.format("%02d", minutes));
		buff.append(":");
		buff.append(String.format("%02d", seconds));
		return buff.toString();
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public long getMillis() {
		return millis;
	}

	public void setSeconds(long millis) {
		this.millis = millis;
	}
	
}
