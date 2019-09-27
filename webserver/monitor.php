<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <style>
        .fade-enter-active {
            animation: go 2s;
        }

        @keyframes go {
            from{
                opacity: 0 ;
                -webkit-transform: translateX(100%)
            }

            to {
                opacity: 1.0;
                -webkit-transform: translateX(0%)
            }
        }

        .unit{
            background-color:mediumseagreen
        }
        .g{
            background-color:aqua
        }
        body{
            background-color: black
        }
    </style>
</head>
<body>
    <div id="d" >
        <transition-group appear appear-active-class="fade-enter-active">
            <div v-for="(item,index) in list" :key="item.id" :id="item.id" :title="item.title"
                style="width:1225px;height:820px;border:1px solid black;padding: 5px;margin: 3px" class="unit">

                <div id="graph1"
                    style="width:600px;height:400px;float: left;border:1px solid black;margin: 3px" class="g">
                </div>
                <div id="graph2" 
                    style="width:600px;height:400px;float: right;border:1px solid black;margin: 3px" class="g">
                </div>
                <br>
                <div id="graph3" 
                    style="width:600px;height:400px;float: left;border:1px solid black;margin: 3px" class="g">
                </div>
                <div id="graph4" 
                    style="width:600px;height:400px;float: right;border:1px solid black;margin: 3px" class="g">
                </div>

            </div>
        </transition-group>
    </div>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
    <!-----https://github.com/plotly/plotly.js/><!--> 
    <!-- 开发环境版本，包含了有帮助的命令行警告 -->
    <!-- <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script> -->
    <!-- 生产环境版本，优化了尺寸和速度 -->
    <script src="https://cdn.jsdelivr.net/npm/vue"></script>
    <script>
        //get webserver config
        let cfg=<?php 
            $user="root";
            $pass="a43235167";
            $DBname="data";
            $link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
            if($link->connect_error){ echo "error!";}
            $s="SELECT * from "."devices_config"."";
            $r=$link->query($s);
            $r=$r->fetch_all();
            $r=json_encode($r);
            echo $r;
        ?>;
        //div generator list
        let divlist=[];
        for (let index = 0; index < cfg.length; index++) {
            divlist[index]={id: "d"+cfg[index][1], title: cfg[index][0]}
            cfg[index][2]=JSON.parse(cfg[index][2]);
        }
        //init dom
        var vm = new Vue({
            el: '#d',
            delimiters: ['${', '}'],
            data: {
                list: divlist
            }
        });
        //global var
        let webserver = "http://showdata.nctu.me/webserver/testjson.php";
        if (location.protocol == "https:") {
            webserver = "https://showdata.nctu.me/webserver/testjson.php";
        }

        let normalColor = '#80CAF6';
        let wraningColor = "red";
        let unknown = 0;//unknown value
        let timeout=10000;//10s
        //
        for (let index = 0; index < cfg.length; index++) {
            let div=document.getElementById(divlist[index].id);
            
            //devices
            {
                let sensorNum = cfg[index][2].length;//sensor num
                let delay = 5000;//refresh rate in ms
                let tarray = new Array();//time array
                let darray = new Array();//data array
                let txtarray=new Array();//text array
                let graphSet = [];
                let titleSet = [];
                for (let i = 0; i < sensorNum; i++){
                    let n=(i==2?i+1:i);
                    div.children[n].id=divlist[index].id+div.children[n].id;
                    graphSet[i]=div.children[n].id;
                    titleSet[i]=div.title+"-"+cfg[index][2][i].id;
                }
                let pastLimit = 100;//how many points can look back
                let target = div.title;

                //call refresh in fixed time
                setInterval(refresh, delay);

                //initialize plotly.js
                let now = new Date();
                let pre = now;//pre time holder
                let data = [{
                    x: [now],
                    y: [0],
                    mode: 'lines+markers',
                    line: { color: normalColor }
                }];

                //initialize time and data array to nested
                //start plot
                for (let i = 0; i < sensorNum; i++) {
                    tarray[i] = [];
                    darray[i] = [];
                    txtarray[i]=[];
                    Plotly.plot(graphSet[i], data, { title: titleSet[i] });
                }


                //refresh data from web server
                function refresh() {
                    let request = new XMLHttpRequest();
                    request.timeout=timeout;
                    request.ontimeout=function(e){console.log("web server no response");};
                    request.addEventListener("readystatechange", function () {
                        if (this.readyState == 4) {// 4 => complete
                            console.log(this.responseText);
                            let ar = this.responseText.split(";");

                            let devicesid = ar[0];//devicesid
                            let devicesstatus = ar[1];//devicesstatus
                            let time = new Date(ar[2]);//time
                            let obj = JSON.parse(ar[3]);//json value
                            console.log(new Date(ar[2]).toString());
                            for (const iterator of obj) {
                                console.log(iterator);
                            }


                            if (time.valueOf() == pre.valueOf()) {//dulplicate

                            } else {//not dulplicate
                                pre = time;//update pre time holder

                                for (let i = 0; i < obj.length; i++) {//loop dif sensor graph
                                    //append new data
                                    let value = obj[i].value;
                                    if (value == "unknown") {
                                        value = unknown
                                    }
                                    tarray[i].unshift(time);
                                    darray[i].unshift(value);
                                    txtarray[i].unshift((devicesstatus == "unavailable" ? devicesstatus : (obj[i].status)));
                                    let color = normalColor;//set line color

                                    //update data set
                                    let update = {
                                        x: [tarray[i]],
                                        y: [darray[i]],
                                        line: { color: color },
                                        text: [txtarray[i]],
                                        textposition: "top"
                                    };

                                    //prepare view obj
                                    let olderTime = time.setMinutes(time.getMinutes() - 1);
                                    let futureTime = time.setMinutes(time.getMinutes() + 1);
                                    let min = Math.min(...darray[i]);
                                    let max = Math.max(...darray[i]);
                                    let dif = max - min;
                                    let minuteView = {
                                        'yaxis.range': [min - dif, max + dif],
                                        'yaxis.title': obj[i].type,
                                        xaxis: {
                                            type: 'date',
                                            range: [olderTime, futureTime]
                                        },
                                        title: titleSet[i] + "<br>" + (devicesstatus == "unavailable" ? devicesstatus : (obj[i].status))
                                    }
                                    //set view point and update data
                                    Plotly.relayout(graphSet[i], minuteView);
                                    Plotly.update(graphSet[i], update);

                                    //clear too many data if in need
                                    if (tarray[i].length > pastLimit) {
                                        tarray[i].pop();
                                        darray[i].pop();
                                        txtarray[i].pop();
                                    }
                                }
                            }
                        }
                    });

                    //send http get request to webserver
                    request.open("GET", webserver + "?target=" + target, true);
                    request.send();
                }
            }
        }
    </script>
    
</body>
</html>