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
    public String info="";
    public JSONObject[] sensors;

    public DataWindow(String devicesID, int sensorNum, String[] content) {
        this.devicesID = devicesID;
        this.sensorNum = sensorNum;
        sensors = new JSONObject[this.sensorNum];
        int index = 0;
        JSONObject obj = null;
        for (String line : content) {
            if (line.startsWith("devices") || line.startsWith("target")) {
                continue;
            } else if (line.startsWith("sensor")) {
                obj = new JSONObject();
                sensors[index] = obj;
                index++;
                obj.put("id", line.substring(0, line.length() - 1));
                //set initial pre state
                obj.put("pre-status", "normal");
                continue;
            } else if(line.startsWith("info=")){
                info=line.substring("info".length());
                continue;
            }else if (line.equals("")) {
                continue;
            }
            String pair[] = line.split("=");
            if (pair.length == 2) {
                obj.put(pair[0], pair[1]);
            }
        }

    }
/**
 * 
 * if some sensor is not defined in setting.ini, it will be skip;
 * else if some sensor is defined but missing in arg data, it will be skip too<br>
 * return concated(by '&') obj_json string and msg=[a,b,c...], the [] means none ,otherwise split by "," notation 
 * @param data data to be evaluated
 * @param isknown the data is unvailabe one or not
 * @return string "obj_json_strformat&msg=[a,b,c,...]";
 */
    public synchronized String evaluate(String data, boolean isknown) {
        JSONArray ar = new JSONArray(data);
        String ret = "[";
        String msg = "[";
        for (int i = 0; i < sensorNum; i++) {//input data
            JSONObject obj = ar.getJSONObject(i);
            String status = "";
            String target = obj.getString("id");
            for (JSONObject des : sensors) {//elvaluae if defined
                if (des.getString("id").equals(target)) {
                    if (isknown) {
                        float value = obj.getFloat("value");
                        if (value > Float.parseFloat(des.getString("hdanger"))) {
                            status = "hdanger(" + value + ">" + Float.parseFloat(des.getString("hdanger")) + ")";
                        } else if (value > Float.parseFloat(des.getString("hwarn"))) {
                            status = "hwarn(" + value + ">" + Float.parseFloat(des.getString("hwarn")) + ")";
                        } else if (value < Float.parseFloat(des.getString("ldanger"))) {
                            status = "ldanger(" + value + "<" + Float.parseFloat(des.getString("ldanger")) + ")";
                        } else if (value < Float.parseFloat(des.getString("lwarn"))) {
                            status = "lwarn(" + value + "<" + Float.parseFloat(des.getString("lwarn")) + ")";
                        } else {
                            status = "normal";
                        }
                    } else {
                        status = "unknown";
                    }
                    //testing status change event
                    if (!des.getString("pre-status").equals(status) && isknown) {
                        msg += "devices" + devicesID + "-" + target + " " + des.getString("pre-status") + " turn to " + status + ",";
                        des.put("pre-status", status);
                    }

                    obj.put("status", status);
                    ret += obj.toString();
                    if (i < sensorNum - 1) {
                        ret += ", ";
                    }
                    break;
                }
            }
        }
        
        msg = "&msg=" + (msg.length()<=1?"[":msg.substring(0, msg.length() - 1)) + "]";
        ret += "]" + msg;
        return ret;
    }

}
