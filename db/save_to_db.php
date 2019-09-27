<?php
 $user="root";
 $pass="a43235167";
 $DBname="monitor_db";
 
 $target=$_POST['target'];
 $devicesid=$_POST['devicesid'];
 $status=$_POST['status'];
 $time=$_POST['time'];
 $obj=$_POST['obj'];
 

 $link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
 if($link->connect_error){ echo "error!";}
  
 $array=explode("-",$time);
 $year=$array[0];
 $month=$array[1];
 $day=explode("T",$array[2])[0];
 $time=explode("T",$array[2])[1];

 $s="INSERT INTO "."$target"."(id,status,year,month,day,time,value) VALUES('"."$devicesid"."','"."$status"."','"."$year"."','"."$month"."','"."$day"."','"."$time"."','"."$obj"."')";
//  $link->query("set names utf8");
  $link->query($s);
  
?>