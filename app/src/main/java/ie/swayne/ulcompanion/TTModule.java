package ie.swayne.ulcompanion;

public class TTModule implements Cloneable {

    private String name;
    private String code;
    private String startTime;
    private String endTime;
    private String room;
    private String type;
    private int dur;
    private String day;

    public TTModule(String n, String c, String sT, String eT, String r, String t, String d) {
        name = n;
        code = c;
        startTime = sT;
        endTime = eT;
        room = r;
        type = t;
        day = d;

        String start = sT.substring(0,2);
        String end = eT.substring(0,2);

        dur = Integer.parseInt(eT.substring(0,2)) - Integer.parseInt(sT.substring(0,2));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getStart() {
        return startTime;
    }

    public String getEnd() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return dur;
    }

    public String getDay() {
        return day;
    }

    public String toString(){
        return startTime + "\t" + endTime + "\t" + code + "\t" + room + "\t" + type + "\t" + day;
    }

    public void addHour() {
        int time = Integer.parseInt(this.startTime.substring(0,2));
        this.startTime = Integer.toString(time + 1) + ":" + this.startTime.substring(3,5);
    }

    public Object clone() {
        String name = new String(this.name);
        String code = new String(this.code);
        String startTime = new String(this.startTime);
        String endTime = new String(this.endTime);
        String room = new String(this.room);
        String type = new String(this.type);
        String day = new String(this.day);

        TTModule mod = new TTModule(name, code, startTime, endTime, room, type, day);
        return mod;
    }

}
