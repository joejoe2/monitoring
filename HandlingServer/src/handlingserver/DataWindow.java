/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;

import org.json.JSONObject;
import org.json.JSONArray;
/**
 *
 * @author 70136
 */
public class DataWindow {
    public String devicesID;
    public int sensorNum;

    public JSONObject[] sensors;
    
    public DataWindow(String devicesID, int sensorNum, String[] content) {
        this.devicesID = devicesID;
        this.sensorNum = sensorNum;
        sensors=new JSONObject[this.sensorNum];
        int index=0;
        JSONObject obj=null;
        for (String line : content) {
            if(line.startsWith("devices")||line.startsWith("target")){
                continue;
            }else if(line.startsWith("sensor")){
                obj = new JSONObject();
                sensors[index]=obj;
                index++;
                obj.put("id", line.substring(0,line.length()-1));
                continue;
            }else if(line.equals("")){  
                continue;
            }
            String pair[]=line.split("=");
            if(pair.length==2){
                obj.put(pair[0], pair[1]);
            }
        }
        
    }
    
    public synchronized String evaluate(String data) {
        JSONArray ar=new JSONArray(data);
        String ret="[";
        for(int i =0 ;i<sensorNum;i++){
            JSONObject obj = ar.getJSONObject(i);
            String status="";
            String target = obj.getString("id");
            for (JSONObject des : sensors) {
                if(des.getString("id").equals(target)){
                    float value=obj.getFloat("value");
                    if(value>Float.parseFloat(des.getString("hdanger"))){
                        status="hdanger";
                    }else if(value>Float.parseFloat(des.getString("hwarn"))){
                        status="hwarn";
                    }else if(value<Float.parseFloat(des.getString("lwarn"))){
                        status="lwarn";
                    }else if(value<Float.parseFloat(des.getString("ldanger"))){
                        status="ldanger";
                    }else{
                        status="normal";
                    }
                    obj.put("status", status);
                    ret+=obj.toString();
                    if(i<sensorNum-1)
                    {ret+=", ";}
                    break;
                }
            }
        }
        ret+="]";
        return ret;
    }
    
    
}
